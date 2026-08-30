# BE 13일차 - FK, ALTER TABLE, VIEW, DML과 자동 번호 생성

## 1. 오늘의 핵심

오늘은 테이블 사이의 관계를 실제 DDL로 구현하는 Foreign Key와 참조 무결성을 학습했다. 이어서 기존 테이블 구조를 변경하는 `ALTER TABLE`, 필요한 데이터만 공개하는 `VIEW`, 데이터를 수정·삭제하는 DML과 Transaction을 실습했다.

마지막에는 행을 구분할 번호를 자동으로 생성하는 Sequence와 `AUTO_INCREMENT`를 비교했다.

```text
부모 테이블 생성
  ↓ PK 설정
자식 테이블 생성
  ↓ FK 설정
참조 무결성 유지
  ↓ ALTER TABLE
Column·제약조건 변경
  ↓ VIEW
필요한 조회 결과 공개
  ↓ UPDATE·DELETE
데이터 변경
  ↓ Transaction
COMMIT 또는 ROLLBACK
  ↓ Sequence·AUTO_INCREMENT
식별 번호 자동 생성
```

### 주요 문법

- `PRIMARY KEY`
- `FOREIGN KEY ... REFERENCES`
- `ON DELETE`, `ON UPDATE`
- `CASCADE`, `RESTRICT`, `NO ACTION`, `SET NULL`
- `SHOW INDEX`
- `NOT NULL`, `CHECK`, `DEFAULT`
- `ALTER TABLE`
- `ADD`, `MODIFY`, `CHANGE`, `DROP COLUMN`
- `ADD CONSTRAINT`, `DROP CONSTRAINT`
- `information_schema.table_constraints`
- `CREATE OR REPLACE VIEW`
- `UPDATE`, `DELETE`
- `COMMIT`, `ROLLBACK`, `START TRANSACTION`
- `CREATE SEQUENCE`, `NEXTVAL()`
- `AUTO_INCREMENT`

---

## 2. 부모 테이블 생성

사원의 직급과 부서를 별도 테이블로 만들고 각각의 PK를 설정했다.

### 직급 테이블

```sql
CREATE TABLE tb_job (
    job_id    VARCHAR(10),
    job_title VARCHAR(50),
    CONSTRAINT tb_job_pk
        PRIMARY KEY (job_id)
);
```

```sql
INSERT INTO tb_job (job_id, job_title)
VALUES ('j1', '대표이사');

INSERT INTO tb_job (job_id, job_title)
VALUES ('j2', '상무');
```

### 부서 테이블

```sql
CREATE TABLE tb_dept (
    dept_id    VARCHAR(10),
    dept_title VARCHAR(50),
    CONSTRAINT tb_dept_pk
        PRIMARY KEY (dept_id)
);
```

```sql
INSERT INTO tb_dept (dept_id, dept_title)
VALUES ('10', '교육팀');

INSERT INTO tb_dept (dept_id, dept_title)
VALUES ('20', '운영팀');
```

`tb_job`과 `tb_dept`는 다른 테이블이 참조하는 부모 테이블이 된다.

---

## 3. Foreign Key와 참조 무결성

Foreign Key는 자식 테이블의 Column이 부모 테이블의 PK 또는 UNIQUE Key를 참조하도록 만드는 제약조건이다.

```text
tb_dept                         tb_emp
dept_id = '10' (PK) ─────────> dept_id = '10' (FK)

tb_job                          tb_emp
job_id = 'j1' (PK) ──────────> job_id = 'j1' (FK)
```

자식 FK에는 다음 값만 저장할 수 있다.

- 부모 테이블에 실제로 존재하는 Key
- FK Column이 NULL을 허용하는 경우 `NULL`

부모에 없는 값을 저장하면 참조 무결성 위반이다.

---

## 4. FK가 있는 사원 테이블

