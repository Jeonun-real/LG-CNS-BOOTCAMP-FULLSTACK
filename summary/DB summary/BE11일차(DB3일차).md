# BE 11일차 - GROUP BY, HAVING, Window Function과 JOIN

## 1. 오늘의 핵심

오늘은 여러 행을 기준별로 묶어 통계를 구하는 `GROUP BY`와 집계 결과를 제한하는 `HAVING`을 학습했다. 이어서 기존 행을 유지하면서 집계값과 순위를 추가하는 Window Function, 여러 테이블의 데이터를 연결하는 JOIN을 실습했다.

```text
여러 행
  ↓ WHERE
집계할 원본 행 선택
  ↓ GROUP BY
같은 값을 가진 행끼리 그룹화
  ↓ SUM·AVG·COUNT
그룹별 통계 계산
  ↓ HAVING
집계 결과 필터링
  ↓ ORDER BY
최종 결과 정렬
```

### 주요 문법

- `GROUP BY`
- `HAVING`
- `SUM()`, `AVG()`, `COUNT()`, `MIN()`, `MAX()`
- `WITH ROLLUP`
- `OVER()`, `PARTITION BY`
- `RANK()`, `DENSE_RANK()`, `ROW_NUMBER()`
- `INNER JOIN`
- `ON`, `USING`
- `CROSS JOIN`
- `LEFT JOIN`, `RIGHT JOIN`
- Equi Join과 Non-Equi Join
- 다중 JOIN
- Self Join
- 정규화

---

## 2. GROUP BY

`GROUP BY`는 특정 Column 또는 표현식의 값이 같은 행들을 하나의 그룹으로 묶는다. 주로 그룹별 통계를 구하기 위해 집계 함수와 함께 사용한다.

### 전체 데이터 집계

```sql
SELECT MIN(salary)
FROM employee;
```

그룹 기준이 없으므로 전체 사원 중 최소 급여 하나를 반환한다.

### 부서별 급여 합계

```sql
SELECT dept_id,
       SUM(salary) AS total_salary
FROM employee
GROUP BY dept_id;
```

```text
employee 전체 행
  ↓ dept_id 기준으로 분류
50번 부서 → SUM(salary)
60번 부서 → SUM(salary)
90번 부서 → SUM(salary)
NULL 부서 → SUM(salary)
```

### SELECT에 작성할 수 있는 항목

그룹 조회의 `SELECT` 절에는 일반적으로 다음 두 종류만 작성한다.

- `GROUP BY`에 포함된 Column 또는 표현식
- 집계 함수로 처리한 값

```sql
SELECT dept_id,
       SUM(salary)
FROM employee
GROUP BY dept_id;
```

다음 SQL은 부서별 그룹에서 어떤 사원 이름을 출력해야 하는지 정할 수 없으므로 올바른 그룹 조회가 아니다.

```sql
SELECT dept_id,
       emp_name,
       SUM(salary)
FROM employee
GROUP BY dept_id;
```

MySQL 설정에서 실행을 허용하는 경우가 있더라도 `emp_name`은 그룹을 대표하는 확정적인 값이 아니다. `ONLY_FULL_GROUP_BY` 모드에서는 오류가 발생한다.

### 여러 Column으로 그룹화

```sql
SELECT groupname,
       num,
       SUM(price * amount) AS total_price
FROM buytbl
GROUP BY groupname,
         num;
```

`groupname`과 `num` 값의 조합이 같은 행끼리 하나의 그룹이 된다.

---

## 3. SQL의 논리적 처리 순서

```sql
SELECT dept_id,
       SUM(salary)
FROM employee
WHERE salary > 3000000
GROUP BY dept_id
HAVING SUM(salary) >= 4000000
ORDER BY SUM(salary) DESC;
```

논리적으로는 다음 순서로 처리된다.

```text
1. FROM     → employee에서 데이터 가져오기
2. WHERE    → 급여가 3,000,000 초과인 행 선택
3. GROUP BY → dept_id별 그룹 생성
4. HAVING   → 합계가 4,000,000 이상인 그룹 선택
5. SELECT   → 부서 번호와 합계 생성
6. ORDER BY → 합계 내림차순 정렬
```

SQL을 작성하는 순서와 실제 논리적 처리 순서는 다르다.

| 작성 순서 | 논리적 처리 순서 |
|---|---|
| SELECT | FROM |
| FROM | WHERE |
| WHERE | GROUP BY |
| GROUP BY | HAVING |
| HAVING | SELECT |
| ORDER BY | ORDER BY |

