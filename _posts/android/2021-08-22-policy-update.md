---
title: "2021 8월 정책 업데이트 세미나"
categories:
    - Android
---
[정책 업데이트 히스토리](https://support.google.com/googleplay/android-developer/topic/9877065)

# 7월 정책 업데이트

플레이스토어에 데이터 프라이버시 및 보안 세션 추가됨. 개인정보를 앱에서 어떻게 수집하고 공유하며 보호하는지 알려주는 역할을 함. 10월 내로 작성되어야 함.
모든 앱은 개인정보 처리방침을 필수적으로 가지고 있어야 함(22년)

광고ID : 안드 12에서는 사용자가 앱에게 광고ID를 제공할지 안할 지 시스템 차원에서 선택가능하다.(10월)

앱세트ID : 앱 식별자로써 광고 ID 쓰지말라고 만들어 줌. 분석, 사기방지용으로만 쓰셈. 광고관련해서 쓰면 혼난다. 기기에 설치된 같은 개발자가 배포한 앱의 식별자. 사람인&아이엠그라운드 등은 같은 앱 세트ID를 공유. 앱을 1년간 쓰지않으면 리셋된다.(9월)

어린이 타게팅 앱에는 광고식별자로 사용될 수있는 모든 일련번호는 전송이 금지된다.(AAIS, SIM NO, BUILD NO, MAC, SSID, IMEI 등…)

스토어 등록정보 및 프로모션 메타데이터 : 앱 제목 30자 이하, 이모티콘과 특문 반복 금지, 앱 아이콘에 오해의 소지가 있는 기호X, 브랜드명을 제외하면 전체 대문자 표기 금지. 스토어 실적 및 순위, 수상경력 등을 나타내는 앱 아이콘과 텍스트 사용금지. 가격 및 프로모션을 나타내는 (50% off 나 SALE, FREE 100와 같은)문구 사용 역시 금지, Play 프로그램을 나타내는 이미지나 텍스트(NEW, Editor’s choise등) 사용 금지

휴면계정 : 1년 이상 휴면 계정은 폐쇄 및 앱 삭제될 것. 앱 업로드한 적이 없거나, 1년이상 콘솔에 엑세스하지 않은 계정을 말함.

앱 프로모션 : 위반 사례 추가

콘솔 : 데이터 프라이버시 및 보안 섹션 요구사항을 하루빨리 작성할 것. 유효한 데모계정 및 로그인 정보, 앱을 검토하는데 필요한 리소스를 제공해야 한다.


# 지난 주요 업데이트

기존앱은 11월부터 Android 11이상을 타게팅해야하고, 결제 라이브러리 3버전 이상을 사용해야 한다.
And 11에서 And11이상을 타게팅하는 앱의 경우 다른 모든 앱을 쿼리(QUERY_ALL_PACKAGES)하려면 콘솔에 양삭을 작성해야 함. 함부로 이 권한 사용하면 앱 정지될 수 있다.


# 상습적인 위반 사례

소유권이 웹 사이트로의 웹뷰만을 제공하는 앱은 스팸으로 취급된다.
기능과 컨텐츠가 지나치게 유사한 앱은 스팸으로 취급된다.
광고 게재가 주 목적인 앱은 스팸으로 취급된다.
타인으 지적 재산을 침해하지 마세요.
개인정보 및 사용자 데이터 수집 전에 동의를 꼭 받을 것. 동의 전에 이미 수집되고 있으면 안된다.