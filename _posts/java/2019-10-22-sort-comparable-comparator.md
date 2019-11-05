---
title: "[Java] 기본적인 정렬(sort, Comparable, Comparator)"
categories:
    - Java
---
★배열의 정렬

private static int[] arr = {81, 91, 46, 32, 45, 64, 15, 24, 93, 11};
Arrays.sort(arr);	//	전체 오름차순 정렬
Arrays.sort(arr, 0, 5);	//	0번째부터 4번째 까지만 정렬

★List 인터페이스 구현체의 정렬

ArrayList<String> list = new ArrayList<String>();
		list.add("사과");
		list.add("딸기");
		list.add("포도");
		list.add("바나나");

Collections.sort(list);	//	전체 오름차순 정렬, 참고로 String은 사전순이다.

★Comparator객체를 커스텀해 정렬 기준을 설정한다. 이를 이용하면 값을 직접 비교할 수 없는 객체 자료형도 정렬할 수 있다.

★객체 자료형을 정렬하는 또 다른 방법은 자료형을 Comparable<T>의 구현체로 만드는 것이다. Comparale<T>의 compareTo(T other)를 오버라이드 해 정렬 방법을 명시한 후, 위와 똑같이 Collections.sort()를 사용한다.

public class People implements Comparable<People> {
	private String name;
	private int age;
	
	@Override
	public int compareTo(People o) {
		return age - o.age;	//	결과가 양수이면 자리를 바꾼다
	}

	...
}

★만약 여러개의 정렬기준을 사용하고 싶으면 커스텀 클래스(People) 내부에 Comparator인스턴스를 static으로 만들어 준다. static으로 주는 이유는 각 클래스별로 하나의 인스턴스만 생성하기 위해서. 위의 Comparable방법과 혼용하여 기본적으로는 compareTo()에 명시된 대로 정렬하다가 특수한 순간에 Comparator에 정의한 대로 정렬하도록 할 수도 있다.

public class People {
	private String name;
	private int age;
	
	public static Comparator<People> asc = new Comparator<People>() {
		@Override
		public int compare(People o1, People o2) {
			return o1.getAge() - o2.getAge();	//	결과가 양수이면 자리를 바꾼다
		}
	};
	
	public static Comparator<People> desc = new Comparator<People>() {
		@Override
		public int compare(People o1, People o2) {
			return o2.getAge() - o1.getAge();
		}
	};

	...
}