```sql
CREATE TABLE tb_emp (
    emp_id    VARCHAR(10),
    emp_name  VARCHAR(50) NOT NULL,
    hire_date DATE,
    dept_id   VARCHAR(10) NOT NULL,
    job_id    VARCHAR(10) NOT NULL,

    CONSTRAINT tb_emp_pk
        PRIMARY KEY (emp_id),

    CONSTRAINT tb_emp_dept_fk
        FOREIGN KEY (dept_id)
        REFERENCES tb_dept (dept_id)
        ON DELETE CASCADE,

    CONSTRAINT tb_emp_job_fk
        FOREIGN KEY (job_id)
        REFERENCES tb_job (job_id)
        ON UPDATE CASCADE
);
```

- `PRIMARY KEY`: 사원을 유일하게 구분
- `NOT NULL`: 필수 입력
- `FOREIGN KEY`: 부모 Key만 참조
- `ON DELETE CASCADE`: 부모 삭제 시 자식도 삭제
- `ON UPDATE CASCADE`: 부모 Key 변경 시 자식 FK도 변경

제약조건에 이름을 붙이면 오류 분석과 추후 변경이 편리하다.

---

## 5. ON DELETE와 ON UPDATE 정책

부모 Key가 삭제되거나 변경될 때 자식 행을 어떻게 처리할지 지정한다.

| 정책 | 동작 |
|---|---|
| `RESTRICT` | 참조 중인 자식이 있으면 부모 변경·삭제 제한 |
| `NO ACTION` | 표준상 검사 시점 차이가 있을 수 있으나 MySQL 계열에서는 보통 RESTRICT와 유사 |
| `CASCADE` | 부모 변경·삭제를 자식에게 전파 |
| `SET NULL` | 자식 FK를 NULL로 변경 |

### ON DELETE CASCADE

```sql
DELETE FROM tb_dept
WHERE dept_id = '10';
```

FK가 다음과 같이 정의되어 있으면 10번 부서를 참조하는 사원도 함께 삭제된다.

```sql
ON DELETE CASCADE
```

편리하지만 부모 한 행 삭제가 많은 자식 데이터 삭제로 이어질 수 있다. 운영 데이터에서는 업무 규칙과 삭제 범위를 확인한 후 사용해야 한다.

### ON UPDATE CASCADE

부모의 PK가 변경되면 자식의 FK도 함께 변경한다.

```sql
ON UPDATE CASCADE
```

PK는 일반적으로 자주 바뀌지 않는 값을 선택하므로 실제 사용 빈도는 높지 않을 수 있다.

### SET NULL 사용 조건

```sql
ON DELETE SET NULL
```

자식 FK Column이 `NOT NULL`이면 `SET NULL`을 사용할 수 없다. FK Column이 `NULL`을 허용해야 한다.

---

## 6. FK 입력 성공과 실패

### 정상 입력

부모에 존재하는 부서와 직급을 사용한다.

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    dept_id,
    job_id
)
VALUES (
    '100',
    'jslim',
    '10',
    'j1'
);
```

### NOT NULL 위반

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    dept_id,
    job_id
)
VALUES (
    '101',
    'jslim',
    NULL,
    NULL
);
```

`dept_id`와 `job_id`가 `NOT NULL`이므로 실패한다.

### FK 위반

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    dept_id,
    job_id
)
VALUES (
    '102',
    'jslim',
    '30',
    'j3'
);
```

부모 테이블에 30번 부서와 j3 직급이 없다면 FK 위반으로 실패한다.

원본 SQL의 `dpet_id`는 `dept_id`의 오타이므로 해당 Column을 찾을 수 없다는 오류가 발생한다.

---

## 7. DEFAULT의 동작

```sql
hire_date DATE DEFAULT (CURRENT_DATE)
```

기본값은 INSERT에서 Column을 생략하거나 `DEFAULT` 키워드를 사용했을 때 적용된다.

### Column 생략

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    dept_id,
    job_id
)
VALUES (
    '200',
    'jslim',
    '10',
    'j1'
);
```

### DEFAULT 명시

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    hire_date,
    dept_id,
    job_id
)
VALUES (
    '300',
    'jslim',
    DEFAULT,
    '10',
    'j1'
);
```

### NULL 명시

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    hire_date,
    dept_id,
    job_id
)
VALUES (
    '400',
    'jslim',
    NULL,
    '10',
    'j1'
);
```

