---
title: "Transition"
categories:
    - Android
---
★Transition : 사전적 의미로 이행, 전의 등 상태의 변화를 뜻한다. Android에서 말하는 Transition은 액티비티나 뷰의 위치나 모양 등의 변화를 뜻하며 보통 Animation과 함께 이야기 된다.

★Transition을 적용하는 방법은 3크게 3가지가 있다.

1. anim을 xml파일로 정의한 후 적용
2. transition을 커스텀(API 5.0이상)
3. Shared Elements

★anim을 xml파일로 정의한 후 적용하는 법
1. res/anim디렉토리에 <set>을 루트 태그로 하는 애니메이션 리소스파일을 만든다.
2. 아래와 같이 애니메이션을 정의한다.
{% highlight xml %}
<set
    android:duration="1000"
    >
    <translate 
        android:fromYDelta="-100%"
        android:toYDelta="0%"
        />
</set>
{% endhighlight %}
3. 액티비티 실행시 overridePendingTransition()으로 적용 시킨다.
{% highlight kotlin %}
button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            // 첫번째 인자는 새로 등장할 액티비티의 등장 애니메이션
            // 두번째 인자는 사라질 현재 액티비티의 퇴장 애니메이션
        }
{% endhighlight %}

★Transition을 정의하여 Theme Style에 적용
1. res/transition디렉토리에 <transitionSet>을 루트 태그로 하는 전환 리소스 파일을 만든다.
2. 아래와 같이 전환을 정의한다. 여러 Animation을 합체시킨 듯 한 느낌이다.
{% highlight xml %}
<transitionSet>

    <slide
        android:duration="1000"
        />

    <fade
        android:duration="1000"
        android:fadingMode="fade_in_out"
        />

</transitionSet>
{% endhighlight %}
3. values/style.xml에 다음과 같은 라인을 추가한다.
{% highlight xml %}
<item name="android:windowContentTransitions">true</item>
<item name="android:windowEnterTransition">@transition/2에서 만든 파일 이름</item>
<item name="android:windowExitTransition">@transition/2에서 만든 파일 이름</item>
{% endhighlight %}
4. startActivity() 호출시 다음과 같은 내용을 추가한다.
{% highlight kotlin %}
...
val options = ActivityOptions.makeSceneTransitionAnimation(this)
startActivity(intent, options.toBundle())
...
{% endhighlight %}

이 밖에 소스 코드에서 TransitionManager를 사용하여 뷰를 전환하거나, Scene을 사용하여 두 레이아웃간 전환 시 뷰가 이동하는 듯한 효과를 낼 수도 있다. 

★Shared Elements : 두 액티비티간 전환시 한 액티비티의 뷰가 다른 액티비티의 뷰로 마치 이동하는 듯한 느낌을 주는 애니메이션이다. Hero Animation이라도고 함
1. 두 layout xml파일의 각각 대상으로 할 뷰에 android:transitionName 속성을 서로 일치하도록 정해준다.
2. startActivity()시 아래와 같이 호출한다.
{% highlight kotlin%}
val options = ActivityOptions.makeSceneTransitionAnimation(this, 적용시킬 뷰, 정해둔 transitionName)
startActivity(intent, options.toBundle())
{% endhighlight %} 

★Shared Elements의 경우 효과를 적용할 뷰가 TextView이고 효과 발동 시 textSize가 같이 바뀌지 않아 의도한 대로 효과가 나지 않는다.
이를 해결하기 위한 링크 https://stackoverflow.com/questions/26599824/how-can-i-scale-textviews-using-shared-element-transitions

근데 도무지 이해가 안가므로 나중에 정리 ㄱㄱ