---
title: "Flutter Riverpod 기초 정리"
categories:
    - Flutter
---
이 포스트는 아래 블로그를 참고한 포스트입니다.

[참고문서1](https://codewithandrea.com/articles/flutter-state-management-riverpod/), [참고문서2](https://codewithandrea.com/articles/flutter-riverpod-async-notifier/)

> TODO 대충 리버팟이 나온 배경, 리버팟의 개략적인 설명

## ProviderScope
`ProviderScope`는 우리가 생성하는 모든 Provider들의 state를 보관하는 위젯이다. 내부적으로 `ProviderContainer`를 생성하게 돼 있다. 최상단 앱을 래핑하며 시작한다.
~~~
void main() {
  runApp(ProviderScope(
    child: MyApp(),
  ));
}
~~~

## Provider
[Provider](https://riverpod.dev/docs/concepts/providers)란 state를 캡슐화 하고, 외부에서 리스닝 할수있도록 만들어진 객체라고 한다. Riverpod의 핵심 개념이다.

- singleton, service locators, dependency injection, InheritedWidgets등의 개념을 대체한다.
- 여러위치에서 쉽게 state를 저장하고, 조회 및 수정할 수 있도록 해준다.
- rebuild될 Widget을 필터링하거나, 비용이 큰 계산이 필요한 state를 캐싱할 수 있도록 하여 성능을 개선할 수 있도록 해준다.
- 테스트 중 각 Provider의 동작의 overriding을 제공하여 더 테스트 가능한 코드를 작성하게 해준다.

## Creating and Reading a Provider
~~~
final helloWorldProvider = Provider<String>((ref) {
  return 'Hello world';
});
~~~
위 코드는 'Hello world'를 출력하는 기본 Provider이다. 3부분으로 구성되어 있다.

1. final helloWorldProvider : Provider의 state를 읽기위한 전역 변수이다.
2. Provider<String> : Provider의 종류와, state의 데이터타입을 나타낸다.
3. state를 생성하는 함수 : 다른 Provider에 접근하거나 dispose로직 등을 실행할 수 있도록 `ref` 변수가 주어진다.

Widget의 build() 함수에는 BuildContext가 파라미터로 주어지지만, Riverpod의 Provider는 Widget tree의 밖에 있기 때문에, BuildContext는 필요가 없다. 대신 ref 객체를 사용해야 한다.

### ref객체를 사용하는 방법

#### 1. ConsumerWidget 상속

~~~
final helloWorldProvider = Provider<String>((_) => 'Hello world');

// 1. widget class now extends [ConsumerWidget]
class HelloWorldWidget extends ConsumerWidget {
  @override
  // 2. build method has an extra [WidgetRef] argument
  Widget build(BuildContext context, WidgetRef ref) {
    // 3. use ref.watch() to get the value of the provider
    final helloWorld = ref.watch(helloWorldProvider);
    return Text(helloWorld);
  }
}
~~~

StatelessWidget 대신 ConsumerWidget을 상속한다. build()메서드에 WidgetRef타입의 ref객체를 파라미터로 추가로 받는다. 가장 간단하고 대부분의 케이스에서 사용하는 방법.

#### 2. Consumer 사용

~~~
final helloWorldProvider = Provider<String>((_) => 'Hello world');

class HelloWorldWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // 1. Add a Consumer
    return Consumer(
      // 2. specify the builder and obtain a WidgetRef
      builder: (_, WidgetRef ref, __) {
        // 3. use ref.watch() to get the value of the provider
        final helloWorld = ref.watch(helloWorldProvider);
        return Text(helloWorld);
      },
    );
  }
}
~~~

Text 위젯을 Consumer로 감쌌다. 이 케이스에서 Consumer의 builder arguments의 하나로 ref를 참조하여 Provider를 watch()함으로써 state를 확인할 수 있다. state 변경시 build()메서드에서 반환하는 Widget subtree상의 특정 Widget만 rebuild하도록 하고 싶을때 사용한다.

#### 3. ConsumerStatefulWidget & ConsumerState 사용