`hire_date`가 NULL을 허용하면 일반적으로 `NULL`이 저장된다. `NULL`이 항상 DEFAULT로 대체되는 것은 아니다.

날짜 기본값 표현식 지원은 DBMS와 버전에 따라 다를 수 있다. 생성 시각까지 필요하다면 다음처럼 사용하는 방법이 일반적이다.

```sql
hire_at DATETIME DEFAULT CURRENT_TIMESTAMP
```

원본 SQL에는 동일한 `VALUES` 절이 세 번 연속 있지만 하나의 INSERT 뒤에 여러 `VALUES`만 독립적으로 작성할 수는 없다. 여러 행 입력은 다음과 같이 작성한다.

```sql
INSERT INTO tb_emp (
    emp_id,
    emp_name,
    hire_date,
    dept_id,
    job_id
)
VALUES
    ('300', 'A', DEFAULT, '10', 'j1'),
    ('301', 'B', DEFAULT, '10', 'j1'),
    ('302', 'C', DEFAULT, '20', 'j2');
```

---

## 8. 인덱스 확인

```sql
SHOW INDEX FROM tb_job;
SHOW INDEX FROM tb_dept;
SHOW INDEX FROM tb_emp;
```

PK에는 일반적으로 Unique Index가 자동 생성된다. InnoDB에서는 FK 검사를 위해 자식 FK Column에도 인덱스가 필요하며 적절한 인덱스가 없으면 자동 생성될 수 있다.

주요 확인 항목은 다음과 같다.

- 인덱스 이름
- Unique 여부
- 인덱스에 포함된 Column
- Column 순서
- Cardinality 추정값

FK와 Index는 같은 개념이 아니다.

- FK: 참조 무결성을 보장하는 제약조건
- Index: 검색과 관계 검사를 빠르게 하는 자료구조

---

## 9. 추가 제약조건이 있는 테이블

```sql
DROP TABLE IF EXISTS tb_employee;

CREATE TABLE tb_employee (
    emp_id    VARCHAR(10),
    emp_name  VARCHAR(50) NOT NULL,
    salary    INT CHECK (salary > 0),
    gender    CHAR(1) CHECK (gender IN ('f', 'm')),
    hire_date DATE,
    job_id    VARCHAR(10),
    dept_id   VARCHAR(10)
);
```

### NOT NULL

값을 반드시 입력하게 한다.

```sql
emp_name VARCHAR(50) NOT NULL
```

`NOT NULL`은 Column의 속성이므로 Column Level에서 정의하거나 `MODIFY COLUMN`으로 변경한다.

### CHECK

저장할 값의 범위를 제한한다.

```sql
salary INT CHECK (salary > 0)
```

```sql
gender CHAR(1)
    CHECK (gender IN ('f', 'm'))
```

급여는 양수만, 성별 코드는 f 또는 m만 허용한다. MySQL 구버전에서는 CHECK 구문을 실제로 강제하지 않았던 버전이 있으므로 사용 버전을 확인해야 한다.

---

## 10. ALTER TABLE

`ALTER TABLE`은 이미 존재하는 테이블의 구조를 변경한다.

### 주요 문법

```sql
ALTER TABLE table_name
ADD COLUMN column_name data_type;
```

```sql
ALTER TABLE table_name
MODIFY COLUMN column_name new_definition;
```

```sql
ALTER TABLE table_name
CHANGE COLUMN old_name new_name new_definition;
```

```sql
ALTER TABLE table_name
DROP COLUMN column_name;
```

```sql
ALTER TABLE old_table_name
RENAME TO new_table_name;
```

| 명령 | 역할 |
|---|---|
| `ADD COLUMN` | 새 Column 추가 |
| `MODIFY COLUMN` | 타입·NULL 허용 등 정의 변경 |
| `CHANGE COLUMN` | 이름과 정의 변경 |
| `DROP COLUMN` | Column 삭제 |
| `RENAME TO` | 테이블 이름 변경 |

