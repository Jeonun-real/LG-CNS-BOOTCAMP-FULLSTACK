# BE 9일차 - 데이터베이스 기초와 SQL SELECT 문법

## 1. 오늘의 핵심

오늘은 데이터베이스와 관계형 모델의 기본 개념을 학습하고, MySQL에서 `SELECT` 문으로 원하는 데이터를 조회하는 방법을 실습했다.

```text
데이터베이스 기초
  ↓
관계형 데이터 모델링
  ↓
PK·FK·참조 무결성·인덱스
  ↓
SELECT와 조건 검색
```

### 핵심 키워드

- DB, DBMS, SQL
- 관계형 데이터베이스와 NoSQL
- 개념·논리·물리 모델링
- Entity, Column, Record
- PK, FK, 참조 무결성
- 정규화와 역정규화
- Index
- DDL, DML, DCL, TCL, DQL
- `SELECT`, `FROM`, `WHERE`
- `DISTINCT`, 별칭, 표현식
- `IFNULL()`, `NULLIF()`, `IS NULL`
- `BETWEEN`, `LIKE`, `IN`
- `AND`, `OR`, `NOT`, `CONCAT()`

---

## 2. 데이터베이스 기본 개념

데이터는 가공 전의 사실이나 값이고, 정보는 데이터를 목적에 맞게 가공하여 의미를 만든 결과다.

```text
데이터(Data) → 가공·분석 → 정보(Information)
쌀           → 조리       → 밥
```

데이터베이스는 여러 사용자가 공유할 수 있도록 통합하여 관리하는 데이터의 집합이다. 파일만 사용하면 중복, 동시 접근, 검색, 보안, 복구와 데이터 일관성 관리가 어려워진다.

```text
사용자 또는 애플리케이션
  ↓ SQL
DBMS
  ↓
Database
```

- DB: 실제 데이터의 집합
- DBMS: 데이터베이스를 생성하고 관리하는 소프트웨어
- SQL: 관계형 DBMS에 명령을 전달하는 언어

### 관계형 데이터베이스와 NoSQL

관계형 데이터베이스는 데이터를 행과 열로 구성된 테이블에 저장하고, PK와 FK로 업무적인 관계를 표현한다.

NoSQL은 관계형 테이블 외의 모델을 사용하는 데이터베이스를 통칭한다. 키-값 방식은 Java의 Map과 비슷하게 이해할 수 있지만, NoSQL에는 문서·컬럼 패밀리·그래프 등 다양한 모델이 있다.

---

## 3. 데이터베이스 모델링

```text
개념 모델링 → 논리 모델링 → 물리 모델링 → DB 구축
```

### 개념 모델링

업무에서 관리할 Entity와 관계를 찾고 ERD로 표현한다.

```text
부서 ── 소속 ── 사원
고객 ── 주문 ── 주문내역
```

### 논리 모델링

Entity의 속성, PK, FK와 관계를 구체화하고 정규화를 수행한다. 정규화는 중복과 삽입·수정·삭제 이상을 줄이도록 데이터를 논리적으로 분해하는 과정이다.

### 물리 모델링

실제 DBMS에 맞춰 데이터 타입, 테이블, 제약조건과 인덱스를 결정한다. 조회 성능을 위해 의도적으로 구조를 합치는 역정규화를 적용할 수도 있지만, 데이터 일관성과 성능을 함께 검토해야 한다.

---

## 4. 관계형 데이터베이스의 구성 요소

| 개념 | DB 표현 | 예시 |
|---|---|---|
| Entity | Table | 사원 → `employee` |
| Attribute | Column | 이름 → `emp_name` |
| Instance | Row 또는 Record | 사원 한 명의 데이터 |
| Identifier | PK | `emp_id` |

Java 애플리케이션에서는 DTO 필드가 조회 결과의 Column과 대응될 수 있다.

```text
EMP_ID   ↔ empId
EMP_NAME ↔ empName
SALARY   ↔ salary
```

DTO는 전달 목적에 따라 필요한 필드만 가질 수 있으므로 테이블 구조와 항상 완전히 일치하는 것은 아니다.

---

## 5. PK, FK와 관계

### PK(Primary Key)

테이블의 각 행을 유일하게 식별한다.

- 중복 불가
- `NULL` 불가
- 여러 Column을 묶은 복합키 가능

### FK(Foreign Key)

다른 테이블의 PK 또는 UNIQUE Key를 참조하는 Column이다. 일반적인 1:N 관계에서는 부모의 PK 값이 자식의 FK로 전이된다.

