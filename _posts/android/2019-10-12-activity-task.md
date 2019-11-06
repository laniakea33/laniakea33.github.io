---
title: "[Android] Activity Task"
categories:
    - Android
---
★Activity Task : 액티비티를 묶은 단위, 액티비티 스택, 액티비티들의 집이자 유저관점에서의 앱. 최근 실행한 앱 화면의 내용 하나하나가 Activity Task이자 Activity Stack.

★한 태스크 내에 같은 액티비티가 여러개 생성될 수 있고, 이를 `Context`객체로 구분한다.

★moveTaskToBack(boolean root) : 태스크를 백그라운드로 보내는 메소드. root가 false면 이 액티비티가 root액티비티일 때만 백그라운드로 보내고, true라면 그냥 보낸다. 예를들어 앱을 암호로 잠금한 경우 태스크에 액티비티가 여러개 있는 채로 백그라운드에 보관되어 있다가 다시 포그라운드로 올라올 때 암호 입력 액티비티를 표시하는 상황에서 back버튼을 누르면 암호 액티비티만 종료하지 말고 태스크 전체를 백그라운드로 보내야 하는데 그 때 암호 입력 액티비티의 onBackPressed()를 오버라이드 하여 moveTaskToBack()을 호출 하는 방식으로 사용할 수 있다.

★TaskAffinity(태스크 친화력, 사실상 태스크 이름) : 모든 태스크에는 이름이 있다. default는 package이름이고, 자연스럽게 최근 실행한 앱의 구분 기준이 된다.

★액티비티를 실행할 때 태스크와의 관계를 제어하는 방법이 2가지 있다. 매니페스트에서 launchMode를 설정하는 방법, 액티비티를 실행 시킬 Intent에 addFlag(int flag)를 사용하는 방법이다.

★Activity의 LaunchMode를 이용하는 방법
1. standard : 기본속성이다. 그냥 태스크의 맨 위에 차곡 얹는다.

2. singleTop : 이 액티비티가 새로 호출될 때 기존 태스크 맨 위에 같은 액티비티가 있으면 액티비티를 새로 만들지 않고 기존의 액티비티를 onNewIntent()를 통해 재사용한다. 그래서 태스크에 같은 액티비티가 연속으로 올 수 없게 된다.

3. singleTask : 자신이 속한 taskAffinity의 내부에서만 생성 되고, 태스크 안에 이 액티비티는 하나밖에 없게 된다. 다른 task에는 전혀 외출하지 않으면서 자신의 task에서도 같은 액티비티를 용납하지 않는다. taskAffinity에 해당하는 task가 존재 하지 않는다면 새 태스크를 생성한다. 존재 하는 경우에는 내부에 같은 액티비티가 존재하는 경우와 그렇지 않은 경우로 분기되는데, 같은 액티비티가 존재 하지 않는 경우는 그냥 위에 차곡 얹는 반면 같은 액티비티가 존재하는 경우에는 현재 존재하는 그 액티비티가 나올 때 까지 스택의 액티비티들을 pop하고 그 액티비티가 나오면 onNewIntent()로 실행한다.

ex) A - B - C Activity중 B가 singleTask일 때

3-1. 모두 같은 taskAffinity인 경우
A - B - C 순으로 호출하면 stack : [A, B, C]
A - B - C - B 순으로 호출하면 stack : [A, B, C]에서 B가 나올때 까지 액티비티들을(여기서는 C를) pop하고 B를 onNewIntent()로 실행, stack : [A, B]가 됨

3-2. B만 다른 taskAffinity일 때
A - B - C 순으로 호출하면 [A], [B, C]가 된다. B는 자신의 taskAffinity에서만 실행 되고, C는 자신을 실행시킨 컴포넌트와 같은 task에서 실행되므로. C도 singleTask여야 자신의 taskAffinity에서만 실행된다는 조건에 따라 [A, C], [B]가 된다.

태스크가 전환될 때에는 약간의 딜레이가 있으니 이를 잘 고려해 구현해야 한다.

4. singleInstance : 무조건 자신이 속한 taskAffinity에서만 실행되고, 해당 task에는 혼자밖에 못들어 간다.

★Service나 Broadcase Receiver처럼 액티비티 이외의 컴포넌트에서, 즉 태스크가 없는 컴포넌트에서 액티비티를 실행시키면 새 액티비티가 쌓일 멍석이 없으므로 액티비티 호출을 요청하는 Intent에 flag(FLAG_ACTIVITY_NEW_TASK)를 줘야 한다.

★보통 알람시계의 알람 액티비티와 같이 특수목적의 액티비티를 제외하면 taskAffinity를 명시하지 않는다.

★최근 앱 목록에 액티비티를 안보이게 하려면 매니페스트에서 액티비티에 excludeFromRecents속성을 true로 주면 된다.

★Intent의 Flag를 이용하는 방법
1. FLAG_ACTIVITY_SINGLE_TOP : launchMode의 singleTop(위의 2번)과 같다. 스택의 맨 위에 같은 액티비티가 존재하는 경우 재활용 한다.
2. FLAG_ACTIVITY_NEW_TASK : launchMode의 singleTask(위의 3번)과 같다. 액티비티의 taskAffinity와 같은 task가 있다면 그 곳의 맨 위에 차곡 들어가고, 없다면 새로운 task를 만든다. 즉 자신이 속한 taskAffinity이외에는 들어가지 않는다.
3. FLAG_ACTIVITY_CLEAR_TOP : 실행하려는 액티비티가 스택에 이미 존재하는 경우 그 액티비티를 맨 위로 끌어올리고 그 위에 있던 액티비티들은 모두 삭제한다. ABCDE에서 C를 호출하면 ABC가 되며 C의 onCreate()가 호출되는데 이 때 SINGLE_TOP을 함께 사용하면 onCreate()대신에 onNewIntent()로 재활용 할 수 있다.
4. FLAG_ACTIVITY_CLEAR_TASK : 스택이 전부 청소되고 이 액티비티가 root가 된다. 보통 기존 태스크를 전부 정리하고 처음부터 다시 시작할 때 NEW_TASK와 함께 사용한다.
5. FLAG_ACTIVITY_REORDER_TO_FRONT : 스택에 같은 액티비티가 존재하면 맨 위로 이동시켜 onResume()으로 재시작 시킨다. 자리만 바꿀 뿐이므로 위에 올라가있는 모든 액티비티를 삭제하는 CLEAR_TOP은 무시된다.