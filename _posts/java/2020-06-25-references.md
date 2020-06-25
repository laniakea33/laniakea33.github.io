---
title: "References(weak, soft, phantom, strong)"
categories:
    - Java
---
 * [Java Reference와 GC - NAVER D2](https://d2.naver.com/helloworld/329631)를 요약 정리함. 나만 볼려고

 * GC는 reachability에 따라 참조가 불가능한 객체는 모두 제거한다. 참조의 타입(weak, soft, phantom, strong)에 따라 GC의 참조 가능성 판단의 기준이 조금씩 다르다.

 * strong reference : 일반적인 new 키워드로 생성하는 참조 타입. reachability를 잃지 않는 한 이 객체는 GC의 대상이 되지 않는다.
{% highlight java %}
SomeClass sc = new SomeClass();
{% endhighlight %}

* weak reference : WeakReference<T> 타입으로 참조하는 참조타입. 객체를 참조하는 변수가 WeakReference타입만 있는 경우이다. 이 경우 GC가 발생하면 무조건 같이 삭제된다. 항상 유효할 필요까지는 없는 캐시같은 임시객체로 유용하게 사용할 수 있다. GC가 발생할 때 WeakReference가 참조하는 객체를 발견하면 이 객체를 null로 설정하여 참조를 끊어버린다. 그렇게 이 객체는 unreachable상태가 되고 메모리에서 제거되게 된다. 
{% highlight kotlin %}
SomeClass sc = new SomeClass();
WeakReference<SomeClass> ref = new WeakReference<SomeClass>(sc);
//  첫 줄에서 생성한 SomeClass 인스턴스는 sc와 ref 두가지 변수에서 참조하고 있는데
//  sc = null; 등으로 참조타입을 WeakReference만 남기게 되면 이 객체는 다음번 GC때 사라진다.
//  new SoftReference로 생성한 이 SoftReference객체 자체는 strong reference이다.
{% endhighlight %}

* soft reference : SoftReference<T> 타입으로 참조하는 참조타입. 객체를 참조하는 변수가 SoftReference타입만 있는 경우이다. 이 경우 메모리가 부족하면 제거의 대상이 되고, 메모리가 남아있다면 제거되지 않는다.(객체의 soft 참조 시간, 유휴 메모리 사이즈 등의 값으로 계산하여 판단한다.) 메모리를 회수하기로 결정되면 객체참조를 null로 설정하여 unreachable로 만들어 제거되도록 한다.
{% highlight kotlin %}
SomeClass sc = new SomeClass();
SoftReference<SomeClass> ref = new SoftReference<SomeClass>(sc);
{% endhighlight %}

* 참고 : GC의 대상을 찾는 과정과, 실제로 대상을 제거(finalize)하는 과정, 메모리를 회수하는 과정은 연속적이지 않으며, GC한번에 모든 대상을 제거하지도 않는다.

* ReferenceQueue : Soft/WeakReference객체가 GC대상이 되면 해당 참조는 null로 변경되고, 참조 객체 자체는 Reference Queue에 enqueue된다(GC에 의해). enqueue된 참조 객체를 가지고 GC에 대한 후처리를 할 수가 있는데 이는 참조 객체의 생성자에 Reference Queue를 인자로 주었을 때만 해당된다.

* phantom reference : Soft, WeakReference타입과는 달리 생성자에 무조건 ReferenceQueue를 넣어 주어야 한다. GC대상 탐색시 객체 내부의 참조를 null로 하지 않고 phantomly reachable객체로 만든 후, 참조 객체를 enqueue한다. 다시 말하면 GC대상 여부와 관계되는 Soft, Weak와는 달리 Phantom은 finalize와 메모리 회수사이에 관여하며, Phantom Reference로 참조되는 객체는 finalize이후 phantomly reachable로 간주된다. 즉 객체의 참조가 PhantomReference만 남게 되면 바로 finalize된다.

* 이후는 그냥 복붙함..(어려워서)ㅠㅠ

* GC가 객체를 처리하는 순서는 항상 다음과 같다.
    1. soft references
    2. weak references
    3. 파이널라이즈
    4. phantom references
    5. 메모리 회수

* 즉, 어떤 객체에 대해 GC 여부를 판별하는 작업은 이 객체의 reachability를 strongly, softly, weakly 순서로 먼저 판별하고, 모두 아니면 phantomly reachable 여부를 판별하기 전에 파이널라이즈를 진행한다. 그리고 대상 객체를 참조하는 PhantomReference가 있다면 phantomly reachable로 간주하여 PhantomReference를 ReferenceQueue에 넣고 파이널라이즈 이후 작업을 애플리케이션이 수행하게 하고 메모리 회수는 지연시킨다.

* 앞서 설명한 것처럼 PhatomReference는 항상 ReferenceQueue를 필요로 한다. 그리고 PhantomReference의 get() 메서드는 SoftReference, WeakReference와 달리 항상 null을 반환한다. 따라서 한 번 phantomly reachable로 판명된 객체는 더 이상 사용될 수 없게 된다. 그리고 phantomly reachable로 판명된 객체에 대한 참조를 GC가 자동으로 null로 설정하지 않으므로, 후처리 작업 후에 사용자 코드에서 명시적으로 clear() 메서드를 실행하여 null로 설정해야 메모리 회수가 진행된다.

* 이와 같이, PhantomReference를 사용하면 어떤 객체가 파이널라이즈된 이후에 할당된 메모리가 회수되는 시점에 사용자 코드가 관여할 수 있게 된다. 파이널라이즈 이후에 처리해야 하는 리소스 정리 등의 작업이 있다면 유용하게 사용할 수 있다. 그러나 개인적으로는 PhantomReference를 사용하는 코드를 거의 본 적이 없으며, 그 효용성에 대해서는 의문이 있다.