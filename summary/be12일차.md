# BE 12일차 - Subquery, TOP-N, 상관 서브쿼리와 DDL

## 1. 오늘의 핵심

오늘은 하나의 SQL 안에 다른 SQL을 포함하는 Subquery를 학습했다. 서브쿼리의 반환 행과 Column 수에 따라 연산자를 다르게 사용하고, 서브쿼리가 들어가는 위치에 따라 Scalar Subquery, Inline View, WHERE 절 Subquery로 구분했다.

이후 `LIMIT`을 이용한 TOP-N 조회, 외부 쿼리의 값을 사용하는 상관 서브쿼리, 실행 계획을 확인하는 `EXPLAIN`을 살펴봤다. 마지막에는 DDL로 데이터베이스와 테이블을 생성하고 제약조건을 설정한 뒤 `INSERT`로 데이터를 입력했다.

```text
Subquery
  ├─ 단일행 / 다중행
  ├─ 단일열 / 다중열
  ├─ Scalar Subquery
  ├─ Inline View
  └─ Correlated Subquery
        ↓
TOP-N · LIMIT · OFFSET
        ↓
EXPLAIN으로 실행 계획 확인
        ↓
DDL로 Table 생성
        ↓
Constraint로 무결성 설정
        ↓
DML INSERT로 데이터 입력
```

### 주요 문법

- 단일행·다중행 서브쿼리
- 단일열·다중열 서브쿼리
- `IN`, `ANY`, `ALL`
- Scalar Subquery
- Inline View
- 상관 서브쿼리
- `LIMIT`, `OFFSET`
- `RANK()`
- `EXPLAIN`
- `CREATE DATABASE`, `CREATE TABLE`
- `DROP DATABASE`, `DROP TABLE`
- `PRIMARY KEY`, `FOREIGN KEY`
- `NOT NULL`, `UNIQUE`, `CHECK`, `DEFAULT`
- `INSERT INTO ... VALUES`

---

## 2. Subquery란?

Subquery는 하나의 SQL 안에 포함된 또 다른 SQL이다.

```sql
SELECT ...
FROM ...
WHERE column = (
    SELECT ...
    FROM ...
    WHERE ...
);
```

```text
외부 쿼리(Main Query)
  ↓ 서브쿼리 결과 사용
내부 쿼리(Subquery)
```

일반적으로 내부 서브쿼리가 먼저 실행되어 결과를 만들고, 외부 쿼리가 그 결과를 사용한다. 다만 실제 실행 방식은 DBMS Optimizer가 실행 계획에 따라 바꿀 수 있다.

---

## 3. 서브쿼리의 분류

서브쿼리는 반환하는 행과 Column 수에 따라 나눌 수 있다.

| 유형 | 반환 형태 | 주로 사용하는 연산자 |
|---|---|---|
| 단일행·단일열 | 값 하나 | `=`, `>`, `<`, `>=`, `<=`, `<>` |
| 단일행·다중열 | 한 행의 여러 값 | 행 값 비교 |
| 다중행·단일열 | 한 Column의 여러 행 | `IN`, `ANY`, `ALL` |
| 다중행·다중열 | 여러 행과 여러 Column | 다중 Column `IN`, JOIN 등 |

서브쿼리가 여러 행을 반환하는데 `=`을 사용하면 “Subquery returns more than 1 row” 오류가 발생할 수 있다.

---

## 4. 서브쿼리가 사용되는 위치

### SELECT: Scalar Subquery

SELECT 절에서 하나의 값을 반환하는 서브쿼리다.

```sql
SELECT e.emp_name,
       e.salary,
       (
           SELECT AVG(salary)
           FROM employee
       ) AS company_average
FROM employee AS e;
```

각 사원 행에 전체 사원의 평균 급여를 하나의 Column으로 추가한다. SELECT에 사용하는 Scalar Subquery는 행마다 값 하나를 반환해야 한다.

### FROM: Inline View

