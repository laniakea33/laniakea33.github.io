---
title: "[Algorithm] 계수 정렬(Counting sort)"
categories:
    - Algorithm
---
★배열의 원소를 직접 비교해 정렬하는 것이 아니라, 또 다른 배열에 정렬할 배열의 수를 입력한다. 그 후 갯수만큼 출력한다.

★출력하는 시간을 제외하면 배열의 1번만 루프하면 되므로 O(n)시간에 끝낼 수 있으나, 배열의 원소의 값이 0이상이어야 하고, 원소의 크기의 상한을 알고있어야 한다.

private static int[] arr = {0, 8, 1, 6, 4, 7, 9, 2, 3, 1, 6, 4, 8, 9, 6, 5};
private static int[] counting = new int[10];

public static void main(String[] args) {
	for(int i = 0; i < arr.length; i++) {
		counting[arr[i]] += 1;
	}
	
	print();
}