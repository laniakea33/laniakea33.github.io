
---
title: "Esspresso-web 적용"
categories:
    - Android
---
![Alt text](<../../assets/images/스크린샷 2023-07-11 오전 9.50.11.png>)

이러한 에러가 등장하였다.

웹 페이지 내부의 애니메이션들이 메인스레드를 idle상태가 되지 못하도록 막는가? 테스트 어떻게..?

공고뷰가 애니메이션이 없어보이니 한번 이걸로 해보자.

랜딩을 공고뷰로 타게팅 하는 김에 scheme테스트도 진행해보기로 했다.

```
@Test
    fun test_landing_url_open_from_scheme() {
        val webFormIntent = Intent().apply {
            data = Uri.parse(
                "saramin://web?url=https%3A%2F%2Fbrandapp-m.saramin.co.kr%2Fjob-search%2Fview%3Frec_idx%3D46114805"
            )
        }

        activityScenarioRule.launchActivity(webFormIntent)

        onWebView()
            .check(webMatches(getCurrentUrl(), equalTo("https://brandapp-m.saramin.co.kr/job-search/view?rec_idx=46114805")))
    }
```

오잉... 그러나 이번에는 메인 뷰의 스플레시 화면이 사라지지 않는다. show, hide처리가 애니메이션을 통해 진행되기 때문일 수도 있겠다. 어디선가 듣기로는 테스트 자동화는 기기 애니메이션을 꺼줘야 한다고...

```
android {
    ...
    testOptions {   //  이 속성은 테스트가 진행되는 동안 기기 애니메이션을 사용안함 상태로 변경한다.
        animationsDisabled = true
    }
}
```

애니메이션은 없어졌는데, 메인 뷰의 스플레시 화면이 사라지지 않는건 마찬가지였다. 근데 얼떨결에
```
@get:Rule
    val activityScenarioRule = object : ActivityTestRule<MainActivity>(
        MainActivity::class.java, false, false
    ) {
        override fun afterActivityLaunched() {
            onWebView().forceJavascriptEnabled()
        }
    }
```

ActivityTestRule의 생성자의 마지막 파라미터를 false로 바꿨더니 정상적으로 작동됐다. 맨 위에 뭐 스레드 점유어쩌고 에러도 사라졌다. 이 파라미터가 뭐길래??

파라미터 명은 launchActivity, 즉 각 테스트 함수가 실행되기 전에 Activity를 알아서 실행시켜 줄지 여부를 결정한다. 근데 우리는 진입 Intent를 직접 만들어 호출해야 하므로 false로 하는게 맞다. 근데 여태 true였는데, 이러면 앱에 Intent가 연속적으로 호출되어 액티비티가 여러번 열리게 되어 생명주기가 뭐 어떻게 꼬이나보다. false로 하니까 일단 잘 열린다.

근데 warm start테스트가 가능할 것인가??