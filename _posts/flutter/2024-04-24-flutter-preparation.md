---
title: "Flutter 프로젝트 사전 작업"
categories:
    - Flutter
---
도토리 프로젝트를 본격적으로 시작하기 전의 사전작업들을 기록한다.

## 프로젝트 생성

안드로이드 스튜디오에서 New Flutter Project를 통해 앱을 생성한다.

android앱 내부의 **app수준 build.gradle**파일에 들어가면 `compileSdk`버전, `targetSdk`버전, `minSdk`버전이 아래처럼 설정 돼 있다.

~~~
android {
    ...
    compileSdkVersion flutter.compileSdkVersion
    ...

    defaultConfig {
        ...
        minSdkVersion flutter.minSdkVersion
        targetSdkVersion flutter.targetSdkVersion
        ...
    }
    
    ...
}
~~~

flutter의 설정을 따르도록 돼 있는데, 저 설정이 저장된 파일의 위치는 Flutter SDK내부이기 때문에 프로젝트 내부에서는 텍스트 검색을 통해 찾아볼 수가 없다.
Mac OS환경, Flutter 3.19.4버전 기준으로 해당 설정은 **~/Library/Flutter/packages/flutter_tools/gradle/src/main/groovy/flutter.groovy**파일에 저장돼 있다.

~~~
class FlutterExtension {
    /** Sets the compileSdkVersion used by default in Flutter app projects. */
    static int compileSdkVersion = 34

    /** Sets the minSdkVersion used by default in Flutter app projects. */
    static int minSdkVersion = 19

    /**
     * Sets the targetSdkVersion used by default in Flutter app projects.
     * targetSdkVersion should always be the latest available stable version.
     *
     * See https://developer.android.com/guide/topics/manifest/uses-sdk-element.
     */
    static int targetSdkVersion = 33
    
    ...
}
~~~

해당 설정이 Flutter 버전에 따라 바뀔 수 있기 때문에 직접 관리하기로 한다. **local.properties**파일에 아래처럼 작성한다.

~~~
flutter.flutterCompileSdkVersion=34
flutter.flutterTargetSdkVersion=34
flutter.flutterMinSdkVersion=23
~~~

다시 **app수준 build.gradle**파일로 돌아와 위 설정을 참조하도록 수정한다.

~~~
//  설정 3개를 불러옴
def flutterCompileSdkVersion = localProperties.getProperty('flutter.flutterCompileSdkVersion')
if (flutterCompileSdkVersion == null) {
    flutterCompileSdkVersio = '34'
}
def flutterTargetSdkVersion = localProperties.getProperty('flutter.flutterTargetSdkVersion')
if (flutterTargetSdkVersion == null) {
    flutterTargetSdkVersion = '34'
}
def flutterMinSdkVersion = localProperties.getProperty('flutter.flutterMinSdkVersion')
if (flutterMinSdkVersion == null) {
    flutterMinSdkVersion = '23'
}

//  불러온 설정값을 적용
android {
    ...
    compileSdk flutterCompileSdkVersion.toInteger()
    ...

    defaultConfig {
        ...
        minSdkVersion flutterMinSdkVersion.toInteger()
        targetSdkVersion flutterTargetSdkVersion.toInteger()
        ...
    }
    
    ...
}
~~~

## lint 설정

**pubspec.yaml**
~~~
dev_dependencies:
  ...
  flutter_lints: ^3.0.0 # 기본 설정되어 있는 lint셋
  build_runner: 2.4.6   # 플러터에서 코드 생성 기능을 제공해주는 플러그인
  riverpod_lint: #  riverpod에서 제공하는 lint셋
  custom_lint:  #   riverpod_lint가 사용하는 패키지
  ...
~~~

**analysis_options.yaml**
~~~
include: package:flutter_lints/flutter.yaml

analyzer:
  plugins:
    # dart run custom_lint 한번 실행해 줘야 riverpod_lint의 어시스트가 사용가능해 진다.
    - custom_lint 

custom_lint:
  enable_all_lint_rules: true

...

linter:
...
~~~

`dart run custom_lint`를 실행해서 riverpod_lint의 룰들을 생성한다. 

## riverpod 설정

