---
title: "[Java] Socket Programming"
categories:
    - Java
---
★InetAddress : IP주소 관리 클래스이다.
{% highlight java %}
InetAddress ia = InetAddress.getByName("url");
{% endhighlight %}

★ServerSocket : 서버에 접속이 가능한 포트를 찾고, 요청을 관리한다.
{% highlight java %}
ServerSocket ss = new ServerSocket("port number");
//	사용중인 포트번호라면 IOException이 발생한다.
{% endhighlight %}

★Socket : 접속 성공 후 커넥션을 관리
{% highlight java %}
InetAddress ia = InetAddress.getByName("url");
Socket socket = new Socket(ia, port number);
//	ia의 주소에 해당하는 port number에 접속한다.
{% endhighlight %}

★서버 - 클라이언트 접속 플로우
{% highlight java %}
//	1. 서버에서 요청을 받는 포트를 연다
ServerSocket serverSocket = new ServerSocket(54321);

//	2. 서버는 누군가 해당 포트로 접속하길 기다린다.
//	대기하다가 누군가 접속에 성공하면 연결을 관리하는 Socket객체가 반환된다.
Socket socket = serverSocket.accept();

//	3. 클라이언트쪽에서 연결한다
Socket socket = new Socket(ia, 22222);
//	이제 서버와 클라이언트 둘다 Socket객체를 가지게 되었고,
//	이 객체를 이용해 서로 통신한다.

//	4. 클라이언트에서 소켓의 OutputStream을 이용해 메시지를 전송함
PrintWriter pw = new PrintWriter(socket.getOutputStream());
pw.println("메렁");
pw.flush();

//	5. 서버에서 소켓의 InputStream으로부터 메시지를 받는다
Scanner sc = new Scanner(socket.getInputStream());
String data = sc.nextLine();
{% endhighlight %}