FROM 절에 들어간 서브쿼리 결과를 가상의 테이블처럼 사용한다.

```sql
SELECT v.dept_name,
       v.total_salary
FROM (
    SELECT d.dept_name,
           SUM(e.salary) AS total_salary
    FROM employee AS e
    JOIN department AS d
      ON e.dept_id = d.dept_id
    GROUP BY d.dept_name
) AS v;
```

```text
내부 SELECT 결과
  ↓
가상 테이블 v
  ↓
외부 SELECT에서 다시 조회
```

Inline View에는 반드시 별칭을 지정해야 한다.

### WHERE: 조건 서브쿼리

```sql
SELECT *
FROM employee
WHERE salary = (
    SELECT MIN(salary)
    FROM employee
);
```

전체 사원 중 최소 급여와 같은 급여를 받는 사원을 조회한다.

---

## 5. 단일행·단일열 서브쿼리

나승원 사원과 같은 직급이면서 더 많은 급여를 받는 사원을 검색한다.

```sql
SELECT *
FROM employee
WHERE job_id = (
          SELECT job_id
          FROM employee
          WHERE emp_name = '나승원'
      )
  AND salary > (
          SELECT salary
          FROM employee
          WHERE emp_name = '나승원'
      );
```

두 서브쿼리는 각각 하나의 값을 반환한다고 가정한다.

```text
나승원의 job_id ──> 같은 직급 조건
나승원의 salary ──> 더 높은 급여 조건
```

만약 `emp_name = '나승원'`인 행이 여러 개라면 단일행 서브쿼리가 여러 행을 반환하여 오류가 생긴다. 실제 시스템에서는 이름보다 PK 같은 유일한 값으로 대상을 찾는 것이 안전하다.

---

## 6. 중첩 서브쿼리 예제

국어국문학과에 소속된 휴학생 중 주민번호 뒷자리 첫 숫자가 2인 학생을 조회한다.

```sql
SELECT s.student_name
FROM tb_student AS s
WHERE s.absence_yn = 'Y'
  AND s.department_no = (
          SELECT d.department_no
          FROM tb_department AS d
          WHERE d.department_name = '국어국문학과'
      )
  AND SUBSTRING(s.student_ssn, 8, 1) = '2';
```

원본 SQL에는 다음 오타와 연산자 누락이 있다.

- `ABSENSE_YN` → `ABSENCE_YN`
- `STUDNET_SSN` → `STUDENT_SSN`
- `DEPARTMENT_NO (subquery)` → `DEPARTMENT_NO = (subquery)`

서브쿼리 안에서 국어국문학과 번호를 구한 뒤 외부 학생 조회 조건으로 사용한다.

---

## 7. 부서별 급여 합계가 가장 높은 부서

### 방법 1: GROUP BY + ORDER BY + LIMIT

```sql
SELECT d.dept_name,
       SUM(e.salary) AS total_salary
FROM employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id
GROUP BY d.dept_name
ORDER BY total_salary DESC
LIMIT 1;
```

```text
부서별 급여 합계
  ↓ 내림차순 정렬
가장 큰 합계가 첫 번째 행
  ↓ LIMIT 1
최상위 한 행 반환
```

이 방식은 가장 간단하지만 공동 1위가 여러 부서여도 한 행만 반환한다.

### 방법 2: Inline View + MAX()

```sql
SELECT v.dept_name,
       v.total_salary
FROM (
    SELECT d.dept_name,
           SUM(e.salary) AS total_salary
    FROM employee AS e
    JOIN department AS d
      ON e.dept_id = d.dept_id
    GROUP BY d.dept_name
) AS v
WHERE v.total_salary = (
    SELECT MAX(v2.total_salary)
    FROM (
        SELECT SUM(e2.salary) AS total_salary
        FROM employee AS e2
        GROUP BY e2.dept_id
    ) AS v2
);
```

내부에서 부서별 합계를 만든 뒤 그중 최댓값과 같은 부서를 찾는다. 동일한 최대 합계를 가진 부서가 여러 개라면 모두 반환할 수 있다.

