---
title: "Flutter Riverpod 기초 정리"
categories:
    - Flutter
---
이 포스트는 아래 블로그를 참고한 포스트입니다.

[참고문서](https://codewithandrea.com/articles/flutter-state-management-riverpod/)

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

Future를 반환하는 API 통신등의 결과를 받기위해 사용할 수 있다.