```text
department                   employee
dept_id(PK) = 90 ─────────> dept_id(FK) = 90
```

FK는 관계가 선택적이면 `NULL`을 허용할 수 있다.

### 관계 유형

- 존재 관계: 부서에 사원이 소속된다.
- 행위 관계: 고객이 주문한다.
- 식별 관계: 부모 PK가 자식 PK의 일부가 된다.
- 비식별 관계: 부모 PK가 자식의 일반 FK가 된다.

ERD 표기법에 따라 비식별 관계는 점선으로 표현할 수 있다.

---

## 6. 참조 무결성

FK 값은 부모 테이블에 실제로 존재하거나, 허용된 경우 `NULL`이어야 한다.

```text
부모에 dept_id = 90 존재 → 자식의 FK에 90 저장 가능
부모에 dept_id = 999 없음 → 자식의 FK에 999 저장 제한
```

자식이 참조하는 부모를 삭제할 때는 다음 정책을 사용할 수 있다.

- RESTRICT / NO ACTION: 자식이 있으면 부모 삭제 제한
- CASCADE: 부모와 관련 자식을 함께 삭제
- SET NULL: 자식 FK를 `NULL`로 변경

`SET NULL`을 사용하려면 FK Column이 `NULL`을 허용해야 한다.

---

## 7. 정규화와 Transaction

서로 다른 성격의 데이터를 한 테이블에 넣으면 중복과 많은 `NULL`이 발생한다.

```text
하나의 큰 테이블
  ├─ 데이터 중복
  ├─ 삽입 이상
  ├─ 수정 이상
  └─ 삭제 이상
```

정규화는 테이블을 업무 단위로 분리하고 PK와 FK로 다시 연결한다. 분리된 여러 테이블을 하나의 업무로 함께 처리해야 할 때는 Transaction으로 묶는다.

```text
Transaction 시작 → 여러 SQL 실행 → COMMIT 또는 ROLLBACK
```

---

## 8. 인덱스

인덱스는 특정 Column의 값과 행의 위치를 이용해 검색 속도를 높이는 별도의 자료구조다.

- PK에는 일반적으로 인덱스가 자동 생성된다.
- FK 인덱스 자동 생성 여부는 DBMS에 따라 다르다.
- 검색과 정렬 성능을 높일 수 있다.
- 저장 공간을 사용한다.
- 입력·수정·삭제 시 인덱스도 갱신해야 한다.
- 모든 Column에 무조건 생성하면 오히려 비용이 커진다.

`NULL`이 포함됐다고 인덱스가 무조건 무효화되는 것은 아니다. DBMS와 인덱스 종류, 쿼리에 따라 달라지므로 실행 계획으로 확인해야 한다.

---

## 9. SQL 명령어 분류

| 분류 | 역할 | 명령어 |
|---|---|---|
| DDL | 구조 정의 | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| DML | 데이터 조작 | `INSERT`, `UPDATE`, `DELETE` |
| DQL | 데이터 조회 | `SELECT` |
| DCL | 권한 제어 | `GRANT`, `REVOKE` |
| TCL | 트랜잭션 제어 | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

`COMMIT`과 `ROLLBACK`은 일반적으로 DCL이 아니라 TCL로 분류한다.

---

## 10. 데이터베이스와 테이블 확인

`inspire_rds_6th.sql`에서는 MySQL의 `lgcns` 데이터베이스를 사용했다.

```sql
USE lgcns;
SHOW DATABASES;
DESC country;
```

- `USE lgcns`: 기본 데이터베이스 선택
- `SHOW DATABASES`: 데이터베이스 목록 확인
- `DESC country`: 테이블의 Column, 타입, Key, NULL 허용 여부 확인

```sql
SELECT * FROM job;
SELECT * FROM employee;
SELECT * FROM department;
```

---

## 11. SELECT 기본 문법

```sql
SELECT [DISTINCT] Column | 표현식 | 함수
FROM 테이블
WHERE 행 조건
GROUP BY 그룹 기준
HAVING 그룹 조건
ORDER BY 정렬 기준 ASC | DESC;
```

| 절 | 역할 |
|---|---|
| `SELECT` | 출력할 Column과 표현식 결정 |
| `FROM` | 대상 테이블 지정 |
| `WHERE` | 행 필터링 |
| `GROUP BY` | 데이터 그룹화 |
| `HAVING` | 그룹 결과 필터링 |
| `ORDER BY` | 최종 결과 정렬 |

```text
작성 순서: SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY
처리 순서: FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY
```