### 방법 3: HAVING + 중첩 Inline View

```sql
SELECT d.dept_name,
       SUM(e.salary) AS total_salary
FROM employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id
GROUP BY d.dept_name
HAVING SUM(e.salary) = (
    SELECT MAX(v.total_salary)
    FROM (
        SELECT SUM(salary) AS total_salary
        FROM employee
        GROUP BY dept_id
    ) AS v
);
```

`HAVING`은 그룹별 `SUM(salary)` 결과를 최대 합계와 비교한다.

---

## 8. Inline View와 Window Function 조합

국어국문학과에서 평균 학점이 가장 높은 학생을 구한다.

```sql
SELECT v.student_no,
       v.student_name,
       v.average_point
FROM (
    SELECT s.student_no,
           s.student_name,
           AVG(g.point) AS average_point,
           RANK() OVER (
               ORDER BY AVG(g.point) DESC
           ) AS grade_rank
    FROM tb_grade AS g
    JOIN tb_student AS s
      ON g.student_no = s.student_no
    JOIN tb_department AS d
      ON s.department_no = d.department_no
    WHERE d.department_name = '국어국문학과'
    GROUP BY s.student_no,
             s.student_name
) AS v
WHERE v.grade_rank = 1;
```

```text
국어국문학과 학생 선택
  ↓
학생별 AVG(point)
  ↓
평균 내림차순 RANK()
  ↓ Inline View
rank = 1 필터링
```

`RANK()`를 사용했으므로 공동 1위가 여러 명이면 모두 반환한다. `ORDER BY AVG(point) DESC LIMIT 1`을 사용하면 한 명만 반환한다.

---

## 9. 집계 함수와 일반 Column 혼용 오류

```sql
SELECT e.*,
       MIN(salary)
FROM employee AS e;
```

위 SQL은 전체 최소 급여를 계산하면서 어떤 사원 행을 함께 출력해야 하는지 기준이 없다.

집계 결과만 필요하면 다음처럼 작성한다.

```sql
SELECT MIN(salary)
FROM employee;
```

최소 급여를 받는 사원 정보가 필요하면 서브쿼리를 사용한다.

```sql
SELECT *
FROM employee
WHERE salary = (
    SELECT MIN(salary)
    FROM employee
);
```

---

## 10. 다중열 서브쿼리

부서별 최소 급여를 받는 사원을 조회한다.

```sql
SELECT emp_name,
       dept_id,
       salary
FROM employee
WHERE (dept_id, salary) IN (
    SELECT dept_id,
           MIN(salary)
    FROM employee
    GROUP BY dept_id
);
```

서브쿼리는 부서 번호와 최소 급여라는 두 Column을 여러 행으로 반환한다.

```text
서브쿼리 결과
(dept_id, min_salary)

(50, 2500000)
(60, 3000000)
(90, 4000000)
```

외부 행의 `(dept_id, salary)` 조합이 결과 안에 존재하면 선택된다.

### Window Function으로 해결

```sql
SELECT sub.emp_name,
       sub.dept_name,
       sub.salary
FROM (
    SELECT e.emp_name,
           d.dept_name,
           e.salary,
           RANK() OVER (
               PARTITION BY e.dept_id
               ORDER BY e.salary
           ) AS salary_rank
    FROM employee AS e
    JOIN department AS d
      ON e.dept_id = d.dept_id
) AS sub
WHERE sub.salary_rank = 1;
```

부서별 급여 오름차순 순위를 구하고 1위만 선택한다. 동점 최소 급여자가 있으면 모두 반환한다.

---

## 11. 다중행 서브쿼리: IN, ANY, ALL

서브쿼리가 여러 행을 반환할 때 단일 값 비교 연산자만 사용하면 안 된다.

### IN

결과 중 하나와 같으면 참이다.

```sql
WHERE job_id IN (
    SELECT job_id
    FROM employee
    WHERE dept_id = '50'
)
```