DDL은 데이터와 구조에 직접 영향을 주므로 운영 환경에서는 기존 데이터가 새 제약조건과 호환되는지 먼저 확인해야 한다.

---

## 11. ALTER로 PK 추가

```sql
ALTER TABLE tb_employee
ADD CONSTRAINT tb_employee_pk
PRIMARY KEY (emp_id);
```

PK를 추가하려면 기존 `emp_id` 값이 다음 조건을 만족해야 한다.

- 중복이 없어야 한다.
- NULL이 없어야 한다.

기존 데이터가 조건을 위반하면 제약조건 추가가 실패한다.

---

## 12. ALTER로 NOT NULL과 FK 추가

### 직급 Column 변경과 FK 추가

```sql
ALTER TABLE tb_employee
MODIFY COLUMN job_id VARCHAR(10) NOT NULL,
ADD CONSTRAINT tb_employee_job_fk
    FOREIGN KEY (job_id)
    REFERENCES tb_job (job_id);
```

### 부서 Column 변경과 FK 추가

```sql
ALTER TABLE tb_employee
MODIFY COLUMN dept_id VARCHAR(10) NOT NULL,
ADD CONSTRAINT tb_employee_dept_fk
    FOREIGN KEY (dept_id)
    REFERENCES tb_dept (dept_id);
```

제약조건 추가 전 기존 데이터도 검사된다.

```text
NOT NULL 추가
  → 기존 NULL 값이 있으면 실패

FK 추가
  → 부모에 없는 기존 값이 있으면 실패
```

먼저 잘못된 데이터를 정리한 뒤 제약조건을 추가해야 한다.

---

## 13. 제약조건 삭제와 변경

제약조건 삭제 문법은 DBMS와 제약조건 종류에 따라 차이가 있다.

MySQL에서 FK는 다음처럼 이름으로 삭제한다.

```sql
ALTER TABLE tb_employee
DROP FOREIGN KEY tb_employee_dept_fk;
```

PK는 다음과 같이 삭제한다.

```sql
ALTER TABLE tb_employee
DROP PRIMARY KEY;
```

CHECK 제약조건은 지원 버전에서 다음 형태를 사용할 수 있다.

```sql
ALTER TABLE tb_employee
DROP CHECK constraint_name;
```

제약조건을 변경할 때는 보통 기존 제약조건을 삭제한 뒤 새 조건을 추가한다.

---

## 14. Information Schema로 제약조건 확인

DBMS가 관리하는 제약조건 메타데이터를 조회한다.

```sql
SELECT constraint_name,
       constraint_type,
       table_name
FROM information_schema.table_constraints
WHERE table_schema = 'tabledb'
  AND table_name = 'tb_employee';
```

원본 SQL의 `CONSTRAINT NAME`은 공백이 아니라 `CONSTRAINT_NAME`으로 작성해야 한다.

`information_schema`는 데이터베이스와 테이블·Column·제약조건 등의 메타데이터를 제공하는 시스템 스키마다.

---

## 15. VIEW

View는 SELECT 문에 이름을 붙여 테이블처럼 조회할 수 있게 만든 논리적 객체다.

```sql
CREATE OR REPLACE VIEW emp_view (
    name,
    dept
)
AS
SELECT e.emp_name,
       d.dept_name
FROM employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id
WHERE e.dept_id = '90';
```

```sql
SELECT *
FROM emp_view;
```

```text
employee + department
  ↓ JOIN·WHERE
필요한 Column과 행만 선택
  ↓ VIEW
emp_view로 공개
```

### VIEW를 사용하는 이유

- 복잡한 JOIN을 단순한 조회 형태로 재사용
- 필요한 Column만 공개
- 특정 행만 공개
- 애플리케이션이 조회 구조에 덜 의존하도록 추상화

View가 항상 읽기 전용인 것은 아니다. 단순한 단일 테이블 View는 수정 가능한 경우도 있지만 JOIN, 집계, DISTINCT 등이 포함되면 수정이 제한되는 경우가 많다.

### VIEW 삭제