상태관리로 Riverpod을 사용할 예정이기 때문에 **pubspec.yaml**에 디펜던시를 추가한다

**pubspec.yaml**
~~~
dependencies:
    ...
    flutter_riverpod: ^2.5.1
    ...
~~~

## Live Template 추가

Riverpod을 사용하는데 쓸만한 간단한 Live Template도 몇개 추가 해봄.

**ConsumerWidget 생성**
![alt text](<../../assets/images/스크린샷 2024-04-24 오후 6.34.51.png>)

**ref.read()**
![alt text](<../../assets/images/스크린샷 2024-04-24 오후 6.34.57.png>)

**ref.watch()**
![alt text](<../../assets/images/스크린샷 2024-04-24 오후 6.35.01.png>)

이렇게 해두면 아래처럼 사용 가능.

![alt text](<../../assets/images/스크린샷 2024-04-24 오후 6.37.01.png>)

## freezed 설정

[공식문서](https://pub.dev/packages/freezed)

freezed는 코드 제너레이터 패키지이다. 즉, 여러 용도의 코드를 자동생성해줌. 나는 데이터 모델 클래스를 생성하기 위해 사용하기로 한다.

**pubspec.yaml**
~~~
dev_dependencies:
  ...
  json_serializable:
  freezed:
~~~

보고 참고할 수 있게 아래처럼 예시를 하나 만들어 놓기로 한다.

**lib/model/schedule_model.dart**
~~~
import 'package:freezed_annotation/freezed_annotation.dart';

part 'schedule_model.freezed.dart';

part 'schedule_model.g.dart';

@freezed
class ScheduleModel with _$ScheduleModel {
  factory ScheduleModel({
    required String id,
    required String content,
    required DateTime date,
    required int startTime,
    required int endTime,
  }) = _ScheduleModel;

  factory ScheduleModel.fromJson(Map<String, dynamic> json) =>
      _$ScheduleModelFromJson(json);
}
~~~

위와 같이 작성 후 아래 명령어를 실행한다.

`flutter pub run build_runner build --delete-conflicting-outputs`

![alt text](<../../assets/images/스크린샷 2024-04-25 오전 1.15.12.png>)

`part` 구문에 써둔 2개의 파일이 생성된다. 이제 이 ScheduleModel이라는 model클래스는 equal, copyWith등등 유용한 기능을 갖게 되었다.

## Flavor(dev, prod) 설정

build flavor를 dev, prod로 구분하기 위해 [이 링크](https://sebastien-arbogast.com/2022/05/02/multi-environment-flutter-projects-with-flavors/#Getting_Started)를 참고했다.

### Flavor별 Main파일 생성

**lib**폴더에 **app_config.dart**파일을 만들고 아래 코드를 작성한다.

~~~
import 'package:flutter/material.dart';

enum Environment { dev, prod }

class AppConfig extends InheritedWidget {
  final Environment environment;
  final String appTitle;

  const AppConfig({
    Key? key,
    required Widget child,
    required this.environment,
    required this.appTitle,
  }) : super(
    key: key,
    child: child,
  );

  static AppConfig of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<AppConfig>()!;
  }

  @override
  bool updateShouldNotify(covariant InheritedWidget oldWidget) => false;
}
~~~

**main_dev.dart**와 **main_prod.dart**파일을 만들고, 아래처럼 작성한다. 각 flavor별 main파일이므로 기존의 main.dart파일은 제거한다.

**main_dev.dart**
~~~
import 'package:myapp/my_app.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app_config.dart';

void main() {
  const configuredApp = AppConfig(
    environment: Environment.dev,
    appTitle: '[DEV] MyApp',
    child: MyApp(),
  );
  runApp(const ProviderScope(child: configuredApp));
}
~~~

**main_prod.dart**
~~~
import 'package:myapp/my_app.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app_config.dart';

void main() {
  const configuredApp = AppConfig(
    environment: Environment.prod,
    appTitle: 'MyApp',
    child: MyApp(),
  );
  runApp(const ProviderScope(child: configuredApp));
}
~~~

### Flavor 생성

**Run/Debug Configurations**를 열어서 build flavor를 2개 생성한다.

![alt text](<../../assets/images/스크린샷 2024-04-27 오전 9.36.13.png>)

dev, prod 두가지를 생성해 주고, Dart entrypoint에 각 main파일을 지정한다.

### Android앱 설정

app 수준 **build.gradle**파일을 연다(android/app/build.gradle). 아래 설정 코드를 추가.

**app/build.gradle**
~~~
android{

  ...

  flavorDimensions "env"

    productFlavors {
        dev {
            dimension "env"
            applicationIdSuffix ".dev"
            resValue "string", "app_name", "[DEV] MyApp"
        }
        prod {
            dimension "env"
            resValue "string", "app_name", "MyApp"
        }
    }
}
~~~

AndroidManifest파일에서 앱 label도 수정한다.

**AndroidManifest.xml**
~~~
<application
        android:label="@string/app_name"
        ...
~~~

### iOS앱 설정

Xcode로 ios폴더를 연다.

Info탭의 Configurations를 아래처럼 수정한다.

![alt text](../../assets/images/image022.png)

상단 탭 바의 **Runner** 스킴을 클릭하고, **Manage Schemes**를 연다.

![alt text](<../../assets/images/스크린샷 2024-04-27 오전 10.16.38.png>)

2가지 Scheme을 생성한다.

![alt text](../../assets/images/image025.png)

생성 후 **dev**를 더블클릭해 편집창으로 들어가서, **Run**의 Build Configuration을 **Debug-dev**로 세팅한다.

![alt text](../../assets/images/image027.png)

같은 방식으로 아래처럼 세팅한다.

**dev**

Run : Debug-dev<br>
Test : Debug-dev<br>
Profile : Profile-dev<br>
Analyze : Debug-dev<br>
Archive : Release-dev<br>

**prod**

Run : Debug-prod<br>
Test : Debug-prod<br>
Profile : Profile-prod<br>
Analyze : Debug-prod<br>
Archive : Release-prod<br>

완료했으면 다음으로 Info.plist 진입. **Bundle display name**를 **${APP_NAME}**로 변경.

![alt text](../../assets/images/image032.png)

다시 프로젝트 네비게이터의 루트에 있는 Runner를 클릭한 후 TARGETS의 Runnder로 진입한다. 상단 탭의 Build Settings를 클릭. 바로 아래 탭의 +버튼을 눌러 **Add User-Defined Setting**으로 진입한다.

![alt text](<../../assets/images/스크린샷 2024-04-27 오전 10.31.31.png>)

APP_NAME을 각 빌드 설정별로 지정해 준다.

![alt text](../../assets/images/image034.png)

다음으로, 탭 바의 검색창을 통해 **bundle identifier**를 검색한다. 나타나는 **Product Bundle Identifier**를 세팅한다. dev설정일 때 value에 **.dev**를 붙여준다.

![alt text](../../assets/images/Capture-2022-05-02-16.57.52-2048x764.png)

이제 다시 안드로이드 스튜디오로 돌아와 앱을 실행하면 각 Run Configuration별로 Build flavor가 설정되어 실행된다.

## 파이어베이스 프로젝트 설정

firestore와 storage, auth를 사용할 예정이다. 단 파이어베이스 프로젝트를 2개 만들어서 dev, prod flavor에 하나씩 연결할 것이다. 일단 파이어베이스 콘솔에서 프로젝트를 2개 만든다.

다음으로 커맨드라인에서 firebase를 사용하기 위해 firebase CLI와 flutterfire CLI설치해야 한다.

안드로이드 스튜디오로 다시 돌아와 프로젝트 루트 위치에서 터미널을 열고 다음 커맨드를 순서대로 실행하여 필요한 툴을 설치한 후 앱과 파이어베이스 프로젝트를 연결한다. 프로젝트 2개를 연결해야 하기 때문에 2개를 실행한다.

1. firebase-tools 설치
~~~
npm install -g firebase-tools
~~~

2. firebase CLI 로그인
~~~
firebase login
~~~

3. flutterfire CLI 설치
~~~
dart pub global activate flutterfire_cli
~~~

4. 앱의 **pubspec.yaml**에 아래 추가
~~~
dependencies:
  ...
  firebase_core: 2.19.0
  cloud_firestore: 4.11.0
~~~

5. dev용 프로젝트에 앱 연결
~~~
flutterfire configure -p [dev용 프로젝트 이름] -i [iOS앱 번들 id] -a [Android앱 id] -o lib/firebase/dev/firebase_options.dart --no-apply-gradle-plugins
~~~
**example** :
~~~
flutterfire configure -p myapp-dev-3n4b2 -i com.myapp.myapp.dev -a com.myapp.myapp.dev -o lib/firebase/dev/firebase_options.dart --no-apply-gradle-plugins
~~~

6. prod용 프로젝트에 앱 연결
~~~
flutterfire configure -p [prod용 프로젝트 이름] -i [iOS앱 번들 id] -a [Android앱 id] -o lib/firebase/prod/firebase_options.dart --no-apply-gradle-plugins
~~~

**example** : 
~~~
flutterfire configure -p myapp-9s78f -i com.myapp.myapp -a com.myapp.myapp -o lib/firebase/prod/firebase_options.dart --no-apply-gradle-plugins
~~~

5와 6을 실행하면 아래처럼 각 flavor에서 사용할 `firebase_options.dart`파일들이 생성된다. Api key값들이 들어있으므로 git ignore에 추가하는 것이 권장된다.

![alt text](<../../assets/images/스크린샷 2024-04-27 오전 11.06.35.png>)

마지막으로 main파일의 main()함수에서 Firebase를 초기화 해 주면 사용 준비 완료.

**main_dev.dart**
~~~
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );

  const configuredApp = AppConfig(
    environment: Environment.dev,
    appTitle: '[DEV] MyApp',
    child: MyApp(),
  );
  runApp(const ProviderScope(child: configuredApp));
}
~~~