---

## 4. WHERE와 HAVING의 차이

### WHERE

그룹을 만들기 전 개별 행을 필터링한다.

```sql
WHERE salary > 3000000
```

급여가 3,000,000 이하인 사원은 그룹 계산에 참여하지 않는다.

### HAVING

그룹화와 집계가 끝난 결과를 필터링한다.

```sql
HAVING SUM(salary) >= 4000000
```

급여 합계가 4,000,000 이상인 부서 그룹만 남긴다.

| 구문 | 필터링 대상 | 집계 함수 조건 |
|---|---|---|
| `WHERE` | 개별 행 | 일반적으로 사용 불가 |
| `HAVING` | 그룹 결과 | 사용 가능 |

### 부서별 급여 총액이 9,000,000을 초과하는 부서

```sql
SELECT dept_id,
       SUM(salary) AS total_salary
FROM employee
GROUP BY dept_id
HAVING SUM(salary) > 9000000;
```

요구사항이 “9,000,000 이상”이면 `>`가 아니라 `>=`를 사용해야 한다.

---

## 5. 표현식으로 그룹화

급여를 초급·중급·고급으로 분류한 뒤 등급별 인원수를 계산한다.

```sql
SELECT CASE
           WHEN salary <= 3000000 THEN '초급'
           WHEN salary <= 4000000 THEN '중급'
           ELSE '고급'
       END AS grade,
       COUNT(*) AS employee_count
FROM employee
GROUP BY grade;
```

```text
각 사원의 salary
  ↓ CASE
초급 / 중급 / 고급
  ↓ GROUP BY grade
등급별 그룹
  ↓ COUNT(*)
등급별 인원수
```

MySQL에서는 SELECT 별칭을 `GROUP BY`에서 사용할 수 있다. DBMS 간 호환성을 고려하면 CASE 표현식을 반복하거나 서브쿼리·CTE로 먼저 등급을 만드는 방법도 있다.

### 위치 번호로 그룹화

```sql
SELECT CASE
           WHEN salary <= 3000000 THEN '초급'
           WHEN salary <= 4000000 THEN '중급'
           ELSE '고급'
       END AS grade,
       COUNT(*)
FROM employee
GROUP BY 1;
```

`GROUP BY 1`은 SELECT 목록의 첫 번째 표현식을 의미한다. 간단하지만 SELECT 순서가 바뀌면 그룹 기준도 달라지므로 이름이나 표현식을 명시하는 편이 유지보수에 유리하다.

---

## 6. FIELD를 이용한 사용자 지정 정렬

문자열의 가나다순이 아니라 원하는 업무 순서로 정렬할 수 있다.

```sql
SELECT CASE
           WHEN salary <= 3000000 THEN '초급'
           WHEN salary <= 4000000 THEN '중급'
           ELSE '고급'
       END AS grade,
       COUNT(*) AS employee_count
FROM employee
GROUP BY grade
ORDER BY FIELD(grade, '초급', '중급', '고급');
```

`FIELD(value, v1, v2, v3)`는 `value`가 목록에서 몇 번째인지 반환하며 그 번호를 기준으로 정렬한다.

```text
초급 → 1
중급 → 2
고급 → 3
```

---

## 7. 구매 데이터 그룹 실습

### 사용자별 구매 총액

```sql
SELECT userid,
       SUM(price * amount) AS total_price
FROM buytbl
GROUP BY userid;
```

각 구매 행에서 `price * amount`를 계산한 뒤 사용자별로 더한다.

### 사용자별 평균 구매 개수

```sql
SELECT userid,
       ROUND(AVG(amount)) AS average_amount
FROM buytbl
GROUP BY userid;
```

`AVG(amount)`로 평균을 구하고 `ROUND()`로 반올림한다.

### 구매 총액이 100을 초과한 사용자

```sql
SELECT userid,
       SUM(price * amount) AS total_price
FROM buytbl
GROUP BY userid
HAVING SUM(price * amount) > 100
ORDER BY total_price DESC;
```

```text
buytbl
  ↓ userid별 그룹화
price × amount 합산
  ↓ HAVING
총액 100 초과 사용자만 선택
  ↓ ORDER BY
총액 내림차순
```

---

## 8. 조건 검색 후 그룹화

50번 부서 또는 미배치 사원을 대상으로 성별 평균 급여를 구한다.

