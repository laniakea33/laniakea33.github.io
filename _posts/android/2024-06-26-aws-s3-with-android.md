---
title: "AWS S3 With Android"
categories:
    - Android
---

이 포스트에서는 Amplify 프레임워크를 사용하여 Android 앱에서 S3저장소에 파일을 업/다운로드 하는 법을 정리했다.

## 준비물

1. Android 프로젝트
2. AWS 계정

# Amplify 초기화

## amplify 설치

아래 커맨드를 통해 amplify cli 패키지를 설치한다.

~~~
npm install -g @aws-amplify/cli
~~~

다음으로 Amplify를 적용할 Android 프로젝트의 루트 디렉토리로 이동해 아래 커맨드를 실행하여 설정을 시작한다.

~~~
amplify configure
~~~

`Region`은 `ap-northeast-2(서울)`로 지정.

다음으로는 IAM 정보를 입력해야 하는데, IAM이란 AWS의 리소스에 접근할 수 있는 사용자 및 엑세스 권한 정보이다.

## IAM 생성

AWS 콘솔에서 IAM 생성 플로우를 시작한다. 아래처럼 3단계를 거쳐 IAM을 생성.

![alt text](<../../assets/images/스크린샷 2024-06-26 오후 7.35.28.png>)

![alt text](<../../assets/images/스크린샷 2024-06-26 오후 7.36.32.png>)

![alt text](<../../assets/images/스크린샷 2024-06-26 오후 7.36.47.png>)

사용자가 생성되면 콘솔의 IAM - 사용자 - 방금 만든 사용자 상세정보로 들어와 엑세스 키를 생성하도록 한다. 아래와 같이 생성했음.

![alt text](<../../assets/images/스크린샷 2024-06-26 오후 7.46.41.png>)

![alt text](<../../assets/images/스크린샷 2024-06-26 오후 7.47.36.png>)

사실 설명 태그값에 한글은 입력할 수 없다.

다시 콘솔로 돌아와 방금 만든 엑세스 키의 `accessKeyId`와 `secretAccessKey`를 입력해 준다. `profile name`은 `default`로 설정해 줬음.

`Successfully set up the new user.`라는 메시지와 함께 프로젝트에 Amplify 프로필 설정이 끝난 듯 하다.

## 앱 디펜던시 추가

이제 앱으로 돌아와서 아래처럼 디펜던시를 세팅한다.

**libs.versions.toml**
~~~toml
[versions]
...
awsAndroidSdkVersion = "2.2.0"

[libraries]
...
aws-core = { module = "com.amplifyframework:core", version.ref = "awsAndroidSdkVersion" }
aws-auth-cognito = { module = "com.amplifyframework:aws-auth-cognito", version.ref = "awsAndroidSdkVersion" }
aws-storage-s3 = { module = "com.amplifyframework:aws-storage-s3", version.ref = "awsAndroidSdkVersion" }
~~~

**app/build.gradle.kts**
~~~kotlin
dependencies {

    ...

    //  AWS
    implementation(libs.aws.core)
    implementation(libs.aws.storage.s3)
    implementation(libs.aws.auth.cognito)
}
~~~

## Amplify 초기화

다시 프로젝트의 루트 디렉토리로 돌아와서 명령어를 입력한다.
~~~
amplify init
~~~

~~~
? Enter a name for the project AWSTestApplication

The following configuration will be applied:

Project information
| Name: AWSTestApplication
| Environment: dev
| Default editor: Visual Studio Code
| App type: javascript
| Javascript framework: none
| Source Directory Path: src
| Distribution Directory Path: dist
| Build Command: npm run-script build
| Start Command: npm run-script start

? Initialize the project with the above configuration? No

? Enter a name for the environment dev

? Choose your default editor: Android Studio
✔ Choose the type of app that you're building · android

Please tell us about your project
? Where is your Res directory:  app/src/main/res
Using default provider  awscloudformation

? Select the authentication method you want to use: AWS profile