**main_prod.dart**파일도 마찬가지로 수정한다.

### 하나의 flavor에 여러개의 파이어베이스 프로젝트 설정하기

도토리 관리자 앱은 flavor구분 없이 한번에 두 dev, prod 프로젝트에 모두 접근해야 한다. 따라서 아래와 같이 구성한다.

**firebase_options_dev.dart** 파일 생성

`flutterfire configure -p myapp-dev-3n4b2 -i com.myapp.myapp -a com.myapp.myapp -o lib/firebase/firebase_options_dev.dart --no-apply-gradle-plugins`

**firebase_options_prod.dart** 파일 생성

`flutterfire configure -p myapp-3n4b2 -i com.myapp.myapp -a com.myapp.myapp -o lib/firebase/firebase_options_prod.dart --no-apply-gradle-plugins`

firebase_option파일이 두개 생성됐다. 초기화도 마찬가지로 두번 해 준다.

**main.dart**
~~~
Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  //  prod용 파이어베이스 프로젝트 초기화
  await Firebase.initializeApp(
    options: prod.DefaultFirebaseOptions.currentPlatform,
  );

  //  dev용 파이어베이스 프로젝트 초기화
  await Firebase.initializeApp(
    name: firebaseAppNameDev,
    options: dev.DefaultFirebaseOptions.currentPlatform,
  );

  ...
}
~~~