StatelessWidget을 ConsumerWidget으로 변경했던것 처럼 StetefulWidget은 ConsumerStatefulWidget으로, State<T> ConsumerState<T>로 변경한다.

~~~
final helloWorldProvider = Provider<String>((_) => 'Hello world');

// 1. extend [ConsumerStatefulWidget]
class HelloWorldWidget extends ConsumerStatefulWidget {
  @override
  ConsumerState<HelloWorldWidget> createState() => _HelloWorldWidgetState();
}

// 2. extend [ConsumerState]
class _HelloWorldWidgetState extends ConsumerState<HelloWorldWidget> {
  @override
  void initState() {
    super.initState();
    // 3. if needed, we can read the provider inside initState
    final helloWorld = ref.read(helloWorldProvider);
    print(helloWorld); // "Hello world"
  }

  @override
  Widget build(BuildContext context) {
    // 4. use ref.watch() to get the value of the provider
    final helloWorld = ref.watch(helloWorldProvider);
    return Text(helloWorld);
  }
}
~~~

이 방식에서 ref는 프로퍼티화 되어 Widget의 전 생명주기에서 접근할 수 있다.

## WidgetRef란?

[WidgetRef](https://pub.dev/documentation/flutter_riverpod/latest/flutter_riverpod/WidgetRef-class.html)는 Widget과 Provider의 연결고리이다. BuildContext를 통해 Widget tree에 접근하는 것과 비슷하다. WidgetRef를 통해 Widget은 코드 전 지역에 작성된 모든 Provider에 접근할 수 있다. Riverpod의 Provider는 글로벌 객체로 설계돼 있기 때문. 많은 Widget들에 분산된 state와 로직을 Provider로 이동시키면 유지보수에 도움이 될 것이다.

## Provider의 종류 8가지

### 1. Provider

[공식문서](https://riverpod.dev/docs/providers/provider)

~~~
// declare the provider
final dateFormatterProvider = Provider<DateFormat>((ref) {
  return DateFormat.MMMEd();
});

class SomeWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // retrieve the formatter
    final formatter = ref.watch(dateFormatterProvider);
    // use it
    return Text(formatter.format(DateTime.now()));
  }
}
~~~

`Provider`는 변하는 state가 없는 Repository, Logger 등의 객체에 접근할 때 간편하게 쓰기 좋다.

### 2. StateProvider

[공식문서](https://riverpod.dev/docs/providers/state_provider)

~~~
final counterStateProvider = StateProvider<int>((ref) {
  return 0;
});

class CounterWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // 1. watch the provider and rebuild when the value changes
    final counter = ref.watch(counterStateProvider);
    return ElevatedButton(
      // 2. use the value
      child: Text('Value: $counter'),
      // 3. change the state inside a button callback
      onPressed: () => ref.read(counterStateProvider.notifier).state++,
    );
  }
}
~~~

`StateProvider`는 UI에서 Provider에 직접 접근하여 값을 수정할 수 있다. 위 예제처럼 build()에서 watch를 통해 state를 읽어 Widget을 rebuild하고, ref.read()를 통해 state를 업데이트 한다.

간단한 가변객체(enums, strings, booleans, numbers 등)를 저장할 때 사용할 수 있으며 UI코드 내에서 state를 업데이트 하려고 할 때 사용할 수 있다. state의 validation등 추가 로직이 필요하다거나, 데이터 타입이 커스텀 클래스 등 복잡한 타입이거나, count++이상으로 state 업데이트 로직이 복잡한 경우에는 `NotifierProvider`를 대신 사용하라고 공식문서에서 권장한다.

### 3. StateNotifierProvider

[공식문서](https://riverpod.dev/docs/providers/state_notifier_provider)

`StateNotifier`를 노출하기 위해 사용한다. StateNotifier는 state를 홀딩하며 변경사항이 생기면 리스너에게 notify하는 클래스이다. Widget에서 StateNotifierProvider를 watch하여 이 변경사항을 전달받는다.

**StateNotifier**
~~~
import 'dart:async';

class Clock extends StateNotifier<DateTime> {
  // 1. initialize with current time
  Clock() : super(DateTime.now()) {
    // 2. create a timer that fires every second
    _timer = Timer.periodic(Duration(seconds: 1), (_) {
      // 3. update the state with the current time
      state = DateTime.now();
    });
  }

  late final Timer _timer;

  // 4. cancel the timer when finished
  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }
}
~~~

