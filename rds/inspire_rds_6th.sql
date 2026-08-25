

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

-- SQL DAY2(함수)
/*
함수?
- 하나의 큰 프로그램에서 반복적으로 사용되는 부분들을 분리한 작은 서브프로그램
- 호출하고 반환
- 유형
	- 단일행 함수(문자, 날짜, 숫자, 기타변환 함수...)
	- 복숳행 함수(그룹) : min, max, sum, count, avg, etc...(window function)
- 정렬
- 서브그룹(group by, having)	 
*/

SELECT *
FROM employee;

-- 칼럼의 타입: 문자, 날짜, 숫자
-- 문자열 함수:
-- LENGTH, CONCAT, SUBSTRING, LEFT, RIGHT, INSTR, REPLACE, UPPER, LOWER, TRIM .. '

SELECT LENGTH('서정운')
		, LENGTH('jeongun')
		, CHAR_LENGTH('서정운')

SELECT EMP_NAME
		, LENGTH(EMP_NAME) 
FROM employee;

SELECT UPPER('lgcns')
		, LOWER('LGCNS');


SELECT LENGTH(TRIM('  TRIM  '))
		,LENGTH(LTRIM('  TRIM  '))
		,LENGTH(RTRIM('  TRIM  '))

-- 문자열을 채우는 함수 : LPAD, RPAD
SELECT LPAD('5',3,'#')
		,LENGTH(LPAD('5',3,' '))
		,RPAD('5',3,' ')
		,LENGTH(RPAD('5',3,' '));


SELECT EMAIL
		,LENGTH (EMAIL)
		,LPAD(EMAIL, 20, '#')
FROM employee;

SELECT CHAR(65)
		,CONCAT('서정운','님');

-- SUBSTRING : 부분문자열을 반환하는 함수(LEFT, RIGHT)
SELECT SUBSTRING('ABCDEF',1,2)
		,LEFT('ABCDEF', 2)
		,RIGHT('ABCDEF', 2);

SELECT SUBSTRING('THIS IS INSPIRE CAMP' FROM 9 FOR 7)
		,SUBSTRING('THIS IS INSPIRE CAMP' ,9 , 7)
		,SUBSTRING_INDEX('WWW.LGCNS.COM' ,'.' , 3);


-- INSTR: 문자열을 이용해서 구분문자열의 인덱스를 반환
SELECT INSTR('LGCNS CAMP','CAP');

-- Q) . 앞의 문자 'C' 인덱스 번지를 검색
-- Q) 메일 아이디만 추출

SELECT *
FROM employee;

SELECT EMAIL
      ,INSTR(EMAIL, 'c.')
      ,LEFT(EMAIL, INSTR(EMAIL, '@') - 1)
      ,SUBSTRING_INDEX(EMAIL, '@', 1)
FROM EMPLOYEE;
		

SELECT EMAIL,
		INSTR(EMAIL,'C.'),
		CHAR_LENGTH(EMAIL)-CHAR_LENGTH('.COM'),
		LEFT(EMAIL,INSTR(EMAIL,'@')-1),
		SUBSTRING_INDEX(EMAIL,'@',1)
FROM employee;

-- 반복 및 부분무자열 변경
SELECT REPEAT('LGCNS',3)
		,REPLACE('오늘은 즐거운 함수 수업','함수','메서드');


-- 입사년도만 추출
SELECT SUBSTRING(HIRE_DATE, 1, 4)
		,LEFT(HIRE_DATE, 4)
		,SUBSTRING_INDEX(HIRE_DATE,'-',1)
FROM employee;

SELECT CONCAT(LEFT(EMP_NO,8),'******')
FROM employee;


-- CASTING : CAST(TYPE AS TYPE)
SELECT SUBSTRING(EMP_NO, 1, 6)
		,SUBSTRING(EMP_NO, 8, 7)
		,CAST(SUBSTRING(EMP_NO, 1, 6) AS INT) + CAST(SUBSTRING(EMP_NO, 8, 7) AS INT)
FROM employee;

SELECT SUBSTRING(EMP_NO, 1, 6)
		,SUBSTRING(EMP_NO, 8, 7)
		,SUBSTRING(EMP_NO, 1, 6) + SUBSTRING(EMP_NO, 8, 7)
FROM employee;



USE SQLDB;

SELECT * 
FROM usertbl;

SELECT * 
FROM buytbl;

-- 고객들에 대한 평균 구매 개수를 검색
SELECT AVG(AMOUNT)
		,CAST(AVG(AMOUNT) AS INT)
		,CAST(AVG(AMOUNT) AS SIGNED INTEGER)
FROM buytbl;

-- 구매번호, 총금액(PRICE*AMOUNT =), 구매액을 검색한다면?
-- CAST() 함수 활용


SELECT num AS '구매번호',
		CONCAT(
			cast(price AS VARCHAR(10))
			,'*'
			, cast(amount AS VARCHAR(10))
			, '='
			) AS '총금액'
		, price * amount AS '구매액'
FROM buytbl;

-- 숫자함수
SELECT ABS(-100)
		,CEILING(4.7)
		,CEILING(4.1)
		,FLOOR(4.7)
		,FLOOR(4.1)
		,ROUND(3234.2314, 2)
		,ROUND(3234.2314, -2)
		,TRUNCATE(3234.2314, 2)
		,TRUNCATE(3234.2314, -2)
		,GREATEST(10,20,100,30)
		,LEAST(10,20,100,30);

-- 날짜함수
SELECT NOW()
		,SYSDATE()
		,CURDATE()
		,CURTIME()
		,ADDDATE(CURDATE(), INTERVAL 30 YEAR)
		,ADDDATE(CURDATE(), INTERVAL 30 MONTH)
		,ADDDATE(CURDATE(), INTERVAL 30 DAY);


SELECT HIRE_DATE
		,CAST(HIRE_DATE+1 AS DATE)
FROM employee;

-- 입사일을 기준으로 근속연수가 30년이 되는 일자를 검색한다면?
SELECT HIRE_DATE,
		ADDDATE(HIRE_DATE, INTERVAL 30 YEAR) 
FROM employee;

SELECT * 
FROM employee;

-- 오늘 날짜를 기준으로 근속연수가 30년 이상인 사원의 모든 정보를 검색한다면?
SELECT emp_name, DATEDIFF(CURDATE(), HIRE_DATE)
FROM employee
WHERE DATEDIFF(CURDATE(), HIRE_DATE) > 30* 365;