### ANY

서브쿼리 결과 중 하나라도 조건을 만족하면 참이다.

```text
> ANY → 결과의 최솟값보다 크면 참
< ANY → 결과의 최댓값보다 작으면 참
```

### ALL

서브쿼리의 모든 값에 대해 조건을 만족해야 참이다.

```text
> ALL → 결과의 최댓값보다 커야 참
< ALL → 결과의 최솟값보다 작아야 참
```

예를 들어 과장 중 가장 높은 급여보다 더 많은 급여를 받는 대리를 찾으려면 `> ALL`을 사용한다.

```sql
SELECT e.emp_name,
       e.salary
FROM employee AS e
JOIN job AS j
  ON e.job_id = j.job_id
WHERE j.job_title = '대리'
  AND e.salary > ALL (
      SELECT e2.salary
      FROM employee AS e2
      JOIN job AS j2
        ON e2.job_id = j2.job_id
      WHERE j2.job_title = '과장'
  );
```

과장 중 한 명보다만 급여가 높으면 되는 조건은 `> ANY`다.

원본 SQL의 `e.salary < ALL (...과장 급여...)`은 모든 과장보다 급여가 낮은 대리를 찾는 조건이므로 “과장보다 많은 급여”라는 주석과 반대다.

### 빈 결과에 대한 주의

서브쿼리 결과가 비어 있으면 `ANY`와 `ALL`의 논리 결과가 직관과 다르게 느껴질 수 있다. 비교 대상 데이터가 존재하는지 업무 조건과 함께 확인해야 한다.

---

## 12. TOP-N: LIMIT

정렬한 결과에서 상위 N개 행만 가져온다.

```sql
SELECT dept_id,
       SUM(salary) AS total_salary
FROM employee
GROUP BY dept_id
ORDER BY total_salary DESC
LIMIT 3;
```

급여 총합이 높은 상위 3개 부서를 반환한다.

```text
GROUP BY → 부서별 합계
ORDER BY DESC → 높은 순 정렬
LIMIT 3 → 앞의 세 행
```

`LIMIT`은 MySQL 문법이며 다른 DBMS에서는 `FETCH FIRST`, `TOP` 등 다른 문법을 사용할 수 있다.

---

## 13. OFFSET과 Pagination

```sql
SELECT dept_id,
       SUM(salary) AS total_salary
FROM employee
GROUP BY dept_id
ORDER BY total_salary DESC
LIMIT 3 OFFSET 0;
```

- `LIMIT 3`: 최대 3개 행
- `OFFSET 0`: 처음부터 시작

두 번째 페이지는 다음처럼 조회할 수 있다.

```sql
LIMIT 3 OFFSET 3
```

```text
페이지 크기 = 3

1페이지 → OFFSET 0
2페이지 → OFFSET 3
3페이지 → OFFSET 6
```

일반적인 Offset 계산은 다음과 같다.

```text
offset = (page - 1) × pageSize
```

OFFSET은 “n+1번부터 n번까지”가 아니라 앞의 N개 행을 건너뛴다는 의미다.

큰 OFFSET은 앞의 행을 많이 읽고 버려야 해 성능이 떨어질 수 있다. 대용량 데이터에서는 마지막으로 읽은 Key를 기준으로 다음 페이지를 찾는 Keyset Pagination도 고려한다.

---

## 14. 같은 직급의 평균 급여와 같은 사원

요구사항은 각 사원의 급여를 자기 직급의 평균 급여와 비교하는 것이다. 실습에서는 평균 급여를 정수 5자리 단위로 절삭했다.

### 방법 1: 다중열 IN

```sql
SELECT e.emp_name,
       j.job_title,
       e.salary
FROM employee AS e
JOIN job AS j
  ON e.job_id = j.job_id
WHERE (e.job_id, e.salary) IN (
    SELECT job_id,
           TRUNCATE(AVG(salary), -5)
    FROM employee
    GROUP BY job_id
);
```

