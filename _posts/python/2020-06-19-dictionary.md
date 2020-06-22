---
title: "Dictionary"
categories:
    - Python
---
* Distionary는 순서가 없고 맵과 같이 key와 value로 구성되어 있으며 다음과 같이 생성할 수 있다.

{% highlight python %}
>>> dic = {Key1:Value1, Key2:Value2, Key3:Value3, ...}
{% endhighlight %}

* 키값쌍 추가, 삭제
{% highlight python %}
>>> dic['list'] = [1,2,3]   //  dic이라는 dictionary에 {'list':[1,2,3]}쌍을 추가한다.
>>> del dic['list']   //  key가 'list'인 쌍을 삭제
{% endhighlight %}

* Key는 중복될 수 없으며, list처럼 가변적인 객체는 들어갈 수 없다.

* 관련 함수들
{% highlight python %}
>>> dic.keys()  //  key들을 dict_keys객체에 담아 반환한다. list(dic.keys())를 호출하면 리스트에 담아준다.
dict_keys(['name', 1])
>>> dic.values()  //  value들을 dict_values객체에 담아 반환한다
dict_values(['pey', 2])
>>> dic.items() //  key:value쌍을 dict_items객체에 담아 반환한다.
dict_items([('name', 'pey'), (1, 2)])
>>> dic.clear() //  dic을 청소한다.
>>> dic.get(key)    //  dic[key]와 같으나 값이 없는 경우 get()은 None을 반환하고, 인덱싱은 오류를 뿜는다
>>> dic.get(key, defaultValue)    //  dic[key]와 같으나 값이 없는 경우 defauleValue를 반환한다.
>>> 'name' in dic   //  dic안에 'name'이라는 key가 있는 지 검색한다. True/False를 반환한다.
{% endhighlight %}