? Please choose the profile you want to use default
~~~

여기까지 입력했는데 앗! 오류 메시지가 나타났다.

~~~
User: arn:aws:iam::123456789012:user/myusername is not authorized to perform: amplify:CreateApp on resource: arn:aws:amplify:ap-northeast-2:123456789012:apps/*
~~~

현재 사용자가 권한이 없나보다. IAM 생성 시 아무런 권한도 안줘서 그랬던 것 같다. 

## IAM 권한 설정

AWS는 사용자들을 그룹지어서 각 그룹별로 권한을 관리하는 것을 권장한다. IAM 콘솔 - 엑세스 관리 - 사용자 그룹에 들어가 새 그룹을 생성한다. 간단하게 그룹 이름(admin이라고 이름지음), 그룹에 추가할 사용자(아까 만들었던 IAM 사용자)을 입력한 후 권한을 지정해 준다. 뭐가 필요한지 잘 모르겠으나 admin이니 일단 `AdministratorAccess`와 `AdministratorAccess-Amplify`를 추가한다. 그룹 생성, 권한 설정, 그룹에 IAM 사용자 추가까지 완료되면 `amplify init`플로우를 다시 실행한다.

~~~
✅ Initialized your environment successfully.
~~~
이런 메시지와 함께 초기화가 완료됐다. 앱 루트 디렉토리에 `amplify`라는 백엔드 디렉토리가 생성됨을 알 수 있다. 여기까지 프레임워크 기초 작업이 완료됐다.

# S3, Cognito

## S3 버킷 생성

콘솔에서 S3를 찾아 들어가 새 버킷을 생성한다. 버킷의 이름을 지어주고 나머지 항목은 디폴트 값으로 놔둔 채로 완료한다. 별로 설정할 것이 없다.

## Cognito

### 사용자 풀 생성

Cognito 콘솔 - 사용자 풀로 들어간 후 `사용자 풀 생성`을 시작한다. 사용자 풀이란 말 그대로 앱의 사용자 풀인것 같은데, 내 앱에서는 사용자를 Firebase Auth로 관리하기때문에 사용할 계획은 없지만 앱에서 Cognito auth를 초기화 시킬 때 필요하기때문에 만들어야 한다. 그래서 거의 모든 항목을 디폴트 값으로 뒀다. 중간에 사용자에게 SMS를 발송하기위한 IAM이 필요하다고 해서 하나 만들어 준다. 또한 초기 앱 클라이언트 설정을 하라고 한다. 아래처럼 해 준다.

![alt text](<../../assets/images/스크린샷 2024-06-27 오전 12.23.34.png>)

앱 클라이언트는 여러개를 만들어도 되지만 클라이언트 보안키가 없는 클라이언트가 적어도 하나는 있어야 하는것이 핵심이다. 안그러면 앱에서 초기화 할 수가 없음.

### 자격 증명 풀 생성

Cognito 콘솔 - 자격 증명 풀로 들어간 후 `자격 증명 풀 생성` 버튼을 눌러 자격 증명 풀 생성을 시작한다.

![alt text](<../../assets/images/스크린샷 2024-06-27 오전 12.26.36.png>)

앱에서 접근하려면 게스트 엑세스를 해야 하지만 앱에서 초기화를 위해 인증된 엑세스도 같이 선택해야 하는 듯 하다.

![alt text](<../../assets/images/스크린샷 2024-06-27 오전 12.27.27.png>)
![alt text](<../../assets/images/스크린샷 2024-06-27 오전 12.27.35.png>)

인증된 역할, 게스트 역할을 각각 생성해 준다.

![alt text](<../../assets/images/스크린샷 2024-06-27 오전 12.28.41.png>)

자격 증명 공급자에 아까 만든 사용자 풀과, 앱 클라이언트의 ID를 넣는다.

![alt text](<../../assets/images/스크린샷 2024-06-27 오전 12.29.19.png>)

마지막으로 자격 증명 풀의 이름 정하고 완료한다. 

## Rule 설정

방금 전 자격 증명 풀을 생성하면서 2단계 권한 구성에서 인증된 역할, 게스트 역할을 각각 생성해 줬다. 여기서 역할이란 쉽게 말해 권한의 집합인 것으로 보이는데, 인증된 사용자는 TestRule_Authed, 게스트 사용자는 TestRule_UnAuthed가 부여되도록 설정해 준 상태이다. 즉 앱에서 `test-identity-pool`이라는 자격 증명을 사용하여 `TestRule_Authed`, `TestRule_UnAuthed`이라는 역할을 부여받아 리소스에 접근할 수 있게 된 것이다. 아직 TestRule들에 아무런 권한을 설정하지 않았으니 설정해 보자. 앱 내용에 인증은 없기 때문에 `TestRule_UnAuthed`만 설정해 주도록 한다.

IAM 콘솔 - 엑세스 관리 - 역할 - `TestRule_UnAuthed`으로 들어가면 아래처럼 권한 정책 하나가 져 부여돼 있다.

![alt text](<../../assets/images/스크린샷 2024-06-26 오후 9.18.41.png>)

해당 권한의 상세화면으로 들어가 아래처럼 편집한다. 아까 만들어 둔 S3에 대한 권한을 부여하는 것이다. 이 권한 정책이 설정된 역할을 가지는 자격 증명을 통해 앱에서 S3에 접근하게 된다.

~~~json
{
	"Version": "2012-10-17",
	"Statement": [
		{
			"Effect": "Allow",
			"Action": [
				"s3:PutObject",
				"s3:GetObject",
				"s3:DeleteObject"
			],
			"Resource": [
				"버킷의 ARN/*"  //  ex) arn:aws:s3:::test-test-bucket/*
			]
		}
	]
}
~~~

 업/다운/삭제만 가능하도록 권한을 부여했다. 이것 이외에도 AWS에서 관리할 수 있는 권한이 천개가 넘는다.

# 앱 연동

App 클래스를 만들어 앱 실행시 Amplify 초기화 코드를 실행하도록 한다.

**App.kt**
~~~kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initAmplify()
    }

    private fun initAmplify() {
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        Amplify.addPlugin(AWSS3StoragePlugin())
        Amplify.configure(applicationContext)
    }
}
~~~

생성한 App 클래스를 설정하기 위해 `android:name=".App"` 추가.

**AndroidManifest.xml**
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<manifest 
    ...
    >

    <application
        android:name=".App"
        ...
        >
        ...
    </application>

</manifest>
~~~

## Auth(Cognito), Storage(S3) import

콘솔에서 설정한 Cognito 리소스를 앱으로 가져옴
~~~
amplify import auth
~~~

콘솔에서 설정한 S3 리소스를 앱으로 가져옴
~~~
amplify import storage
~~~

로컬 amplify 설정을 동기화시킴
~~~
amplify import push
~~~


**src/main/res/raw** 디렉토리에 다음 두 JSON파일이 생성 돼 있을 것이다.

- amplifyconfiguration.json
- awsconfiguration.json

제대로 설정이 됐다면 다음처럼 업다운로드

### 업로드

~~~kotlin
Amplify.Storage.uploadFile(
    "temp/text.txt", file, {
        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
    }, {
        it.printStackTrace()
        Toast.makeText(context, "실패~", Toast.LENGTH_SHORT).show()
    }
)
~~~

버킷의 /public/temp/text.txt 경로에 file을 업로드 한다. 꼭 루트 디렉토리의 `public 디렉토리`가 실질적인 루트 디렉토리라는 것을 기억할 것.

### 다운로드

~~~kotlin
Amplify.Storage.downloadFile("temp/file.jpg", file, {
    Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show()
}, {
    Toast.makeText(context, "실패~", Toast.LENGTH_SHORT).show()
    it.printStackTrace()
})
~~~

버킷의 /public/temp/file.jpg 경로에 있는 file을 다운로드 한다.
