---
title: "[Algorithm] List - ArrayList"
categories:
    - Algorithm
---
★ArrayList는 프로그램 실행 동안 배열의 길이를 가변적으로 사용할 수 있게 해주는 Collection 프레임워크의 List를 구현한 구현체 중 하나이다. 기본적으로 배열이지만, 배열의 길이가 꽉 차게 되면 더 큰 새 배열을 만들고 복사하거나 하는 작업을 정의해 두었다. 직접 만들어 보자.

{% highlight java %}
public class MyArrayList<T extends Object> {
	
	private int capacity = 10;
	private Object[] array = null;
	private int size = 0;
	
	public MyArrayList() {
		this.array = new Object[capacity];
	}

	public MyArrayList(int capacity) {
		this.capacity = capacity;
		this.array = new Object[capacity];
	}
	
	//	추가 시 사이즈 터지면 늘려준다.
	public void add(T v) {
		if (size == capacity) extendCapacity();
		array[size] = v;
		size++;
	}
	
	public void add(T v, int index) {
		if (index < 0 || size <= index) {
			throw new RuntimeException("Index Out Of Bound");
		}
		
		if (size == capacity) extendCapacity();
		
		for (int i = size - 1; i >= index; i--) {
			array[i + 1] = array[i];
		}
		
		array[index] = v;
		
		size++;
	}
	
	//	아이템 제거시 아이템 수가 전체 용량의 3분의 1 밑으로 줄면 전체 용량을 반으로 줄였다.
	//	메모리 확보를 위함
	public void remove(int index) {
		if (index < 0 || size <= index) {
			throw new RuntimeException("Index Out Of Bound");
		}
		
		for (int i = index + 1; i < size; i++) {
			array[i-1] = array[i];
			array[i] = null;
		}
		size--;
		
		if (size < capacity/3 && capacity > 10) {
			capacity /= 2;
			Object[] tmp = new Object[capacity];
			for (int i = 0; i < size; i++) {
				tmp[i] = array[i];
			}
			array = tmp;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index < 0 || size <= index) {
			throw new RuntimeException("Index Out Of Bound");
		}
		
		return (T)array[index];
	}
	
	private void extendCapacity() {
		capacity *= 2;
		Object[] tmp = new Object[capacity];
		for (int i = 0; i < size; i++) {
			tmp[i] = array[i];
		}
		array = tmp;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public void print() {
		System.out.print(size + "/" + capacity + " : ");
		if (size == 0) System.out.println("empty");
		else {
			for (int i = 0; i < size; i++) {
				System.out.print(array[i] + " "); 
			}
			System.out.println();
		}
	}
}
{% endhighlight %}

★시간 복잡도
1. add() : O(1)
2. get() : O(1), 주어진 index의 값으로 배열에서 꺼내기만 하면 되어 O(1)의 시간이 걸린다.
3. remove() : O(n), 값을 삭제하면 빈 자리를 메우는 작업에 O(n)의 시간이 걸린다.
4. contains() : O(n)