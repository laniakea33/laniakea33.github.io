---
title: "[Algorithm] List - LinkedList"
categories:
    - Algorithm
---
★앞의 노드와 뒤의 노드를 레퍼런스로 연결하여 리스트를 만드는 방식이다. 연속된 공간인 배열에 저장하지 않고, 메모리 상의 위치에 상관없이 리스트를 구현한다.

{% highlight java %}
public class MyLinkedList<T> {
	
	private Node head;
	private Node tail;
	private int size = 0;
	
	public MyLinkedList() {
		head = null;
	}

	//	리스트의 맨 뒤에 넣는다.
	public void addLast(T v) {
		Node newNode = new Node(v);
		if (size == 0) {
			head = newNode;
			tail = newNode;
		} else {
			tail.next = newNode;
			tail = newNode;
		}
		size++;
	}
	
	//	리스트의 특정 위치에 넣는다.
	public void add(T v, int index) {
		if (index < 0 || index > size) {
			throw new RuntimeException("Index Out Of Bound");
		}
		Node newNode = new Node(v);
		if (index == 0) {	//	리스트의 맨 앞일 때
			newNode.next = head;
			head = newNode;
		} else if (index == size) {	//	리스트의 맨 뒤일 때
			addLast(v);
		} else {	//	리스트의 가운데 어딘가일때
			Node pre = getNode(index - 1);
			Node next = getNode(index);
			pre.next = newNode;
			newNode.next = next;
		}
		size++;
	}
	
	private Node getNode(int index) {	//	특정 위치의 Node를 찾는다.
		Node result = head;
		for (int i = 0; i < index; i++) {
			result = result.next;
		}
		return result;
	}
	
	public void remove(int index) {
		if (index < 0 || index >= size) {
			throw new RuntimeException("Index Out Of Bound");
		}
		
		if (index == 0) {	//	리스트의 맨 앞일 때
			head = head.next;
		} else if (index == size - 1) {	//	리스트의 맨 뒤일 때
			Node pre = getNode(index - 1);
			pre.next = null;
			tail = pre;
		} else {	//	리스트의 가운데 어딘가일 때
			Node pre = getNode(index - 1);
			Node next = pre.next.next;
			pre.next = next;
		}
		
		size--;
	}
	
	public T get(int index) {
		return (T)getNode(index).data;
	}
	
	public int getSize() {
		return size;
	}
	
	public void print() {
		Node node = head;
		System.out.print("[" + head.data + " -> " + tail.data + "]");
		while (node != null) {
			System.out.print(node.data + " ");
			node = node.next;
		}
		System.out.println();
	}
	
	private class Node {
		private T data;
		private Node next;
		
		public Node(T data) {
			this.data = data;
			this.next = null;
		}
	}
}
{% endhighlight %}

★시간 복잡도
1. add() : O(1)
2. get() : O(n), 찾는 노드가 나올 때 까지 연결된 노드를 모두 거쳐야 한다.
3. remove() : O(1), 삭제할 노드의 전, 후 노드를 서로 연결해주고 해당 노드를 삭제하면 되므로 ArrayList처럼 재배치에 드는 시간이 없다. 이는 삭제할 노드의 레퍼런스를 알고있다는 가정 하에 드는 시간이다. 레퍼런스를 모른다면 그 레퍼런스를 얻는데에 O(n)의 시간이 걸릴 것이다.
4. contains() : O(n)

★ ArrayList와의 성능상의 차이점은 검색, 삭제에 걸리는 시간에 있으며, ArrayList는 검색에 강점이 있고, LinkedList는 삭제에 강점이 있다. 검색이 많은 경우 ArrayList, 추가/삭제 연산이 많은 경우 LinkedList가 적당하다.