직급 번호와 급여 두 값을 함께 비교해야 한다.

다음처럼 급여만 비교하면 다른 직급의 평균과 우연히 같은 급여도 선택될 수 있다.

```sql
-- 직급 관계가 빠진 잘못된 비교
WHERE e.salary IN (
    SELECT TRUNCATE(AVG(salary), -5)
    FROM employee
    GROUP BY job_id
)
```

### 방법 2: Inline View JOIN

```sql
SELECT e.emp_name,
       j.job_title,
       e.salary
FROM (
    SELECT job_id,
           TRUNCATE(AVG(salary), -5) AS job_average
    FROM employee
    GROUP BY job_id
) AS v
JOIN employee AS e
  ON v.job_id = e.job_id
 AND v.job_average = e.salary
JOIN job AS j
  ON e.job_id = j.job_id;
```

직급별 평균을 가상 테이블로 만든 뒤 직급과 급여 조건으로 사원과 연결한다.

---

## 15. 상관 서브쿼리

상관 서브쿼리는 내부 쿼리가 외부 쿼리의 Column을 참조한다.

```sql
SELECT e.emp_name,
       j.job_title,
       e.salary
FROM employee AS e
JOIN job AS j
  ON e.job_id = j.job_id
WHERE e.salary = (
    SELECT TRUNCATE(AVG(ee.salary), -5)
    FROM employee AS ee
    WHERE ee.job_id = e.job_id
);
```

핵심은 내부 쿼리의 다음 조건이다.

```sql
WHERE ee.job_id = e.job_id
```

- `ee.job_id`: 내부 쿼리의 사원
- `e.job_id`: 외부 쿼리의 현재 사원

```text
외부 사원 한 행 e
  ↓ e.job_id 전달
내부 쿼리
  ↓ 같은 job_id의 평균 급여 계산
외부 e.salary와 비교
```

일반 서브쿼리는 독립적으로 실행할 수 있지만, 상관 서브쿼리는 외부 쿼리의 값에 의존하므로 단독으로 완전한 의미를 갖지 못한다.

DBMS Optimizer가 실행 방식을 최적화할 수 있지만, 개념적으로는 외부 행마다 내부 조건이 달라진다.

---

## 16. EXPLAIN

`EXPLAIN`은 SQL이 데이터를 어떤 방법과 순서로 조회할지 실행 계획을 보여준다.

```sql
EXPLAIN
SELECT e.emp_name,
       j.job_title,
       e.salary
FROM employee AS e
JOIN job AS j
  ON e.job_id = j.job_id
WHERE e.salary = (
    SELECT TRUNCATE(AVG(ee.salary), -5)
    FROM employee AS ee
    WHERE ee.job_id = e.job_id
);
```

MySQL 실행 계획에서 주로 확인할 내용은 다음과 같다.

- 테이블 접근 순서
- 사용 가능한 인덱스
- 실제 선택된 인덱스
- JOIN 방식
- 예상 조회 행 수
- 추가 정렬이나 임시 테이블 사용 여부
- Subquery 실행 방식

`EXPLAIN`은 SQL 결과가 맞는지 확인하는 명령이 아니라 성능상 어떻게 실행되는지 분석하는 도구다.

---

## 17. DDL과 데이터베이스 객체

DDL은 데이터 저장 구조를 정의하는 언어다.

- `CREATE`: 객체 생성
- `ALTER`: 객체 구조 변경
- `DROP`: 객체 삭제

대표적인 데이터베이스 객체는 다음과 같다.

- TABLE
- VIEW
- INDEX
- SEQUENCE 또는 DBMS별 자동 증가 객체

### Table과 View

- Table: 데이터가 실제로 저장되는 기본 객체
- View: SELECT 결과를 이름으로 정의한 논리적·가상 테이블

일반 View는 조회 정의를 저장하며 결과 데이터를 독립적으로 물리 저장하지 않는다. Materialized View처럼 결과를 저장하는 별도 유형도 있지만 DBMS마다 지원 방식이 다르다.

