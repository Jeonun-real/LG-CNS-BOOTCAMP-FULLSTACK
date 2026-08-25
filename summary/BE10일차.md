# BE 10일차 - SQL 함수, 형변환, 정렬과 그룹 함수

## 1. 오늘 배운 내용

오늘은 `inspire_rds_6th.sql`의 `SQL DAY2(함수)` 이후 내용을 중심으로 MySQL의 내장 함수를 학습했다. 이후 `practice.sql`의 학생·교수 데이터 조회 문제를 통해 문자열, 날짜, 숫자, 흐름 제어 함수와 정렬 문법을 적용했다.

```text
단일행 함수
  ├─ 문자열 함수
  ├─ 숫자 함수
  ├─ 날짜 함수
  ├─ 형변환 함수
  └─ 흐름 제어 함수
        ↓
복수행 함수
  ├─ COUNT
  ├─ SUM
  ├─ AVG
  ├─ MIN
  └─ MAX
        ↓
GROUP BY · HAVING · ORDER BY
```

### 핵심 키워드

- 단일행 함수와 복수행 함수
- `LENGTH()`, `CHAR_LENGTH()`
- `UPPER()`, `LOWER()`, `TRIM()`
- `LPAD()`, `RPAD()`
- `SUBSTRING()`, `LEFT()`, `RIGHT()`
- `SUBSTRING_INDEX()`, `INSTR()`
- `REPEAT()`, `REPLACE()`, `CONCAT()`
- `CAST()`와 묵시적·명시적 형변환
- `ABS()`, `CEILING()`, `FLOOR()`, `ROUND()`, `TRUNCATE()`
- `NOW()`, `CURDATE()`, `ADDDATE()`, `DATEDIFF()`
- `YEAR()`, `MONTH()`, `DAY()`, `DAYNAME()`
- `IF()`, `IFNULL()`, `NULLIF()`, `CASE`
- `COUNT()`, `SUM()`, `AVG()`, `MIN()`, `MAX()`
- `GROUP BY`, `HAVING`, `ORDER BY`

---

## 2. SQL 함수란?

함수는 전달받은 값을 일정한 규칙에 따라 처리한 뒤 결과를 반환하는 기능이다.

```text
입력값 → 함수 → 결과값
```

예를 들어 문자열의 길이를 구할 때는 `LENGTH()`, 평균을 구할 때는 `AVG()`를 사용한다.

### 2.1 단일행 함수

각 행을 하나씩 입력받아 행마다 하나의 결과를 반환한다.

```sql
SELECT emp_name,
       UPPER(email)
FROM employee;
```

사원이 10명이라면 각 행에 `UPPER()`가 적용되어 최대 10개의 결과가 나온다.

### 2.2 복수행 함수

여러 행을 입력받아 하나의 집계 결과를 반환한다.

```sql
SELECT AVG(salary)
FROM employee;
```

여러 사원의 급여를 전달하지만 평균이라는 하나의 결과를 반환한다. 그룹을 나누면 그룹마다 하나의 결과가 나온다.

---

## 3. 문자열의 바이트 길이와 문자 길이

### LENGTH()

문자열이 차지하는 바이트 수를 반환한다.

```sql
SELECT LENGTH('서정운'),
       LENGTH('jeongun');
```

UTF-8 환경에서는 한글 한 글자가 일반적으로 3바이트이므로 결과가 다음처럼 나올 수 있다.

```text
LENGTH('서정운')  → 9
LENGTH('jeongun') → 7
```

바이트 수는 DB 문자 집합에 따라 달라질 수 있다.

### CHAR_LENGTH()

문자열의 실제 문자 개수를 반환한다.

```sql
SELECT CHAR_LENGTH('서정운'),
       CHAR_LENGTH('jeongun');
```

```text
CHAR_LENGTH('서정운')  → 3
CHAR_LENGTH('jeongun') → 7
```