처음에는 두 프로젝트 모두 name을 지정했는데, 에러가 발생했다. 디폴트가 없어졌다고 패키지에서 판단하는 것 같다. 이제 아래처럼 사용하면 된다.

~~~
//  dev용 앱 객체 생성
final firebaseAppForDev = Firebase.app(firebaseAppNameDev);

final firestore = FirebaseFirestore.instance;
final firestoreForDev = FirebaseFirestore.instanceFor(app: firebaseAppForDev);
~~~


### iOS앱에 파이어베이스 설정

ios/Podfile을 연다. 최상단의 코멘트 처리 돼 있는 ios버전 설정을 코멘트 해제해 준다.

~~~
# Uncomment this line to define a global platform for your project
platform :ios, '12'
~~~

최소 platform버전을 지정하는 곳인 모양인데, Firebase와 Firestore에서 요구하는 버전을 지정한다. 나는 디폴트값인 12로 설정함.

또, 같은 파일 아래쪽에 다음 코드를 추가

~~~
target 'Runner' do
  ...

  pod 'FirebaseFirestore', :git => 'https://github.com/invertase/firestore-ios-sdk-frameworks.git', :tag => '10.15.0'
end
~~~

tag의 버전은 달라질 수 있는데, 오류 발생시 오류 메시지에 필요한 버전안내가 나온다.

