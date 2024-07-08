---
title: "Flutter Background Service 실행시키기 with drift, flutter_local_notifications"
categories:
    - Flutter
---
도토리 앱은 나의 하이킹 루트를 기록하는 것이 핵심 기능이다. 하지만 하이킹을 하는 몇 시간 내내 앱을 포그라운드에 켜 놓고 위치 정보를 수집할 수는 없기 때문에 필연적으로 이를 전담할 백그라운드 서비스를 만들어야 한다.

## 요약

`flutter_background_service` 패키지를 사용해 백그라운드 서비스를 구현하였다. 이 서비스 안에서 `geolocator`패키지를 통해 위치정보를 수집하여 `drift` 패키지를 통해 DB에 저장한다. 위치정보를 보여줄 screen이 전면에 열리면 db에서 해당 내용을 stream으로 읽어 화면에 실시간으로 표시한다. 포그라운드 알림 표시는 `flutter_local_notifications`를 사용했다. 이 게시글은 `geolocator`와 `drift` 패키지는 이미 초기화가 끝났고, 사용중이라는 것을 가정하고 작성하였다.

## 1. drift DB 서버용 Isolate 설정

서비스를 구현하기에 앞서 `drift` 관련 사전 세팅이 필요하다. [공식문서](https://drift.simonbinder.eu/docs/advanced-features/isolates/)에 따르면, 서로 다른 Isolate에서 DB의 query stream을 구독하는 경우 그게 같은 db파일이더라도 동기화 되지 않는다. 그래서 DB에 접근하는 서버 Isotate 하나를 별도로 만든 후, 클라이언트 Isolate(Main과 백그라운드 서비스를 실행할 Isolate)들에서 이 서버 Isolate를 통해 소통해야 한다.

**provider.dart**
~~~dart
//  지연 초기화를 적용하기 위한 provider세팅
final localDatabaseProvider = Provider<LocalDatabase>((ref) {
  throw UnimplementedError();
});
~~~

**main.dart**
~~~dart
Future<void> main() async {
  ...

  final localDatabase = await LocalDatabase.instance();

  runApp(ProviderScope(
    overrides: [
        //  런타임에서 provider value 설정. 이렇게 안할꺼면 FutureProvider를 사용해야 한다.
      localDatabaseProvider.overrideWithValue(localDatabase),
    ],
    child: configuredApp,
  ));
}
~~~

**drift_database.dart**
~~~dart
@DriftDatabase(
  tables: [
    Positions,
  ],
)
class LocalDatabase extends _$LocalDatabase {
  
  ...

    //  Server Isolate를 직접 만들어 database connection 생성.
  static Future<LocalDatabase> instance() async {
    final isolate = await createDatabaseIsolate();
    final connection = await isolate.connect();
    return LocalDatabase(connection);
  }
}

Future<DriftIsolate> createDatabaseIsolate() async {
    //  port name을 설정하여 Isolate port를 name server에 저장한다. 이렇게 해야 매번 같은 Isolate에 연결할 수 있다.
  final port = IsolateNameServer.lookupPortByName(dbPortName);
  if (port != null) {
    //  이미 port가 name server에 있으면 해당 port를 통해 isolate에 접근.
    return DriftIsolate.fromConnectPort(port);
  } else {
    //  port가 name server에 없으면 새로 생성(spawn)해서 name server에 저장한다.
    final token = RootIsolateToken.instance;
    return DriftIsolate.spawn(() {
      BackgroundIsolateBinaryMessenger.ensureInitialized(token!);

      return LazyDatabase(() async {
        final dbFolder = await getDatabaseDirectory();
        final file = File(p.join(dbFolder.path, dbFileName));
        return NativeDatabase(file);
      });
    }).then((value) {
      IsolateNameServer.registerPortWithName(value.connectPort, dbPortName);
      return value;
    });
  }
}
~~~

이제 `LocalDatabase.instance()`를 호출하여 어디서든 같은 server isolate를 통해 db에 접근할 수 있다.

## 2. background service
### flutter_background_service 패키지 초기화
터미널을 열어 아래 커맨드를 통해 패키지를 프로젝트에 통합시킨다.

`flutter pub add flutter_background_service`

이 패키지는 각 플랫폼별 네이티브 설정이 필요하기 때문에 [공식문서](https://pub.dev/packages/flutter_background_service)를 따라서 설정해 줘야 한다. 별로 안어려움.

### service 초기화 코드 생성
**position_service.dart**
~~~dart
...

Future<void> initializeService() async {
    //  service객체 생성
  final service = FlutterBackgroundService();

    //  Android 알림 채널 설정
  const AndroidNotificationChannel channel = AndroidNotificationChannel(
    androidNotificationChannelId,
    androidNotificationChannelName,
    importance: Importance.high,
  );

    //  Android 알림 채널 생성
  await flutterLocalNotificationsPlugin
      .resolvePlatformSpecificImplementation<
          AndroidFlutterLocalNotificationsPlugin>()
      ?.createNotificationChannel(channel);

    //  Service 설정
  await service.configure(
      iosConfiguration: IosConfiguration(
        autoStart: false,   //  초기화 후 자동 실행 여부
        onForeground: onStart,  //  foreground에서 서비스가 시작됐을 때
        onBackground: onIosBackground,  //  background에서 서비스가 시작됐을 때, iOS만 따로 설정하는 듯 하다.
      ),
      androidConfiguration: AndroidConfiguration(
        onStart: onStart,
        isForegroundMode: true, //  알림이 전면에 뜨는 foreground 서비스인지?
        autoStart: false,
        initialNotificationTitle: '도토리', //  서비스 시작시 최초로 뜨는 알림 설정
        initialNotificationContent: '루트 기록 준비중...',
        notificationChannelId: androidNotificationChannelId,
        foregroundServiceNotificationId: notificationId,
      ));
}

@pragma('vm:entry-point')
FutureOr<bool> onIosBackground(ServiceInstance service) {
  WidgetsFlutterBinding.ensureInitialized();
  DartPluginRegistrant.ensureInitialized();
  return true;
}

@pragma('vm:entry-point')
Future<void> onStart(ServiceInstance service) async {
  DartPluginRegistrant.ensureInitialized();

  ...   //  추후 작성
}
~~~

앱 시작 시 초기화 해준다.

**main.dart**
~~~dart
Future<void> main() async {
  ...

  await initializeService();

  ...

  runApp(...);
}
~~~

이제 service의 생성과 초기화가 완료됐다. 초기화 할 때 autoStart속성을 false로 설정했기 때문에 자동으로 시작되지는 않고 대기중인 상태.

### service 실행

이제 하이킹을 시작하며 service를 시작한다.
~~~dart
Future<void> startPositionService() async {
    await FlutterBackgroundService().startService();

    FlutterBackgroundService().invoke('startSavingHikingPosition', arguments);
  }
~~~

위 코드는 Service를 시작한 후 `startSavingHikingPosition`라는 함수를 실행시킨 것이라고 보면 된다. `startService()`를 호출하면 실행되는 `onStart()`내에서 이러한 동작을 설정하면 된다.

**position_service.dart**
~~~dart
@pragma('vm:entry-point')
Future<void> onStart(ServiceInstance service) async {
  DartPluginRegistrant.ensureInitialized();

    //  서비스를 foreground service로 설정한다.
  if (service is AndroidServiceInstance) {
    service.setAsForegroundService();
  }

  StreamSubscription? positionSubscription;
  Timer? notiTimer;
  LocalDatabase? db;
  
    //  startSavingHikingPosition 호출 시 동작 설정
  service.on('startSavingHikingPosition').listen((event) {
    String hikingId = event?['hikingId'] as String;
    String courseId = event?['courseId'] as String;
    
    //  3초에 1번씩 알림을 열어준다. Android 14부터는 스와이프로 알림을 제거하는 것을 막을 수 없기 때문.
    FlutterLocalNotification.showNotification(
      CourseHikingIdModel(
        courseId: courseId,
        hikingId: hikingId,
      ),
    );
    notiTimer = Timer.periodic(const Duration(seconds: 3), (timer) {
      FlutterLocalNotification.showNotification(
        CourseHikingIdModel(
          courseId: courseId,
          hikingId: hikingId,
        ),
      );
    });

    g.Position? lastPosition;

    //  위치 정보 stream 구독 시작
    positionSubscription = g.Geolocator.getPositionStream(
      locationSettings: const g.LocationSettings(
        accuracy: g.LocationAccuracy.high,
        distanceFilter: positionDistanceFilter,
      ),
    ).listen((event) async {
      double distance = double.parse((lastPosition != null
              ? g.Geolocator.distanceBetween(
                  lastPosition!.latitude,
                  lastPosition!.longitude,
                  event.latitude,
                  event.longitude,
                )
              : 0)
          .toStringAsFixed(2));

        //  DB에 저장한다.
      await db?.createPosition(
        PositionsCompanion(
          hikingId: Value(hikingId),
          latitude: Value(event.latitude),
          longitude: Value(event.longitude),
          timestamp: Value(DateTime.now().microsecondsSinceEpoch),
          distance: Value(distance),
        ),
      );

      lastPosition = event;
    });
  });

    //  stopService 동작 역시 정의해 준다.
  service.on('stopService').listen((event) async {
    notiTimer?.cancel();
    await FlutterLocalNotification.cancelNotification();
    await db?.close();
    await positionSubscription?.cancel();
    await service.stopSelf();
  });

    //  db객체 생성. 
  db = await LocalDatabase.instance();
}
~~~

## 3. Notification

### flutter_local_notifications 패키지 추가 및 설정

알림은 `flutter_local_notifications`패키지를 사용할 것이다. 아래 커맨드를 통해 패키지를 추가한다.

`flutter pub add flutter_local_notifications`

이 패키지도 `flutter_background_service`와 마찬가지로 네이티브 설정이 필요하다. [공식문서](https://pub.dev/packages/flutter_local_notifications#-android-setup) 참고

### Notification 초기화

**notification.dart**
~~~dart
final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
    FlutterLocalNotificationsPlugin();

class FlutterLocalNotification {
  FlutterLocalNotification._();

  static init() async {
    //  알림 설정 for Android
    AndroidInitializationSettings android =
        const AndroidInitializationSettings("@mipmap/ic_launcher");

    //  알림 설정 for iOS
    DarwinInitializationSettings ios = const DarwinInitializationSettings(
      requestSoundPermission: false,
      requestBadgePermission: false,
      requestAlertPermission: false,
    );

    InitializationSettings settings =
        InitializationSettings(android: android, iOS: ios);

    await flutterLocalNotificationsPlugin.initialize(
      settings,
      //    앱이 열린 상태에서 알림을 통해 앱 진입한 경우의 실행 코드. 앱이 닫혀있을 때의 실행코드는 여기서 세팅하지 않는다.
      onDidReceiveNotificationResponse: onDidReceiveNotificationResponse,
    );
  }

  // Foreground 상태(앱이 열린 상태에서 받은 경우)
  static void onDidReceiveNotificationResponse(NotificationResponse notificationResponse) async {
    //  payload 가져옴
    final String payload = notificationResponse.payload ?? "";

    if (notificationResponse.payload != null ||
        notificationResponse.payload!.isNotEmpty) {
      //    pushLandingStreamController라는 stream에 payload를 추가한다.
      pushLandingStreamController.add(payload);
    }
  }

  // Background 상태(앱이 닫힌 상태에서 받은 경우)
  static void onBackgroundNotificationResponse() async {
    final NotificationAppLaunchDetails? notificationAppLaunchDetails =
        await flutterLocalNotificationsPlugin.getNotificationAppLaunchDetails();

    if (notificationAppLaunchDetails?.didNotificationLaunchApp ?? false) {
      String payload =
          notificationAppLaunchDetails!.notificationResponse?.payload ?? "";
      pushLandingStreamController.add(payload);
    }
  }

  ...
}
~~~

이렇게 작성한 초기화 코드는 `main.dart`에서 실행해준다.

**main.dart**
~~~dart
Future<void> main() async {
  ...

  FlutterLocalNotification.init();

  ...
}
~~~

### Notification 노출/제거

**notification.dart**
~~~dart
class FlutterLocalNotification {

  ...

    //  알림 노출
  static Future<void> showNotification(CourseHikingIdModel model) async {
    //  알림 설정 for Android
    const AndroidNotificationDetails androidNotificationDetails =
        AndroidNotificationDetails(
      androidNotificationChannelId,
      androidNotificationChannelName,
      importance: Importance.max,
      priority: Priority.max,
      showWhen: false,
      autoCancel: false,
      ongoing: true,
      silent: true,
    );

    //  알림 설정
    const NotificationDetails notificationDetails = NotificationDetails(
        android: androidNotificationDetails,
        iOS: DarwinNotificationDetails(badgeNumber: 1));

    //  알림 노출
    await flutterLocalNotificationsPlugin.show(
      notificationId,
      '도토리',
      '루트 기록하는 중...',
      notificationDetails,
      payload: jsonEncode(model),   //  이 payload는 onDidReceiveNotificationResponse()/onBackgroundNotificationResponse()에서 받는다.
    );
  }

    //  알림 취소
  static Future<void> cancelNotification() async {
    await flutterLocalNotificationsPlugin.cancel(notificationId);
  }
}
~~~

### Notification Landing 설정

Notification을 초기화 할 때 알림 클릭 시의 동작을 onDidReceiveNotificationResponse()/onBackgroundNotificationResponse()에서 정의해 줬다. 이 함수들은 알림으로 부터 전달받은 payload를 pushLandingStreamController를 통해 전달했다. 이제 이 pushLandingStreamController를 앱 내에서 구독시켜야 한다. 나는 main_screen.dart에서 구독하도록 했다.

**main_screen.dart**
~~~
//  stream controller를 초기화 시킴. notification에서 여기에 payload를 보낸다.
StreamController<String> pushLandingStreamController =
    StreamController.broadcast();

...


class _MainScreenState extends ConsumerState<MainScreen> {
  ...

  StreamSubscription<String>? pushLandingStreamSubscription;

  @override
  void initState() {
    ...

    //  pushLandingStreamController를 구독한다. 이벤트가 발생하면(알림을 탭하여 앱을 열면) 화면 stack 최상단이 hiking_screen이 아닌 경우에만 열어주도록 한다.
    pushLandingStreamSubscription =
        pushLandingStreamController.stream.listen((event) {
      if (event.isNotEmpty) {
        WidgetsBinding.instance.addPostFrameCallback((timeStamp) {
          if (getTopRouteName(context) != '/hiking_screen') {
            Navigator.pushNamed(
              context,
              '/hiking_screen',
              arguments: event,
            );
          }
        });
      }
    });

    //  이 함수 내에서 알림을 통해 앱을 연 경우에 pushLandingStreamController에 payload를 발행한다.
    FlutterLocalNotification.onBackgroundNotificationResponse();

    super.initState();
  }

  String? getTopRouteName(BuildContext context) {
    String? top;
    Navigator.popUntil(context, (route) {
      top = route.settings.name;
      return true;
    });
    return top;
  }

  ...

    //  screen 닫을 때 스트림 구독을 취소시킴.
  @override
  void dispose() {
    pushLandingStreamSubscription?.cancel();
    super.dispose();
  }
}
~~~

이렇게 flutter에서 foreground service를 실행시키고, 알림을 발생시키고, 앱으로 랜딩하는 방법 까지 정리하였다.