| 함수 | 기준 | 사용 예 |
|---|---|---|
| `LENGTH()` | 바이트 수 | 저장 용량 확인 |
| `CHAR_LENGTH()` | 문자 개수 | 이름 글자 수 확인 |

교수 이름이 세 글자가 아닌 사람은 다음과 같이 검색한다.

```sql
SELECT professor_name,
       professor_ssn
FROM tb_professor
WHERE CHAR_LENGTH(professor_name) <> 3;
```

`<>`는 “같지 않다”라는 ANSI SQL 비교 연산자이며 MySQL의 `!=`와 같은 목적으로 사용할 수 있다.

---

## 4. 대소문자 변환

```sql
SELECT UPPER('lgcns'),
       LOWER('LGCNS');
```

- `UPPER()`: 영문을 대문자로 변환
- `LOWER()`: 영문을 소문자로 변환

```text
UPPER('lgcns') → LGCNS
LOWER('LGCNS') → lgcns
```

---

## 5. 공백 제거 함수

```sql
SELECT LENGTH(TRIM('  TRIM  ')),
       LENGTH(LTRIM('  TRIM  ')),
       LENGTH(RTRIM('  TRIM  '));
```

| 함수 | 제거 범위 |
|---|---|
| `TRIM()` | 좌우 공백 |
| `LTRIM()` | 왼쪽 공백 |
| `RTRIM()` | 오른쪽 공백 |

중간에 있는 공백은 제거하지 않는다.

```text
TRIM('  A B  ') → 'A B'
```

사용자가 입력한 검색어나 코드의 불필요한 앞뒤 공백을 정리할 때 유용하다.

---

## 6. 문자열 채우기: LPAD와 RPAD

지정한 길이가 될 때까지 문자열의 왼쪽 또는 오른쪽을 채운다.

```sql
SELECT LPAD('5', 3, '#'),
       RPAD('5', 3, '#');
```

```text
LPAD('5', 3, '#') → ##5
RPAD('5', 3, '#') → 5##
```

공백으로 채워 출력 형식을 맞출 수도 있다.

```sql
SELECT email,
       LPAD(email, 20, '#')
FROM employee;
```

첫 번째 인자는 원본 문자열, 두 번째는 최종 길이, 세 번째는 채울 문자열이다.

---

## 7. 문자열 자르기

MySQL의 문자열 위치는 0이 아니라 1부터 시작한다.

### 7.1 SUBSTRING()

```sql
SELECT SUBSTRING('ABCDEF', 1, 2);
```

```text
첫 번째 문자부터 2개 → AB
```

다음 두 문법은 같은 의미다.

```sql
SELECT SUBSTRING('THIS IS INSPIRE CAMP', 9, 7),
       SUBSTRING('THIS IS INSPIRE CAMP' FROM 9 FOR 7);
```

### 7.2 LEFT()와 RIGHT()

```sql
SELECT LEFT('ABCDEF', 2),
       RIGHT('ABCDEF', 2);
```

```text
LEFT  → AB
RIGHT → EF
```

성씨를 제외한 교수 이름은 다음처럼 구할 수 있다.

```sql
SELECT SUBSTRING(professor_name, 2) AS 이름
FROM tb_professor;
```

또는 문자 길이를 이용해 오른쪽에서 성씨를 제외한 만큼 가져올 수 있다.

```sql
SELECT RIGHT(
           professor_name,
           CHAR_LENGTH(professor_name) - 1
       ) AS 이름
FROM tb_professor;
```

`practice.sql`의 다음 코드는 괄호와 인자 위치가 잘못되어 실행할 수 없다.

```sql
RIGHT(professor_name, CHAR_LENGTH(professor_name, 2)
```

### 7.3 SUBSTRING_INDEX()

구분자를 기준으로 문자열 일부를 반환한다.

```sql
SELECT SUBSTRING_INDEX('WWW.LGCNS.COM', '.', 1),
       SUBSTRING_INDEX('WWW.LGCNS.COM', '.', 2),
       SUBSTRING_INDEX('WWW.LGCNS.COM', '.', -1);
```