### android앱에 파이어베이스 설정

`local.properties`를 열어 `minSdk`버전을 21이상으로 설정한다. (상단의 프로젝트 설정 부분을 먼저 완료 해야한다.)

**local.properties**
~~~
flutter.flutterMinSdkVersion=23
~~~

다음은 `app/build.gradle`파일을 열어 다음과 같이 multidex를 설정해준다.

**app/build.gradle**
~~~
android {
    ...

    defaultConfig {
        ...

        multiDexEnabled true
    }

    ...
}

...

dependencies {
    implementation 'com.android.support:multidex:1.0.3'
}
~~~

이제 파이어베이스 사용 설정이 완료됐다.

## Env 설정

우리 도토리 프로젝트는 네이버 지도 API를 사용한다. 앱 내에서 Client ID를 사용하기 위해, 그리고 암호화와 VCS에 등록하지 않기위해 env패키지를 사용해 환경변수화 시키기로 한다. `envied`패키지를 사용하기 위해 앱의 **pubspec.yaml**에 아래를 추가한다.

**pubspec.yaml**
~~~
...

dependencies:
  ...
  envied: ^0.5.4+1

dev_dependencies:
  ...
  envied_generator: ^0.5.4+1

...
~~~

프로젝트 루트 디렉토리에 아래 내용의 `.env`파일을 생성한다.

**.env**
![alt text](../../assets/images/image.png)

lib 디렉토리에 아래 파일을 생성한다.

**env.dart**
~~~
import 'package:envied/envied.dart';

part 'env.g.dart';

@Envied(path: '.env')
abstract class Env {
  @EnviedField(varName: 'NAVER_MAP_CLIENT_ID', obfuscate: true)
  static final String naverMapClientId = _Env.naverMapClientId;
}
~~~

`flutter pub run build_runner build --delete-conflicting-outputs` 커맨드를 실행해 `env.g.dart`파일이 생성되도록 한다.

이 파일에는 `.env`파일에 기재된 값이 암호화되어(obfuscate: true 옵션) 저장돼 있다. 이제 `.env`파일을 git ignore에 추가하면 Client ID 값을 외부에 노출하지 않을 수 있게 된다.

## ci/cd 설정

CI/CD는 `Github Actions`를 통해서 빌드 후 `Firebase App Distribution`을 통해 배포하도록 할 것이다. 일단 git에 연결해 준다. iOS는 개발자 계정이 아직 없어서 일단 Android만... 기본적인 빌드와 배포만 진행한다. 먼저 스크립트를 작성하기 위해 
`프로젝트 루트/.github/workflows`에 `main.yml`파일을 만들어 준다. 이 파일에 yml문법으로 스크립트를 작성하면 된다.

### 기본 설정
~~~
name: Flutter Github Actions

on: # 트리거 설정
  push: # release 브랜치에서 푸시했을 때 트리거
    branches:
      - release

jobs: # 실행 작업 설정
  build:
    # runner 실행 환경
    runs-on: ubuntu-latest

    steps:
      # Repository에서 코드 체크 아웃
      - name: Checkout
        uses: actions/checkout@v3
~~~

### Flutter Action 설치

Flutter Action은 Flutter 프로젝트를 Github Actions에서 사용할 수 있게 해주는 패키지이다.
~~~
    steps:
      ...

      # Flutter action 설치
      - name: Install Flutter action
        uses: subosito/flutter-action@v2.16.0
        with:
            channel: 'stable'
            flutter-version: '3.19.4'
~~~

### Flutter 초기화 및 테스트 코드 실행
~~~
    steps:
      ...

      # 디펜던시 패키지 설치
      - name: Import Flutter Package Dependancy
        run: flutter pub get

      # 테스트 실행(/test/), TODO 테스트 생성하면 추가할 것. 테스트 없이 명령 실행하면 오류 발생.
      - name: Test Flutter
        run: flutter test
~~~

### keystore파일 생성

