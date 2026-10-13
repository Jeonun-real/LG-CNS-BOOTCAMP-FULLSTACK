# LG CNS AM INSPIRE CAMP Fullstack Learning Archive

LG CNS AM INSPIRE CAMP에서 학습한 코드를 정리한 Fullstack 저장소입니다.
---

## 학습 목록

- Java 문법과 객체지향 설계 원리 이해
- 계층형 Architecture와 객체 간 의존 관계 이해
- JDBC, MyBatis, JPA를 활용한 데이터 접근 방식 비교
- Spring Boot 기반 REST API 개발
- React와 Spring Boot의 비동기 통신
- JWT와 Spring Security를 이용한 인증·인가
- Redis를 이용한 Refresh Token 관리
- OpenAPI와 Spring AI 연동
- Monolithic Architecture와 MSA 이해
- GitHub Actions, Docker 등 DevOps 기초 학습

---
## Tech Stack

### Frontend

![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![React](https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat-square&logo=vite&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=flat-square&logo=axios&logoColor=white)
![MUI](https://img.shields.io/badge/MUI-007FFF?style=flat-square&logo=mui&logoColor=white)

### Backend

![Java](https://img.shields.io/badge/Java_21-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-BD1E2D?style=flat-square)
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat-square)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white)

### Database · Infrastructure

![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)

### API · Documentation · AI

![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=flat-square&logo=openai&logoColor=white)
![Kakao Map](https://img.shields.io/badge/Kakao_Map-FFCD00?style=flat-square&logo=kakao&logoColor=black)

---

## Repository Structure

```text
LG-CNS-BOOTCAMP
├─ be
│  ├─ inspire_java_grammer_6th
│  │  └─ Java 문법, 객체지향, Collection, Stream, JDBC 실습
│  │
│  ├─ demo
│  │  └─ Spring Boot 및 JDBC 기초 실습
│  │
│  ├─ testcase
│  │  └─ JUnit, Spring Test, MyBatis 연결 실습
│  │
│  ├─ inspire_mybatis
│  │  └─ MyBatis 기반 User, Blog, Comment REST API
│  │
│  └─ inspire_jpa
│     └─ JPA, Security, Redis, OpenAPI, Spring AI 통합 프로젝트
│
├─ fe
│  ├─ front
│  │  └─ HTML, CSS, JavaScript 기초 실습
│  │
│  ├─ react-app
│  │  └─ React 기반 회원·블로그·댓글·날씨·AI 기능
│  │
│  └─ react-app-ts
│     └─ React와 TypeScript 기초 실습
│
├─ rds
│  ├─ SQL 실습 파일
│  ├─ DDL 및 Schema
│  └─ ERD 자료
│
└─ summary
   ├─ FE summary
   ├─ BE summary
   ├─ DB summary
   └─ DevOps Tools 특강.md
```

---

# 실행 방법

## Requirements

- Java 21
- Node.js
- MariaDB
- Docker Desktop
- Redis
- Gradle Wrapper

## 1. Repository Clone

```bash
git clone https://github.com/Jeonun-real/LG-CNS-BOOTCAMP.git
cd LG-CNS-BOOTCAMP
```

## 2. 환경 변수 설정

`be/inspire_jpa` 경로에 `.env` 파일을 생성합니다.

```properties
DB_DRIVER=org.mariadb.jdbc.Driver
DB_URL=jdbc:mariadb://localhost:3306/데이터베이스명
DB_USER=데이터베이스사용자
DB_PASSWORD=데이터베이스비밀번호

JWT_SECRET_KEY=JWT서명키

OPEN_AI_KEY=OpenAI_API_Key
OPEN_AI_MODEL=사용할_OpenAI_Model

OPENAPI_SERVICE_KEY=공공데이터포털_인증키
OPENAPI_CALLBACKURL=기상청_API_URL
OPENAPI_TYPE=JSON
```
## 3. Redis 실행

```bash
docker run -d --name redis-container -p 6379:6379 redis:7.2
```

Redis Insight가 필요한 경우 다음과 같이 실행할 수 있습니다.

```bash
docker run -d \
  --name redis-insight \
  -p 8001:5540 \
  redis/redisinsight:latest
```

접속 주소:

```text
http://localhost:8001
```

## 4. Backend 실행

```bash
cd be/inspire_jpa
./gradlew bootRun
```

Backend 기본 주소:

```text
http://localhost:8000
```

## 5. Frontend 실행

```bash
cd fe/react-app
npm install
npm start
```

Frontend 기본 주소는 Vite 설정을 따릅니다.

## 6. Swagger 확인

```text
http://localhost:8000/swagger-ui/index.html
```
---