```text
count =  1 → WWW
count =  2 → WWW.LGCNS
count = -1 → COM
```

양수는 왼쪽에서, 음수는 오른쪽에서 구분자 개수만큼 가져온다.

이메일 아이디는 다음처럼 추출할 수 있다.

```sql
SELECT email,
       SUBSTRING_INDEX(email, '@', 1) AS email_id
FROM employee;
```

---

## 8. 문자열 위치 찾기: INSTR()

`INSTR(문자열, 찾을 문자열)`은 검색 문자열이 시작되는 위치를 반환한다.

```sql
SELECT INSTR('LGCNS CAMP', 'CAMP'),
       INSTR('LGCNS CAMP', 'CAP');
```

```text
'CAMP' 시작 위치 → 7
'CAP'을 못 찾음  → 0
```

SQL 문자열 위치는 1부터 시작하므로 반환값 0은 찾지 못했다는 의미로 사용할 수 있다.

`@`의 위치를 찾아 이메일 아이디를 추출할 수도 있다.

```sql
SELECT email,
       LEFT(email, INSTR(email, '@') - 1) AS email_id
FROM employee;
```

```text
abc@lgcns.com
   ↑ INSTR() = 4

LEFT(email, 4 - 1) → abc
```

---

## 9. 반복, 치환과 문자열 연결

### CHAR()

숫자 코드에 해당하는 문자를 반환한다.

```sql
SELECT CHAR(65);
```

사용하는 문자 집합에 따라 해석이 달라질 수 있지만 일반적인 ASCII 기준으로 65는 `A`에 해당한다.

### REPEAT()

```sql
SELECT REPEAT('LGCNS', 3);
```

결과는 `LGCNSLGCNSLGCNS`다.

### REPLACE()

```sql
SELECT REPLACE(
           '오늘은 즐거운 함수 수업',
           '함수',
           '메서드'
       );
```

원본 문자열에서 지정한 문자열을 찾아 새 문자열로 바꾼다.

### CONCAT()

```sql
SELECT CONCAT('서정운', '님');
```

문자열과 Column 값을 연결할 수 있다.

### 주민번호 마스킹

```sql
SELECT CONCAT(LEFT(emp_no, 8), '******')
FROM employee;
```

```text
앞 8자리 유지 + 뒤 6자리 마스킹
```

---

## 10. 형변환

### CHAR와 VARCHAR

- `CHAR(n)`: 고정 길이 문자열 타입
- `VARCHAR(n)`: 실제 데이터 길이에 따라 사용하는 가변 길이 문자열 타입

`CHAR`는 길이가 일정한 코드 등에, `VARCHAR`는 이름이나 이메일처럼 길이가 달라지는 데이터에 주로 사용한다. 실제 저장 방식과 공백 처리에는 DBMS 및 설정의 영향이 있으므로 타입의 최대 길이와 업무 특성을 함께 고려한다.

### 10.1 묵시적 형변환

DBMS가 연산을 수행하기 위해 타입을 자동으로 바꾸는 것이다.

```sql
SELECT SUBSTRING(emp_no, 1, 6)
       + SUBSTRING(emp_no, 8, 7)
FROM employee;
```

`SUBSTRING()`의 결과는 문자열이지만 `+`가 숫자 덧셈으로 해석되면서 MySQL이 숫자로 변환할 수 있다. 자동 변환은 의도를 파악하기 어렵고 예상하지 못한 결과를 만들 수 있다.

### 10.2 명시적 형변환

`CAST(값 AS 타입)`으로 개발자가 변환할 타입을 직접 지정한다.

```sql
SELECT CAST(SUBSTRING(emp_no, 1, 6) AS SIGNED)
       + CAST(SUBSTRING(emp_no, 8, 7) AS SIGNED)
FROM employee;
```

```text
문자열 → CAST → 숫자 → 덧셈
```

