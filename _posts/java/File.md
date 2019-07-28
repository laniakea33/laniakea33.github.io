---
title: "[Java] File, IO"
categories:
    - Java
---
★File 클래스 : 파일을 참조하는 객체이다. 사용법은 메소드 이름보면 됌

★File IO<br>
...Stream : 1바이트단위로 움직임(기본 단위), 바이너리 입출력이라고도 함<br>
...Reader/Writer : 2바이트 단위로 움직임(문자 단위)
{% highlight java %}
FileOutputStream fos1 = new FileOutputStream(FileDescriptor.out);
//	FileDescriptor : 파일 설명자
//	FileOutputStream의 생성자에 목적지를 지정한다.
//	FileDescriptor.out : 출력할 장소를 콘솔창으로 지정하는 것

fos1.write(65);
byte[] b = new byte[] {'h','e','l','l','o'};
fos1.write(b);
fos1.write(b, 0, 4);
{% endhighlight %}

{% highlight java %}
File file = new File("test.txt");
FileOutputStream fos2 = new FileOutputStream(file);
//	출력할 장소를 file로 지정하였다.

fos2.write(65);
fos2.write(b);
fos2.write(b, 0, 4);

//	데이터가 크면 클수록 비효율적이다.
{% endhighlight %}

★Buffer : 임시 저장공간에 담았다가 한꺼번에 출력할 때 사용. 대용량 데이터를 입출력하기 효율적이게 된다.
{% highlight java %}
FileOutputStream fos1 = new FileOutputStream(FileDescriptor.out);
BufferedOutputStream bos1 = new BufferedOutputStream(fos1, 10);
//	생성자에있는 숫자는 버퍼의 크기, 버퍼의 크기를 초과했을때 넘치는 부분은 출력이 된다.
for(int i = 0; i < 11; i++) {
	bos1.write(65);
	//	write해도 버퍼에 담을 뿐이라 출력이 안됌
	if(i % 3 == 0) {
		bos1.write('\n');
		bos1.flush();	//	버퍼를 비운다.
	}
}
//	flush를 하지 않으면 버퍼가 넘치거나 스트림이 close될때 버퍼가 비워진다.
bos1.close();	//	스트림을 닫는 메소드
{% endhighlight %}

{% highlight java %}
File file = new File("test.txt");
FileOutputStream fos2 = new FileOutputStream(file, false);
//	true : 파일에 내용을 추가함, false : 내용을 교체함
BufferedOutputStream bos2 = new BufferedOutputStream(fos2, 10);
for(int i = 0; i < 11; i++) {
	bos2.write(65);
	//	write해도 버퍼에 담을 뿐이라 출력이 안됌
	if(i == 100) {
		bos2.flush();	//	버퍼를 비운다.
	}
}
bos2.close();
{% endhighlight %}



//FileInputStream fis1 = new FileInputStream(FileDescriptor.in);
		//int n = isr1.read();	//	10이라고 입력했을때, 1바이트씩 읽어오기 때문에 처음의 1만 가져온다. 그리고 아스키 코드를 리턴
		//System.out.println(n);
		
		File file = new File("test.txt");
		FileInputStream fis2 = new FileInputStream(file);	//	InputStream의 빨대를 file에다가 꽂음
		//	1바이트씩 읽어오고, 읽을때 마다 커서가 다음으로 이동한다.
		//	더 이상 읽을 값이 없으면 -1를 출력한다.
		while(true) {
			int n = fis2.read();
			if(n == -1) {
				break;
			}
			System.out.println(n);
		}
		fis2.close();
		
		//	1바이트씩 읽어오기때문에, 2바이트의 문자를 콘솔에 출력하면 글자가 깨진다.
		//	근데 파일을 쓰거나 복사하거나 할 때는 크게 상관이 없다. 왜냐면 확인하는 과정에서 깨지는 것이기 때문.
		//	하지만 쓸 때와 마찬가지로 대용량 데이터를 읽을때는 굉장히 비효율적이다.





/*
 * 버퍼를 사용해서 읽기
 */
public class Main {

	public static void main(String[] args) throws IOException {
		File file = new File(".\\test.txt");
		FileInputStream fis2 = new FileInputStream(file);
		BufferedInputStream bis2 = new BufferedInputStream(fis2, 1024);
		byte[] buffer = new byte[1024];
		
		int readSize = 0;
		while((readSize = bis2.read(buffer)) != -1) {
			System.out.println("사이즈 : " + readSize);
			
			for(int i = 0; i < readSize; i++) {
				System.out.print((char)buffer[i]);
			}
			
		}
		fis2.close();
	}
}


파일 카피
public class Main {

