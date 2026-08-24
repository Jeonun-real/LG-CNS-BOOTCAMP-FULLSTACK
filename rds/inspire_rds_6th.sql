

-- SQL DAY 01

USE lgcns;

SHOW DATABASES;

DESC country;

/*
SELECT 문법(데이터를 검색할 때 사용ㅎ라는 구문)

SELECT DJSTINCT 컬럼명| 컬럼명 | * | 표현식 및 함수 | [AS] 별칭
FROM 테이블 이름
[WHERE] 행의 제한
[GROUP BY] 데이터를 그룹으로 묶을때
[HAVING] 그룹에 대한 조건
[ORDER BY] 정렬(ASC, DESC)

별칭은 공백을 포함하는 문자 및  특수문자 사용할수 있으나
사용하기 위해서는 '',`` 사용

키워드는 대소문자를 구분하지 않는다.
데이터는 대소문자를 구분한다.
*/

SELECT *
FROM job;

SELECT *
FROM employee;

SELECT *
FROM department;


SELECT emp_id
		,emp_name
FROM employee;

-- Q) 부서번호가 90번인 사람의 모든 정보를 검색한다면?

SELECT *
FROM employee
WHERE dept_id = '90';

SELECT emp_name
		, salary
		,(salary+(salary + bonus_pct))*12 AS '(연봉)'
FROM employee;

-- null 처리 함수 : IFNULL(NULL, 'XXXXX'), NULLIF(100,'NOT NULL');

SELECT IFNULL (NULL, '포세이돈은 외눈박이')
		, NULLIF(100, 200)


SELECT emp_name
		, salary
		,(salary+(salary + IFNULL(bonus_pct,0)))*12 AS '(연봉)'
FROM employee;

-- DISTINCT : 칼럼에 포함된 중복 값을 한번만 출력, 중복이여도 조합은 1번만
SELECT DISTINCT DEPT_ID, JOB_ID
FROM employee;

-- WHERE
-- 연산(비교(LIKE, NOTLIKE), 산술, 논리(AND, OR, NOT))


SELECT *
FROM employee;

-- Q) 부서번호가 90번 이상이거나 급여가 4000000 이상인 사원이 모든정보를 검색한다면?
SELECT *
FROM employee
WHERE DEPT_ID = '90' AND SALARY >= 4000000;

-- CONCAT: 연결 연산자
SELECT CONCAT('임정섭','강사님은','점심을','먹었을까');

SELECT CONCAT(EMP_NAME, '님의 금여는', SALARY,'(월)입니다.') AS 급여정보
FROM employee;

-- 급여가 350 이상 550이하인 사원의 이름, 급여, 직급을 검색한다면?
-- 상한과 하한을 포함하는 경계사용 between - AND

SELECT emp_name
		, salary
		,job_id
FROM  employee
WHERE salary BETWEEN 3500000 AND 5500000; 

/*
LIKE, NOT LIKE 패턴검색, 연산자
% : 하나이상의 문자와 매핑
_: 하나의 문자와 매핑
*/
-- Q) 김씨 성을 가지는 사원의 모든 정보를 검색
SELECT *
FROM employee
WHERE emp_name not LIKE '김%';

--Q) 메일 아이디 중 _ 앞자리가 3자리인 사원의 정보를 검색

SELECT *
FROM employee
WHERE EMAIL LIKE '___\_%';


--Q) 부서배치를 받지 않는 사원에 대한 정보를 검색
-- IS NULL, IS NOT NULL

SELECT *
FROM employee
WHERE dept_id IS not NULL;

SELECT *
FROM employee
WHERE ISNULL(DEPT_ID)=1;

-- OR(이거나),IN
-- Q) 부서번호가 60번 이거나 90번인 사원의 정보를 검색한다면?

SELECT *
FROM employee
WHERE dept_id IN('60','90');