```sql
SELECT CASE
           WHEN SUBSTRING(emp_no, 8, 1) IN ('1', '3')
               THEN '남자'
           WHEN SUBSTRING(emp_no, 8, 1) IN ('2', '4')
               THEN '여자'
           ELSE '확인 불가'
       END AS gender,
       ROUND(AVG(salary)) AS average_salary
FROM employee
WHERE dept_id = '50'
   OR dept_id IS NULL
GROUP BY gender
ORDER BY average_salary DESC;
```

처리 과정은 다음과 같다.

```text
WHERE
50번 부서 또는 미배치 사원 선택
  ↓ CASE
주민번호로 성별 값 생성
  ↓ GROUP BY
성별 그룹 생성
  ↓ AVG
성별 평균 급여
  ↓ ORDER BY DESC
평균 급여가 높은 순서
```

“급여가 많은 사원부터” 개별 행을 검색할 때는 다음과 같이 `DESC`가 필요하다.

```sql
SELECT emp_name,
       salary,
       dept_id
FROM employee
WHERE dept_id = '50'
   OR dept_id IS NULL
ORDER BY salary DESC;
```

`ORDER BY salary`만 작성하면 기본값인 오름차순이다.

---

## 9. WITH ROLLUP

`WITH ROLLUP`은 그룹별 집계와 상위 단계의 소계·총계를 함께 생성한다.

```sql
SELECT groupname,
       num,
       SUM(price * amount) AS total_price
FROM buytbl
GROUP BY groupname,
         num WITH ROLLUP;
```

그룹 기준을 오른쪽부터 하나씩 제거하면서 상위 집계를 만든다.

```text
(groupname, num)별 합계
          ↓
groupname별 소계
          ↓
전체 총계
```

ROLLUP으로 생성된 소계나 총계 행의 그룹 Column에는 `NULL`이 나타날 수 있다. 원본 데이터의 실제 `NULL`과 구분하려면 MySQL의 `GROUPING()` 지원 여부와 버전을 확인해 사용할 수 있다.

---

## 10. Window Function

그룹 함수와 Window Function 모두 여러 행을 이용해 계산하지만 결과의 행 수가 다르다.

### GROUP BY

그룹마다 한 행만 남는다.

```sql
SELECT dept_id,
       AVG(salary)
FROM employee
GROUP BY dept_id;
```

### Window Function

기존 사원 행을 유지하면서 계산 결과를 새로운 Column으로 추가한다.

```sql
SELECT emp_name,
       salary,
       dept_id,
       AVG(salary) OVER (
           PARTITION BY dept_id
       ) AS department_average
FROM employee;
```

```text
GROUP BY
사원 여러 행 → 부서별 한 행

Window Function
사원 여러 행 유지 + 각 행에 부서 평균 추가
```

### OVER와 PARTITION BY

- `OVER()`: 집계 함수를 Window Function으로 사용
- `PARTITION BY`: 계산할 그룹 범위 지정
- `ORDER BY`: Window 내부 순서 지정

---

## 11. 순위 Window Function

### RANK()

동점자에게 같은 순위를 주고 다음 순위를 건너뛴다.

```sql
SELECT emp_name,
       salary,
       RANK() OVER (
           ORDER BY salary DESC
       ) AS salary_rank
FROM employee;
```

```text
급여: 500, 400, 400, 300
RANK:   1,   2,   2,   4
```

### DENSE_RANK()

동점자에게 같은 순위를 주지만 다음 순위를 건너뛰지 않는다.

```sql
SELECT emp_name,
       salary,
       DENSE_RANK() OVER (
           ORDER BY salary DESC
       ) AS salary_rank
FROM employee;
```

```text
급여:       500, 400, 400, 300
DENSE_RANK:   1,   2,   2,   3
```

### ROW_NUMBER()

동점이어도 각 행에 서로 다른 순번을 부여한다.

```sql
SELECT emp_name,
       salary,
       ROW_NUMBER() OVER (
           ORDER BY salary DESC
       ) AS row_num
FROM employee;
```

```text
급여:       500, 400, 400, 300
ROW_NUMBER:   1,   2,   3,   4
```

### 부서별 급여 순위

```sql
SELECT emp_name,
       dept_id,
       salary,
       RANK() OVER (
           PARTITION BY dept_id
           ORDER BY salary DESC
       ) AS department_rank
FROM employee;
```

부서가 바뀔 때마다 순위가 1부터 다시 시작한다.

---

## 12. Mapping Rule과 정규화

Mapping Rule은 ERD의 Entity와 관계를 관계형 데이터베이스 스키마로 변환하는 규칙이다.

