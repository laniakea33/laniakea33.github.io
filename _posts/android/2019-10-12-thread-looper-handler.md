---
title: "Thread, Looper, Handler"
categories:
    - Android
---
★Message : 스레드간 주고 받을 정보를 담은 객체이자 MessageQueue에 들어갈 작업의 단위

★Message는 새로 생성하지 않고 Android 시스템이 만들어 둔 MessagePool에서 Message.obtain()메소드로 가져와서 사용한다. 파라메터로 내가 정한 메시지의 종류를 반영해서 Handler.handleMessage()에서 다른 처리를 하도록 만들 수 있다.

★Runnable : 데이터를 담은 Message와 달리 실행할 함수를 담고 있다.

★Runnable보다 Message가 코드를 집중시킬 수 있고 빠르게 실행되므로 권장된다고 한다.

★Looper : MessageQueue에 들어온 Message, Runnable을 순서대로 Handler에게 전달하는 무한루프를 돈다. UI스레드는 기본적으로 Looper가 있어서 메시지를 받을 수 있지만, 새로 생성한 Thread는 Looper가 없다. 그래서 prepare(), loop()를 호출해서 수동으로 작동시켜야 하고, quit()나 quitSafely()를 호출해서 역시 수동으로 중지시켜야 한다.

★Looper.prepare() : MessageQueue를 세팅하고 Looper를 준비시킨다.

★Looper.loop() : 루프를 시작한다. 이후 run()에서 아무것도 실행되지 않는다. 메소드 실행 전에 prepare()가 선행되어야 한다.

★Looper.quit() : 즉시 루프를 중지한다.

★Looper.quitSafely() : MessageQueue에 있는 작업을 다 처리한 후 루프를 중지한다.

★Handler : 스레드의 MessageQueue와 연계하여 Message와 Runnable객체를 Queue에 넣고 차례대로 실행한다. 다른 스레드에서 특정 스레드의 Handler를 이용하여 다른 스레드에 Message와 Runnable을 전달할 수 있다.

★Handler.handleMessage() : Looper가 Queue에서 꺼내준 Message와 Runnable을 처리한다.

★Handler.sendMessage(Message) : Queue에 Message을 전달한다.

★Handler.post(Runnable) : Queue에 Runnable을 전달한다.

★Handler.post/sendMessageAtFrontOfQueue() : Message를 Queue의 맨 앞에 전달한다.

★Handler.post/sendMessageDelayed() : Message를 예약전송한다.

★Handler.removeCallback(Runnable) : Queue에 Runnable이 대기중이라면 제거한다.

★Handler를 상속하여 handleMessage()를 오버라이딩 하는 방법 외에도 Handler.Callback을 implement해서 사용하는 방법도 있다. 이 경우에는 handleMessage()가 처리여부에 따라 boolean값을 return하게 되어 있는데 만약 false를 리턴한다면 원래의 Handler에서 처리한다. 

★HandlerThread : Looper와 MessageQueue를 미리 가지고 있는 확장된 Thread class이다. Looper를 준비하지 않고도 편하게 작업을 보낼 수 있다.
HandlerThread ht = new HandlerThread(...);
ht.start()  //  start()를 호출해야 Looper가 준비된다.

Handler handler = new Handler(ht.getLooper());
handler.post(Runnable);

Handler.Callback callback = new Handler.Callback();
Handler handler2 = new Handler(ht.getLooper(), callback);