	public static void main(String[] args) {
		//	파일 객체 만듬
		File ref = new File("test.jpg");
		File target = new File("test-c.jpg");
		
		//	변수 선언
		FileInputStream fis = null;
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			//	복사받을 타겟 파일이 없으면 하나 만든다.
			target.createNewFile();

			fis = new FileInputStream(ref);
			fos = new FileOutputStream(target);
			//	입출력 버퍼크기 512바이트로 지정, 즉 한덩어리에 512바이트씩 복사한다
			bis = new BufferedInputStream(fis, 512);
			bos = new BufferedOutputStream(fos, 512);
			
			//	가져온 한 덩어리를 저장할 바이트어레이, 똑같이 512로 만든다
			byte[] buffer = new byte[512];
			
			int size = 0;
			//	한 덩어리씩 퍼다 올린다. 이때 퍼올린 한 덩어리의 크기가 size가 되고, 내용물은 buffer에 저장된다.
			//	size가 -1이면 더이상 가져올 데이터가 없다는 의미이므로 종료시킨다.
			while((size = bis.read(buffer)) != -1) {
				//	파일에 꽂혀있는 빨대에다가 buffer한덩어리를 배출한다.
				//	데이터가 512바이트 이하이면 쓰레기들로 남은 공간이 채워져 있기 때문에 size만큼만 배출한다. 
				bos.write(buffer, 0, size);
			}
			//	출력버퍼에 남아있는 나머지 파일을 배출한다.
			bos.flush();
		} catch (IOException e) {
			System.out.println("입출력중 오류 발생");
			e.printStackTrace();
		} finally {
			try {
				//	전부 닫아줌
				bis.close();
				bos.close();
				fis.close();
				fos.close();
			} catch (IOException e) {
				System.out.println("스트림 폐쇄중 오류 발생");
				e.printStackTrace();
			}
		}
	}
}




/*
 * DataOutputStream
 * 
 * 참고로 스트림객체 같이 new 안에 new 안에 new 써주는 이런 패턴을
 * 데코레이션 패턴이라고 함
 */
public class Main {

	public static void main(String[] args) {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			fos = new FileOutputStream(new File("test.txt"));
			dos = new DataOutputStream(fos);
			//	덩어리 형태로 입력함
			//	반드시 자료형에 맞게 넣어줘야 하고, 출력도 자료형과 정확히 맞아야 한다.
			dos.writeInt(100);
			dos.writeDouble(3.14);
			dos.writeUTF("안녕하세용");
			dos.writeChar('가');
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}



/*
 * DataOutputStream
 * 
 * 참고로 스트림객체 같이 new 안에 new 안에 new 써주는 이런 패턴을
 * 데코레이션 패턴이라고 함
 */
public class Main {

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(new File("test.txt"));
			//	덩어리형태로 출력함, 자료형 별로 덩어리가 다름
			DataInputStream dis = new DataInputStream(fis);
			//	집어 넣은 형태로 그대로 가져와야 함, 입력할때 형태 그대로 출력해야 함
			System.out.println(dis.readInt());
			System.out.println(dis.readDouble());
			//	이건 문자열
			System.out.println(dis.readUTF());
			System.out.println(dis.readChar());
			dis.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

/*
 * 객체를 파일에 입력하기
 */
public class Main {
	public static void main(String[] args) throws IOException{
		Info info1 = new Info("홍길동", 20);
		
		File f = new File("test.txt");
		FileWriter fw = new FileWriter(f);
		PrintWriter pw = new PrintWriter(fw);
		pw.println(info1.name);
		pw.println(info1.age);
		pw.close();
	}
}




/*
 * 객체 입출력
 * 객체는 입출력을 위해 직렬화작업이 필요하다
 * 직렬화 가능하게 객체를 Serializable인터페이스를 구현하게 만든다.
 */
public class Main {
	public static void main(String[] args) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File("test.txt")));
			oos.writeObject(new Info("강덕현", 28, "경남 양산시 물금읍 범구4길 6 보라주택 202호"));
			oos.writeObject(new Info("이태관", 28, "율리어딘가..."));
			oos.writeObject(new Info("허석현", 28, "해병대"));
			
			ois = new ObjectInputStream(new FileInputStream(new File("test.txt")));
			Info info = null;
			while (true) {
				info = (Info)ois.readObject();
				System.out.println(info.toString());
			}
		} catch (EOFException e) {
			System.out.println("다 읽음");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				ois.close();	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}




/*
 * FileWriter, FileReader를 이용한 메모장
 * 입력한 한글 문자들을 파일에 저장한다.
 * 엔터를 두번 입력하면 입력 종료.
 * 파일에 저장된 한글 문자들은 콘솔에 출력한다.
 */
public class Main {

	public static void main(String[] args) {
		Scanner sc = null;
		FileWriter w = null;
		FileReader r = null;
		try {
			File file = new File("memo.txt");
			
			sc = new Scanner(System.in);
			//	new FileWriter()를 호출한것 만으로 파일의 내용이 싹 날라감
			w = new FileWriter(file);
			
			while(true) {
				String str = sc.nextLine();
				if (str.isEmpty()) {
					break;
				}
				w.write(str + "\n");
			}
			//	close()해야만 완전히 저장이 된다.
			w.close();
			
			System.out.println("=======================================================\n메모 내용 : ");
			r = new FileReader(file);
			int readSize = 0;
			while ((readSize = r.read()) != -1) {
				System.out.print((char)readSize);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sc.close();
				w.close();
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