### 전체 Column과 일부 Column 조회

```sql
SELECT *
FROM employee;

SELECT emp_id,
       emp_name
FROM employee;
```

실무에서는 필요한 Column을 명시하면 의도가 분명하고 불필요한 데이터 전송을 줄일 수 있다.

---

## 12. WHERE와 연산자

### 비교와 논리 연산자

```sql
SELECT *
FROM employee
WHERE dept_id = '90'
  AND salary >= 4000000;
```

- `AND`: 모든 조건이 참
- `OR`: 하나 이상 참
- `NOT`: 조건을 반대로 변경
- 비교: `=`, `!=`, `<>`, `>`, `>=`, `<`, `<=`

“부서가 90이거나 급여가 4,000,000 이상”이면 `AND`가 아니라 `OR`다.

```sql
WHERE dept_id = '90'
   OR salary >= 4000000;
```

### BETWEEN

상한과 하한을 모두 포함한다.

```sql
SELECT emp_name,
       salary,
       job_id
FROM employee
WHERE salary BETWEEN 3500000 AND 5500000;
```

이는 `salary >= 3500000 AND salary <= 5500000`과 같다.

### IN

같은 Column을 여러 값과 비교한다.

```sql
SELECT *
FROM employee
WHERE dept_id IN ('60', '90');
```

이는 다음 OR 조건과 같다.

```sql
WHERE dept_id = '60'
   OR dept_id = '90'
```

---

## 13. 별칭과 CONCAT()

### 별칭

```sql
SELECT emp_name AS 사원이름,
       salary AS `월 급`
FROM employee;
```

MySQL에서 공백이나 특수문자가 포함된 식별자 별칭은 백틱을 사용하는 것이 명확하다. 문자열 데이터는 작은따옴표로 감싼다.

### CONCAT()

```sql
SELECT CONCAT(
           emp_name,
           '님의 급여는 ',
           salary,
           '(월)입니다.'
       ) AS 급여정보
FROM employee;
```

여러 문자열과 Column 값을 하나의 문자열로 연결한다.

---

## 14. NULL 처리

`NULL`은 값이 없거나 알 수 없다는 의미이며 0이나 빈 문자열과 다르다.

```text
NULL ≠ 0
NULL ≠ ''
NULL ≠ 공백
```

산술식에 `NULL`이 포함되면 결과도 일반적으로 `NULL`이 된다.

### IFNULL()

첫 번째 값이 `NULL`이면 두 번째 값으로 대체한다.

```sql
SELECT IFNULL(bonus_pct, 0)
FROM employee;
```

### NULLIF()

두 값이 같으면 `NULL`, 다르면 첫 번째 값을 반환한다.

```sql
SELECT NULLIF(100, 100),  -- NULL
       NULLIF(100, 200);  -- 100
```

### IS NULL과 IS NOT NULL

```sql
SELECT *
FROM employee
WHERE dept_id IS NULL;

SELECT *
FROM employee
WHERE dept_id IS NOT NULL;
```

`NULL`은 `= NULL`로 비교하지 않는다.

MySQL의 `ISNULL(value)`는 값이 `NULL`이면 1, 아니면 0을 반환한다.

```sql
WHERE ISNULL(dept_id) = 1
```

### 보너스를 반영한 연봉

```sql
SELECT emp_name,
       salary,
       (salary + salary * IFNULL(bonus_pct, 0)) * 12
           AS annual_salary
FROM employee;
```

---

## 15. DISTINCT

```sql
SELECT DISTINCT dept_id,
                job_id
FROM employee;
```

`DISTINCT`는 중복된 결과를 한 번만 출력한다. 여러 Column을 지정하면 각 Column이 아니라 값의 조합을 기준으로 중복을 제거한다.

```text
(60, J1)
(60, J1) → 중복 제거
(60, J2) → 다른 조합이므로 유지
```

---

## 16. LIKE와 와일드카드

| 기호 | 의미 |
|---|---|
| `%` | 0개 이상의 임의 문자 |
| `_` | 정확히 한 개의 임의 문자 |

### 김씨 성을 가진 사원

```sql
SELECT *
FROM employee
WHERE emp_name LIKE '김%';
```

김씨가 아닌 사원은 `NOT LIKE`를 사용한다.

```sql
WHERE emp_name NOT LIKE '김%'
```

### 언더스코어 앞이 세 글자인 이메일

```sql
SELECT *
FROM employee
WHERE email LIKE '___\_%';
```

```text
___  → 앞 문자 정확히 세 개
\_   → 실제 언더스코어 문자
%    → 뒤에 문자 0개 이상
```