**Provider and Widget**
~~~
// Note: StateNotifierProvider has *two* type annotations
final clockProvider = StateNotifierProvider<Clock, DateTime>((ref) {
  return Clock();
});

import 'package:intl/intl.dart';

class ClockWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // watch the StateNotifierProvider to return a DateTime (the state)
    final currentTime = ref.watch(clockProvider);
    // format the time as `hh:mm:ss`
    final timeFormatted = DateFormat.Hms().format(currentTime);
    return Text(timeFormatted);
  }
}
~~~

StateNotifierProvider의 제네릭 타입은 StateNotifier의 타입과 state의 타입, 총 2개다. Widget에서 watch를 호출하면 state를 반환받고, `read(clockProvider.notifier)`를 호출하면 StateNotifier, 여기서는 Clock을 반환받는다.

 주로 특정한 이벤트(유저 상호작용 등)에 따라 UI 코드 내에서 상태를 변경하거나, state 갱신 로직을 StateNotifier 내부로 정리하고싶을 때 사용할 수 있지만 공식문서상 NotifierProvider를 대신 사용하라고 권장된다.

### 4. FutureProvider

[공식문서](https://riverpod.dev/docs/providers/future_provider)

Future를 반환하는 API 통신등의 결과를 받기위해 사용할 수 있다.

**Provider**
~~~
final weatherFutureProvider = FutureProvider.autoDispose<Weather>((ref) {
  // get repository from the provider below
  final weatherRepository = ref.watch(weatherRepositoryProvider);
  // call method that returns a Future<Weather>
  return weatherRepository.getWeather(city: 'London');
});

// example weather repository provider
final weatherRepositoryProvider = Provider<WeatherRepository>((ref) {
  return WeatherRepository(); // declared elsewhere
});
~~~

**Widget**
~~~
Widget build(BuildContext context, WidgetRef ref) {
  // watch the FutureProvider and get an AsyncValue<Weather>
  final weatherAsync = ref.watch(weatherFutureProvider);
  // use pattern matching to map the state to the UI
  return weatherAsync.when(
    loading: () => const CircularProgressIndicator(),
    error: (err, stack) => Text('Error: $err'),
    data: (weather) => Text(weather.toString()),
  );
}
~~~

실제 weather API의 호출은 build함수 실행시에 이루어진다. 일반적으로 `autoDispose` modifier와 함께 사용된다. Future Provider는 watch하면 AsyncValue를 반환하는데, 이는 data, loading, error의 패턴을 사용할 수 있도록 Riverpod에서 제공하는 유틸리티 클래스이다.

API 통신 등의 비동기 작업, loading과 error처리가 필요한 작업, 여러 비동기 작업을 하나로 묶을 때, 새로고침 작업 등에 사용할 수 있다.

### 5. StreamProvider

[공식문서](https://riverpod.dev/docs/providers/stream_provider)

**Provider**
~~~
final authStateChangesProvider = StreamProvider.autoDispose<User?>((ref) {
  // get FirebaseAuth from the provider below
  final firebaseAuth = ref.watch(firebaseAuthProvider);
  // call a method that returns a Stream<User?>
  return firebaseAuth.authStateChanges();
});

// provider to access the FirebaseAuth instance
final firebaseAuthProvider = Provider<FirebaseAuth>((ref) {
  return FirebaseAuth.instance;
});
~~~

**Widget**
~~~
Widget build(BuildContext context, WidgetRef ref) {
  // watch the StreamProvider and get an AsyncValue<User?>
  final authStateAsync = ref.watch(authStateChangesProvider);
  // use pattern matching to map the state to the UI
  return authStateAsync.when(
    data: (user) => user != null ? HomePage() : SignInPage(),
    loading: () => const CircularProgressIndicator(),
    error: (err, stack) => Text('Error: $err'),
  );
}
~~~

실시간 API등 stream형태로 들어오는 데이터를 UI에 적용시킬 때 사용된다. StreamBuilder와 함께 요긴하게 사용할 수 있다.

### 6. ChangeNotifierProvider

[공식문서](https://riverpod.dev/docs/providers/change_notifier_provider)

**Provider**
~~~
class AuthController extends ChangeNotifier {
  // mutable state
  User? user;
  // computed state
  bool get isSignedIn => user != null;

  Future<void> signOut() {
    // update state
    user = null;
    // and notify any listeners
    notifyListeners();
  }
}

final authControllerProvider = ChangeNotifierProvider<AuthController>((ref) {
  return AuthController();
});
~~~

**Widget**
~~~
Widget build(BuildContext context, WidgetRef ref) {
  return ElevatedButton(
    onPressed: () => ref.read(authControllerProvider).signOut(),
    child: const Text('Logout'),
  );
}
~~~

ChangeNotifier는 Flutter SDK의 일부이다. state를 관리하고, 바뀌면 리스너들에게 알리는 기능이 있는 클래스이다. StateNotifier와 유사하지만 state가 mutable이라는 문제점이 있어 StateNotifier사용이 권장되는 바이다. 또한 StateNotifierProvider처럼 NotifierProvider로 대체할 것이 권장된다.

### 7. (Async)NotifierProvider

[공식문서](https://riverpod.dev/docs/providers/notifier_provider)

`Notifier`란 StateNotifier와 ChangeNotifier처럼 state를 초기화 하고, 홀딩하고, 관리하는 클래스이다. 메서드를 노출시켜 외부에서, 특히 UI에서 state 수정을 가능케 할 수 있다. 초기화가 동기적이면 Notifier, 비동기적이면 AsyncNotifier를 사용한다. StateNotifier와 ChangeNotifier가 Flutter SDK의 일부인데 반해 Notifier는 Riverpod과 호환되도록 Riverpod에서 제공하는 API다. 그래서 Riverpod 공식문서에서 StateNotifierProvider, ChangeNotifierProvider대신 (Async)NotifierProvider를 사용하도록 권장하고 있다.

클래스 내부 전역에서 WidgetRef객체에 접근할 수 있어 생성자에서 다른 Provider를 주입받을 필요가 없다. StateNotifier에서 초기값을 설정하기 위해 super 생성자의 인자로 값을 주입하는 것과 달리, Notifier는 build() 메서드에서 return하여 초기값을 설정한다. build()에서 값을 초기화 하는 과정이 동기적이면 Notifier, 비동기적이면 AsyncNotifier클래스를 사용한다. 이 때 AsyncNotifier는 Future<T> 타입을 반환한다. (Async)NotifierProvider는 StateNotifierProvider처럼 제네릭 인자 2개로 선언한다.

**Notifier**
~~~
@riverpod
class Todos extends _$Todos {
  @override
  List<Todo> build() {
    return [];
  }

  // Let's allow the UI to add todos.
  void addTodo(Todo todo) {
    // Since our state is immutable, we are not allowed to do `state.add(todo)`.
    // Instead, we should create a new list of todos which contains the previous
    // items and the new one.
    // Using Dart's spread operator here is helpful!
    state = [...state, todo];
    // No need to call "notifyListeners" or anything similar. Calling "state ="
    // will automatically rebuild the UI when necessary.
  }

  // Let's allow removing todos
  void removeTodo(String todoId) {
    // Again, our state is immutable. So we're making a new list instead of
    // changing the existing list.
    state = [
      for (final todo in state)
        if (todo.id != todoId) todo,
    ];
  }

  // Let's mark a todo as completed
  void toggle(String todoId) {
    state = [
      for (final todo in state)
        // we're marking only the matching todo as completed
        if (todo.id == todoId)
          // Once more, since our state is immutable, we need to make a copy
          // of the todo. We're using our `copyWith` method implemented before
          // to help with that.
          todo.copyWith(completed: !todo.completed)
        else
          // other todos are not modified
          todo,
    ];
  }
}

final todosProvider = NotifierProvider<Todos, List<Todo>>(
  () {
    return Todos();
  },
);
~~~

**AsyncNotifier**
~~~
// This will generates a AsyncNotifier and AsyncNotifierProvider.
// The AsyncNotifier class that will be passed to our AsyncNotifierProvider.
// This class should not expose state outside of its "state" property, which means
// no public getters/properties!
// The public methods on this class will be what allow the UI to modify the state.
// Finally, we are using asyncTodosProvider(AsyncNotifierProvider) to allow the UI to
// interact with our Todos class.
@riverpod
class AsyncTodos extends _$AsyncTodos {
  Future<List<Todo>> _fetchTodo() async {
    final json = await http.get('api/todos');
    final todos = jsonDecode(json) as List<Map<String, dynamic>>;
    return todos.map(Todo.fromJson).toList();
  }

  @override
  FutureOr<List<Todo>> build() async {
    // Load initial todo list from the remote repository
    return _fetchTodo();
  }

  Future<void> addTodo(Todo todo) async {
    // Set the state to loading
    state = const AsyncValue.loading();
    // Add the new todo and reload the todo list from the remote repository
    state = await AsyncValue.guard(() async {
      await http.post('api/todos', todo.toJson());
      return _fetchTodo();
    });
  }

  // Let's allow removing todos
  Future<void> removeTodo(String todoId) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      await http.delete('api/todos/$todoId');
      return _fetchTodo();
    });
  }

  // Let's mark a todo as completed
  Future<void> toggle(String todoId) async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() async {
      await http.patch(
        'api/todos/$todoId',
        <String, dynamic>{'completed': true},
      );
      return _fetchTodo();
    });
  }
}