---

## 18. 데이터베이스 생성과 삭제

```sql
DROP DATABASE IF EXISTS tabledb;
CREATE DATABASE tabledb;
USE tabledb;
```

- `IF EXISTS`: 객체가 있을 때만 삭제하여 오류 방지
- `CREATE DATABASE`: 데이터베이스 생성
- `USE`: 이후 사용할 기본 데이터베이스 선택

`DROP DATABASE`는 데이터베이스와 내부 객체를 삭제하는 파괴적인 명령이므로 실제 환경에서는 대상 이름을 반드시 확인해야 한다.

원본 SQL의 다음 부분은 데이터베이스 이름이 중복된 오타로 보인다.

```sql
DROP DATABASE IF EXISTS TABLEDBtb_blogtb_blog;
```

의도가 `TABLEDB` 초기화라면 `DROP DATABASE IF EXISTS tabledb;`로 작성한다.

---

## 19. CREATE TABLE 기본 문법

```sql
CREATE TABLE table_name (
    column_name data_type(size)
        [DEFAULT default_value]
        [column_constraint],

    [table_constraint]
);
```

Blog 테이블을 생성한 예는 다음과 같다.

```sql
DROP TABLE IF EXISTS tb_blog;

CREATE TABLE tb_blog (
    blog_id  VARCHAR(50),
    title    VARCHAR(100),
    content  VARCHAR(100),
    email    VARCHAR(100),
    category VARCHAR(100),
    create_at DATE,
    CONSTRAINT tb_blog_blog_id_pk
        PRIMARY KEY (blog_id),
    CONSTRAINT tb_blog_category_ck
        CHECK (
            category IN ('전체', '취미', '일상', '코딩')
        )
);
```

`DROP TABLE IF EXISTS`는 기존 테이블이 있을 때만 삭제한다.

---

## 20. 제약조건

제약조건은 잘못된 데이터가 저장되지 않도록 데이터베이스가 검사하는 규칙이다.

| 제약조건 | 역할 |
|---|---|
| `PRIMARY KEY` | 행을 유일하게 식별, 중복·NULL 불가 |
| `FOREIGN KEY` | 다른 테이블의 Key 참조 |
| `NOT NULL` | NULL 입력 금지 |
| `UNIQUE` | 중복 값 금지 |
| `CHECK` | 지정한 조건을 만족하는 값만 허용 |
| `DEFAULT` | 값이 생략될 때 기본값 사용 |

### Column Level 제약조건

Column 선언 옆에 작성한다.

```sql
category VARCHAR(100)
    CHECK (
        category IN ('전체', '취미', '일상', '코딩')
    )
```

### Table Level 제약조건

Column 정의가 끝난 뒤 작성한다.

```sql
CONSTRAINT tb_blog_blog_id_pk
    PRIMARY KEY (blog_id)
```

복합 PK나 복합 FK는 Table Level로 정의한다.

### 제약조건 이름

```text
테이블명_Column명_제약조건종류

tb_blog_blog_id_pk
tb_blog_category_ck
```

명시적인 이름을 붙이면 오류 메시지 확인과 제약조건 변경·삭제가 쉬워진다.

---

## 21. CHECK 제약조건

```sql
CONSTRAINT tb_blog_category_ck
CHECK (
    category IN ('전체', '취미', '일상', '코딩')
)
```

허용된 Category만 저장하도록 제한한다.

```text
'코딩' → 허용
'일상' → 허용
'뉴스' → CHECK 조건 위반
```

MySQL 버전에 따라 과거에는 CHECK 구문을 파싱만 하고 실제로 강제하지 않았던 버전이 있다. 현재 사용하는 DBMS 버전에서 제약조건이 실제 적용되는지 확인해야 한다.

---

## 22. DEFAULT

값을 생략했을 때 자동으로 사용할 기본값을 지정한다.

날짜만 저장하려면 다음과 같은 형태를 고려할 수 있다.

