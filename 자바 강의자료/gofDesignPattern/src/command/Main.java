package command;

import java.util.*;

/*
 * 커맨드 패턴
 * 	명령의 객체화 - 싱글 메소드 인터페이스
 */
public class Main {

	public static void main(String[] args) {
		/*
		List<Command> list = new LinkedList<>();
		list.add(new Command() {
			
			@Override
			public void execute() {
				System.out.println("작업 1");
				
			}
		});
		list.add(new Command() {
			
			@Override
			public void execute() {
				System.out.println("작업 2");
				
			}
		});
		list.add(new Command() {
			
			@Override
			public void execute() {
				System.out.println("작업 3");
				
			}
		});
		
		for (Command command : list) {
			command.execute();
		}
		*/
		
		PriorityQueue<Command> queue = new PriorityQueue<Command>();
		queue.add(new StringPrintCommand("ABCD"));
		queue.add(new StringPrintCommand("ABC"));
		queue.add(new StringPrintCommand("AB"));
		queue.add(new StringPrintCommand("A"));
		
		for (Command command : queue) {
			command.execute();
		}
	}
}