정규화는 데이터 중복과 이상 현상을 줄이도록 테이블을 분해하는 과정이다.

### 제1정규형

하나의 Column에는 원자값 하나만 저장하고 반복되는 속성을 분리한다.

```text
고객(전화번호1, 전화번호2, 전화번호3)
  ↓
고객 ── 1:N ── 고객전화번호
```

### 제2정규형

복합 PK를 사용하는 테이블에서 일반 Column이 복합키 전체에 완전하게 의존해야 한다. 복합키 일부에만 의존하는 부분 함수 종속을 분리한다.

### 제3정규형

일반 Column이 PK가 아닌 다른 일반 Column에 의존하는 이행 함수 종속을 분리한다.

```text
PK → 일반 Column A → 일반 Column B
                     ↓
              별도 테이블로 분리
```

정규화의 목적은 단순히 `NULL`을 모두 없애는 것이 아니라 중복과 삽입·수정·삭제 이상을 줄이고 데이터 종속성을 올바르게 만드는 것이다.

---

## 13. JOIN이란?

JOIN은 둘 이상의 테이블을 관계 조건으로 연결해 하나의 결과 집합을 만드는 문법이다.

```text
employee                    department
emp_name, dept_id     +     dept_id, dept_name
          └──── JOIN 조건 ────┘
                    ↓
          emp_name, dept_name
```

관계형 데이터베이스에서 데이터는 정규화를 통해 여러 테이블로 분리된다. JOIN은 분리된 데이터를 조회할 때 다시 연결한다.

### 관계의 수

- 1:1
- 1:N
- N:M

관계형 모델에서 N:M 관계는 연결 Entity를 추가해 두 개의 1:N 관계로 해소한다.

```text
학생 N : M 과목
       ↓
학생 1 : N 수강 N : 1 과목
```

---

## 14. JOIN 기본 문법

### ANSI JOIN

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id;
```

`JOIN`만 작성하면 기본적으로 `INNER JOIN`이다.

### 이전 방식의 JOIN

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e,
     department AS d
WHERE e.dept_id = d.dept_id;
```

실행 결과는 같을 수 있지만 JOIN 조건과 행 필터 조건이 모두 `WHERE`에 섞인다. 관계 조건을 `ON`에 분리하는 ANSI JOIN 방식이 의도를 파악하기 쉽다.

---

## 15. ON과 USING

### ON

연결할 Column 이름이 같거나 달라도 사용할 수 있으며 복잡한 조건도 작성할 수 있다.

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id;
```

### USING

두 테이블에서 JOIN Column의 이름이 같을 때 사용할 수 있다.

```sql
SELECT emp_name,
       dept_name
FROM employee
JOIN department
USING (dept_id);
```

| 구문 | 특징 |
|---|---|
| `ON` | Column 이름이 달라도 가능, 복합 조건 가능 |
| `USING` | 이름이 같은 JOIN Column을 간결하게 표현 |

`USING(dept_id)`를 사용하면 결과에서 공통 JOIN Column이 하나로 표현된다.

### NATURAL JOIN

이름이 같은 모든 Column을 자동으로 JOIN 조건으로 사용한다.

```sql
SELECT emp_name,
       dept_name
FROM employee
NATURAL JOIN department;
```

테이블 구조가 바뀌거나 우연히 같은 이름의 Column이 추가되면 조건이 달라질 수 있으므로 실무에서는 `ON`이나 `USING`으로 조건을 명시하는 편이 안전하다.

---

## 16. Equi Join

Equi Join은 동등 비교 `=`로 테이블을 연결한다. PK와 FK를 비교하는 JOIN이 대표적이다.

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id;
```

```text
department.dept_id(PK)
             =
employee.dept_id(FK)
```

조건이 일치하는 행만 반환하므로 부서가 없는 사원과 사원이 없는 부서는 INNER JOIN 결과에서 제외된다.

---

## 17. 다중 JOIN

세 개 이상의 테이블도 관계를 따라 차례대로 연결할 수 있다.

### 사원, 직급, 부서

```sql
SELECT e.emp_name,
       j.job_title,
       d.dept_name
FROM job AS j
JOIN employee AS e
  ON j.job_id = e.job_id
JOIN department AS d
  ON e.dept_id = d.dept_id;
```

```text
job
  ↓ job_id
employee
  ↓ dept_id
department
```

### 사원, 직급, 부서, 지역

