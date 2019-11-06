---
title: "[Android] Data Binding"
categories:
    - Android
---
★Android 프로젝트에서 먼저 데이터 바인딩을 사용하기 위해서는 모듈수준 app/build.gradle파일에 다음 내용을 넣어줘야 한다.
{% highlight gradle %}
android {
    ...
    dataBinding {
        enabled = true
    }
}
{% endhighlight %}

★