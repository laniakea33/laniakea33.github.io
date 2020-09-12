---
title: "Naver Tech Conconcert - 안드로이드 디버깅 꿀팁"
categories:
    - Android
---
에뮬레이터 100% 활용하기

스냅샷 찍을 수 있음


개발자 옵션에서 키면 좋은 것들

enable View attribute inspection을 on -> layout inspector로 조회 가능
strict mode enabled : 메인스레드에서 비용 많이 들어갈때 깜빡거림
always show crash dialog : 충돌 다이얼로그 항상 표시. OS정책이 잘 안보여 주는 쪽으로 감
logger buffer size는 크게
wireless debugging : Android 11부터 무선 연결 간편해짐
App Compatibility Changes : Android11 호환성 테스트


App기능 안정성 테스트

LeakCanary
Android Profiler
Thread dump : 안스에 있는 카메라 버튼은 스레드 덤프임
    adb shell ps -T | grep <PID>
    adb shell ps -T | grep <PID> | wc -l
Stetho
Fill RAM memory
Fill Device Disk



Android Framework 분석

주로 getSystemService로 접근
주로 시스템서버 프로세스에서 돌아감
앱과 시스템 서버에서 제공하는 시스템 서비스는 XXXManager - XXXManagerService로 이름이 매핑되어 있음.
바인딩 과정에서 앱쪽에는 프록시, 시스템 서버에는 스텁이 있음. AIDL로 명시되어 있고, 바인딩하기 전의 전처리 코드에 해당함.

Dexplorer
Apk Extractor - apk파일 추출





네트워크 프록시
Charles
Fiddler
WireShark
BurpSuite





Network Connections : 현재 접속된 네트워크 분석

Developer Assistant : 화면상에 보이는 앱의 레이아웃 구성을 확인할 수 있게 해줌



Native Libs Monitor



OpenSource License에서 대략적인 기술스택 활용가능.




놓치기 쉬운 안드로이드 UI디테일?

XML 코드는 주로 리소스에 많이 사용됨
머티리얼 디자인 가이드상 터치영역은 최소 48 x 48dp 이상
이미지 뷰에 터치(리플같이)효과를 주려면 background말고 foreground로 배경 설정하면 댐(api23이상)
android:tint >> 색을 넣으면 이미지 하나로 다양한 곳에서 다양한 색상을 입힐 수 있음
rootView말고 windowBackground를 white로 할 것
ripple에 mask설정으로 효과 모양을 다르게 줄 수 있다.
selector, level-list, layer-list, clip을 사용하면 동적인 drawable을 만들 수 있다.