MySQL에서 부호 있는 정수 변환에는 `SIGNED` 또는 `SIGNED INTEGER`를 사용할 수 있다.

### 평균을 정수로 변환

```sql
SELECT AVG(amount),
       CAST(AVG(amount) AS SIGNED INTEGER)
FROM buytbl;
```

`CAST()`는 타입 변환이며 반올림 함수가 아니다. 소수부 처리 방식이 목적이라면 `ROUND()`, `FLOOR()`, `TRUNCATE()` 중 업무 규칙에 맞는 함수를 명시하는 편이 낫다.

### 숫자를 문자열로 변환

구매 계산식을 문자열로 표현한다.

```sql
SELECT num AS 구매번호,
       CONCAT(
           CAST(price AS CHAR),
           '*',
           CAST(amount AS CHAR),
           '=',
           CAST(price * amount AS CHAR)
       ) AS 총금액,
       price * amount AS 구매액
FROM buytbl;
```

MySQL의 `CAST()`에서 문자열 변환은 `CHAR`를 사용하는 방식이 호환성이 좋다.

---

## 11. 숫자 함수

```sql
SELECT ABS(-100),
       CEILING(4.7),
       FLOOR(4.7),
       ROUND(3234.2314, 2),
       ROUND(3234.2314, -2),
       TRUNCATE(3234.2314, 2),
       TRUNCATE(3234.2314, -2),
       GREATEST(10, 20, 100, 30),
       LEAST(10, 20, 100, 30);
```

| 함수 | 역할 | 예 |
|---|---|---|
| `ABS()` | 절댓값 | `ABS(-100) → 100` |
| `CEILING()` | 크거나 같은 최소 정수 | `CEILING(4.1) → 5` |
| `FLOOR()` | 작거나 같은 최대 정수 | `FLOOR(4.7) → 4` |
| `ROUND()` | 지정 위치에서 반올림 | `ROUND(3.141, 2) → 3.14` |
| `TRUNCATE()` | 지정 위치 이후 버림 | `TRUNCATE(3.149, 2) → 3.14` |
| `GREATEST()` | 인자 중 최댓값 | `100` |
| `LEAST()` | 인자 중 최솟값 | `10` |

두 번째 인수가 양수면 소수점 아래 자릿수, 음수면 정수부 자릿수를 기준으로 처리한다.

```text
ROUND(3234.2314,  2) → 3234.23
ROUND(3234.2314, -2) → 3200
```

금액이나 성적을 절삭할 때는 데이터 손실이 발생하므로 업무 규칙을 먼저 확인해야 한다.

---

## 12. 날짜와 시간 함수

관계형 데이터베이스에서는 날짜와 시간을 문자열이 아닌 날짜 타입으로 관리한다. MySQL의 주요 타입에는 `DATE`, `TIME`, `DATETIME`, `TIMESTAMP` 등이 있다. 연도는 두 자리보다 네 자리로 저장해야 세기를 모호하지 않게 표현할 수 있다.

```sql
CREATE TABLE coupon_tbl (
    created_at DATE NOT NULL PRIMARY KEY,
    end_at DATE
);
```

### 12.1 현재 날짜와 시간

```sql
SELECT NOW(),
       SYSDATE(),
       CURDATE(),
       CURTIME();
```

| 함수 | 반환 내용 |
|---|---|
| `NOW()` | 현재 날짜와 시간 |
| `SYSDATE()` | 함수가 실행된 시점의 날짜와 시간 |
| `CURDATE()` | 현재 날짜 |
| `CURTIME()` | 현재 시간 |

MySQL 설정과 한 SQL의 실행 시간에 따라 `NOW()`와 `SYSDATE()`의 평가 시점이 다를 수 있다.

### 12.2 날짜 더하기와 빼기

```sql
SELECT ADDDATE(CURDATE(), INTERVAL 30 YEAR),
       ADDDATE(CURDATE(), INTERVAL 30 MONTH),
       ADDDATE(CURDATE(), INTERVAL 30 DAY),
       SUBDATE(CURDATE(), INTERVAL 30 DAY),
       SUBTIME(NOW(), '01:00:00');
```