final todosProvider = AsyncNotifierProvider<AsyncTodos, List<Todo>>(
  () {
    return AsyncTodos();
  },
);
~~~

## ref.watch vs ref.read

- ref.watch(provider) : Widget의 build() 메서드 내에서 provider의 state를 구독하여 변경사항이 발생했을 경우 Widget을 rebuild하고 싶을 때 사용한다.
- ref.read(provider) : 딱 한번 state를 읽고싶을때 사용한다. lifecycle 메서드에서 유용.

- Widget의 build()메서드 내에서 상태를 수정할 수 있도록 고안된 StateProvider, StateNotifierProvider는 ref.read(provider.notifier) 구문을 통해 provider에 접근할 수 있다. 이 구문을 통해 StateProvider는 StateController, StateNotifierProvider는 StateNotifier객체를 반환하여 값을 수정하거나 특정 메서드를 실행시킬 수 있도록 한다.

~~~
final counterStateProvider = StateProvider<int>((_) => 0);

class CounterWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // 1. watch the provider and rebuild when the value changes
    final counter = ref.watch(counterStateProvider);
    return ElevatedButton(
      // 2. use the value
      child: Text('Value: $counter'),
      // 3. change the state inside a button callback
      onPressed: () => ref.read(counterStateProvider.notifier).state++,
    );
  }
}
~~~

