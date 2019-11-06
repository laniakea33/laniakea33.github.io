---
title: "[Android] 안드로이드의 4대 컴포넌트"
categories:
    - Android
---
★안드로이드의 4대 컴포넌트란 안드로이드 애플리케이션을 구성하는 4가지 구성요소(Activity, Service, Broadcast Receiver, Content Provider)를 말한다. 각각의 컴포넌트들은 독립적으로 각자의 기능을 수행하며 Intent를 이용해 정보를 교환한다.

★Activity : 가장 많이 사용되는 컴포넌트로 하나의 액티비티는 사용자가 보고 상호작용할 수 있는ㄴ 한 페이지의 화면을 말한다. 앱은 최소 하나의 액티비티로 구성되어 있고, 액티비티는 하나이상의 뷰나 뷰그룹을 포함한다. 인텐트를 통해 앱 내/외부의 다른 액티비티를 호출할 수 있다. Activity클래스를 상속받아 구현한다.

★Service : UI없이 기능만을 수행하는 컴포넌트이다. 음악재생이나 네트워크 연결과 같은 화면이 필요없는 작업에 사용될 수 있으며, 기본적으로 백그라운드에서 앱을 실행시키기 위해 사용하는 컴포넌트이다. 사용자가 다른 앱을 사용하는 동안 서비스를 통해 백그라운드에서 음악을 재생시키거나 UI와의 상호작용을 차단하지 않고 네트워크를 통해 데이터를 가져올 수도 있다. 액티비티와 같은 다른 구성요소가 서비스를 시작한 다음 그 구성요소와 서비스를 바인딩 하여 서로 통신을 가능하게 할 수 있다. 기본적으로 서비스를 호출한 컴포넌트와 같은 thread에서 실행되나 서비스 내부에서 새로운 thread를 생성해 실행시킬 수도 있다. Service클래스를 상속받아 구현한다.

★Broadcast Receiver : 앱이 안드로이드 시스템 전체에서 날아오는 브로드캐스트 알림에 응답할 수 있게 한다. 앱이 실행되고 있지 않은 상황에서도 브로드캐스트를 수신할 수 있어 알람을 예약한 경우 알람을 설정한 시각까지 앱을 실행하고 있을 필요가 없다. Broadcase Receiver는 UI를 포함하지 않으며 보편적으로 다른 컴포넌트를 실행하는 통로로써 사용되고, 극소량의 작업만을 하도록 만들어진다. BroadcastReceiver를 상속하여 구현하며 Intent객체를 전달받는다.

★Content Provider : 내용 제공자는 파일시스템, SQLite DB와 같은 저장된 앱 데이터를 제공할 수 있도록 해준다. 다른 앱은 이 Content Provider를 통해 허용되는 범위 내에서 앱 내부의 데이터에 접근하고 수정할 수 있다. 시스템에서 각 앱들의 각 Content Provider는 URI로 식별된다. 예를 들어 안드로이드 시스템은 사용자의 연락처 정보를 관리하는 Content Provider를 제공하여 다른 앱에서 연락처 정보에 접근할 수 있도록 허용한다. (물론 권한을 가진 앱이어야 한다.)

★Android 시스템의 특징은 어떠한 앱이든 다른 앱의 컴포넌트를 실행 시킬 수 있다는데에 있다. 카메라로 사진을 찍는 액티비티가 필요한 경우 직접 구현하는 대신 카메라앱을 실행시켜 그 결과만 받음으로써 마치 카메라 액티비티가 우리가 만든 앱의 일부분인것 처럼 사용할 수 있다. 시스템이 어떠한 컴포넌트를 시작한 경우 그 앱에 대한 프로세스를 시작하고, 그 프로세스에서 해당 컴포넌트를 인스턴스화 하여 작동시킨다. 그러므로 main() 메소드와 같은 단일 진입지점이 없다. 따라서 다른 앱의 컴포넌트를 직접 활성화할 수 없고, 시스템에 Intent를 전달 해 시스템이 실행시키도록 해야 한다.

★Intent : Activity, Service, Broadcase Receiver는 Intent라는 비동기식 메시지로 활성화시킨다. 이 컴포넌트들이 어떤 앱에 속해있든 상관없이 Intent로 작업을 요청할 수 있으며, 명시적 Intent(특정한 컴포넌트를 명시함) 또는 암시적 Intent(컴포넌트의 유형만 명시함)를 정의할 수 있다.

★Activity와 Service의 경우 Intent는 수행할 작업과 Uri를 명시할 수 있다. 예를들어 특정 웹페이지를 여는 경우이다. 또 작업의 결과를 수신해야 하는 경우에도 Intent를 통해 전달 받는다.

★Broadcase Receiver의 경우 Intent는 단순히 브로드캐스트 될 Action문자열 만을 정의한다. 예를들어 기기 배터리가 row상태가 되었을 경우 배터리 부족을 알리는 Action문자열만을 포함한다.

★Content Provider는 Intent가 아닌 ContentResolver가 보내는 요청으로 활성화 된다.

★각 컴포넌트를 시작하는 방법
1. Activity : startActivity(), startActivityForResult()
2. Service : startService(), bindService()
3. Broadcase Receiver : sendBroadcast(), sendOrderedBroadcase(), sendStickyBroadcase()
4. Content Provider : ContentResolver.query()

★앱이 가진 모든 컴포넌트들은 매니페스트에 등록되어야 하며, 이 파일은 앱 프로젝트의 루트에 위치해야 한다. Android 시스템이 컴포넌트를 실행하기 위해 이 파일을 읽어 해당 컴포넌트가 존재하는지 확인하기 때문이다.