- `SUBDATE()`: 날짜에서 지정한 기간을 뺀다.
- `SUBTIME()`: 날짜·시간 값에서 지정한 시간을 뺀다.

입사일로부터 30년이 되는 날짜는 다음과 같다.

```sql
SELECT emp_name,
       hire_date,
       ADDDATE(hire_date, INTERVAL 30 YEAR) AS 근속30년일
FROM employee;
```

날짜에 정수 `1`을 직접 더하기보다 `ADDDATE()`나 `DATE_ADD()`처럼 단위를 명확히 표현하는 함수를 사용하는 것이 안전하다.

### 12.3 DATEDIFF()

두 날짜 사이의 일수 차이를 반환한다.

```sql
SELECT emp_name,
       hire_date,
       DATEDIFF(CURDATE(), hire_date) AS 근속일수
FROM employee;
```

30년 이상 근무한 사원은 날짜 자체를 비교하면 윤년까지 자연스럽게 반영할 수 있다.

```sql
SELECT *
FROM employee
WHERE hire_date <= SUBDATE(CURDATE(), INTERVAL 30 YEAR);
```

다음처럼 `30 * 365`로 비교하면 윤년을 반영하지 못하므로 근사값이 된다.

```sql
WHERE DATEDIFF(CURDATE(), hire_date) >= 30 * 365
```

### 12.4 날짜 요소 추출

```sql
SELECT hire_date,
       YEAR(hire_date),
       MONTH(hire_date),
       DAY(hire_date)
FROM employee;
```

각 함수는 숫자를 반환한다. 문자열이 필요하다면 다음처럼 변환할 수 있다.

```sql
SELECT CAST(YEAR(hire_date) AS CHAR)
FROM employee;
```

---

## 13. 요일 함수

```sql
SELECT WEEKDAY('2020-12-25'),
       DAYOFWEEK('2020-12-25'),
       DAYNAME('2020-12-25');
```

| 함수 | 숫자 기준 |
|---|---|
| `WEEKDAY()` | 월요일 0 ~ 일요일 6 |
| `DAYOFWEEK()` | 일요일 1 ~ 토요일 7 |
| `DAYNAME()` | 요일 이름 반환 |

`DAYNAME()` 결과는 DB 세션 언어 설정의 영향을 받을 수 있다.

---

## 14. 흐름 제어 함수

### 14.1 IF()

```sql
SELECT IF(100 > 200, 'TRUE', 'FALSE');
```

조건이 참이면 두 번째 값, 거짓이면 세 번째 값을 반환한다.

### 14.2 IFNULL()과 NULLIF()

```sql
SELECT IFNULL(NULL, '대체 값'),
       NULLIF(100, 100),
       NULLIF(100, 200);
```

- `IFNULL(a, b)`: `a`가 `NULL`이면 `b`
- `NULLIF(a, b)`: 두 값이 같으면 `NULL`, 다르면 `a`

### 14.3 단순 CASE

하나의 표현식 값을 여러 값과 비교한다.

```sql
SELECT CASE 2
           WHEN 1 THEN '1'
           WHEN 2 THEN '2'
           ELSE '없음'
       END AS 구분;
```

```sql
SELECT CASE SUBSTRING(emp_no, 8, 1)
           WHEN '1' THEN '남자'
           WHEN '3' THEN '남자'
           WHEN '2' THEN '여자'
           WHEN '4' THEN '여자'
           ELSE '확인 불가'
       END AS 성별
FROM employee;
```

단순 CASE의 `WHEN` 하나에 `'1', '3'`처럼 여러 값을 나열할 수 없다.

### 14.4 검색 CASE

각 `WHEN`에 독립적인 조건을 작성한다.

