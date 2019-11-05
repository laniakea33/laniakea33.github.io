---
title: "[Java] Vector와 ArrayList의 차이점"
categories:
    - Java
---
★Vector와 ArrayList는 기본적으로 같은 매커니즘을 사용한다. Vector는 JDK 1때 만들어 졌고, 그 당시에는 List개념이 없었기 때문에 Vector가 애용되어 왔다. Java 1.2에서 ArrayList와 LinkedList가 추가되었고 1.2부터 Vector는 사실상 하위호환성을 위해 유지되고 있다.

★Thread 동기화 : 하나의 데이터에 여러 스레드가 동시에 접근을 한다면 데이터간의 모순과 같은 문제가 생길 수 있다. 하나의 자원을 서로 가져가려고 싸우는 것이다. 이를 위해 자원을 스레드에 안전하게(Thread safe) 동기화 하여 한번에 한 스레드만 접근 가능하게 만들어 주고, 자바에서는 synchronized 키워드를 사용한다. 멀티 스레드 환경에서 이는 데이터의 안정성을 보장해 주지만, 싱글스레드 환경이나 동시에 여러 스레드가 같은 자원에 접근할 일이 없는 경우에 이러한 동기화 작업은 오히려 성능을 저하시키는 문제가 발생한다.

★Vector가 퇴물이 된 이유이다. Vector는 Thread safe를 위해 무조건적인 동기화를 적용했으나 싱글스레드 환경에서 이는 오히려 성능의 저하를 낳았다. 따라서 이런 경우에는 강제적인 동기화 기능이 없는 ArrayList나 Li  nked List를 적용하는 것이 더 효율적이게 되었다.

★ArrayList는 기본적으로 Thread 동기화 되어있지 않지만, 동기화가 필요한 경우에는 Collection.synchronizedList(List list) 메소드를 사용할 수 있다. 다른 Collection타입도 같은 방법으로 동기화 할 수 있다.