---
title: "[Algorithm] 순열 생성하기"
categories:
    - Algorithm
---
★아래 코드는 MIN부터 MAX까지의 자연수의 집합에서 가능한 모든 순열을 구하여 출력하는 알고리즘이다.

★list에는 순열을 저장하고, using에는 각 수를 사용중인지, 아닌지를 저장한다.

★부분 집합 생성하기 문제에서는 수를 1씩 증가시키며 이를 넣을지 말지를 정했다면, 이 문제는 각 자릿 수에 어떤 수를 넣을지를 정하는 방식이다.

★기본중의 기본 알고리즘이니 외우자.

public class Main {
	
	private static ArrayList<Integer> list = new ArrayList<Integer>();
	private static final int MIN = 1;
	private static final int MAX = 3;
	private static boolean[] using = new boolean[MAX + 1];
	
	public static void main(String[] args) {
		permutation();
	}
	
	static void permutation() {
		if (list.size() == MAX - MIN + 1) {
			printList();
			return;
		}
		
		for (int i = MIN; i < MAX + 1; i++) {
			if (using[i]) continue;
			using[i] = true;
			list.add(i);
			permutation();
			
			using[i] = false;
			list.remove((Integer)i);
		}
	}
	
	static void printList() {
		if (list.size() == 0) {
			System.out.println("empty");
			return;
		}
		
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + " ");
		}
		System.out.println();
	}
}