package memento;

import java.util.Stack;

import memento.abc.Memento;
import memento.abc.Originator;

/*
 * 메멘토 패턴
 * 
 * 객체(Originator)의 상태를 객체(Memento)로서 저장하고 이전상태로 복구한다.
 * 
 * 접근 제한자 protected의 사용을 이해 한다.
 * 	다른 패키지로 Originator와 Memento를 빼둬서
 * 	정해놓은 규칙 이외에 조작을 금지한다.
 * 
 */
public class Main {
	public static void main(String[] args) {
		
		Stack<Memento> mementos = new Stack<>();
		
		Originator originator = new Originator();
		originator.setState("state 1");
		mementos.push(originator.createMemento());
		originator.setState("state 2");
		mementos.push(originator.createMemento());
		originator.setState("state 3");
		mementos.push(originator.createMemento());
		originator.setState("state final");
		mementos.push(originator.createMemento());
		
		
		originator.restoreMemento(mementos.pop());
		System.out.println(originator.getState());
		originator.restoreMemento(mementos.pop());
		System.out.println(originator.getState());
		originator.restoreMemento(mementos.pop());
		System.out.println(originator.getState());
		originator.restoreMemento(mementos.pop());
		System.out.println(originator.getState());
	}
}