#### ref.listen

listen()은 콜백을 제공하여 Widget의 build() 내에서 특정 provider의 state 변경시 Snackbar, Dialog 노출 등의 비동기적인 작업을 할 수 있도록 해준다. 이 콜백은 rebuild시 호출되지 않는다.

~~~
final counterStateProvider = StateProvider<int>((_) => 0);

class CounterWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // if we use a StateProvider<T>, the type of the previous and current 
    // values is StateController<T>
    ref.listen<StateController<int>>(counterStateProvider.state, (previous, current) {
      // note: this callback executes when the provider value changes,
      // not when the build method is called
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Value is ${current.state}')),
      );
    });
    // watch the provider and rebuild when the value changes
    final counter = ref.watch(counterStateProvider);
    return ElevatedButton(
      // use the value
      child: Text('Value: $counter'),
      // change the state inside a button callback
      onPressed: () => ref.read(counterStateProvider.notifier).state++,
    );
  }
}
~~~

## 추가적인 Riverpod 기능들

#### autoDispose modifier

FutureProvider나 StreamProvider를 사용할 때, 우리는 이 provider가 더 이상 어떤 옵저버도 갖지 않게 되면 dispose()하려고 한다. `autoDispose()` modifier를 통해 이를 수행할 수 있다. 내부적으로 Riverpod은 ref.watch()나 ref.listen()을 통해 provider를 구독중인 리스너들을 모두 추적 관리한다. autoDispose()를 사용하면 모든 리스너가 제거될 때(Widget tree에서 unmount될 때) provider가 dispose되는 것이다.

