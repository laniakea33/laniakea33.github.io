---
title: "AsyncTask"
categories:
    - Android
---
★백그라운드 작업을 일일이 워커 스레드로 구현하기 번거롭기 때문에 존재하는 헬퍼 클래스

※ 현재는 @deprecated되었으므로 멀티스레딩을 위해 RxJava나 Coroutine를 배우자.

★별도의 Handler구현 없이 UI를 갱신 할 수 있다.

★짧은시간의 작업에 적당하다. 긴 시간 수행되는 작업은 Executor, FutherTask, ThreadPoolExecutor 등을 사용하는 것이 좋다.

★3가지 제네릭 타입을 가지고 onPreExecute(), doInBackgroune(), onProgressUpdate(), onPostExecute()순으로 작업을 진행한다. 

★사용될 클래스의 Inner class로 사용하는 것이 권장되며 AsyncTask 클래스를 상속받아 구현한다.

★재사용이 불가능 하며 재사용이 필요할 땐 인스턴스를 새로 생성해서 사용한다.

★UI스레드에서 생성, 실행되어야 하며 콜백메소드를 수동호출해서는 안된다.

★액티비티에 종속되지 않게 doInBackground() 수행 중 Activity가 종료될 경우 해당 AsyncTask도 cancel()을 호출해야 하며 cancel()되더라도 현재 진행중인 작업은 끝마치기 때문에 주기적으로 isCancelled()로 확인해줘야 한다.

★AsyncTask는 순차적으로 실행되는 것이 기본이며 병렬처리를 위해 executeOnExcutor(...)를 사용할 수 있다.

★사용 방법
1. AsyncTask상속시 3개의 제네릭 타입을 결정한다. Params는 실행시 execute()의 인자로 받을 파라메터 타입, Progress는 진행상황을 나타내기 위한 타입, Result는 실행 후 반환할 결과 타입이다.

2. 순서대로 오버라이드 한다.
2-1. onPreExecute() : 실행전 준비작업을 한다.(UI 스레드)
2-2. doInBackground() : 작업할 내용들을 작성한다.(워커 스레드)
2-3. onProgressUpdate() : doInBackground()에서 publishProgess()호출시 호출된다.(UI 스레드)
2-4. onPostExecute() : 작업 종료후 실행된다.(UI 스레드)
2-5. onCancelled() : cancel()호출이나 doInBackground()가 종료되었을 때 호출된다.

★doInBackground()는 반드시 오버라이드 해야 한다.