`_` 자체가 와일드카드이므로 실제 문자로 검색할 때는 이스케이프한다.

---

## 17. practice.sql 문제 풀이

### 1. 학과 이름과 계열

```sql
SELECT department_name AS `학과 명`,
       category AS 계열
FROM tb_department;
```

### 2. 학과별 정원 문장

```sql
SELECT CONCAT(
           department_name,
           '의 정원은 ',
           capacity,
           '명 입니다'
       ) AS `학과별 정원`
FROM tb_department;
```

### 3. 국어국문학과의 휴학 중인 여학생

```sql
SELECT student_name
FROM tb_student
WHERE department_no = (
          SELECT department_no
          FROM tb_department
          WHERE department_name = '국어국문학과'
      )
  AND student_ssn LIKE '%-2%'
  AND absence_yn = 'Y';
```

학생 테이블에는 학과 이름 대신 학과 번호가 있으므로 서브쿼리로 국어국문학과의 번호를 먼저 구한다. `practice.sql`의 첫 번째 3번 쿼리는 `WHERE s`처럼 미완성되어 실행할 수 없고, 그 아래 서브쿼리 형태가 완성된 쿼리다.

### 4. 지정된 학번의 학생

```sql
SELECT student_name
FROM tb_student
WHERE student_no IN (
    'A513079', 'A513090', 'A513091',
    'A513110', 'A513119'
);
```

### 5. 정원이 20명 이상 30명 이하인 학과

```sql
SELECT department_name,
       category
FROM tb_department
WHERE capacity BETWEEN 20 AND 30;
```

### 6. 소속 학과가 없는 교수

```sql
SELECT professor_name
FROM tb_professor
WHERE department_no IS NULL;
```

### 7. 소속 학과가 없는 학생 수

```sql
SELECT COUNT(*) AS `학생 수`
FROM tb_student
WHERE department_no IS NULL;
```

`COUNT(*)`는 조건을 만족하는 행의 개수를 반환한다.

### 8. 선수 과목이 있는 과목

```sql
SELECT class_no
FROM tb_class
WHERE preattending_class_no IS NOT NULL;
```

### 9. 학과 계열의 종류

```sql
SELECT DISTINCT category
FROM tb_department;
```

### 10. 2002년 입학한 전주 거주 재학생

```sql
SELECT student_no,
       student_name,
       student_ssn
FROM tb_student
WHERE student_address LIKE '%전주%'
  AND absence_yn = 'N'
  AND entrance_date >= '2002-01-01'
  AND entrance_date <  '2003-01-01';
```

실습 파일의 `entrance_date LIKE '2002%'`도 환경에 따라 동작할 수 있지만, 날짜 타입이라면 날짜 범위를 사용하는 방식이 타입과 의도가 분명하고 인덱스 활용에도 유리할 수 있다.

---

## 18. 실습 SQL에서 확인할 부분

### 문제 설명과 AND/OR 맞추기

“90번 부서이거나 급여가 4,000,000 이상”이면 다음과 같다.

```sql
WHERE dept_id = '90'
   OR salary >= 4000000
```

### LIKE와 NOT LIKE 구분

- `LIKE '김%'`: 김씨인 사원
- `NOT LIKE '김%'`: 김씨가 아닌 사원

### NULL 비교

```sql
-- 잘못된 방식
WHERE department_no = NULL

-- 올바른 방식
WHERE department_no IS NULL
```

### 문장 끝의 세미콜론

각 SQL 문장은 `;`으로 끝내는 습관을 들이면 여러 문장을 실행할 때 경계를 명확히 할 수 있다.

---

## 19. 최종 정리

```text
현실 업무 분석
  ↓
Entity와 관계 도출
  ↓
PK와 FK 설정
  ↓
참조 무결성과 정규화 적용
  ↓
테이블 구축
  ↓
SELECT로 데이터 검색
  ├─ WHERE: 행 제한
  ├─ DISTINCT: 중복 제거
  ├─ IFNULL: NULL 대체
  ├─ BETWEEN: 범위 검색
  ├─ LIKE: 패턴 검색
  ├─ IN: 여러 값 비교
  └─ CONCAT: 문자열 연결
```

오늘 학습의 핵심은 SQL 문법만 외우는 것이 아니다. 업무 대상을 Entity로 나누고 PK와 FK로 관계를 표현한 뒤, 참조 무결성을 유지하면서 필요한 데이터를 SQL로 조회하는 전체 흐름을 이해하는 것이다.