```sql
DROP VIEW emp_view;
```

View를 삭제해도 원본 테이블의 데이터가 삭제되는 것은 아니다.

---

## 16. DML과 Transaction

DML은 테이블 안의 데이터를 조작한다.

- `INSERT`: 행 추가
- `UPDATE`: 기존 행 수정
- `DELETE`: 행 삭제

데이터 변경 작업은 Transaction 단위로 관리할 수 있다.

```text
START TRANSACTION
  ↓
INSERT·UPDATE·DELETE
  ↓
COMMIT   → 변경 확정
또는
ROLLBACK → 변경 취소
```

---

## 17. UPDATE 기본 문법

```sql
UPDATE table_name
SET column_name = value
WHERE condition;
```

90번 부서명을 변경한다.

```sql
UPDATE department
SET dept_name = '전략기획팀'
WHERE dept_id = '90';
```

`WHERE`가 없으면 모든 행이 변경된다. UPDATE 전에 같은 조건으로 SELECT를 실행해 대상을 확인하는 습관이 중요하다.

```sql
SELECT *
FROM department
WHERE dept_id = '90';
```

---

## 18. UPDATE에서 서브쿼리 사용

심하균 사원의 직급과 부서를 성해교 사원과 같게 변경한다.

```sql
UPDATE employee
SET job_id = (
        SELECT job_id
        FROM employee
        WHERE emp_name = '성해교'
    ),
    dept_id = (
        SELECT dept_id
        FROM employee
        WHERE emp_name = '성해교'
    )
WHERE emp_name = '심하균';
```

각 Scalar Subquery는 값 하나를 반환해야 한다. 같은 이름의 사원이 여러 명이라면 오류가 발생할 수 있으므로 실제 환경에서는 PK로 대상을 식별하는 것이 안전하다.

MySQL은 같은 테이블을 수정하면서 직접 서브쿼리로 읽는 일부 형태에서 “You can't specify target table” 오류가 날 수 있다. 버전과 쿼리 형태에 따라 Self Join UPDATE나 추가 Inline View가 필요할 수 있다.

---

## 19. DEFAULT로 UPDATE

```sql
UPDATE employee
SET marriage = DEFAULT
WHERE emp_name = '한선기';
```

Column에 설정된 기본값으로 되돌린다. 실제 Column 이름이 `marriage`인지 확인해야 하며 원본 SQL의 `mariage`는 오타일 가능성이 있다.

---

## 20. UPDATE와 참조 무결성

```sql
UPDATE employee
SET dept_id = NULL
WHERE emp_id = '100';
```

`dept_id`가 `NOT NULL`이면 실패한다. FK가 NULL을 허용하면 NULL 자체는 부모 Key를 참조하지 않는 값으로 허용될 수 있다.

부모에 존재하지 않는 부서로 변경해도 FK 위반이다.

```sql
UPDATE employee
SET dept_id = '999'
WHERE emp_id = '100';
```

999번 부서가 부모 테이블에 없다면 저장할 수 없다.

---

## 21. 다른 테이블 조건으로 UPDATE

해외영업 2팀 사원의 보너스율을 0.3으로 변경하려면 부서 테이블을 통해 부서 번호를 찾아야 한다.

### 서브쿼리 방식

```sql
UPDATE employee
SET bonus_pct = 0.3
WHERE dept_id = (
    SELECT dept_id
    FROM department
    WHERE dept_name = '해외영업 2팀'
);
```

### JOIN UPDATE 방식

MySQL에서는 다음처럼 작성할 수 있다.

```sql
UPDATE employee AS e
JOIN department AS d
  ON e.dept_id = d.dept_id
SET e.bonus_pct = 0.3
WHERE d.dept_name = '해외영업 2팀';
```

원본 SQL은 `employee`에서 `dept_name`을 조회하는데, 일반적인 스키마에서 부서명은 `department` 테이블에 있으므로 올바른 테이블을 조회해야 한다.

---

## 22. DELETE

```sql
DELETE FROM department
WHERE dept_id = '50';
```

