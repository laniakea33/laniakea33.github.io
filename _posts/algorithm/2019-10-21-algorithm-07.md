---
title: "[Algorithm] 병합 정렬(Merge sort)"
categories:
    - Algorithm
---
★재귀호출을 이용한 정렬방식. 

★배열을 원소의 갯수가 1개가 될때 까지 분할한 후, 정렬하며 병합한다.

★자세한 원리는 검색하자...왜냐면 그림 붙이는 법을 몰라서

★병합 정렬은 다음의 순서로 진행된다.
1. 부분 배열의 원소가 1개라면 아무것도 하지 않는다.
2. 부분 배열의 왼쪽위치, 오른쪽 위치를 이용해 가운데 위치를 구한다.
3. 가운데 위치를 기준으로 부분 배열을 반으로 가른다.
4. 반으로 나눠진 두 부분 배열을 정렬하며 합병한다.
5. 합병된 부분 배열들을 또 합병하는 방식으로 전체 배열을 완성한다.

★기본적인 정렬 중 하나이고, 분할정복 알고리즘의 원리를 이용한 정렬 방법이다. 외우자.

public static void main(String[] args) {
		mergeSort(arr, 0, arr.length - 1);
		print(arr);
}

//	원본 배열과 분할될 부분 배열의 범위(인덱스)를 전달한다.
//	이 때 분할은 실제로 분할하여 저장하지 않고 left와 right값을 사용하여 가상으로 나눠만 놓는다.
//	재귀호출로 나누는 작업이 끝나면 병합작업이 시작된다.
private static void mergeSort(int[] arr, int left, int right) {
	if (left == right) return;
	int mid = (left + right) / 2;
	mergeSort(arr, left, mid);
	mergeSort(arr, mid + 1, right);
	merge(arr, left, mid, right);
}

//	분할된 두 부분 배열을 합체한다.
//	두 부분 배열을 합한 크기만큼의 임시배열 tmp에 두 부분배열을 정렬하여 합병한다.
//	합병이 끝나고 tmp가 완성되면 원래의 자리인 left부터 right까지 넣는다.
private static void merge(int[] arr, int left, int mid, int right) {
	int[] tmp = new int[right - left + 1];
	int iLeft = left;
	int iRight = mid + 1;
	int i = 0;
	
	while (iLeft < mid + 1 && iRight < right + 1) {
		if (arr[iLeft] > arr[iRight]) tmp[i++] = arr[iRight++];
		else tmp[i++] = arr[iLeft++];
	}
	
	if (iLeft < mid + 1) {
		while (iLeft < mid + 1) tmp[i++] = arr[iLeft++];
	} else {
		while (iRight < right + 1) tmp[i++] = arr[iRight++];
	}
	
	for (i = 0; i < tmp.length; i++) arr[i + left] = tmp[i];
}

★시간 복잡도 : 위 코드는 두 부분으로 나눌 수 있는데, 첫 번째는 main함수에서 mergeSort()를 호출하여 mergeSort()가 재귀적으로 계속 호출되는 부분(분할), 두 번째는 mergeSort()내부의 mergeSort()가 종료되고 merge()가 재귀적으로 계속 호출되는 부분(정복)이다. 분할 부분에서는 단계마다 부분 배열의 크기가 반으로 줄어드므로 시간 복잡도는 O(log n)이고, 정복 단계에서는 두 부분 배열이 이미 정렬되어 있기 때문에 선형 시간인 O(n)시간이 걸린다. 분할된 횟수만큼 정복을 진행하므로 총 시간 복잡도는 O(n log n)이다.