```sql
SELECT e.emp_name,
       j.job_title,
       d.dept_name,
       l.loc_describe
FROM job AS j
JOIN employee AS e
  ON j.job_id = e.job_id
JOIN department AS d
  ON e.dept_id = d.dept_id
JOIN location AS l
  ON l.location_id = d.loc_id;
```

JOIN 조건이 빠지면 행이 잘못 조합될 수 있으므로 각 테이블이 어느 테이블과 어떤 Key로 연결되는지 먼저 확인해야 한다.

---

## 18. 다중 JOIN 후 WHERE 조건

해외 부서이면서 아시아 지역에 있는 사원을 검색한다.

```sql
SELECT e.emp_name,
       j.job_title,
       d.dept_name,
       l.loc_describe
FROM job AS j
JOIN employee AS e
  ON j.job_id = e.job_id
JOIN department AS d
  ON e.dept_id = d.dept_id
JOIN location AS l
  ON l.location_id = d.loc_id
JOIN country AS c
  ON c.country_id = l.country_id
WHERE d.dept_name LIKE '해외%'
  AND l.loc_describe LIKE '아시아%';
```

- `ON`: 테이블 사이의 관계 조건
- `WHERE`: 연결된 결과에서 필요한 행을 선택하는 조건

역할을 구분하면 복잡한 JOIN도 읽기 쉬워진다.

---

## 19. Non-Equi Join

Non-Equi Join은 `=`가 아닌 범위나 부등호 조건으로 테이블을 연결한다.

급여가 어느 급여 등급 범위에 포함되는지 찾는 예다.

```sql
SELECT e.emp_name,
       e.salary,
       s.slevel
FROM employee AS e
JOIN sal_grade AS s
  ON e.salary BETWEEN s.lowest AND s.highest
ORDER BY s.slevel DESC;
```

```text
sal_grade
lowest ≤ employee.salary ≤ highest
```

PK와 FK 관계가 아니더라도 업무 조건에 따라 범위 JOIN을 만들 수 있다.

---

## 20. CROSS JOIN

CROSS JOIN은 왼쪽 테이블의 모든 행과 오른쪽 테이블의 모든 행을 조합한다.

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
CROSS JOIN department AS d;
```

```text
employee 10행 × department 5행 = 최대 50행
```

JOIN 조건을 빠뜨린 INNER JOIN처럼 작성하면 의도하지 않은 Cartesian Product가 발생할 수 있다. 모든 조합이 실제로 필요할 때만 명시적으로 `CROSS JOIN`을 사용한다.

---

## 21. 사용자와 구매 내역 JOIN

### JYP 사용자의 이름과 구매 상품

```sql
SELECT u.name,
       b.prodname
FROM buytbl AS b
JOIN usertbl AS u
  ON b.userid = u.userid
WHERE b.userid = 'JYP';
```

### 사용자 정보와 구매 상품

```sql
SELECT b.userid,
       u.name,
       b.prodname,
       u.addr,
       CONCAT(u.mobile1, '-', u.mobile2) AS 연락처
FROM buytbl AS b
JOIN usertbl AS u
  ON b.userid = u.userid;
```

INNER JOIN이므로 구매 이력이 있는 사용자만 결과에 나타난다.

---

## 22. OUTER JOIN

INNER JOIN은 조건이 일치하는 행만 반환한다. OUTER JOIN은 기준 테이블의 일치하지 않는 행도 유지한다.

### LEFT JOIN

왼쪽 테이블의 모든 행을 유지한다.

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
LEFT JOIN department AS d
  ON e.dept_id = d.dept_id;
```

부서가 없는 사원은 유지되며 `dept_name`이 `NULL`이 된다.

### 부서에 배치되지 않은 사원

```sql
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
LEFT JOIN department AS d
  ON e.dept_id = d.dept_id
WHERE d.dept_id IS NULL;
```

```text
employee는 모두 유지
  ↓
department와 일치하지 않은 행의 d.dept_id는 NULL
  ↓
WHERE d.dept_id IS NULL
  ↓
미배치 사원만 선택
```

`WHERE d.dept_name IS NULL`도 현재 데이터에서는 동작할 수 있지만, “JOIN에 실패한 행”을 판별하려면 부모의 PK처럼 원래 `NULL`일 수 없는 Column을 검사하는 것이 명확하다.

### RIGHT JOIN

오른쪽 테이블의 모든 행을 유지한다.

```sql
SELECT u.userid,
       u.name,
       b.prodname
FROM buytbl AS b
RIGHT JOIN usertbl AS u
  ON b.userid = u.userid;
```

구매 내역이 없는 사용자는 `prodname`이 `NULL`이다.

