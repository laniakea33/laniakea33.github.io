---
title: "[Algorithm] 버블 정렬(Bubble Sort)"
categories:
    - Algorithm
---
★버블정렬 : n개의 원소를 서로 인접한 원소들끼리 비교하여 정렬하는 알고리즘이다. n번의 라운드를 돌며 최대 n번의 비교 및 정렬연산을 수행하므로 O(n^2)의 시간이 걸리며, k번의 라운드가 끝나면 맨 뒤에서 k번째까지의 원소가 정렬된 상태가 된다. 

★버블정렬의 자세한 원리는 네이버 검색을 해보자.

private static int[] bubble(int[] input) {
	int[] result = input.clone();
	
	for (int i = result.length - 1; i > 0; i--) {
		for (int j = 0; j < i - 1; j++) {
			if (result[j] > result[j + 1]) {
				int tmp = result[j];
				result[j] = result[j + 1];
				result[j + 1] = tmp;
			}
		}
	}
	return result;
}