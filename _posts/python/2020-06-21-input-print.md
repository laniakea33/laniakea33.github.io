---
title: "입력값 받기, print()"
categories:
    - Python
---
{% highlight python %}
//  콘솔 입력을 받는다
>>> a = input()
//  프롬프트 입력을 받는다.
>>> a = input('숫자를 입력하세요')
{% endhighlight %}

{% highlight python %}
>>> print(객체) //  객체 출력
>>> print("a" "b" "c")  //  abc 출력
>>> print("a" + "b" + "c")  //  abc 출력
>>> print("a", "b", "c")  //  a b c 출력(콤마찍어서 띄워쓰기)
>>> for i in range(10):
        print(i, end=' ')   //  끝문자를 end로 지정하면 한줄에 계속 이어서 출력된다.
{% endhighlight %}