```sql
create_at DATE DEFAULT (CURRENT_DATE)
```

MySQL 버전에 따라 표현식 DEFAULT 지원 범위가 다를 수 있다. 생성 시각까지 필요하다면 다음처럼 작성하는 방식이 일반적이다.

```sql
create_at DATETIME DEFAULT CURRENT_TIMESTAMP
```

`DEFAULT`는 Column을 INSERT 목록에서 생략하거나 `DEFAULT` 키워드를 명시할 때 적용된다.

```sql
INSERT INTO tb_blog (
    blog_id,
    category,
    create_at
)
VALUES (
    '1',
    '전체',
    DEFAULT
);
```

명시적으로 `NULL`을 넣을 때 항상 DEFAULT로 바뀌는 것은 아니다. Column의 NULL 허용 여부와 DBMS 설정에 따라 `NULL` 저장 또는 오류가 발생할 수 있다.

---

## 23. DML INSERT

DDL로 구조를 만든 뒤 DML로 데이터를 입력한다.

```sql
INSERT INTO tb_blog (
    blog_id,
    category
)
VALUES (
    '1',
    '전체'
);
```

```text
INSERT INTO 테이블명 (입력할 Column)
VALUES (각 Column에 대응하는 값)
```

Column 목록과 VALUES의 값은 다음 조건을 만족해야 한다.

- 개수가 같아야 한다.
- 순서가 대응해야 한다.
- 데이터 타입이 호환되어야 한다.
- PK, NOT NULL, CHECK 등의 제약조건을 만족해야 한다.

입력 결과는 다음과 같이 확인한다.

```sql
SELECT *
FROM tb_blog;
```

---

## 24. 오늘 코드에서 주의할 점

### 서브쿼리 반환 건수 확인

- 한 행이면 일반 비교 연산자
- 여러 행이면 `IN`, `ANY`, `ALL`
- 여러 Column이면 비교할 외부 Column 수도 맞춰야 함

### 별칭 사용

Inline View에는 반드시 별칭을 지정한다.

```sql
FROM (
    SELECT ...
) AS v
```

### 공동 1위 처리

- `ORDER BY ... LIMIT 1`: 한 행만 반환
- `RANK() = 1`: 공동 1위 모두 반환
- `MAX()`와 비교: 최댓값과 같은 행 모두 반환

### ANY와 ALL 방향

```text
> ANY → 최소값보다 큼
> ALL → 최대값보다 큼
< ANY → 최대값보다 작음
< ALL → 최소값보다 작음
```

### 상관 조건 누락 주의

직급별 평균을 비교할 때 급여만 비교하면 다른 직급의 평균과 잘못 매칭될 수 있다. 반드시 `job_id` 관계도 함께 비교한다.

### DROP 대상 확인

`DROP DATABASE`, `DROP TABLE`은 객체와 데이터를 삭제하므로 실행 전 대상 이름과 환경을 확인한다.

---

## 25. 최종 흐름 정리

```text
Main Query
  ↓
Subquery로 필요한 기준값 계산
  ├─ 값 하나: 일반 비교 연산자
  ├─ 여러 값: IN·ANY·ALL
  └─ 여러 Column: 행 값 비교
  ↓
Inline View로 중간 결과를 Table처럼 사용
  ↓
LIMIT·RANK로 TOP-N 조회
  ↓
Correlated Subquery로 외부 행별 기준 계산
  ↓
EXPLAIN으로 실행 계획 확인
  ↓
CREATE TABLE로 구조 생성
  ↓
Constraint로 데이터 무결성 설정
  ↓
INSERT로 데이터 입력
```

오늘 학습의 핵심은 서브쿼리를 무조건 중첩하는 것이 아니다. 서브쿼리가 몇 개의 행과 Column을 반환하는지 먼저 판단하고, 목적에 따라 JOIN·Inline View·Window Function·상관 서브쿼리 중 읽기 쉽고 정확한 방법을 선택하는 것이다.
