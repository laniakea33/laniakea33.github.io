---
title: "Delegation Property"
categories:
    - Kotlin
---
* Delegation Property(위임 프로퍼티) : 객체의 프로퍼티에 접근(get/set)하는 과정을 
    다른 객체에게 위임하는 것을 의미 한다. get/set을 호출할 때 로그를 찍는 로직을 예시로 든다.

{% highlight kotlin %}
class Person(val _age: Int) {
    var age: Int = _age
        get() {
            println("age get $field")   //  프로퍼티 마다 로그를 일일이 추가함
            return field
        }
        set(value) {
            println("age set $value")
            field = value
        }
}
{% endhighlight %}

* 일일이 로그를 추가해야 하므로 비효율이 증가한다. 따라서 operator fun get/setValue()를 대신 수행할
    Delegator클래스를 만들고, by 키워드로 이를 프로퍼티에 위임을 설정한다.

{% highlight kotlin %}
class Delegator<T>(var value: T) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("${property.name} get $value")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        println("${property.name} set $newValue")
        value = newValue
    }
}

class Person(age: Int) {
    var age: Int by Delegator(age)
    // age의 get/setValue()메소드를 Delegator가 대신 수행한다.
}
{% endhighlight %}

* get/setValue()의 파라메터 설명
    1. thisRef : 위임을 사용하는 클래스 타입. 위 경우에는 Person
    2. property : 프로퍼티 자체를 다루는 클래스. KProperty<*>이거나 Any어야 한다.
    3. newValue : 위임에 사용되는 프로퍼티의 타입

* Delegates.observable() : 프로퍼티의 변경을 감지하여 어떠한 로직을 수행한다. 위의 Delegator를 직접 구현하지 않고도
    간단하게 사용할 수 있으나 프로퍼티 하나하나에 다 달아줘야 함.
{% highlight kotlin %}
class Person(age: Int) {
    var age: Int by Delegates.observable(age) { property, oldValue, newValue ->
        println("${property.name} set $oldValue -> $newValue")
    }
}
{% endhighlight %}

* by map : Kotlin의 Map 인터페이스는 프로퍼티의 getValue()와 setValue()에 대한 위임이 가능하다.
{% highlight kotlin %}
//  map[age] = 20 뭐 이런식으로 넣어서 위임 가능
class Person(val map: MutableMap<String, Int>) {
    var age: Int by map
}
{% endhighlight %}

* Lazy initialization(지연 초기화) : 프로퍼티 초기화를 객체 생성 시점에 하지 않고, 지연시키는 전략이다.
    일반적으로 프로퍼티의 초기화를 null로 한 후 프로퍼티 첫 접근 시 초기화를 진행하고, 프로퍼티가 null이 아니라면 저장된 값을 반환하는 방식을 사용한다. Kotlin에서는 이러한 로직을 lazy()라는 함수로 래핑해 주었다.
    
{% highlight kotlin %}
class Person() {
    private var _name: String? = null
    val name: String
     get() {
         if (_name == null) _name = loadName()
         // loadName은 서버나 DB에서 가져오는 등 오래 걸리는 작업일 수 있다
         return _name!!
     }
}
{% endhighlight %}

* 위 구문을 간단하게 아래와 같이 쓸 수 있다.

{% highlight kotlin %}
class Person() {
    //  지연된 초기화를 loadName()에게 위임한다.
    val name: String by lazy { loadName() }
}
{% endhighlight %}