Github Actions환경에는 keystore파일이 없기 때문에 keystore.jks파일의 내용을 Repository에 Actions Secret으로 올린 후 workflow에서 불러와 다시 파일로 재구성해야 한다.

일단 내가 업로드 키로 사용하고 있는 키를 `openssl base64 -in ./keystore.jks` 커맨드로 base64 인코딩한다. Github 프로젝트의 Settings -> Secrets and Variables -> Actions로 진입 해 인코딩 된 내용을 새 Secret으로 만든다. 이후 workflow에서 해당 내용을 아래처럼 `timheuer/base64-to-file`패키지를 이용해 디코딩 후 파일로 생성한다.
~~~
    steps:
      ...

      # 현 작업 위치 : /home/runner/work/프로젝트명/프로젝트명(프로젝트 루트 폴더)
      # 안드로이드 Keystore파일 생성 : /home/runner/work/프로젝트명/keystore/keystore.jks
      - name: Create Android keystore
        id: android_keystore
        uses: timheuer/base64-to-file@v1.2
        with:
          fileName: keystore.jks
          fileDir: '../keystore'
          # base64 디코딩한다.
          encodedString: ${{ secrets.KEYSTORE }}
~~~

### keystore.properties파일 생성, app build.gradle 설정

android의 앱 수준 build.gradle파일을 열어 빌드 할 때 keystore관련 값이 `keystore.properties` 파일을 따르도록 설정한다. 이후 이 파일을 또 workflow에서 만들면 된다. keystore.properties에 storePassword, keyAlias, keyPassword 값을 지정할 것이기 때문에 이 값들을 또 Actions Secret으로 등록한다. 이 것들은 base64인코딩 안해도 된다.

