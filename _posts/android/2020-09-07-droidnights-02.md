---
title: "드로이드 나이츠 - 1주차"
categories:
    - Android
---
1. 레거시에서 멀티모듈시스템까지 A to Z 실전 적용기

병렬 개발, 병렬 빌드 등의 장점이 있음

모듈을 나누는 방법

계층 기반 패키징 : 같은 역할을 하는 것 끼리 패키지로 묶음(activity끼리, view끼리..)
기능 기반 패키징 : 같은 기능을 하는 것 끼리 묶는다(comment, auth...)

단연코 기능 기반 패키징 방식이 좋음. 그리고 이 방식은 모듈을 나눌 때에도 똑같이 적용된다.

로버트 밥은 패키징의 원칙 6가지를 소개한다.

1. 재사용/릴리즈 등가 원칙
2. 공통 재사용 법칙 : 하나의 클래스라도 사용하면 다 사용하는 것
3. 공통 폐쇄 법칙
4. 의존 관계 비순환 법칙
5. 안정된 의존 관계 원칙
6. 안정된 추상화 원칙


- 도메인 주도 설계 - 바운디드 컨택스트 : 도메인 상 논리적인 경계. 같은 VO라도 다른 도메인에서는 다른 개념으로 구현하게 된다.

- 디자인 캔버스 : API 우선 접근 방식의 일환. 모듈을 정의하는 방법 중 하나이다.



<버즈빌에서 모듈화를 적용한 방법>
1. 분리된 저장소에 모듈이 나뉘어 져 있다면 하나의 저장소로 합친다.
- 소스 레벨 연동
- 원자 단위의 변화
- 항상 완전한 소스

2. CI
- 하나의 저장소에 모듈이 몰려있으면 빌드시간이 늘어난다.
- 변화된 모듈만 빌드 및 테스트 하도록 한다.
- 라이브러리가 업데이트 되면 라이브러리를 참조하는 모듈도 같이 빌드해야 한다.
- 변화된 모듈이 무엇인지 파악한다. - Git diff
- 모듈관의 의존관계를 파악한다. - gradle plugin 이용 가능

3. CD
- ??



2. Effective Kotlin을 알아보자

- 코딩컨벤션이 필요함
- Preference -> Code Style
- Effective Kotlin 책 괜찮을 듯
- Property사용시 룰이 필요하다. state는 변수, 행동은 함수
- Scope Function : 장풍 코드가 되기 십상임. safeLet문을 활용하거나 그냥 if문을 활용하는 편이 나을 수도 있음
it이 여러개 겹치면 변수의 스코프에 혼동이 오기 쉽다. 적당히 쓰자..
- Sequence(Collection에 filter등의 람다함수를 사용하는 것) 활용하기
데이터 가공이 많은 경우 Sequence는 도움이 될 수 있지만 단순 데이터라면 오히려 더 성능이 나빠질 수 있다.
Sequence를 쓰면 좋은건 매번 새로운 list를 만드는 비용이 줄어들기 때문
- Coroutine
runBlocking : UI스케줄러에서 runBlocking구문을 사용하면 UI가 멈춘다. 결국 테스트 코드가 아닌 이상 runBlocking은 안쓰는게 나음.
GlobalScope : 기본 IO스레드에서 작동. 앱당 하나만 생성되는 싱글톤 객체. CoroutineScope을 사용하는 것이 바람직함.
오류 처리는 CoroutineScope와 동일(CoroutineExceptionHandler + SupervisorJob).
AAC-ViewModel과 연계하여 CoroutineScope를 활용하면 좋음.
Coroutine, Retrofit연계하는 경우 withContext(Dispatchers.IO)는 사실 불필요하다. 따라서 그냥 launch함수를 사용해도 문제 없다.





3. MVI 아키텍처 적용기

역대 수많은 아키텍처들이 있었다 MVC, MVP, MVVM...
근데 MVI란? 미쳐돌아가는 상태(state)가 항상 문제였음. 데이터들이 분산되고, 그 흐름이 복잡하기 때문에 발생하는 문제들이다.
유저로 부터 나온 Intent를 Model이 받아 View를 업데이드하여 유저에게 전달한다.
Controller, Presenter, Viewmodel 등의 로직 처리 컴포넌트의 개념과는 Intent는 완전히 다르다.
앱의 구조보다 앱의 상태와 데이터 흐름에 더 초점을 맞춘 패러다임의 하나.

-> 유의미한 테스트가 가능하고, 디버깅이 쉬워야 한다. 개발자에 상관없이 일정 수준의 퀄리티가 보장될 것

Github) Box in Android 참고 : MVI 아키텍처를 위한 Kotlin기반 프레임워크
MVI기반의 앱 설계도이다.

cycle에서 벗어나는 다이얼로그와 같은 부분을 SideEffect라 칭함
SideEffect는 또 다른 Intent가 되어 cycle에 합류함
Intent와 Model을 Viewmodel로써 구현할 수 있음.
Intent(이벤트) -> SideEffect -> Model(State) -> View의 순서로 진행됨.

MVI의 장점
1. 단방향 데이터 순환
2. 상태의 충돌이 없음(한번에 하나만 하기 때문)
3. 철저히 분리된 로직(Intent, Model, View)

MVI의 단점
1. 높은 진입 장벽. 높은 러닝커브
2. 파일이 많아짐
3. 간단한 작업도 Intent를 시작으로 하는 사이클을 형성해야 하므로 비효율적일 수 있음