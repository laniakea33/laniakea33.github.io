---
title: "[JetPack] WorkManager"
categories:
    - Android
---
* [공식 문서 링크](https://developer.android.com/topic/libraries/architecture/workmanager)

* WorkManager : 앱이 종료되거나 기기 재시작시 실행예정인 지연가능한 비동기 작업을 쉽게 예약할 수 있다.

* 디펜던시 추가
{% highlight groovy %}
// (Java only)
implementation "androidx.work:work-runtime:$x.y.z"
// Kotlin + coroutines
implementation "androidx.work:work-runtime-ktx:$x.y.z"
// optional - RxJava2 support
implementation "androidx.work:work-rxjava2:x.y.z"
// optional - GCMNetworkManager support
implementation "androidx.work:work-gcm:x.y.z"
// optional - Test helpers
androidTestImplementation "androidx.work:work-testing:x.y.z"
{% endhighlight %}

* 백그라운드 작업 생성 : Worker클래스를 상속하고, doWork()를 오버라이드 한다.
doWork()는 WorkManager에서 제공하는 백그라운드 스레드에서 동기식으로 실행된다.

* doWork()의 리턴타입인 Result는 작업에 관해 WorkManager에게 알린다.
1. Result.success() : 성공
2. Result.failure() : 실패
3. Result.retry() : 나중에 재시도 해야 함.

{% highlight kotlin %}
class UploadWorker(appContext: Context, workerParams: WorkerParameters)
        : Worker(appContext, workerParams) {

        override fun doWork(): Result {
            uploadImages()
            return Result.success()
        }
    }
{% endhighlight %}

* 작업실행 방법 및 시기 구성 : WorkRequest에서 정의한다. 일회성이거나 주기적이다. 일회성인 경우 OneTimeWorkRequest,
주기적 작업인 경우 PeriodicWorkRequest를 사용한다. 제약조건, 입력값, 지연정책, 재시도정책 등을 추가적으로 설정할 수 있다.

{% highlight kotlin %}
//  일회성 WorkRequest생성
val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .build()
{% endhighlight %}

* WorkRequestfmf 생성하고 WorkManager에 enqueue해준다.

{% highlight kotlin %}
WorkManager.getInstance(myContext).enqueue(uploadWorkRequest)
{% endhighlight %}