~~~
final authStateChangesProvider = StreamProvider.autoDispose<User?>((ref) {
  // get FirebaseAuth from another provider
  final firebaseAuth = ref.watch(firebaseAuthProvider);
  // call method that returns a Stream<User?>
  return firebaseAuth.authStateChanges();
});
~~~

이런 식으로 사용자가 화면을 닫으면 자동으로 닫히는 Stream Connection을 만들 수 있다.

또한 FutureProvider에서, 예를들어 HTTP request를 래핑하는데, 응답이 오기전에 사용자가 스크린을 닫아버린 경우 ref.onDispose()함수를 통해 취소 로직을 실행할 수 있다. 아래 예시는 dio 라이브러리를 통해 HTTP 통신을 실행하는 경우의 취소 로직이다.

~~~
final movieProvider = FutureProvider.autoDispose<TMDBMovieBasic>((ref) async {
  // get the repository
  final moviesRepo = ref.watch(fetchMoviesRepositoryProvider);
  // an object from package:dio that allows cancelling http requests
  final cancelToken = CancelToken();
  // when the provider is destroyed, cancel the http request
  ref.onDispose(() => cancelToken.cancel());
  // call method that returns a Future<TMDBMovieBasic>
  return moviesRepo.movie(movieId: 550, cancelToken: cancelToken);
});
~~~

movieProvider를 구독하는 리스너인 스크린이 닫혀 Widget tree에서 제거되면, 이 Provider는 자동으로 dispose되고, 이 때 onDispose()가 호출된다.

##### 캐싱하기

FutureProvider의 수행 결과값을 캐싱하여 같은 요청을 반복하지 않도록 설정할 수 있다.

~~~
final movieProvider = FutureProvider.autoDispose<TMDBMovieBasic>((ref) async {
  // get the repository
  final moviesRepo = ref.watch(fetchMoviesRepositoryProvider);
  // an object from package:dio that allows cancelling http requests
  final cancelToken = CancelToken();
  // when the provider is destroyed, cancel the http request
  ref.onDispose(() => cancelToken.cancel());
  // if the request is successful, keep the response
  ref.keepAlive();
  // call method that returns a Future<TMDBMovieBasic>
  return moviesRepo.movie(movieId: 550, cancelToken: cancelToken);
});
~~~

keepAlive()는 provider가 값을 영구적으로 저장하도록 하며 이 값은 `refresh()`나 `invalidate()`가 호출되어야 바뀔 수 있다.

또한 keepAliveLink()를 사용하여 캐싱된 값을 폐기할 수 있는데, 이를 이용해 timeout-based caching strategy를 구현할 수 있다. 아래는 재사용 가능하도록 AutoDisposeRef(autoDispose() 실행시 받을 수 있는 ref객체)를 extension 함수를 통해 확장하여 캐싱을 구현한 클래스이다. 

~~~
extension AutoDisposeRefCache on AutoDisposeRef {
  // keeps the provider alive for [duration] since when it was first created
  // (even if all the listeners are removed before then)
  void cacheFor(Duration duration) {
    final link = keepAlive();
    final timer = Timer(duration, () => link.close());
    onDispose(() => timer.cancel());
  }
}

final myProvider = Provider.autoDispose<int>((ref) {
  // use like this:
  ref.cacheFor(const Duration(minutes: 5));
  return 42;
});
~~~

#### family modifier

provider에 argument를 전달하기 위해 사용한다.

