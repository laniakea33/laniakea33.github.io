---
title: "Class Delegation(클래스 위임)"
categories:
    - Android
---
* 어떠한 클래스를 재정의 하고 싶은 경우 보통 해당 클래스를 상속 받아 재정의 하여 사용하곤 한다.
하지만 그 클래스가 open class가 아닌 경우는 재정의를 할 수가 없는데, 이 때는 상속을 대신해 델리게이트 패턴사용한다.

1. 재정의하고 싶은 클래스(ArrayList)의 인터페이스(MutableCollection)를 똑같이 구현한 클래스(NewArray)를 제조한다.
2. NewArray는 ArrayList를 멤버로 하나 가진다.
3. NewArray에서 MutableCollection의 메소드를 구현하는데, 재정의하고싶은 부분은 재정의하고, 나머지는 멤버인 ArrayList가 수행하도록 한다.

{% highlight kotlin %}
class NewArray<T> : MutableCollection<T> {

    var objectAdded = 0
    
    val innerList = arrayListOf<T>()

    //  재정의 할 메소드들
    override fun add(element: T): Boolean {
        objectAdded++
        return innerList.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectAdded += elements.size
        return innerList.addAll(elements)
    }
    
    //  아래는 재정의 할 필요가 없으므로 innerList가 위임(델리게이트)하여 수행
    override val size: Int
        get() = innerList.size
    override fun contains(element: T) = innerList.contains(element)
    override fun containsAll(elements: Collection<T>) = innerList.containsAll(elements)
    override fun isEmpty() = innerList.isEmpty()
    override fun clear() = innerList.clear()
    override fun iterator() = innerList.iterator()
    override fun remove(element: T) = innerList.remove(element)
    override fun removeAll(elements: Collection<T>) = innerList.removeAll(elements)
    override fun retainAll(elements: Collection<T>) = innerList.retainAll(elements)
}
{% endhighlight %}

* 위와 같이 위임을 하면 재정의 할 필요가 없는데도 MutableCollection를 구현해야 한다는 이유만으로
많은 메소드들을 일일이 위임시켜줘야 한다. 이 과정을 Kotlin에서는 by키워드를 통해 생략하도록 할 수 있다.

{% highlight kotlin %}
class NewArray<T>(val innerSet: MutableCollection<T>): MutableCollection<T> by innerSet {

    var objectAdded = 0

    override fun add(element: T): Boolean {
        objectAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectAdded += elements.size
        return innerSet.addAll(elements)
    }
}
{% endhighlight %}

1. 같은 MutableCollection인터페이스를 상속하면서도 동작을 위임할 ArrayList를 인자로 받는다.
2. 선언부 뒤에 by 키워드를 사용하여 위임할 객체를 알린다.
3. 바디에 필요한 내용만 오버라이드 한다.