DELETE는 조건에 맞는 행을 삭제한다. `WHERE`가 없으면 테이블의 모든 행이 삭제된다.

### DELETE, TRUNCATE, DROP 비교

| 명령 | 삭제 대상 | 테이블 구조 |
|---|---|---|
| `DELETE` | 조건에 맞는 행 | 유지 |
| `TRUNCATE` | 모든 행 | 유지 |
| `DROP TABLE` | 테이블 객체 전체 | 삭제 |

FK가 연결되어 있으면 부모 행 삭제 정책에 따라 제한, 연쇄 삭제 또는 NULL 변경이 발생한다.

---

## 23. COMMIT과 ROLLBACK

### 명시적 Transaction

```sql
START TRANSACTION;

UPDATE department
SET dept_name = '전략기획팀'
WHERE dept_id = '90';

ROLLBACK;
```

ROLLBACK을 실행하면 Transaction 안에서 수행한 변경을 취소한다.

변경을 확정하려면 다음과 같이 실행한다.

```sql
COMMIT;
```

### Auto Commit

Auto Commit이 활성화되어 있으면 각 DML이 실행 직후 자동으로 확정될 수 있다.

```sql
SELECT @@autocommit;
```

```sql
SET autocommit = 0;
```

이미 COMMIT된 작업은 일반적인 ROLLBACK으로 되돌릴 수 없다. 따라서 중요한 변경 작업은 Auto Commit 상태를 확인하고 명시적인 Transaction 안에서 실행해야 한다.

DDL은 MySQL 계열에서 암묵적 COMMIT을 발생시킬 수 있으므로 DML Transaction과 섞을 때 주의한다.

---

## 24. Sequence

Sequence는 호출할 때마다 새로운 숫자를 만들어 주는 데이터베이스 객체다.

MariaDB 등 Sequence를 지원하는 환경에서는 다음과 같이 사용할 수 있다.

```sql
CREATE SEQUENCE seq_test;
```

```sql
SELECT NEXTVAL(seq_test);
SELECT LASTVAL(seq_test);
```

- `NEXTVAL()`: 다음 값 생성
- `LASTVAL()`: 현재 세션에서 사용한 최근 값 확인

옵션을 지정할 수도 있다.

```sql
CREATE SEQUENCE seq_test
    START WITH 1000
    INCREMENT BY 2
    MAXVALUE 1005
    NOCYCLE;
```

```text
1000 → 1002 → 1004 → MAXVALUE 도달
```

- `START WITH`: 시작값
- `INCREMENT BY`: 증가값
- `MAXVALUE`: 최댓값
- `CYCLE / NOCYCLE`: 최댓값 이후 순환 여부
- `CACHE`: 번호를 미리 확보하여 성능 향상

Sequence 문법은 DBMS별 차이가 크다. MySQL은 일반적인 독립 Sequence 객체를 지원하지 않고 주로 `AUTO_INCREMENT`를 사용하며, MariaDB는 Sequence를 지원한다.

### Sequence 삭제

```sql
DROP SEQUENCE seq_test;
```

---

## 25. Sequence를 기본값으로 사용

Sequence를 지원하는 환경에서는 생성 번호를 Column 기본값으로 연결할 수 있다.

```sql
CREATE TABLE tb_temp (
    order_id INT
        DEFAULT (NEXTVAL(seq_test))
        PRIMARY KEY,
    customer_name VARCHAR(50)
);
```

```sql
INSERT INTO tb_temp (customer_name)
VALUES ('JSLIM');
```

`order_id`를 생략하면 Sequence의 다음 값이 자동으로 들어간다.

DBMS별로 `NEXT VALUE FOR sequence_name`, `sequence_name.NEXTVAL`, `NEXTVAL(sequence_name)`처럼 문법이 다르다.

---

## 26. AUTO_INCREMENT

MySQL 계열에서는 숫자 PK 자동 생성을 위해 `AUTO_INCREMENT`를 주로 사용한다.

```sql
CREATE TABLE tb_auto_temp (
    order_id INT AUTO_INCREMENT,
    customer_name VARCHAR(50),
    CONSTRAINT tb_auto_temp_pk
        PRIMARY KEY (order_id)
);
```