~~~
final movieProvider = FutureProvider.autoDispose
    // additional movieId argument of type int
    .family<TMDBMovieBasic, int>((ref, movieId) async {
  // get the repository
  final moviesRepo = ref.watch(fetchMoviesRepositoryProvider);
  // call method that returns a Future<TMDBMovieBasic>, passing the movieId as an argument
  return moviesRepo.movie(movieId: movieId, cancelToken: cancelToken);
});
~~~

~~~
final movieAsync = ref.watch(movieProvider(550));
~~~

family modifier를 통해 2개 이상의 파라미터를 전달하는 것은 Riverpod에서 지원되지 않는다. 필요시 커스텀 클래스를 만들어 전달해야 함.

## Dependency Overrides

즉시 초기화 되지않는 state를 저장하고 싶은 Provider를 사용할 때는, 일단 UnimplementedError를 발생시키도록 선언한 후 나중에 다른 값으로 override할 수 있다.

예를 들어, 아래와 같은 Future형태의 state 초기화 로직이 있을 때

~~~
final sharedPreferences = await SharedPreferences.getInstance();
~~~

~~~
final sharedPreferencesProvider = Provider<SharedPreferences>((ref) {
  return SharedPreferences.getInstance();
  // The return type Future<SharedPreferences> isn't a 'SharedPreferences',
  // as required by the closure's context.
});
~~~

Provider의 return을 이렇게 설정하면 `SharedPreferences`가 아닌 `Future<SharedPreferences>`객체가 반환된다. 하지만 이 provider는 SharedPreferences타입을 홀딩하게 되어 있다.

이 때, 우선은 아래와 같이 UnimplementedError를 발생시킨 후

~~~
final sharedPreferencesProvider = Provider<SharedPreferences>((ref) {
  throw UnimplementedError();
});
~~~
~~~
// asynchronous initialization can be performed in the main method
Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final sharedPreferences = await SharedPreferences.getInstance();
  runApp(ProviderScope(
    overrides: [
      // override the previous value with the new object
      sharedPreferencesProvider.overrideWithValue(sharedPreferences),
    ],
    child: MyApp(),
  ));
}
~~~

이렇게 ProviderScope Widget 내부에서 `overrideWithValue()`메서드를 통해 override하도록 설정해 주면, Future 기반의 API를 사용하지 않아도 된다.

## Combining Provider

provider는 다른 provider에 의존할 수 있다. 예를 들어 아래와 같이 SettingsRepository 클래스가 SharedPreferences 클래스의 인스턴스를 생성자에서 필요로 할 때

~~~
final settingsRepositoryProvider = Provider<SettingsRepository>((ref) {
  // watch another provider to obtain a dependency
  final sharedPreferences = ref.watch(sharedPreferencesProvider);
  // pass it as an argument to the object we need to return
  return SettingsRepository(sharedPreferences);
});
~~~

이렇게 provider 블록에서 ref를 통해 sharedPreferencesProvider에 접근할 수 있다. 이 때 SettingsRepository 변경시 SettingsRepository도 업데이트 시키기 위해 watch()를 사용한다. 그러면 SettingsRepository에 의존하는 Widget들도 rebuild될 것이다.

#### Argument로 ref전달

또 다른 방법으로, SettingsRepository생성시 ref를 전달할 수도 있다.

~~~
class SettingsRepository {
  const SettingsRepository(this.ref);
  final Ref ref;

  // synchronous read
  bool onboardingComplete() {
    final sharedPreferences = ref.read(sharedPreferencesProvider);
    return sharedPreferences.getBool('onboardingComplete') ?? false;
  }

  // asynchronous write
  Future<void> setOnboardingComplete(bool complete) {
    final sharedPreferences = ref.read(sharedPreferencesProvider);
    return sharedPreferences.setBool('onboardingComplete', complete);
  }
}
~~~

이 방법으로 sharedPreferencesProvider를 암시적으로 의존하게 되며 ref.read() 메서드를 통해 접근하게 된다. SettingsRepositoryProvider는 아래와 같이 간단해진다.

~~~
final settingsRepositoryProvider = Provider<SettingsRepository>((ref) {
  return SettingsRepository(ref);
});
~~~

## Provider의 Scoping

