---
title: "[Java] Java 01"
categories:
    - Java
---
Stack<Integer> stack = new Stack();
		
		stack.push(44);
		stack.push(54);
		stack.push(111);
		stack.push(10);

        Deque<String> q = new ArrayDeque<String>();
		q.add("초코파이");
		q.add("가나파이");
		q.add("몽쉘통통");
		q.add("버거");
		
		System.out.println(q.peek());
		System.out.println(q);
		
		System.out.println(q.poll());
		System.out.println(q);
		

        //	HashSet
        //	중복되는건 저장안되고 false 반환함
        //	순서는 없지만 순서대로 forEach로 사용 가능

        /*
 * Collection 프레임워크 : 무한한 데이터들을 저장하기 위해 사용하는 정형화된 형식 클래스들의 모음
 * 	ㄴ다형성을 활용하면서 다양한 자료구조를 사용할 수 있도록 자바가 만들어 놓음.
 * 	ㄴ프레임워크란 쉽게 말해 기능들을 잘 정리해서 모아놓은 것들임
 * Set : 비순차적, 중복데이터 허용 안함 (<-순서가 없어서 차례대로 사용할 수가 없으므로 순서를 정해주는 Iterator인터페이스를 사용해야 함)
 * Map : 비순차적, 중복데이터 허용함
 * List : 순차적, 중복데이터 허용함
 * 
 * 셋 다 Collection인터페이스를 상속한 또다른 인터페이스들이다.
 * 자바에서는 이 Set, Map, List인터페이스를 구현한 이런저런 클래스들을 사용할 수 있다.
 */
public class Main {
	public static void main(String[] args) {
		//	HashSet은 Set인터페이스를 구현한 클래스임, <>안에는 Set에 넣을 객체의 클래스 타입을 정해준다.
		//	클래스타입을 정해주지 않으면 여러가지 클래스 객체가 무작위로 들어갈수 있으므로 문제(ClassCastException)가 발생할 수 있다.
		//	이런 방법은 모든 Collection객체에 사용하며, 이렇게 자료형을 <>안에 클래스 타입을 정해주는 방법을 제네릭(Generic)이라고 함

		//	Set인터페이스는 순서가 없으므로 순서를 사용할 수 있게 Iterator객체를 만들어 가져옴
		//	Iterator는 처음에 -1번째 인덱스를 가리키고 있으며 hasNext()함수는 다음 자료가 존재하면 true, 현재 마지막 자료의 인덱스를 가리키고 있으면 false를 반환함.
		Iterator<Object> it = hs.iterator();
		while(it.hasNext()) {
			//	next()함수는 다음 자료의 값을 가져옴
			System.out.println(it.next());
		}

		/*
 * List 인터페이스
 * ArrayList, vector, LinkedList...
 * 배열 취급 하면 됀다.
 * 배열과 다르게 처음에 크기를 정해주지 않아도 됀다.
 * Person관리 프로그램을 리스트를 이용하여 작성해보자
 */

 /*
 * HashMap : 키 - 값 쌍으로 저장한다. Iterator대신 Enumeration을 사용해서 순차화 시킨다.
 * 값은 중복될 수 있으나 키는 중복될 수 없다.
 * get(키) 함수로 값을 꺼내올 수 있다.
 * 
 * 로또 1~100회 추첨번호 통계 구하기
 * ㄴ출력모양은 자유
 * ㄴ맵 활용<K(회차), V(당첨번호)>
 * ㄴ임의 당첨 번호 생성 + 당첨 번호 통계 생성
 */

 		//	Integer클래스를 저장하는 HashSet객체를 만든다.
		//	int같은 기본자료형은 객체가 아니기 때문에 객체처럼 다룰 수가 없다.
		//	그래서 int를 객체처럼 다루기 위한 포장 클래스가 Integer클래스임
		//	이런 방식을 Auto Boxing이라고 함.
		HashSet<Integer> nums = new HashSet<Integer>();
		Random r = new Random();
		
		//	nums안에 중복자료는 저장되지 않으므로 6개가 꽉찬다면 모두 중복되지 않은 숫자 6개가 들어갔다는 의미
		while (nums.size() < 6) {
			//	계속 추가해줌
			nums.add(r.nextInt(45)+1);
		}
		//	Iterator를 사용하지 않으면 순서대로 꺼내올 수가 없으므로 set이나 map에서는 꼭 사용하고, 사용할수 있도록 만들어져있음
		//	Iterator는 두가지 함수를 가짐
		//	hasNext() : 현재 인덱스에서 다음 내용이 있으면 true, 없으면 false를 반환
		//	next() : 다음 인덱스를 가져옴. 이때 반환되는 객체는 처음 선언할때 <>안에있는 그 클래스의 객체임
		
		
		Iterator<Integer> iterator = nums.iterator();
		while(iterator.hasNext()) {
			Integer curNum = iterator.next();
			System.out.println(curNum);
		}
		/*
		//	Iterator말고 이렇게 써도 됨. for Each문은 자동적으로 내부에서 Iterator를 사용함
		for(Integer i : nums) {
			System.out.println(i);
		}
		*/
		

		/*
 * Person클래스를 하나 만들어서 같은 값을 준 뒤 중복데이터를 어떻게 처리하는지 아라보자.
 */

public class Main {

	public static void main(String[] args) {
		HashSet<Person> hs = new HashSet<Person>();
		hs.add(new Person("강덕현", 12));
		hs.add(new Person("강덕현", 12));
		hs.add(new Person("강덕현", 12));
		
		Iterator<Person> iterator = hs.iterator();
		while (iterator.hasNext()) {
			Person p = iterator.next();
			System.out.println(p);
		}
		
		//	놀랍게도 다 같은 값인데 모두 중복 저장 되었다.
		//	왜냐면 Set인터페이스는 Object클래스에 정의된 hashCode()값으로 객체를 구분하는데, 지금 만든 객체들은 hashCode값이 모두 달라서
		//	다른 객체로 취급되기 때문이다.
		//	그래서 hashCode를 name과 age만을 반영하도록 오버라이드 해줬음(hashCode계산식은 정해진 방식이 있는 듯)
		//	name과 age가 모두 같으므로 name과 age로 만든 hashCode도 같아졌으니 Set인터페이스가 모두 같은 객체로 취급해서
		//	더이상 중복저장 되지 않는다.
		//	hashCode가 같은 객체라면 equals()까지 호출해서 같은지 다른지를 한번 더 판단한다.
		//	그러니까 같은 객체가 되려면 hashCode와 equals()에 정의된 조건까지 만족시켜야 한다.
		//	equals와 hashCode는 항상 함께 변경해야 한다는 그런 말.
	}