```sql
SELECT CASE
           WHEN salary >= 5000000 THEN '고액'
           WHEN salary >= 3000000 THEN '중간'
           ELSE '일반'
       END AS 급여등급
FROM employee;
```

여러 값을 OR 조건으로 묶고 싶다면 다음처럼 작성한다.

```sql
SELECT CASE
           WHEN SUBSTRING(emp_no, 8, 1) IN ('1', '3')
               THEN '남자'
           WHEN SUBSTRING(emp_no, 8, 1) IN ('2', '4')
               THEN '여자'
           ELSE '확인 불가'
       END AS 성별
FROM employee;
```

`CASE WHEN THEN ELSE END`는 ANSI SQL 표준이라 여러 관계형 DBMS에서 사용할 수 있다.

---

## 15. 복수행 함수

복수행 함수는 여러 행을 집계한다.

```sql
SELECT COUNT(*),
       COUNT(bonus_pct),
       COUNT(IFNULL(bonus_pct, 0)),
       MIN(salary),
       MAX(salary),
       AVG(salary),
       SUM(salary)
FROM employee;
```

| 함수 | 역할 |
|---|---|
| `COUNT(*)` | 조건을 만족하는 모든 행 수 |
| `COUNT(Column)` | `NULL`을 제외한 값의 수 |
| `MIN()` | 최솟값 |
| `MAX()` | 최댓값 |
| `AVG()` | 평균 |
| `SUM()` | 합계 |

```text
COUNT(*)                  → 모든 행 계산
COUNT(bonus_pct)          → NULL 제외
COUNT(IFNULL(bonus_pct,0)) → NULL을 0으로 바꾼 뒤 계산
```

대부분의 집계 함수는 `NULL`을 제외하고 계산한다.

집계 결과와 일반 Column을 아무 기준 없이 함께 조회하면 어떤 행의 일반 Column을 출력해야 하는지 모호하다.

```sql
-- 올바르지 않거나 SQL 모드에 따라 오류
SELECT emp_name,
       COUNT(*)
FROM employee;
```

일반 Column을 함께 출력하려면 보통 `GROUP BY`가 필요하다.

---

## 16. GROUP BY와 HAVING

### GROUP BY

같은 값을 가진 행을 그룹으로 묶고 그룹별 집계를 구한다.

```sql
SELECT dept_id,
       COUNT(*) AS 사원수,
       AVG(salary) AS 평균급여
FROM employee
GROUP BY dept_id;
```

```text
전체 사원
  ↓ dept_id 기준 그룹화
60번 부서 → COUNT, AVG
90번 부서 → COUNT, AVG
NULL 부서 → COUNT, AVG
```

### HAVING

그룹화가 끝난 결과에 조건을 적용한다.

```sql
SELECT dept_id,
       AVG(salary) AS 평균급여
FROM employee
GROUP BY dept_id
HAVING AVG(salary) >= 3000000;
```

| 절 | 필터링 대상 | 처리 시점 |
|---|---|---|
| `WHERE` | 그룹화 전의 개별 행 | `GROUP BY` 전 |
| `HAVING` | 그룹화된 집계 결과 | `GROUP BY` 후 |

```sql
SELECT dept_id,
       AVG(salary)
FROM employee
WHERE salary IS NOT NULL
GROUP BY dept_id
HAVING COUNT(*) >= 2;
```

---

## 17. ORDER BY

조회 결과를 정렬한다.

```sql
SELECT emp_name,
       salary
FROM employee
ORDER BY salary DESC;
```

- `ASC`: 오름차순이며 생략 가능
- `DESC`: 내림차순

정렬 기준에는 Column, 표현식, 별칭, SELECT 목록의 순번을 사용할 수 있다.

```sql
SELECT emp_name AS 이름,
       salary AS 급여
FROM employee
ORDER BY 급여 DESC;
```

```sql
SELECT emp_name,
       salary
FROM employee
ORDER BY 2 DESC;
```