### 구매 이력이 없는 회원

```sql
SELECT u.userid,
       u.name,
       u.addr
FROM buytbl AS b
RIGHT JOIN usertbl AS u
  ON b.userid = u.userid
WHERE b.userid IS NULL;
```

SQL 파일 주석에는 “구매이력이 있는 회원”이라고 되어 있지만 `WHERE prodname IS NULL` 조건은 구매 이력이 없는 회원을 찾는 조건이다. 구매 이력이 있는 회원만 필요하면 INNER JOIN을 사용한다.

---

## 23. Self Join

Self Join은 하나의 테이블을 서로 다른 역할의 별칭으로 두 번 이상 연결한다.

사원 테이블 안에서 `mgr_id`가 다른 사원의 `emp_id`를 참조한다면 사원과 관리자 관계를 표현할 수 있다.

```sql
SELECT e.emp_name AS 사원,
       m.emp_name AS 관리자,
       s.emp_name AS 상위관리자
FROM employee AS e
LEFT JOIN employee AS m
  ON e.mgr_id = m.emp_id
LEFT JOIN employee AS s
  ON m.mgr_id = s.emp_id;
```

```text
employee e: 사원
  ↓ e.mgr_id = m.emp_id
employee m: 관리자
  ↓ m.mgr_id = s.emp_id
employee s: 상위 관리자
```

같은 테이블이므로 `e`, `m`, `s`처럼 각 역할을 구분하는 별칭이 반드시 필요하다.

LEFT JOIN을 사용하면 관리자가 없는 최상위 사원도 결과에 유지할 수 있다.

---

## 24. JOIN 선택 기준

| 원하는 결과 | JOIN |
|---|---|
| 양쪽에 모두 존재하는 데이터 | `INNER JOIN` |
| 왼쪽 데이터를 모두 유지 | `LEFT JOIN` |
| 오른쪽 데이터를 모두 유지 | `RIGHT JOIN` |
| 모든 가능한 조합 | `CROSS JOIN` |
| 같은 테이블의 계층 관계 | Self Join |
| 같은 값으로 연결 | Equi Join |
| 범위 조건으로 연결 | Non-Equi Join |

JOIN을 작성하기 전에 다음을 먼저 확인한다.

1. 어떤 테이블이 필요한가?
2. 테이블 사이의 PK와 FK는 무엇인가?
3. 일치하지 않는 행도 결과에 남겨야 하는가?
4. JOIN 조건과 결과 필터 조건은 각각 무엇인가?

---

## 25. 오늘 코드에서 주의할 점

### GROUP BY와 SELECT 항목 맞추기

그룹 기준이 아닌 일반 Column은 그대로 SELECT하지 않고 집계할지 그룹 기준에 포함할지 결정한다.

### 요구사항의 경계 확인

“9,000,000 이상”은 `>= 9000000`, “초과”는 `> 9000000`이다.

### 내림차순에는 DESC

“급여가 많은 순서”는 다음과 같다.

```sql
ORDER BY salary DESC
```

### OUTER JOIN 후 NULL 검사

일치하지 않은 행을 찾을 때는 JOIN 상대 테이블의 PK처럼 원래 NULL일 수 없는 Column을 검사한다.

### CROSS JOIN은 명시적으로 작성

JOIN 조건 누락과 의도적인 모든 조합을 구분하기 위해 `CROSS JOIN`을 명시한다.

### 세미콜론 사용

각 SQL 문장을 `;`으로 끝내면 여러 쿼리의 경계를 명확하게 구분할 수 있다.

---

## 26. 최종 흐름 정리

```text
정규화
  ↓
업무 데이터를 여러 테이블로 분리
  ↓
PK와 FK로 관계 설정
  ↓
JOIN
필요한 테이블을 다시 연결
  ↓ WHERE
필요한 원본 행 선택
  ↓ GROUP BY
통계 단위별 그룹 생성
  ↓ 집계 함수
SUM·AVG·COUNT 계산
  ↓ HAVING
집계 결과 필터링
  ↓ Window Function
원본 행을 유지하며 평균·순위 추가
  ↓ ORDER BY
최종 결과 정렬
```

오늘 학습의 핵심은 `GROUP BY`와 JOIN을 따로 외우는 것이 아니다. 정규화로 분리된 테이블을 관계에 따라 JOIN하고, 필요한 행을 선택한 뒤 업무 기준으로 그룹화하여 통계를 만드는 전체 조회 흐름을 이해하는 것이다.

