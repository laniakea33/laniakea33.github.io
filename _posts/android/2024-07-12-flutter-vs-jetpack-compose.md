---
title: "Flutter vs Jetpack Compose"
categories:
    - Android
---

나는 최근 3개월간 2개의 신규 프로젝트를 개발했다. 하나는 기존의 Android 프로젝트를 Jetpack Compose를 사용하여 재개발 했고, 다른 하나는 처음부터 Flutter로 개발을 시작하여 작업하는 내내 두 프레임워크를 번갈아 사용할 수 있었다. 구글이 낳은 이 두 유사하지만 다른 모양의 프레임워크를 사용하며 느꼈던 소감을 정리해 보고자 한다.

두 프레임워크가 이미 세상에 나온지 어느새 몇 년이나 흘렀기 때문에 새로운 통찰은 없을 것이다. 다만 직접 실감해본 바 다른 사람들의 공감되는 이야기를 정리해본다.

## Dart vs Kotlin

Flutter는 `Dart`, Jetpack Compose는 Android 앱 개발에 사용되는 `Kotlin`으로 작업하게 된다. 당연하게도 두 언어는 상당히 다른 모습을 보인다. Dart는 마치 Java와 Javascript를 섞은듯한 느낌이 드는데, Kotlin만 쓰다가 Dart로 개발하면 세미콜론이나 객체 인스턴스 생성 문법이 마치 Java시절 안드로이드로 돌아간 것 같다는 느낌도 들지만 `null safty`와 같은 Kotlin의 장점도 많이 채택이 되어 있다.

### data class의 부재

Kotlin에서 가장 편하게 요긴하게 사용하는 기능중 하나인 `data class`가 Dart에는 없다. data class는 각 프로퍼티의 getter, toString, hashcode, copy등등 데이터 객체를 다루는데 필수적인 기능들을 알아서 생성해 주는데, 이 것을 일반 클래스로 구현하면 상당한 보일러 플레이트가 발생한다. 

**data class 사용 전**
~~~kotlin
public class SomeResponseModel {

    private String responseCode;
    private String responseMessage;
    private Data data;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Data getData() {
        if (data == null) {
            data = new Data();
        }
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        private String uuid;
        private String type;
        private String url;
        private String notify;
        private String date;

        public String getUUID() {
            return uuid;
        }

        public void setUUID(String uuid) {
            this.uuid = uuid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNotify() {
            return notify;
        }

        public void setNotify(String notify) {
            this.notify = notify;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
~~~

**data class 사용 후**
~~~kotlin
data class SomeResponseModel(
        var responseCode: String? = null,
        var responseMessage: String? = null,
        var data: Data = Data()
) {
    data class Data (
            var uuid: String? = null,
            var type: String? = null,
            var url: String? = null,
            var notify: String? = null,
            var date: String? = null
    )
}
~~~

이 기능은 Kotlin의 기능이기 때문에 Dart에서는 이 것을 감당해야 한다. 대신 Flutter 생태계에서는 이러한 불편을 Code Generator를 통해 해결한다. 규격에 맞게 데이터 클래스의 명세만을 정의해 두면 `freezed` 등의 패키지가 추가적인 편의 기능을 구현한 클래스를 작성해 주는 것이다.

**freezed를 통해 만든 data class**
~~~dart
part 'some_model.freezed.dart';

@freezed
class SomeModel with _$SomeModel {
  factory SomeModel({
    required String? responseCode,
    required String? responseMessage,
    required Data data,
  }) = _SomeModel;
}
~~~

이렇게만 작성 후 build_runner를 통해 build하면 freezed 패키지가 만들어 주는 some_model.freezed.dart파일에 copyWith등의 메소드가 생성된다. 작성시 오타가 생길 여지도 있고, 데이터 명세가 변경될 때 마다 다시 build해줘야 하기 때문에 data class가 훨씬 나은 것 같다.

### 비동기 플로우 처리 방식

Rx나 여타 프레임워크들이 있지만 Kotlin은 기본적으로 Coroutines를 활용한다. CoroutineScope라는 비동기 처리 스코프를 런칭하여 

## Hot Reload vs Preview

UI 개발과 디버깅의 효율성을 올리기 위해 Flutter와 Compose는 서로 다른 전략을 구사했다.

### Hot Reload of Flutter

Flutter는 디버그 모드에서 JIT Compiler를 사용하기 때문에 Hot Reload가 가능하다.

AOT 컴파일이 프로그램 실행전에 미리 프로그램 전체를 컴파일한 후에 실행하는 것과 달리 JIT 컴파일은 먼저 빌드 후 코드의 실행직전에 컴파일하는 방식이다. 코드 수정 발생시 처음부터 다시 컴파일 하지 않고, 수정된 내용만 다시 컴파일 하여 할 수 있다는 장점이 있다.

개발 중에 변경사항이 있는 위젯 트리를 다시 rebuild()하는 것이다. 따라서 initState()등 build() 함수 밖의 생명주기에서의 동작이 변경된다면 Hot Reload의 혜택을 받을 수 없다. 일단 앱을 에뮬레이터나 실 디바이스 연결한 상태로 개발을 진행하게 되고, 개인적인 경험상 해당 UI를 확인하기 까지의 기능을 발 맞춰 같이 개발하게 된다.(개략적인 UI 밑 바탕 -> 기능1 구현 -> 해당되는 UI 개발 -> 기능2 구현 -> 해당되는 UI 개발). 실제 화면으로 UI를 확인하며 개발할 수 있고, 수정사항 반영이 정말 빠르다는 장점이 있다. 다만 한번에 여러 상태에서의 모습을 확인할 수 없다는 단점도 있다.

### Preview of Jetpack Compose

반면 Compose는 각 독자적인 Composable에 대한 Preview를 제공한다. 앱을 실행하지 않고도 여러 state에서 UI가 어떻게 구성되는지 한번에 볼 수 있다는 장점이 있다. 모든 상황에서의 Composable UI를 Preview를 보며 미리 구현해 둘 수 있기 때문에, UI 개발과 기능 개발의 과정이 좀 더 분리되는 느낌이다. (모든 UI Component들 개발 -> 기능 개발 -> UI 조합) 조합되는 과정도 Preview로 확인이 가능하기 때문에 대략적으로 화면이 완성되기까지 앱을 실행시키지 않고도 개발할 수가 있다. 개인적으로는 각 Composable을 상태별로 미리 한번에 확인하며 조립할 수 있다는 점 때문에 Hot Reload보다 선호하긴 하는데, 수정된 코드가 반영되는 속도가 아무래도 느리다는 단점이 있다.

## Navigator vs NavController

Flutter와 Compose 모두 각 프레임워크의 컨셉을 살려 네비게이션을 구현해냈다. Flutter는 Widget개념을, Compose는 Composable개념을 사용하여 호스팅한다.

### Flutter에서의 Navigation

Flutter의 네비게이션은 InheritedWidget인 Navigator를 사용하여 컨트롤 한다. 목적지가 되는 Widget을 Route라는 개념으로 래핑한다. 

**first_page.dart**
~~~dart
class FirstPage extends StatelessWidget {
  const FirstPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Text("첫번째 화면"),
      GestureDetector(
        onTap: () {
          Navigator.of(context).push(MaterialPageRoute(builder: (context) {
            return SecondPage();
          }));
        },
        child: Text("두번째 화면으로 이동"),
      ),
    ]);
  }
}
~~~

**second_page.dart**
~~~dart
class SecondPage extends StatelessWidget {
  const SecondPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Text("두번째 화면"),
      GestureDetector(
        onTap: () {
          Navigator.of(context).pop();
        },
        child: Text("첫번째 화면으로 돌아가기"),
      ),
    ]);
  }
}
~~~