`ORDER BY 2`는 SELECT 목록의 두 번째 항목을 의미한다. 빠르게 작성할 수 있지만 Column 순서 변경에 취약하므로 명시적인 이름이 유지보수에 유리하다.

다중 정렬도 가능하다.

```sql
SELECT dept_id,
       emp_name,
       salary
FROM employee
ORDER BY dept_id ASC,
         salary DESC;
```

---

## 18. WHERE 1 = 1의 의미

```sql
SELECT *
FROM employee
WHERE 1 = 1
  AND dept_id = '90'
  AND salary >= 4000000;
```

`1 = 1`은 항상 참이다. 정적 SQL에서는 필요하지 않지만 애플리케이션이 조건을 동적으로 이어 붙일 때 모든 추가 조건을 `AND`로 통일하기 위해 사용되기도 한다.

조건값이 `NULL`이면 뒤 조건이 자동으로 무시되는 문법은 아니다. 애플리케이션 또는 SQL에서 조건 포함 여부를 별도로 처리해야 한다.

---

## 19. 오늘의 실습 과제

### 19.1 002번 학과 학생을 입학일 순으로 조회

```sql
SELECT s.student_no AS 학번,
       s.student_name AS 이름,
       s.entrance_date AS 입학일
FROM tb_student AS s
WHERE s.department_no = '002'
ORDER BY s.entrance_date ASC;
```

학과 번호가 문자 타입이면 `'002'`처럼 작은따옴표로 묶어 앞자리 0을 보존한다.

### 19.2 이름이 세 글자가 아닌 교수

```sql
SELECT professor_name,
       professor_ssn
FROM tb_professor
WHERE CHAR_LENGTH(professor_name) <> 3;
```

한글 이름은 바이트 길이가 아니라 문자 수를 구해야 하므로 `LENGTH()` 대신 `CHAR_LENGTH()`를 사용한다.

### 19.3 남자 교수의 현재 나이

주민번호 뒷자리 첫 숫자가 1인 남자 교수를 대상으로 단순 연 나이를 계산한 예다.

```sql
SELECT professor_name,
       YEAR(CURDATE())
           - (1900 + CAST(LEFT(professor_ssn, 2) AS UNSIGNED))
           AS 나이
FROM tb_professor
WHERE SUBSTRING(
          professor_ssn,
          INSTR(professor_ssn, '-') + 1,
          1
      ) = '1'
ORDER BY 나이;
```

원본 과제의 핵심 문법은 다음과 같다.

- `LEFT(ssn, 2)`: 출생연도 두 자리 추출
- `INSTR(ssn, '-') + 1`: 하이픈 다음 위치 계산
- `SUBSTRING(..., 1)`: 성별 구분 숫자 추출
- `YEAR(CURDATE())`: 현재 연도
- `ORDER BY`: 계산한 나이 정렬

다만 이 계산에는 다음 한계가 있다.

- 구분 숫자 1만 처리하므로 1900년대 남성만 대상으로 한다.
- 생일이 지났는지 반영하지 않는 단순한 연도 차이다.
- 실제 시스템에서 주민번호를 평문으로 다루면 안 된다.

### 19.4 교수 이름에서 성 제거

```sql
SELECT SUBSTRING(professor_name, 2) AS 이름
FROM tb_professor;
```

또는 다음과 같이 작성할 수 있다.

```sql
SELECT RIGHT(
           professor_name,
           CHAR_LENGTH(professor_name) - 1
       ) AS 이름
FROM tb_professor;
```

### 19.5 입학 당시 나이가 20세를 초과한 학생

```sql
SELECT student_no,
       student_name
FROM tb_student
WHERE YEAR(entrance_date)
      - CAST(CONCAT('19', LEFT(student_ssn, 2)) AS UNSIGNED)
      > 19;
```

```text
입학 연도 - 출생 연도 > 19
```

이 식 역시 1900년대 출생자로 범위를 가정한 단순 계산이다. 주민번호 뒷자리 구분 숫자와 실제 생일까지 정확히 반영하려면 추가 처리가 필요하다.

