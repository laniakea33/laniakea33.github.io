---
title: "[Android] Data Binding"
categories:
    - Android
---
※이 포스트는 https://www.vogella.com/tutorials/AndroidDatabinding/article.html#using-data-binding-in-android-applications를 참조하여 작성했습니다.

★안드로이드는 layout에 데이터를 접목시켜 적용하는 데이터 바인딩을 지원한다. 이는 UI 요소들에 대해 데이터를 연결시키는데 필요한 코드를 최소화 시켜준다.

★Android 프로젝트에서 먼저 데이터 바인딩을 사용하기 위해서는 모듈수준 app/build.gradle파일에 다음 내용을 넣어줘야 한다.
{% highlight gradle %}
android {
    ...
    dataBinding {
        enabled = true
    }
}
{% endhighlight %}

★데이터 바인딩을 사용하기 위해 먼저 layout파일을 수정해야 한다. 이 파일은 루트가 `<layout>`태그여야 하고 이 태그의 자식태그로써 `<data>`와 지금까지 사용해 왔던 다른 뷰 계층들을 넣는다. `data`는 레이아웃에 바인딩 할 데이터를 의미한다. 레이아웃은 각 속성들의 값에 `@{...}`나 `@={...}`를 넣음으로써 이 데이터를 참조할 수 있다.

{% highlight xml %}
<layout>
    ...
   <data>
       <variable name="temp" type="클래스명"/> 
   </data>

   <LinearLayout>
       ...
       <TextView
        ...
        android:text="@{temp.location}"/>
       <TextView
        ...
        android:text="@{temp.celsius}"/>
   </LinearLayout>
</layout>
{% endhighlight %}

★데이터 바인딩은 이 레이아웃을 사용하여 `Binding`클래스를 만든다. 이 클래스는 뷰들과 그 뷰들에 연결된 데이터들을 가지고 있고, 소스코드에서 이 정보를 이용할 수 있게하고, 데이터에 대한 setter, getter또한 제공하고 있다.