# DevOps Tools 특강 정리

> 참고 자료: [DevOps Tools 특강 Notion](https://app.notion.com/p/DevOps-Tools-ab0ef6e7ff1c8388971501a0c23b10f9)

## 1. 특강 전체 흐름

이번 특강은 개발한 코드를 버전으로 관리하고, 동일한 실행 환경으로 Packaging한 뒤, Cloud Server에 자동 배포하는 전체 DevOps 흐름을 다룬다.

```text
개발자가 코드 작성
  ↓
Git으로 변경 이력 관리
  ↓
GitHub에 Push
  ↓
GitHub Actions 또는 Jenkins가 변경 감지
  ↓
Build + Test
  ↓
Docker Image 또는 JAR 생성
  ↓
AWS EC2에 배포
  ↓
Application 실행 및 운영
```

도구별 역할은 다음과 같다.

| 도구 | 역할 |
| --- | --- |
| Git | Source Code 변경 이력 관리 |
| GitHub | 원격 Repository와 협업 공간 제공 |
| Docker | Application과 실행 환경을 Image로 Packaging |
| AWS IAM | AWS 사용자와 Resource 접근 권한 관리 |
| AWS EC2 | Application을 실행할 가상 Server 제공 |
| AWS ECR | Docker Image 저장소 제공 |
| GitHub Actions | GitHub Event 기반 CI/CD 자동화 |
| Jenkins | 별도 Server에서 동작하는 CI/CD 자동화 Server |

---

## 2. DevOps란?

### 기존 개발·운영 방식의 문제

전통적인 방식에서는 역할별 업무가 단절되기 쉽다.

```text
고객 요구사항
→ PM 분석
→ 개발자 구현
→ Tester 검증
→ 배포 담당자 배포
→ 운영 담당자 관리
```

각 단계의 담당자가 자신의 업무만 알고 있으면 전달 과정이 길어지고, 장애가 발생했을 때 원인과 책임을 찾기 어려워진다.

### DevOps의 의미

DevOps는 단순히 Development와 Operations 조직을 합치는 것이 아니다.

```text
개발자가 운영 환경을 고려하고
운영자가 개발 흐름을 이해하며
도구를 이용해 반복 과정을 자동화하는 문화와 관행
```

핵심 목표는 다음과 같다.

- 개발부터 운영까지의 협업 강화
- 반복적인 Build, Test, Deploy 자동화
- 문제를 빠르게 발견할 수 있는 Feedback Loop 구성
- 작은 변경을 자주, 안정적으로 배포
- 배포 과정의 재현성과 일관성 확보

---

## 3. CI/CD

### CI: Continuous Integration

지속적 통합은 여러 개발자가 작성한 변경 사항을 자주 합치고 자동으로 검증하는 과정이다.

```text
개발자 A·B·C가 코드 작성
→ GitHub에 Push
→ 자동 Build
→ 자동 Test
→ 실패 시 즉시 확인
```

### CD: Continuous Delivery / Deployment

CI를 통과한 결과물을 배포 가능한 상태로 만들거나 실제 Server에 자동 반영하는 과정이다.

```text
Build 성공
→ Test 성공
→ 배포 산출물 생성
→ Server 전달
→ 최신 Version 실행
```

일반적인 CI/CD 흐름은 다음과 같다.

```text
Commit & Push
→ 변경 감지
→ Build
→ Test
→ Deploy
→ Application 재실행
→ 운영·Monitoring
```

---

## 4. Git 기본 명령어

Git은 Source Code의 변경 이력을 관리하는 분산 Version Control System이다.

### 4.1 Repository 초기화

```bash
# 현재 Directory에서 Git Version 관리를 시작한다.
git init
```

실행하면 `.git`이라는 Local Repository가 만들어진다.

### 4.2 변경 사항 확인과 Staging

```bash
# 변경된 파일 확인
git status

# 특정 파일을 Staging Area에 추가
git add index.html

# 현재 Directory의 변경 사항 전체 추가
git add .
```

Git의 기본 영역은 다음과 같다.

```text
Working Directory
  ↓ git add
Staging Area
  ↓ git commit
Local Repository
  ↓ git push
Remote Repository
```

### 4.3 Commit

Commit은 Staging Area의 변경 사항을 하나의 Snapshot으로 저장한다.

```bash
git commit -m "Initial commit: 프로젝트 초기 구조 생성"
```

Commit Message는 다음 구조로 작성할 수 있다.

```text
제목

무엇을, 왜 변경했는지 설명

관련 이슈: #번호
```

예시:

```text
fix: Safari 모달 스크롤 오류 수정

모바일 Safari에서 모달 외부 영역이 함께 스크롤되는 문제를 수정했다.

resolves: #1137
```

### 4.4 원격 Repository 연결과 Push

```bash
# origin이라는 별칭으로 원격 Repository 등록
git remote add origin https://github.com/username/repository.git

# 기본 Branch 이름을 main으로 변경
git branch -M main

# main Branch를 origin에 Push하고 추적 관계 설정
git push -u origin main
```

```bash
# 등록된 원격 Repository 확인
git remote -v

# 현재 Branch와 원격 추적 정보 확인
git branch -vv
```

### 4.5 Fetch와 Pull

```bash
# 원격 변경 정보를 가져오되 현재 Branch에 합치지 않는다.
git fetch origin

# 원격 변경 정보를 가져와 현재 Branch에 병합한다.
git pull origin main
```

```text
git pull
= git fetch
+ git merge
```

### 4.6 Clone

원격 Repository를 먼저 만들었다면 Clone으로 Local 환경에 가져올 수 있다.

```bash
git clone https://github.com/username/repository.git
cd repository
```

### 4.7 `.gitignore`

Version 관리가 필요 없거나 공개하면 안 되는 파일은 `.gitignore`에 등록한다.

```gitignore
.idea/
.vscode/
.DS_Store
.env
application.yml
node_modules/
build/
```

특히 다음 정보는 Git에 Commit하면 안 된다.

- API Key
- DB Password
- AWS Access Key와 Secret Access Key
- EC2 Private Key
- Token과 인증서
- 개인별 환경 설정

이미 Git에 등록된 파일은 `.gitignore`에 추가하는 것만으로 추적이 중단되지 않는다.

```bash
# Local 파일은 유지하면서 Git 추적 대상에서 제거
git rm --cached 파일명
```

---

## 5. Docker 기본 개념

### Docker를 사용하는 이유

Docker의 가장 큰 장점은 이식성과 실행 환경의 일관성이다.

- 매번 같은 설치 과정을 반복하지 않아도 된다.
- Library, Runtime, 설정 Version을 일정하게 유지할 수 있다.
- Application을 다른 Computer나 Server로 옮기기 쉽다.
- Process별 실행 환경을 격리해 충돌을 줄인다.

### Host, Image, Container

```text
Host
└─ Docker Engine
   ├─ Container A: Java 17
   ├─ Container B: Java 21
   └─ Container C: Node.js
```

| 용어 | 의미 |
| --- | --- |
| Host | Docker와 Container를 실행하는 Computer |
| Image | Container 실행에 필요한 Code와 환경이 담긴 읽기 전용 Template |
| Container | Image를 기반으로 생성된 실행 Instance |
| Registry | Image를 저장하고 공유하는 원격 저장소 |

```text
Dockerfile
→ docker build
→ Image
→ docker run
→ Container
```

Image 자체를 실행 중에 수정하는 방식보다 Dockerfile을 변경하고 새로운 Image를 Build하는 방식으로 Version을 관리한다.

---

## 6. Docker 기본 명령어

### Image 내려받기와 확인

```bash
# Docker Hub에서 nginx 최신 Image 다운로드
docker pull nginx

# Local Image 목록
docker image ls

# 같은 의미의 명령어
docker images
```

### Image 삭제

```bash
docker image rm 이미지명

# 축약 명령어
docker rmi 이미지명
```

사용 중인 Image는 연결된 Container를 먼저 정리해야 삭제할 수 있다.

### Container 실행

```bash
# nginx Image를 기반으로 Container 생성 및 실행
# -d: Background 실행
# -p 8080:80: Host 8080 → Container 80 Port 연결
# --name: Container 이름 지정
docker run -d -p 8080:80 --name my-web-server nginx
```

Port Mapping은 다음 의미다.

```text
Browser
→ Host의 8080 Port
→ Container의 80 Port
→ nginx
```

### Container 확인 및 제어

```bash
# 실행 중인 Container
docker ps

# 중지된 Container를 포함한 전체 목록
docker ps -a

# 정상 종료
docker stop my-web-server

# 다시 시작
docker start my-web-server

# 중지된 Container 삭제
docker rm my-web-server
```

`docker rm -f`와 전체 삭제 명령은 실행 중인 Container와 데이터를 제거할 수 있으므로 대상 확인 후 사용해야 한다.

---

## 7. Dockerfile과 Image Build

Dockerfile은 Image를 만드는 절차를 선언한 파일이다.

### Node.js 예시

```dockerfile
# 기반이 되는 Runtime Image
FROM node:14

# 이후 명령의 기준 Directory
WORKDIR /app

# Dependency 정의 파일을 먼저 복사해 Build Cache 활용
COPY package*.json ./

# Image Build 과정에서 Dependency 설치
RUN npm install

# 나머지 Source Code 복사
COPY . .

# Container가 시작될 때 실행할 명령
CMD ["node", "app.js"]
```

| 명령어 | 실행 시점과 역할 |
| --- | --- |
| `FROM` | Base Image 지정 |
| `WORKDIR` | Container 내부 작업 Directory 지정 |
| `COPY` | Host 파일을 Image로 복사 |
| `RUN` | Image Build 중 명령 실행 |
| `CMD` | Container 시작 시 기본 명령 실행 |
| `ENTRYPOINT` | Container의 고정 실행 명령 지정 |

```bash
# 현재 Directory의 Dockerfile로 Image 생성
docker build -t my-app:v1 .
```

### Spring Boot 예시

```dockerfile
# Java 21 Runtime 기반 Image
FROM eclipse-temurin:21-jdk-alpine

# Gradle이 만든 JAR를 Image에 복사
COPY ./build/libs/*SNAPSHOT.jar project.jar

# Container 시작 시 Spring Boot 실행
ENTRYPOINT ["java", "-jar", "project.jar"]
```

```bash
./gradlew clean build
docker build -t backend:local .
docker run -d -p 8088:8088 --name backend backend:local
```

---

## 8. AWS 기본 개념

### AWS와 On-Premises

On-Premises는 Server를 직접 구매해 사무실이나 Data Center에서 운영하는 방식이다. AWS는 필요한 Server, Network, Storage를 Cloud에서 필요한 만큼 사용할 수 있게 제공한다.

```text
On-Premises
→ 장비 구매와 설치
→ 초기 비용이 큼
→ 직접 유지보수

AWS
→ 필요한 Resource를 즉시 생성
→ 사용량 기반 비용
→ 사양 확장·축소 용이
```

### IAM

IAM(Identity and Access Management)은 AWS에서 누가 어떤 Resource에 어떤 작업을 할 수 있는지 관리한다.

```text
Developer A → EC2 조회·관리
Developer B → S3만 접근
CI/CD 계정 → ECR Image Push
Administrator → 전체 관리
```

권한은 필요한 작업만 허용하는 최소 권한 원칙으로 구성해야 한다.

### EC2

EC2(Elastic Compute Cloud)는 AWS가 제공하는 가상 Server다.

- AMI: EC2를 만들 때 사용할 OS Image
- Instance Type: CPU와 Memory 등 Computer 사양
- Key Pair: SSH 접속 인증에 사용하는 Key
- Security Group: EC2의 Inbound·Outbound Traffic을 제어하는 가상 Firewall
- Public IPv4: 외부에서 EC2에 접근할 때 사용하는 주소

---

## 9. EC2와 Security Group

실습에서는 Ubuntu LTS 기반 EC2를 사용한다. Ubuntu Server는 Desktop OS보다 불필요한 GUI 요소가 적어 Server 운영에 적합하다.

### 주요 Port 예시

| Port | 용도 |
| --- | --- |
| `22` | SSH 원격 접속 |
| `80` | HTTP Web Service |
| `8080` | Jenkins 또는 Spring Boot Application |
| `8088` | 별도의 Backend Application 예시 |

`0.0.0.0/0`은 모든 IPv4 주소의 접근을 허용한다. 실습에서는 편리하지만 실제 운영 환경에서는 특히 SSH 22번 Port를 관리자 IP 등 필요한 범위로 제한해야 한다.

### SSH 접속

```bash
# Private Key 권한 제한
chmod 400 devops-server.pem

# Ubuntu EC2 접속
ssh -i devops-server.pem ubuntu@EC2_PUBLIC_IP
```

Private Key는 비밀번호와 같은 민감정보다. Git에 올리거나 Chat, 문서, Log에 노출하면 안 된다.

### EC2 상태

| 동작 | 의미 |
| --- | --- |
| Stop | Instance를 일시적으로 끔 |
| Reboot | Instance 재부팅 |
| Terminate | Instance 삭제 |

Terminate는 되돌릴 수 없고 저장된 데이터가 삭제될 수 있으므로 신중하게 사용한다.

---

## 10. GitHub Actions 기본 구조

GitHub Actions는 GitHub Repository의 Event를 감지해 Build, Test, Deploy 같은 Workflow를 실행한다.

```text
Commit
→ Push
→ Workflow 실행
→ Build
→ Test
→ Deploy
→ Server 재실행
```

Workflow 파일은 반드시 Repository Root의 다음 경로에 둔다.

```text
.github/workflows/deploy.yml
```

### 기본 Workflow

```yaml
# Actions 화면에 표시될 Workflow 이름
name: GitHub Actions 기본 실습

# main Branch에 Push될 때 실행
on:
  push:
    branches:
      - main

# 하나의 Workflow는 하나 이상의 Job으로 구성
jobs:
  deploy:
    # Job을 실행할 GitHub 제공 Runner OS
    runs-on: ubuntu-latest

    # 같은 Job의 Step은 위에서 아래로 순차 실행
    steps:
      - name: Hello World 출력
        run: echo "Hello World"

      - name: 여러 명령어 실행
        run: |
          echo "Good"
          echo "Morning"
```

핵심 단위는 다음과 같다.

```text
Workflow
└─ Job
   └─ Step
```

- Workflow: 하나의 YAML 자동화 정의
- Event: Workflow가 실행되는 조건
- Job: Runner 한 대에서 실행할 작업 묶음
- Step: 실제 명령 또는 Action을 실행하는 최소 단위
- Runner: Job을 실행하는 가상 Computer

여러 Job은 기본적으로 병렬 실행되고, 같은 Job의 Step은 순차 실행된다.

---

## 11. GitHub Actions 변수와 Secret

GitHub Actions가 제공하는 Context나 환경변수를 사용할 수 있다.

```yaml
- name: GitHub 기본 변수 확인
  run: |
    echo "$GITHUB_SHA"
    echo "$GITHUB_REPOSITORY"
```

- `GITHUB_SHA`: Workflow를 실행한 Commit ID
- `GITHUB_REPOSITORY`: `owner/repository` 형태의 Repository 이름

민감한 값은 Repository의 Actions Secret으로 관리한다.

```yaml
- name: Secret 사용
  env:
    APPLICATION_PROPERTIES: ${{ secrets.APPLICATION_PROPERTIES }}
  run: |
    mkdir -p src/main/resources
    printf '%s' "$APPLICATION_PROPERTIES" > src/main/resources/application.yml
```

대표적인 Secret은 다음과 같다.

```text
EC2_HOST
EC2_USERNAME
EC2_PRIVATE_KEY
APPLICATION_PROPERTIES
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

Secret 값을 `echo`로 출력하면 Masking되더라도 Log 노출 위험이 있으므로 확인 목적으로 출력하지 않는다.

---

## 12. GitHub Actions 배포 방식 1: EC2에서 Build

가장 단순한 방식은 Actions가 SSH로 EC2에 접속하고 EC2 내부에서 `git pull`, Build, 재실행을 수행하는 것이다.

```text
GitHub Push
→ GitHub Actions
→ SSH로 EC2 접속
→ git pull
→ Gradle Build
→ 기존 Process 종료
→ 새 JAR 실행
```

```yaml
- name: EC2에서 배포 실행
  uses: appleboy/ssh-action@v1.0.3
  with:
    host: ${{ secrets.EC2_HOST }}
    username: ${{ secrets.EC2_USERNAME }}
    key: ${{ secrets.EC2_PRIVATE_KEY }}
    script_stop: true
    script: |
      cd /home/ubuntu/devops-service
      git pull origin main
      ./gradlew clean build
      sudo fuser -k -n tcp 8080 || true
      nohup java -jar build/libs/*SNAPSHOT.jar > output.log 2>&1 &
```

장점은 간단하다는 것이지만 다음 단점이 있다.

- 운영 EC2에서 Build하므로 Server 자원을 소비한다.
- EC2에 Source와 GitHub 인증정보가 남을 수 있다.
- Build 환경과 운영 환경의 책임이 섞인다.

개인 프로젝트의 빠른 배포에는 사용할 수 있지만 일반적인 운영 구조에서는 Build와 실행 환경을 분리하는 편이 좋다.

---

## 13. GitHub Actions 배포 방식 2: Actions에서 Build

GitHub Actions Runner에서 Build와 Test를 완료하고 산출물만 EC2로 전달한다.

```text
GitHub Push
→ Actions Runner에서 Checkout
→ JDK 설치
→ 설정 파일 생성
→ Gradle Build + Test
→ JAR 생성
→ SCP로 EC2 전송
→ SSH로 최신 JAR 실행
```

```yaml
name: Deploy To EC2

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Source Checkout
        uses: actions/checkout@v4

      - name: JDK 21 설치
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21

      - name: Gradle 실행 권한 부여
        run: chmod +x gradlew

      - name: Build와 Test
        run: ./gradlew clean build

      - name: 배포 파일명 정리
        run: mv ./build/libs/*SNAPSHOT.jar ./project.jar

      - name: JAR를 EC2로 전송
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ${{ secrets.EC2_USERNAME }}
          key: ${{ secrets.EC2_PRIVATE_KEY }}
          source: project.jar
          target: /home/ubuntu/devops-server/output
```

이 방식은 운영 EC2가 Build 작업을 하지 않고 완성된 산출물만 실행한다는 장점이 있다.

---

## 14. GitHub Actions 배포 방식 3: Docker와 ECR

Docker 기반 구조에서는 Actions가 Image를 Build해 ECR에 Push하고, EC2는 Image를 Pull해 Container를 실행한다.

```text
GitHub Repository
  ↓ Checkout
GitHub Actions
  ↓ Gradle Build
JAR
  ↓ docker build
Docker Image
  ↓ docker push
AWS ECR
  ↓ docker pull
AWS EC2
  ↓ docker run
Container 실행
```

주요 Step은 다음과 같다.

```yaml
steps:
  - name: Source Checkout
    uses: actions/checkout@v4

  - name: JDK 21 설치
    uses: actions/setup-java@v4
    with:
      distribution: temurin
      java-version: 21

  - name: Build
    run: ./gradlew clean build

  - name: AWS 인증 설정
    uses: aws-actions/configure-aws-credentials@v4
    with:
      aws-region: ap-northeast-2
      aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
      aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}

  - name: ECR 로그인
    id: login-ecr
    uses: aws-actions/amazon-ecr-login@v2

  - name: Docker Image Build
    run: docker build -t devops-server .

  - name: ECR Tag 설정
    run: |
      docker tag devops-server \
      ${{ steps.login-ecr.outputs.registry }}/devops-server:latest

  - name: ECR Push
    run: |
      docker push \
      ${{ steps.login-ecr.outputs.registry }}/devops-server:latest
```

EC2에서는 다음 과정이 실행된다.

```bash
# 기존 Container가 없어도 배포가 중단되지 않도록 처리
docker stop devops-server || true
docker rm devops-server || true

# ECR에서 최신 Image 다운로드
docker pull ECR_URI/devops-server:latest

# 새로운 Container 실행
docker run -d \
  --name devops-server \
  -p 8080:8080 \
  ECR_URI/devops-server:latest
```

실제 운영에서는 `latest`만 사용하기보다 Commit SHA를 Image Tag로 함께 사용하면 어떤 Version이 배포됐는지 추적하고 Rollback하기 쉽다.

```text
devops-server:latest
devops-server:<commit-sha>
```

---

## 15. EC2가 Private ECR에 접근하는 방법

Private ECR Image를 Pull하려면 EC2에 ECR 접근 권한이 필요하다.

권장 흐름은 다음과 같다.

```text
EC2 Instance
→ IAM Role 연결
→ ECR Pull에 필요한 최소 권한 부여
→ ECR Credential Helper 사용
→ docker pull
```

장기 Access Key를 EC2 내부에 직접 저장하기보다 IAM Role을 사용하면 Credential 관리 위험을 줄일 수 있다.

Credential Helper 설정 예시는 다음과 같다.

```json
{
  "credsStore": "ecr-login"
}
```

이 파일은 일반적으로 다음 경로에 둔다.

```text
~/.docker/config.json
```

---

## 16. Jenkins

Jenkins는 CI와 CD Pipeline을 구성할 수 있는 Open Source 자동화 Server다.

```text
GitHub에 Push
→ Webhook
→ Jenkins Job 실행
→ Source Checkout
→ Build
→ Test
→ Deploy
```

### Jenkins의 장점

- 반복적인 Build와 배포 작업 자동화
- Push 직후 Build·Test를 실행해 빠른 Feedback 제공
- Docker, AWS, GitHub, Slack 등 다양한 Plugin 지원
- 복잡한 Pipeline과 사내 환경을 유연하게 구성 가능

### 고려할 점

- Jenkins Server를 직접 설치하고 운영해야 한다.
- Plugin과 Jenkins 자체 Version을 관리해야 한다.
- Build Agent, Credential, Backup, 보안을 직접 관리해야 한다.

---

## 17. Jenkins 설치 흐름

특강에서는 Ubuntu EC2에 Docker와 Jenkins를 설치한다.

```text
Ubuntu Package 갱신
→ Docker 설치
→ JDK 21 설치
→ Jenkins 공식 Repository 등록
→ Jenkins 설치
→ Jenkins Service 시작
→ 초기 관리자 설정
→ 필요한 Plugin 설치
```

Docker를 먼저 설치하고 Jenkins 사용자를 Docker Group에 추가해야 Jenkins Pipeline에서 Docker 명령을 실행할 수 있다.

```bash
# Docker 설치
sudo apt update
sudo apt install -y docker.io docker-compose-v2

# Jenkins 사용자를 Docker Group에 추가
sudo usermod -aG docker jenkins

# Jenkins Service 시작과 자동 실행
sudo systemctl start jenkins
sudo systemctl enable jenkins
sudo systemctl status jenkins
```

Group 변경 후에는 Jenkins Service 재시작이나 Session 갱신이 필요할 수 있다.

Jenkins 초기 관리자 비밀번호, Credential, API Token 등은 민감정보이므로 외부에 공유하거나 Log에 남기지 않는다.

---

## 18. Jenkins Job 유형

### Freestyle Project

UI에서 Source, Build Trigger, Build Step 등을 설정하는 방식이다.

- 설정이 간단하고 직관적이다.
- 간단한 자동화 실습에 적합하다.
- 단계가 복잡해지면 변경 이력과 재사용 관리가 어렵다.

### Pipeline

Jenkinsfile로 Build, Test, Deploy 단계를 Code로 관리한다.

- Pipeline 변경 이력을 Git으로 관리할 수 있다.
- 조건문, 반복, 병렬 실행 등 복잡한 흐름을 작성할 수 있다.
- 실무 CI/CD 구성에 적합하다.

```text
Repository Root
└─ Jenkinsfile
```

간단한 Declarative Pipeline 예시는 다음과 같다.

```groovy
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t devops-server .'
            }
        }

        stage('Deploy') {
            steps {
                echo '배포 단계 실행'
            }
        }
    }
}
```

---

## 19. GitHub Webhook과 Jenkins 자동 실행

Jenkins가 GitHub Push를 자동으로 감지하려면 양쪽 설정이 필요하다.

```text
GitHub Repository
→ Webhook 등록
→ Push Event 발생
→ Jenkins Webhook Endpoint 호출
→ Jenkins Job Trigger
→ Jenkinsfile 실행
```

Jenkins Job에서는 다음을 설정한다.

- Build Trigger에서 GitHub Hook Trigger 활성화
- Pipeline Definition을 `Pipeline script from SCM`으로 설정
- SCM을 Git으로 선택
- Repository URL과 Branch 설정
- Script Path를 `Jenkinsfile`로 지정

Webhook Endpoint는 외부에서 Jenkins에 도달할 수 있어야 하며, 운영 환경에서는 HTTPS, 인증, 최소 Port 개방 등 보안 설정이 필요하다.

---

## 20. GitHub Actions와 Jenkins 비교

| 구분 | GitHub Actions | Jenkins |
| --- | --- | --- |
| 실행 환경 | GitHub Hosted Runner 사용 가능 | 별도 Server 또는 Agent 필요 |
| 초기 설정 | Repository에 YAML 작성 | Server 설치와 초기 설정 필요 |
| 관리 부담 | 상대적으로 적음 | Server, Plugin, Backup 관리 필요 |
| GitHub 연동 | 기본 통합 | Webhook과 Credential 설정 필요 |
| 확장성 | Marketplace Action | Plugin과 Script로 높은 확장성 |
| 적합한 환경 | GitHub 중심 프로젝트, 빠른 구축 | 복잡한 사내 Pipeline, 자체 인프라 |

둘 중 하나만 사용해도 전체 CI/CD를 구성할 수 있다. 도구 자체보다 프로젝트 환경, 보안 요구사항, 운영 인력, 비용을 기준으로 선택한다.

---

## 21. 배포 전략별 비교

| 방식 | Build 위치 | 전달 산출물 | 특징 |
| --- | --- | --- | --- |
| EC2에서 Build | 운영 EC2 | Source Code | 간단하지만 운영 Server 자원 사용 |
| Actions에서 Build | GitHub Runner | JAR | Build와 운영 환경 분리 |
| Docker + ECR | GitHub Runner | Docker Image | 실행 환경까지 Version화하고 이식성 확보 |

점진적인 개선 흐름은 다음과 같다.

```text
EC2에서 git pull + build
→ Actions에서 build 후 JAR만 전송
→ Docker Image Build
→ ECR Push
→ EC2가 Image Pull 및 Container 실행
```

---

## 22. 보안상 주의할 점

### Secret은 Repository에 저장하지 않는다

```text
.env
application.yml
AWS Access Key
EC2 Private Key
DB Password
API Token
```

이 값들은 `.gitignore`, GitHub Actions Secrets, Jenkins Credentials 같은 별도 저장소로 관리한다.

### 최소 권한 원칙

- CI 계정에는 배포에 필요한 권한만 부여한다.
- EC2에는 ECR Pull 등 필요한 IAM Role만 연결한다.
- SSH 22번 Port를 모든 IP에 공개하지 않는다.
- 사용하지 않는 Inbound Port는 닫는다.

### 민감정보를 Log에 출력하지 않는다

```bash
# Secret을 확인하기 위해 출력하지 않는다.
echo "$AWS_SECRET_ACCESS_KEY"
cat private-key.pem
```

### Version 고정과 검증

- 외부 GitHub Action은 검증된 Publisher와 Version을 사용한다.
- Docker Base Image와 Dependency Version을 관리한다.
- 배포 Image에 Commit SHA Tag를 붙여 추적 가능하게 한다.
- Build와 Test를 통과한 산출물만 배포한다.

---

## 23. 최종 정리

이번 특강의 핵심은 개별 명령어보다 도구가 하나의 배포 흐름에서 어떻게 연결되는지 이해하는 것이다.

```text
Git
→ 변경 이력을 관리한다.

GitHub
→ Source Code를 공유하고 Push Event를 제공한다.

GitHub Actions / Jenkins
→ Build, Test, Deploy를 자동화한다.

Docker
→ Application과 실행 환경을 Image로 만든다.

AWS ECR
→ Docker Image를 저장한다.

AWS IAM
→ 각 도구와 사용자의 접근 권한을 관리한다.

AWS EC2
→ 완성된 Application 또는 Container를 실행한다.
```

전체 CI/CD Pipeline은 다음 한 문장으로 정리할 수 있다.

> 개발자가 GitHub에 코드를 Push하면 자동화 도구가 코드를 Build하고 Test한 뒤, 검증된 JAR 또는 Docker Image를 AWS EC2에 배포해 최신 Version의 서비스를 실행한다.

DevOps는 특정 도구 하나를 의미하지 않는다. Git, Docker, Cloud, CI/CD 도구를 연결해 개발과 운영의 반복 작업을 자동화하고, 변경 사항을 빠르고 안정적으로 사용자에게 전달하는 문화와 과정이다.
