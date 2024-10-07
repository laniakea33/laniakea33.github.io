
---
title: "테스트 자동화 도구 정리"
categories:
    - Android
---
## Unit Test

### JUnit(JUnitRunner)
### Robolectric
테스트용 안드로이드 환경을 JVM위에 제공한다. 테스트는 해당 안드로이드 환경 위에서 돌아가며, 실제 앱을 실행하지 않고도 Activity의 생명주기에 따른 UI테스트 등 프레임워크에 종속되는 테스트를 진행할 수 있다. 단 실제 기기가 아니므로 시스템 서비스 등에는 제약이 있는것 같다.

단 Application객체와 Activity 등 서로 참조하는 상태가 되기 쉬운 컴포넌트들은 추상화를 통해 서로의 테스트에 지장이 없도록 구현되어야 한다. 예를 들어 Application 객체에서 시스템 서비스를 통해 어떤 값을 가져오고, 이것을 Activity에서 사용한다고 했을 때 이 Activity를 테스트하기 위해서는 Application객체가 값을 제공할 때 시스템 서비스에 접근하지 않고 테스트용으로 미리 정의된 값만을 제공할 수 있도록 Mocking해야 하고, Mock Application객체를 사용하기 위해 미리 추상화가 되어 있어야 한다.

~~~
robolectric 테스트는 꼭 직접 해볼 것
~~~

## UI Test

### Espresso(AndroidJUnitRunner)
### UIAutomator(AndroidJUnitRunner)
###  Android Test Orchestrator

## 외부 프레임워크

### UIAutomator2
### appium


## 이것은 무엇?

ActivityScenario