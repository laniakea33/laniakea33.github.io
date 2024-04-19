
---
title: "크래시 리틱스 실험"
categories:
    - Android
---
1. 파일, 라인넘버가 같으면 Exception의 클래스명이 다르더라도 같은 에러로 판정됨.
2. 파일, 라인넘버가 같더라도 Exception이 발생하는 함수의 이름이 다르면 다른 에러로 판정됨.

결론 : Crashlytics가 에러를 분류하는 기준은

1. 파일명
2. 파일 내에서 Exception객체가 생성되는 라인 넘버(1 + 2가 타이틀)
3. 파일 내에서 Exception객체가 생성되는 함수명(서브 타이틀)