### 19.6 2020년 12월 25일의 요일

가장 간단한 방법은 `DAYNAME()`이다.

```sql
SELECT DAYNAME('2020-12-25') AS 요일;
```

숫자 결과를 한글로 바꾸는 `CASE` 연습은 다음과 같다.

```sql
SELECT CASE DAYOFWEEK('2020-12-25')
           WHEN 1 THEN '일요일'
           WHEN 2 THEN '월요일'
           WHEN 3 THEN '화요일'
           WHEN 4 THEN '수요일'
           WHEN 5 THEN '목요일'
           WHEN 6 THEN '금요일'
           ELSE '토요일'
       END AS 요일;
```

### 19.7 생략된 문제

`practice.sql` 주석에 따라 7, 10, 12, 13, 14, 15번 문제는 이번 실습에서 제외했다.

### 19.8 학번이 A로 시작하지 않는 학생

```sql
SELECT student_no,
       student_name
FROM tb_student
WHERE SUBSTRING(student_no, 1, 1) <> 'A';
```

더 직접적으로 패턴을 표현하면 다음과 같다.

```sql
WHERE student_no NOT LIKE 'A%'
```

### 19.9 특정 학생의 평균 학점

```sql
SELECT ROUND(AVG(point), 1) AS 학점
FROM tb_grade
WHERE student_no = 'A517178';
```

처리 흐름은 다음과 같다.

```text
WHERE로 학생의 성적 행 선택
  ↓
AVG(point)로 평균 계산
  ↓
ROUND(..., 1)로 소수점 첫째 자리까지 반올림
```

`WHERE`에서 먼저 한 학생의 행만 남겼기 때문에 별도의 `GROUP BY` 없이 하나의 평균 결과가 나온다.

---

## 20. 자주 틀릴 수 있는 부분

### 문자열 위치는 1부터 시작

```sql
SUBSTRING('ABCDEF', 1, 2)  -- AB
```

### 문자 수는 CHAR_LENGTH()

한글 이름의 글자 수를 구할 때 `LENGTH()`는 바이트 수를 반환하므로 적절하지 않다.

### CASE 문법

다음처럼 여러 값을 쉼표로 나열할 수 없다.

```sql
-- 잘못된 형태
WHEN '1', '3' THEN '남자'
```

각 `WHEN`을 쓰거나 검색 CASE와 `IN`을 사용한다.

### 날짜에 숫자를 직접 더하지 않기

```sql
-- 의도가 불명확
hire_date + 1

-- 단위가 명확
ADDDATE(hire_date, INTERVAL 1 DAY)
```

### 그룹 함수와 일반 Column 혼용

```sql
-- 그룹 기준이 없음
SELECT emp_name, COUNT(*)
FROM employee;
```

일반 Column을 함께 출력하려면 그 Column을 `GROUP BY`에 포함할지 먼저 판단해야 한다.

### 별칭의 작은따옴표와 백틱

- 작은따옴표: 문자열 값
- 백틱: MySQL 식별자
- 공백 없는 별칭: 따옴표 없이 사용 가능

```sql
SELECT department_name AS `학과 명`
FROM tb_department;
```

---

## 21. 최종 문법 흐름

```text
원본 데이터
  ↓
WHERE로 필요한 행 선택
  ↓
단일행 함수로 각 값 가공
  ├─ 문자열 자르기
  ├─ 형변환
  ├─ 숫자 계산
  ├─ 날짜 계산
  └─ CASE 조건 처리
  ↓
GROUP BY로 그룹 생성
  ↓
COUNT·AVG·SUM 등으로 집계
  ↓
HAVING으로 그룹 조건 적용
  ↓
ORDER BY로 최종 결과 정렬
```

오늘의 핵심은 함수 이름을 개별적으로 외우는 것이 아니라, 데이터 타입과 원하는 결과에 맞는 함수를 선택하고 SQL의 처리 흐름 안에서 조합하는 것이다.