provider를 scoping함으로써 코드의 일부 지역에서는 다르게 동작하도록 할 수 있다.

예를 들어 아래와 같이 ListView가 있다고 하자.

~~~
class ProductList extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemBuilder: (_, index) => ProductItem(index: index),
    );
  }
}
~~~

이 코드에서 itemBuilder의 index를 ProductItem 생성자의 인자로 주입한다. 이 코드는 작동하지만 ListView가 rebuild되면 모든 ProductItem들이 다같이 rebuild 되는 상황이 발생한다. 이 상황에서 `ProviderScope`를 중첩시켜 provider의 값을 override할 수 있다.

~~~
// 1. Declare a Provider
final currentProductIndex = Provider<int>((_) => throw UnimplementedError());

class ProductList extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.builder(itemBuilder: (context, index) {
      // 2. Add a parent ProviderScope
      return ProviderScope(
        overrides: [
          // 3. Add a dependency override on the index
          currentProductIndex.overrideWithValue(index),
        ],
        // 4. return a **const** ProductItem with no constructor arguments
        child: const ProductItem(),
      );
    });
  }
}

class ProductItem extends ConsumerWidget {
  const ProductItem({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // 5. Access the index via WidgetRef
    final index = ref.watch(currentProductIndex);
    // do something with the index
  }
}
~~~

위 코드에서 Provider가 우선 UnimplementedError를 발생시키도록 한 후, ProviderScope내에서 provider의 값을 override하고, ProductItem을 감싼다. 그리고 ProductItem의 build()에서 idnex를 watch()한다. 이 코드는 퍼포먼스상 이점이 있는데, 이는 ProductItem()이 const로 선언되어 있고, 덕분에 index가 바뀌지 않는 한 rebuild가 발생하지 않기 때문이다.

## select를 통해 필터링하기

만약 state의 데이터 타입이 여러개의 properties를 가진 모델이고, 이중 일부의 property의 변화만을 Widget에서 구독하여 rebuild하고 싶을 때 `select()`를 사용할 수 있다.

~~~
class Connection {
  Connection({this.bytesSent = 0, this.bytesReceived = 0});
  final int bytesSent;
  final int bytesReceived;
}

// Using [StateProvider] for simplicity.
// This would be a [FutureProvider] or [StreamProvider] in real-world usage.
final connectionProvider = StateProvider<Connection>((ref) {
  return Connection();
});

class BytesReceivedText extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // only rebuild when bytesReceived changes
    final bytesReceived = ref.watch(connectionProvider.select(
      (connection) => connection.state.bytesReceived
    ));
    return Text('$bytesReceived');
  }
}
~~~

위 코드에서 Connection클래스는 2개의 properties를 갖고 있지만, BytesReceivedText는 그 중 bytesReceived가 변화했을 때만 rebuild된다. Riverpod이 state가 변화했을 때 select문의 함수의 반환값들(여기서는 connection.state.bytesReceived)을 비교하여 변경시에만 rebuild하도록 한다.

## Riverpod으로 테스트하기

> [너무 길어서 그냥 링크](https://codewithandrea.com/articles/flutter-state-management-riverpod/#testing-with-riverpod)

## ProviderObserver로 로깅하기

Riverpod은 state의 변화를 모니터링하기 위해 `ProviderObserver`클래스를 제공한다.

~~~
class Logger extends ProviderObserver {
  @override
  void didUpdateProvider(
    ProviderBase provider,
    Object? previousValue,
    Object? newValue,
    ProviderContainer container,
  ) {
    print('[${provider.name ?? provider.runtimeType}] value: $newValue');
  }
}
~~~

로거는 ProviderScope에서 설정한다.

~~~
void main() {
  runApp(
    ProviderScope(observers: [Logger()], child: MyApp()),
  );
}
~~~

로거에서 사용하기 위해 provider에 이름을 설정해 줄 수 있다.

~~~
final counterStateProvider = StateProvider<int>((ref) {
  return 0;
}, name: 'counter');
~~~

ProviderScope를 중첩시켜 Widget tree의 일부 지역에서만 로깅하도록 할 수 있다.