```sql
INSERT INTO tb_auto_temp (customer_name)
VALUES ('JSLIM');
```

`order_id`를 생략하면 다음 번호가 자동 생성된다.

### 현재 시작값 조정

```sql
ALTER TABLE tb_auto_temp
AUTO_INCREMENT = 1000;
```

다음 자동 생성 값을 조정할 수 있지만 기존 PK와 충돌하지 않아야 한다.

원본 메모의 “AUTO_INCREMENT는 값을 임의로 지정할 수 없다”는 표현은 정확하지 않다. 명시적인 값을 INSERT할 수도 있지만 중복 PK가 아니어야 하며, 자동 번호 흐름에 영향을 줄 수 있으므로 일반적으로 생략해서 사용한다.

---

## 27. Sequence와 AUTO_INCREMENT 비교

| 구분 | Sequence | AUTO_INCREMENT |
|---|---|---|
| 형태 | 독립적인 DB 객체 | 테이블 Column 속성 |
| 여러 테이블 공유 | 가능 | 일반적으로 불가 |
| 증가 간격·최댓값 | 다양한 옵션 | 상대적으로 단순 |
| 번호 생성 시점 | NEXTVAL 호출 | INSERT 시 자동 |
| 대표 환경 | Oracle, PostgreSQL, MariaDB 등 | MySQL, MariaDB |
| 번호 공백 | 발생 가능 | 발생 가능 |

Transaction이 ROLLBACK되거나 INSERT가 실패하더라도 이미 사용된 번호가 다시 돌아오지 않을 수 있다. 자동 번호는 순서를 보장하는 업무 번호보다 행을 식별하는 대리키로 사용하는 것이 적합하다.

---

## 28. 오늘 코드에서 확인할 오류와 주의점

### Column 이름 오타

```text
dpet_id → dept_id
CONSTRAINT NAME → CONSTRAINT_NAME
CUSTOMENT_NAME → customer_name
INCREAMENT → INCREMENT
```

### 중복 VALUES

하나의 INSERT 뒤에 독립적인 `VALUES` 절을 여러 번 작성할 수 없다. 여러 행을 입력하려면 하나의 `VALUES` 뒤에 행을 쉼표로 구분한다.

### CASCADE 사용 범위

`ON DELETE CASCADE`는 부모 삭제가 대량 자식 삭제로 이어질 수 있다. 편의성만 보고 설정하지 않고 업무의 소유 관계와 삭제 정책을 확인한다.

### View는 항상 읽기 전용이 아님

View의 구조에 따라 수정 가능 여부가 달라진다. JOIN·집계 View는 대체로 수정이 제한된다.

### DML 실행 전 SELECT

```sql
SELECT *
FROM target_table
WHERE 변경할_조건;
```

변경·삭제 대상이 맞는지 먼저 확인한 후 동일한 WHERE 조건으로 UPDATE 또는 DELETE한다.

### Transaction 상태 확인

Auto Commit 상태에서는 ROLLBACK이 기대대로 동작하지 않을 수 있다. 중요한 DML 전에 Transaction을 명시적으로 시작한다.

---

## 29. 최종 흐름 정리

```text
PK를 가진 부모 테이블
  ↓
FK를 가진 자식 테이블
  ↓
ON DELETE·ON UPDATE 정책 설정
  ↓
ALTER TABLE로 구조와 제약조건 변경
  ↓
information_schema로 제약조건 확인
  ↓
VIEW로 필요한 데이터만 공개
  ↓
UPDATE·DELETE로 데이터 변경
  ↓
COMMIT 또는 ROLLBACK
  ↓
Sequence·AUTO_INCREMENT로 식별 번호 생성
```

오늘 학습의 핵심은 테이블을 생성하는 문법만 외우는 것이 아니다. PK와 FK로 관계를 만들고, 제약조건으로 잘못된 데이터를 차단하고, Transaction으로 변경 작업의 안전성을 관리하는 전체 흐름을 이해하는 것이다.
