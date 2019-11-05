---
title: "[Algorithm] 이진 탐색"
categories:
    - Algorithm
---
★이진 탐색은 정렬된 배열에 특정한 요소가 존재하는지 찾는 탐색 방법이다. 처음에는 전체 배열의 가장 가운데 값이 우리가 찾는 값인지를 확인한다. 찾는 값이 맞다면 탐색을 끝내고 만약 우리가 찾는 값이 가운데 값보다 크면 가운데 값의 오른쪽 배열을, 작다면 왼쪽 배열을 다시 탐색한다. 이 것을 반복하며 O(log n)시간만에 탐색할 수 있다.

private static int[] arr = {1, 2, 4, 6, 7, 9, 10, 11, 12, 15};
	
public static void main(String[] args) {
	int key = 15;
	boolean result = binSearch(key, 0, arr.length - 1);
	System.out.println(result);
}

private static boolean binSearch(int key, int left, int right) {
	int mid = (left + right) / 2;
	if (key == arr[mid]) {
		return true;
	} else if (left == right) {
		return false;
	} else if (key > arr[mid]) {
		return binSearch(key, mid + 1, right);
	} else {
		return binSearch(key, left, mid - 1);
	}
}