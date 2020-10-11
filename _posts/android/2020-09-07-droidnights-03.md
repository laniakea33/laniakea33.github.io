---
title: "드로이드 나이츠 - 2주차"
categories:
    - Android
---
1. Android UI Animation 들이붓기

* rotate Drawable을 활용하면 손쉽게 뱅글뱅글 도는 프로그레스 애니메이션을 설정할 수 있음

* ContentLoadingProgressBar를 사용하면 일정 시간동안 응답이 없을 때만 보여주도록 할 수 있음.

* Progress : 정적인 이미지 + 동적인 이미지를 겹쳐 차오르는 느낌의 애니메이션을 구현할 수 있음.
여기서 동적인 이미지는 android:useLevel="true"를 사용함.

* ProgressBar에 background와 progressDrawable에 각각 정적인 배경과 동적인 애니메이션을 설정

* Frame Animation : animation-list태그를 사용해서 drawable과 duration을 설정해줌.
Activity나 Fragment의 라이프사이클에서 시작과 종료를 관리해 줘야 함. 또는 animatedImageView를 사용하면 xml에
선언하는 형태로 사용할 수 있음. 알림 아이콘에서도 사용할 수 있음(setSmallIcon(int)).

* AnimatedStateListDrawable : selector의 drawable간에 변경에 트랜지션을 넣음. selector대신 animated-selector태그 사용.

* AnimatedVectorDrawable : Frame Animation대신 백터 이미지를 사용한다. group태그에 이름을 정한 후 objectAnimator를 사용해
애니메이션을 수학적?으로 정의함. 자세한 내용은 검색해 볼 것...너무 길어서 못쓰겠다.

* Shape Shifter : path data간 모핑을 쉽게 구현하여 Vector 애니메이션을 만들어 주는 툴

* Ripple : 배경이 투명한 항복에는 미리 정의된 속성으로 ripple지원 가능 : android:background="?selectableItemBackground"

* StateListAnimator : selector + objectAnimator

Transition

* SharedElements : 화면 전환 간 콘텐츠가 이어지는 것 처럼 보이는 트랜지션

* 두 layout파일의 Element에 transitionName을 동일하게 설정 후, makeSceneTransitionAnimation API사용,
수신하는 쪽애서는 sharedElementEnterTransition을 사용. 

* View Animation : Activity나 Fragment등의 전환 시 단순한 Animation을 보여준다. overridePendingTransition, 
Transaction.setCustomAnimation등을 사용. 

* TransitionManager

* 이외에도 많이 있음.






- Declarative UI 도입을 향한 여정

Jetpack compose
Litho
Epoxy