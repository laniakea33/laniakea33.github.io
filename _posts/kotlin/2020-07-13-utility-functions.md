---
title: "Utility Functions"
categories:
    - Kotlin
---
{% highlight kotlin %}
val min = int1.coerceAtMost(int2)   //  최대값을 int2로 강제. 즉 int1과 int2중 작은 값을 return
val max = int1.coerceAtLeast(int2)   //  최소값을 int2로 강제. 즉 int1과 int2중 큰 값을 return
{% endhighlight %}