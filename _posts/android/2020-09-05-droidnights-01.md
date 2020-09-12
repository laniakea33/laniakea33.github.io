---
title: "드로이드 나이츠 - 라이브"
categories:
    - Android
---
1. 안드로이드 아키텍처 총 정리

뉴스레터 페이지 : medium.com/@androidtechweekly

Advice
- 먼저 약간 도전적인 아키텍처 도입
- 주기적인 리팩토링/리스트럭처링

- 도메인의 재발견 : 
DDD Light : DDD의 전술적 패턴만을 채용. 도메인을 격리하나 Transaction script로써의 Use Case구현
Service, Entity, VO

- inline class : 컴파일시에는 이 클래스는 사라진다. VO는 자신에 대한 의미(역할이나 형식 등의 룰)를 스스로 알고있음
enum이나 data class 등으로 구현할 수도 있다. 아무튼 VO임.

- RIBs(Router - Interactor - Builder) : 화면 중심이 아니라 비즈니스 로직이 구현된 Interactor가 중심

- ViewModel은 왜 널리 애용되나
    1. 라이프 사이클에 연관된 많은 메커니즘들.
    2. 데이터바인딩으로 뷰 로직을 간단히 할 수 있음
    
- RxJava, LiveData... ViewModel - View 사이의 이벤트 전달 방식 등
- 최근 Coroutines의 StateFlow도 등장. 아직은 그다지

Activity, Fragment는 View가 아니다. 이 들은 라이프 사이클을 관리하는 컨테이너일 뿐. 실질적인 View는 XML.

Fragment
- 의존성 주입 구성이 스파게티화
- 귀찮은 백스택 구성
- 테스트의 어려움(액티비티 속에 구성되므로... 따로 테스트 하기 어렵)
- 라이프사이클 복잡함
- OnStop()과 OnDestroy()사이에 View만 할당 해제될 수 있음.
- API호출 결과 callback에서 view/context접근
- View, ViewBinding인스턴스를 멤버로 저장하는 경우

But
- 화면 천이 애니메이션의 일관성
- 딥링크 대응 등 복잡한 화면 이동의 일관성
- 멀티윈도우

Fragment의 변화
- Result: Fragment간의 통신을 일관성 있게 구현 가능
Layout ID를 인자로 받는 생성자 가능
테스트도 쉬워짐: launchFragmentInContainer()
FragmentFactory추가 : 의존성 주입 설정이 보다 유연해 짐.
Jetpack : LifecycleObserver, Navigation

Fragment의 미래
보다 유지보수성/안정성 높은 구현이 되도록. 기존 커스텀 인터페이스들 더 작게, 더 테스터블하게
보다 심플한 API로 변신할 예정
보다 고수준의 API들과의 연계


Jetpack compose
예시 : goo.gle/compose-jetchat


클린아키텍처, Redux, Jetpack Compose, Hilt를 배운 뒤 다시 볼 것








2. 어려운 상태관리! 프로그램에 위임시켜버리기

아키텍처 블루프린트 mvp-kotlin 리팩토링
side effect = 참조 불투명 = 함수에 필요한 변수가 함수 스코프 밖에서 변경될 수 있다

https://github.com/omjoonkim/android-architecture

리팩토링 된 코드를 보며 다시 볼 것








3. FindViewBindings - View를 바인딩하는 모든 방법

Kotlin Android Extension 주의점
Fragment처럼 일반적인 클래스에서 특정 레이아웃의 뷰를 사용하면 findViewById를 캐시를 사용하지 않고 매번 호출하므로 주의
RecyclerView의 ViewHolder도 특정 커스텀 뷰 클래스가 아닌 일반적인 ViewHolder를 사용하게 되면 캐시를 사용하지 않음.
이 플러그인을 사용할 때는 명확한 클래스를 사용할 것.

DataBinding vs ViewBinding
ViewBinding은 FindViewById의 기능을 DataBinding으로부터 분리한 것.






Q&A
1. 구글 블로그 - beginner export : 경험은 쌓이는데 계속 비기너의 상태인 경우에 대한 글
2. 크래킹 코딩 인터뷰라는 책이 있음