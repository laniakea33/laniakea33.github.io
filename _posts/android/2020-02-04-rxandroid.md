---
title: "RxAndroid"
categories:
    - Android
---
* RxAndroid는 RxJava에 몇 가지 클래스를 추가해 안드로이드 앱에서 리액티브 구성요소를 쉽게 사용하게 만드는 라이브러리

* 기본적인 구성요소와 원리는 RxJava와 동일.

* RxAndroid에서 추가된 Schedulers
    1. AndroidSchedulers.mainThread() : UI스레드에서 동작
    2. HandlerSchedulers.from(Handler) : 특정 핸들러에 의존하여 동작
    
* RxLifecycle : 안드로이드의 액티비티나 프래그먼트의 라이프사이크를 이용할 수 있게 해줌.
    이는 메모리 누수를 방지하기 위함. complete하지 못한 구독을 자동 dispose함.

* build.gradle의 디펜던시에 추가해 줘야 함
{% highlight groovy %}
implementation 'com.trello.rxlifecycle3:rxlifecycle-android:3.1.0'
implementation 'com.trello.rxlifecycle3:rxlifecycle:3.1.0'
implementation 'com.trello.rxlifecycle3:rxlifecycle-components:3.1.0'
{% endhighlight %}
    
* 컴포넌트 종류 : RxActivity, RxDialogFragment, RxFragment, RxPreferenceFragment, RxAppCompatActivity,
    RxAppCompatDialogFragment, RxFragmentActivity. 각 컴포넌트에 대응하기 위해 존재.
    
* 예를들어 액티비티가 RxAppCompatActivity를 상속하게 하고, Observable을 생성할 때
    .compose(bindToLifecycle())로 라이프사이클을 관리하도록 추가한다.
    이 Observable은 액티비티가 종료되면 자동으로 해제된다.
    라이프사이클 관리는 직접 dispose()를 호출하는 것으로 대체할 수 있다.
    
* UI이벤트 처리 : RxView,RxTextView 등의 클래스를 이용하면 이벤트를 명시적으로 발행하지 않더라도
    내부에 포함된 clicks(), textChangeEvents()등의 메소드가 준비되어 있다.
    
* RxAndroid 활용 : Handler & Looper나 AsyncTask를 대체 가능.
    TimerTask, Handler.postDelayed등을 Observable.delay() 또는 interval()로 대체 가능.
    Volley, Retrofit에 접목가능. Retrofit은 RxJava를 정식지원함.

* 메모리 누수
    1. 라이프사이클에 맞게 onPause()나 onDestroy()에서 dispose()로 명시적 자원 해제
    2. RxLifecycle을 이용해 Observable의 compose()메소드를 이용해 라이프사이클에 바인딩
    3. CompositeDisposable에 Disposable을 담아뒀다가 한꺼번에 dispose()
    