**app build.gradle**
~~~
android {
    ...

    signingConfigs {
        release {
            try {
                def keystorePropertiesFile = rootProject.file("keystore.properties")
                def keystoreProperties = new Properties()
                keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

                storeFile file(keystoreProperties['storeFilePath'])
                storePassword keystoreProperties['storePassword']
                keyAlias keystoreProperties['keyAlias']
                keyPassword keystoreProperties['keyPassword']
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
~~~

**main.yml**
~~~
    steps:
      ...

      # 안드로이드 keystore.properties파일 생성 : /home/runner/work/프로젝트명/프로젝트명/android/keystore.properties
      - name: Create keystore.properties
        run: |
            echo "storeFilePath=/home/runner/work/프로젝트명/keystore/keystore.jks" > android/keystore.properties
            echo "storePassword=${{ secrets.STOREPASSWORD }}" >> android/keystore.properties
            echo "keyPassword=${{ secrets.KEYPASSWORD }}" >> android/keystore.properties
            echo "keyAlias=${{ secrets.KEYALIAS }}" >> android/keystore.properties
~~~

### firebase_options.dart 파일 생성

firebase_options.dart파일의 내용을 업로드하지 않기 위해 git ignore처리 해 뒀다. 그래서 이 파일도 만들어 줘야 한다. 해당 내용을 keystore 파일과 같은 방법으로 base64인코딩 Actions Secret으로 업로드, 이후 workflow에서 다시 디코딩하여 파일로 만든다.
~~~
    steps:
      ...

      # 안드로이드 lib/firebase/prod/firebase_options.dart파일 생성 : /home/runner/work/프로젝트명/프로젝트명/lib/firebase/prod/firebase_options.dart
      - name: Create Firebase Options
        uses: timheuer/base64-to-file@v1.2
        with:
          fileName: firebase_options.dart
          fileDir: 'lib/firebase/prod'
          encodedString: ${{ secrets.FIREBASEOPTIONS }}
~~~

### aab or apk빌드

prod flavor로 빌드한다. main파일의 역할을 main_prod.dart파일이 한다고 명시해 줘야 한다.
~~~
    steps:
      ...

      # 안드로이드 aab 빌드 : build/app/outputs/bundle/prodRelease/app-prod-release.aab
      - name: Build Android aab
#        run: flutter build appbundle --flavor prod -t lib/main_prod.dart
        run: flutter build apk --flavor prod -t lib/main_prod.dart
~~~


### Firebase App Distribution으로 업로드

`wzieba/Firebase-Distribution-Github-Action` 패키지를 사용한다. Actions Secret으로 파이어베이스 App ID와 CREDENTIAL_FILE_CONTENT를 업로드 해야 하는데, [이 페이지](https://steveos.medium.com/github-action-and-firebase-app-distribution-ci-cd-ways-part-2-fcf9ba425c0)를 참고할 것. Firebase App Dist에 aab를 배포하려면 구글 플레이 콘솔의 앱과 Firebase 프로젝트를 연결해야 한다. 그러려면 플레이 콘솔에서 앱이 1회이상 심사를 통과한 상태로 내부, 알파, 베타, 프로덕션 트랙에 배포돼야 한다. apk파일은 그렇게 하지 않아도 배포 업로드 가능하다.
~~~
    steps:
      ...

      # aab파일 Firebase App Distribution으로 업로드
      - name: Upload artifact to Firebase App Distribution
        uses: wzieba/Firebase-Distribution-Github-Action@v1
        with:
          appId: ${{secrets.FIREBASE_APP_ID}}
          serviceCredentialsFileContent: ${{secrets.CREDENTIAL_FILE_CONTENT}}
          groups: 'tester'
#          file: ./build/app/outputs/bundle/prodRelease/app-prod-release.aab
          file: ./build/app/outputs/apk/prod/release/app-prod-release.apk
~~~

### 구글 스토어에 업로드

[이 블로그](https://medium.com/lodgify-technology-blog/deploy-your-flutter-app-to-google-play-with-github-actions-f13a11c4492e)를 참고했다.

~~~
    steps:
      ...

      # 안드로이드 google-play-service-account-key.json파일 생성 : /home/runner/work/Dotori/Dotori/google-play-service-account-key.json
      - name: Create GOOGLE_PLAY_SERVICE_ACCOUNT_KEY file
        uses: timheuer/base64-to-file@v1.2
        with:
          fileName: google-play-service-account-key.json
          fileDir: '.'
          # base64 디코딩한다.
          encodedString: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT_KEY }}

      # Upload generated aab to project artifacts
      - name: Upload generated aab to the artifacts
        uses: actions/upload-artifact@master
        with:
          name: aab-stores
          path: build/app/outputs/bundle/prodRelease/app-prod-release.aab

      # Deploy bundle to Google Play internal testing
      - name: Deploy to Play Store (Internal testing)
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJson: google-play-service-account-key.json
          packageName: com.beforejuly7th.dotori
          releaseFiles: build/app/outputs/bundle/prodRelease/app-prod-release.aab
          track: internal
~~~

이 과정에서 **google-play-service-account-key.json**파일을 만들게 된다. 해당 앱을 플레이 콘솔에 업로드 할 수 있게 허가받은 서비스 계정의 키라고 보면 되는데, 이를 만들려면 다음 과정을 따라야 한다.

1. Google Cloud Platform에서 프로젝트 생성 후 [IAM 및 계정] > [서비스 계정] 진입, 서비스 계정을 하나 생성.
2. 해당 서비스 계정의 상세페이지 > 키 탭에서 키를 생성한다. 이 때 JSON으로 생성하여 파일을 내려받는다.
3. 구글 플레이 콘솔로 진입하여 [사용자 및 권한] 탭으로 진입, 신규 사용자 초대를 눌러 1번 단계에서 만든 서비스 계정의 주소를 입력한다. 또한 아래 권한 섹션에서 앱과 권한을 선택해 주는데, 앱은 업로드 자동화 대상이고, 권한은 앱을 **테스트 트랙으로 출시**를 골라주면 된다.
4. github actions의 secret으로 2번에서 생성받은 output파일을 base64인코딩하여 등록해 준다. 위 예제에서 사용하려면 **GOOGLE_PLAY_SERVICE_ACCOUNT_KEY**를 제목으로 해야 한다.

### Slack으로 결과 발송
이건 어떻게 하는지 까먹어서 설명 생략.
~~~
      # 결과 Slack으로 노티
      - name: action-slack
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          author_name: www-be
          fields: repo,message,commit,author,action,eventName,ref,workflow,job,took
          if_mention: failure,cancelled
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
        if: always()
~~~