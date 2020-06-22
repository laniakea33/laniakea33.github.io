---
title: "Module, Package"
categories:
    - Python
---
* 외부 모듈을 사용하는 키워드는 자바와 같이 import이다. 파일이름에 확장자는 붙이지 않는다.
* 경로는 절대경로, 현재 모듈을 기준으로 한 상대경로를 사용할 수 있다(현재경로는 '.', 상위경로는 '..'로 표기)
{% highlight python %}
import calc //  calc.py파일을 임포트
calc.add(1,2)   //  임포트한 모듈의 이름을 앞에 붙혀줘야 함.

from calc import add //  calc.py파일의 add만 임포트
from calc import add, sub
from calc import *
add(1,2)   //  이렇게 임포트 한 경우 모듈이름은 빼도 된다.
{% endhighlight %}

* __name__ 변수 : 파이썬의 __name__ 변수는 파이썬이 내부적으로 사용하는 특별한 변수 이름이다. 
만약 C:\doit>python mod1.py처럼 직접 mod1.py 파일을 실행할 경우 mod1.py의 __name__ 변수에는
 __main__ 값이 저장된다. 하지만 파이썬 셸이나 다른 파이썬 모듈에서 mod1을 import 할 경우에는 
 mod1.py의 __name__ 변수에는 mod1.py의 모듈 이름 값 mod1이 저장된다.

 * python 3.3 아래로는 패키지의 경로마다 __init__.py파일을 만들어 줘야 한다. 이 파일은 해당 경로가
 패키지의 일부임을 나타낸다. 이 파일에 __all__ = ['모듈명']를 넣어주면 *로 디렉토리를 import했을 때
 여기에 써 있는 모듈만 사용할 수 있게 해준다.