`Navigator`가 `InheritedWidget`라는 점 덕분에 위젯 트리 어디서든 접근할 수 있다.

또한 `pop()`메서드에 특정 데이터를 넣음으로써 해당 Route로부터 response를 받을 수 있다. 이 때 해당 Route를 `push`한 Widget이 await 함수와 같은 원리로 응답을 받는다.

**first_page.dart**
~~~dart
...
final result = await Navigator.of(context).push(MaterialPageRoute(builder: (context) {
            return SecondPage();
          })) as String;
...
~~~

**second_page.dart**
~~~dart
...
Navigator.of(context).pop("result");
...
~~~

### Jetpack Compose에서의 Navigation

Compose에서의 Navigation은 Composable개념을 활용한다. 그래프 정보를 가지고 모든 네비게이션을 호스팅하는 NavHost, NavHost에 연결되어 route에 대한 컨트롤 API를 제공하고, 백 스택을 관리하는 NavController 모두 Composable이다. Destination 또한 당연히 Composable이다.

~~~kotlin
@Composable
fun NavigationTest(
    modifier: Modifier = Modifier
) {
    //  1. NavController
    val navController = rememberNavController()

    //  2. NavHost
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Screen1.name,
    ) {
        //  3. Graph 선언
        composable(Screen.Screen1.name) {
            Screen1 { navController.navigate(Screen.Screen2.name) }
        }
        composable(Screen.Screen2.name) {
            Screen2 { navController.navigate(Screen.Screen3.name) }
        }
        composable(Screen.Screen3.name) {
            Screen3 { navController.popBackStack(route = Screen.Screen1.name, inclusive = false) }
        }
    }
}

enum class Screen {
    Screen1,
    Screen2,
    Screen3,
}
~~~

NavController는 NavHost에서 호스팅되는 모든 Composable이 참고할 수 있어야 하므로 최상단으로 Hoisting해야 한다. 결과적으로 NavHost와 같은 Composable에서 remember 된다.

또한 각 navigation의 결과는 `NavBackStackEntry`에 저장되어 전달된다.

~~~kotlin
...
@Composable
fun NavigationTest(
    modifier: Modifier = Modifier
) {
    //  1. NavController
    val navController = rememberNavController()

    //  2. NavHost
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Screen1.name,
    ) {
        //  3. Graph 선언
        composable(Screen.Screen1.name) {
            val result = navBackStackEntry.savedStateHandle.get<String>("result")
            Screen1 { navController.navigate(Screen.Screen2.name) }
        }
        composable(Screen.Screen2.name) {
            Screen2 { navController.navigate(Screen.Screen3.name) }
        }
        composable(Screen.Screen3.name) {
            Screen3 { 
                navController.popBackStack(route = Screen.Screen1.name, inclusive = false)
                navController.currentBackStackEntry?.savedStateHandle?.set("result", "result")
                }
        }
    }
}
~~~










## Flutter vs Jetpack Compose





## Widget class vs Composable function

## Widget vs Modifier


[참고문서1](https://www.netguru.com/blog/jetpack-compose-versus-flutter)