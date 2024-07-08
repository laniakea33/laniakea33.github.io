---
title: "Coroutine With Android"
categories:
    - Android
---
## 세팅

### app.gradle 디펜던시에 다음을 추가
~~~ groovy
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:$latest_version'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:$latest_version'
~~~

★ Android환경에서 coroutine의 생명주기를 context의 생명주기에 따라 다루기 위해 CoroutineScope을 상속받고, 이를 생명주기에 맞춰 사용하며 Activity가 destroy될 때 Job을 종료하는 것을 추천하고 있다. Job은 CoroutineScope의 동작을 제어하는 역할을 한다.
~~~kotlin
class MyActivity : AppCompatActivity(), CoroutineScope {

  // Job을 등록할 수 있도록 초기화
  lateinit var job: Job

  // 기본 Main Thread 정의와 job을 함께 초기화
  override val coroutineContext: CoroutineContext
      get() = Dispatchers.Main + job

  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      job = Job()
  }

  // 작업 중이던 모든 job의 children을 종료 처리
  override fun onDestroy() {
      super.onDestroy()
      job.cancel() // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
  }
}
~~~

★ jetpack의 viewModel(viewModelScope), activity, fragment(lifecycleScope)에 scope가 정의되어 있기 때문에 쉽게 갖다 쓸 수 있음.

## StateFlow & SharedFlow

https://developer.android.com/kotlin/flow/stateflow-and-sharedflow

##