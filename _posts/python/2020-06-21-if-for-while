---
title: "제어문"
categories:
    - Python
---
* if문 : 들여쓰기, 괄호 안쓰는 것, :쓰는 것, else if가 아닌 elif인 것 주의할 것
{% highlight python %}
if 조건문:
    수행할 문장1
    수행할 문장2
    ...
elif:
    수행할 문장A
    수행할 문장B
    ...
else:
    수행할 문장A
    수행할 문장B
    ...

//  아무것도 수행하지 않을 때는 pass키워드 사용
//  수행할 문장이 하나일 때는 줄바꿈 안하고 콜론 뒤에 써도 됨.

message = "success" if score >= 60 else "failure"
//  이렇게 표현식으로 사용할 수도 있음.
{% endhighlight %}

* 참고할 만한 조건문
{% highlight python %}
>>> 변수 in 콜렉션
>>> 변수 not in 콜렉션
{% endhighlight %}

* while문
{% highlight python %}
while <조건문>:
    <수행할 문장1>
    <수행할 문장2>
    <수행할 문장3>
    ...

//  break, continue로 제어
{% endhighlight %}

* for문
{% highlight python %}
for 변수 in 리스트(또는 튜플, 문자열):
    수행할 문장1
    수행할 문장2
    ...

//  in range(1, 11)은 1~10의 범위타입이다.
{% endhighlight %}

* 리스트 내포를 사용하여 리스트 안에 for문을 포함하여 더 단순하게 for문을 사용할 수 있다.
{% highlight python %}
[표현식 for 항목 in 반복가능객체 if 조건문]

>>> a = [1,2,3,4]
>>> b = [n * 3 for n in a]
>>> b
[3, 6, 9, 12]
{% endhighlight %}
