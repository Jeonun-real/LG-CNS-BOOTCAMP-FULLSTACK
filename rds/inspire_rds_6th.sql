

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
		,ADDDATE(CURDATE(), INTERVAL 30 DAY)
		,SUBDATE(NOW(),INTERVAL 30 DAY)
		,SUBTIME(now(), '01:00:00');

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

SELECT *
FROM employee
WHERE hire_date <=CURDATE() - INTERVAL 30 YEAR;

SELECT emp_name, hire_date, ROUND(DATEDIFF(CURDATE(), hire_date) / 365) AS '근속연수'
FROM employee
WHERE DATEDIFF(CURDATE(), HIRE_DATE)/365 >=30;

-- D DEFINITION L
CREATE TABLE CUPON_TBL(
	CREATE_AT DATE,
	END_AT DATE
);

SELECT * 
FROM CUPON_TBL;

-- DML
INSERT INTO CUPON_TBL(CREATE_AT, END_AT) VALUES(NOW(),ADDDATE(NOW(),INTERVAL 7 DAY));


SELECT hire_date
		,SUBSTRING(hire_date, 1, 4)
		,cast(YEAR(hire_date) AS CHAR)
		,cast(MONTH(hire_date) AS CHAR)
		,CAST(day(hire_date) AS CHAR)
		,CAST(hour(hire_date) AS CHAR)
		,CAST(MINUTE(hire_date) AS CHAR)
		,CAST(SECOND(hire_date) AS CHAR)
FROM employee


-- weekday() : 날짜 요일을 숫자로 반환
-- DAYOFWEEK()
SELECT WEEKDAY(NOW()), DAYOFWEEK(NOW());

-- 기타 함수
-- 흐름 제어 함수(IF, IFNULL, NULLIF, CASE ~ WHEN ~ THEN ~ END
SELECT IF(100>200, 'TRUE','FALSE');

-- SWITCH CASE 느낌
SELECT CASE 2
			WHEN 1 THEN '1'
			WHEN 2 THEN '2'
			ELSE '없음'
		END AS '구분';


-- 성별 ?
-- 부서번호가 50번인 사원의 이름, 주민번호, 성별 검색한다면

SELECT *
FROM employee

SELECT emp_name
      ,emp_no
      ,IF(SUBSTRING(emp_no, 8, 1) IN ('1', '3'), '남자', '여자') AS 'gender'
FROM employee
WHERE dept_id = 50;

SELECT emp_name
      ,emp_no
      ,IF(SUBSTRING(emp_no, 8, 1) IN ('1', '3'), '남자', '여자') AS 'gender_if'
      ,CASE
          WHEN 
			 	SUBSTRING(emp_no, 8, 1) IN ('1', '3') THEN '남자'
          WHEN 
			 	SUBSTRING(emp_no, 8, 1) IN ('2', '4') THEN '여자'
          ELSE '?'
       END AS 'gender_case'
FROM employee
WHERE dept_id = 50;

SELECT SUBSTRING(emp_no, INSTR(emp_no,'-')+1,1)
FROM emplo


SELECT emp_name
      ,emp_no
      ,IF(SUBSTRING(emp_no, 8, 1) IN ('1', '3'), '남자', '여자') AS 'gender_if'
      ,CASE SUBSTRING(emp_no, 8, 1)
          WHEN '1' then '여자'
          WHEN '2' then '남자'
          WHEN '3' then '여자'
          WHEN '4' then '남자'
          ELSE '?'
       END AS 'gender_case'
FROM employee
WHERE dept_id = 50; 


-- 사원 데이터 테이블에서 직급이 j4 사원의 이름, 사번, 사수번호(mgr) 검색한다면?
-- 조건) 사수번호가 없는 사원 mgr_id 칼럼에 '관리자'출력


SELECT job_id, emp_name, emp_id, if(mgr_id='','관리자',mgr_id) AS '사수번호'
FROM employee
WHERE job_id = 'j4'

SELECT job_id
		, emp_name
		, emp_id
		, if(CHAR_LENGTH(mgr_id) <> 0, mgr_id, '관리자')
FROM employee
WHERE job_id = 'j4';

SELECT * FROM employee


-- 급여 등급을 나눠보고 싶다
-- 300 이하면 초급, 400 이하면 중급, 초과하면 고급
-- 사원번호, 이름, 급여, 급여등급 검색

SELECT emp_no
		,emp_name
		,salary
		,case when 
					salary <=3000000 then '초급'
				when 
					salary <=4000000 then '중급'
				ELSE '고급'
		END AS '급여등급'
FROM employee

-- 남자 사원의 정보만 출력
SELECT EMP_NAME
		,EMP_NO
		,'남자' AS GENDER
FROM employee
WHERE SUBSTRING(EMP_NO,8,1)='1';

-- 복수형 함수(GROUP BY ~)
-- 여려행의 결과를 입력으로해서 하나 또는 그 이상의 결과를 반환
-- WHERE 절에는 복수행 함수 X
-- SELECT 절에서는 사용가능하나, 일반 칼럼은 사용 불가

--Q) 사원수를 확인하고 싶다면?
SELECT EMP_NAME, 
		COUNT(*)
		,COUNT(BONUS_PCT)
		,COUNT(IFNULL(BONUS_PCT,0))
		,MIN(SALARY)
		,MAX(SALARY)		
		,AVG(SALARY)		
		,SUM(SALARY)
FROM employee

-- orderby [기준컬럼 | 표현식|칼럼인덱스|칼럼 별칭] ASC / DESC

SELECT emp_no
		,emp_name
		,salary AS S
		,case when 
					salary <=3000000 then '초급'
				when 
					salary <=4000000 then '중급'
				ELSE '고급'
		END AS 'GRADE'
FROM employee
ORDER BY GRADE;

-- sql day3(group by, having, join)

-- group by : 특정 커럼에 대해 동일한 값을 가지는 행들을 하나의 행으로 처리(통계)
-- group by {column | expr | position }
-- group by 절에 명세된 컬럼과 집계함수만 select 구문에 정의 가능


SELECT *
FROM employee;

SELECT MIN(salary)
FROM employee;

-- 그룹함수는 일반적으로 group by와 같이 사용
-- Q) 부서별 급여 총합 검색?

SELECT dept_id
		,SUM(salary)
FROM employee
WHERE salary>3000000
GROUP BY dept_id
HAVING SUM(salary) >= 4000000;

SELECT dept_id
		,MAX(salary)
FROM employee
GROUP BY dept_id


-- Q)급여등급별 인원수 집게

SELECT case when 
					salary <=3000000 then '초급'
				when 
					salary <=4000000 then '중급'
				ELSE '고급'
		END AS grade
		,COUNT(*) AS 'count'
FROM employee
GROUP BY grade
ORDER BY field(grade, '중급','초급','고급');

USE SQLdb

SELECT *
FROM buytbl;

SELECT *
FROM usertbl


-- q) 사용자별 구매총액을 검색한다면?
SELECT userid
		,SUM(price*amount)
FROM buytbl
GROUP BY userid;

-- q) 사용자병 평균 구매 개수
SELECT userid
		,ROUND(AVG(amount))
FROM buytbl
GROUP BY userid

-- q) 부서번호가 50번이거나 부서가 없는 사원의 이름, 급여를 출력하되    
-- 조건) 급여가 많은 사원부터 조회

SELECT emp_name, salary, dept_id
FROM employee e 
WHERE e.dept_id = '50' OR e.dept_id IS null
ORDER BY salary

위 결과를 바탕으로 성별에 따른 평균급여
SELECT 
    CASE 
        WHEN SUBSTRING(emp_no, 8, 1) IN ('1', '3') THEN '남자'
        WHEN SUBSTRING(emp_no, 8, 1) IN ('2', '4') THEN '여자'
    END AS gender,
    ROUND(AVG(salary))
FROM employee e 
WHERE e.dept_id = '50' OR e.dept_id IS NULL
GROUP BY gender
ORDER BY 2 DESC;

-- q) 부서별 급여 총액이 9000000 이상인 부서만 검색한다면?
SELECT dept_id, SUM(salary)
FROM employee
GROUP BY dept_id
HAVING SUM(salary)>9000000

SELECT *
FROM employee

USE sqldb

-- 사용자별 총 구매액이 100 이상인 사용자들만 검색한다면?

SELECT USERID, SUM(PRICE * amount)
FROM buytbl
GROUP BY USERID
HAVING SUM(PRICE * amount) > 100
ORDER BY 2 desc

-- 계층적 집계결과 확인 with rollup

-- 구매한 목록 중 그룹이름별 구매비용 검색


SELECT groupname
		,num
		,SUM(price * amount)
FROM buytbl
GROUP BY groupname, num WITH ROLLUP

/* 
window function(분석함수) 기존 행을 유지하면서 집계결과를 열 추가
group function(avg, rank, row_number, dense_rank) ~ over(partition by | order by)
*/

USE lgcns

SELECT emp_name
		,salary
		,dept_id
		,AVG(salary) OVER(PARTITION BY dept_id) AS 'davg' 
FROM employee

-- 사원에 대한 급여순위
SELECT emp_name
		,salary
		,RANK() OVER(ORDER BY salary DESC) AS 'rank'
FROM employee;

SELECT emp_name
		,salary
		,dense_RANK() OVER(ORDER BY salary DESC) AS 'rank'
FROM employee;

SELECT emp_name
		,salary
		,ROW_NUMBER() OVER(ORDER BY salary DESC) AS 'rank'
FROM employee;

-- 부서별 급여 순위=

SELECT emp_name
		,dept_id
		,salary
		,rank() OVER(PARTITION BY dept_id ORDER BY salary DESC) AS 'rank'
FROM employee;

/*
JOIN
- N개 이상의 테이블을 서로 묶어서(가상의 테이블) 하나의 결과 집합을 만드는 것
- 관계형 데이터베이스의 가장 큰 특징
- 관계(1:1. 1:N, N:M)
- N:M은 1:M으로 풀어줘야됨

ANSI 표준 구문
SELECT
FROM TABLE
[INNER] JOIN TABLE ON(조건식) <- 추천
[INNER] JOIN TABLE USING(공통 칼럼명)

NATURAL [INNER] JOIN TABLE;

LEFT | RIGHT 
[OUTER] JOIN TABLE;
*/

-- equip joun 부모의 pk와 자식의 fk를 비교하여 연결
SELECT e.EMP_NAME
		,d.DEPT_NAME
FROM employee e, department d
WHERE e.dept_id = d.dept_id

SELECT e.EMP_NAME
		,d.DEPT_NAME
FROM employee e
join department d ON(e.DEPT_ID = d.DEPT_ID);

SELECT e.EMP_NAME
		,d.DEPT_NAME
FROM employee e
join department d USING(dept_id);

-- cross join
SELECT e.EMP_NAME
		,d.DEPT_NAME
FROM employee e
join department d;


SELECT *
FROM employee;
SELECT *
FROM department

SELECT EMP_NAME
		,JOB_TITLE
		,DEPT_NAME
FROM JOB J
JOIN EMPLOYEE E USING(JOB_ID)
JOIN DEPARTMENT D USING(DEPT_ID)


SELECT EMP_NAME
		,JOB_TITLE
		,DEPT_NAME
FROM JOB J
JOIN EMPLOYEE E ON(J.JOB_ID=E.JOB_ID)
JOIN DEPARTMENT D ON(E.DEPT_ID=D.DEPT_ID)

SELECT *
FROM sal_grade;


SELECT EMP_NAME
		,JOB_TITLE
		,DEPT_NAME
		,LOC_DESCRIBE
FROM JOB J
JOIN EMPLOYEE E ON(J.JOB_ID=E.JOB_ID)
JOIN DEPARTMENT D ON(E.DEPT_ID=D.DEPT_ID)
JOIN LOCATION L ON(L.LOCATION_ID = D.LOC_ID);


SELECT EMP_NAME
		,JOB_TITLE
		,DEPT_NAME
		,LOC_DESCRIBE
FROM JOB J
JOIN EMPLOYEE E ON(J.JOB_ID=E.JOB_ID)
JOIN DEPARTMENT D ON(E.DEPT_ID=D.DEPT_ID)
JOIN LOCATION L ON(L.LOCATION_ID = D.LOC_ID)
JOIN COUNTRY C ON(C.COUNTRY_ID = L.COUNTRY_ID)
WHERE DEPT_NAME LIKE '해외%'
AND LOC_DESCRIBE LIKE '아시아%';



SELECT EMP_NAME
		,JOB_TITLE
		,DEPT_NAME
		,LOC_DESCRIBE
FROM JOB J
JOIN EMPLOYEE E ON(J.JOB_ID=E.JOB_ID)
JOIN DEPARTMENT D ON(E.DEPT_ID=D.DEPT_ID)
JOIN LOCATION L ON(L.LOCATION_ID = D.LOC_ID)
JOIN COUNTRY C ON(C.COUNTRY_ID = L.COUNTRY_ID)
JOIN sal_grade s ON (e.salary BETWEEN s.lowest AND s.highest)
WHERE DEPT_NAME LIKE '해외%'
AND LOC_DESCRIBE LIKE '아시아%'
ORDER BY s.slevel DESC;

USE sqldb

SELECT * from usertbl;
SELECT * from buytbl;

-- 사용자의 아이디가 jyp 인 유저의 이름과 구매상품을 검색한다면?
SELECT NAME
		,prodname
FROM buytbl b
JOIN usertbl u ON(b.userID = u.userID)
WHERE b.userID = 'JYP'



-- 사용자의 아이디, 이름, 구매상품, 주소, 연락처(mobile+mobile2)를 검색한다면?
SELECT b.userid
		, NAME
		, prodname
		, addr
		, CONCAT(mobile1,'-', mobile2) AS '연락처'
FROM buytbl b
JOIN usertbl u ON(b.userID = u.userID)

-- 위 요구사항에서 구매이력이 있는 회원만 검색한다면? (exist)
SELECT distinct b.userid
		, NAME
		, prodname
		, addr
		, CONCAT(mobile1, mobile2) AS '연락처'
FROM buytbl b
RIGHT join usertbl u ON(b.userID = u.userID)
WHERE prodname IS NULL;

USE sqldb


USE lgcns

-- ourter join
SELECT *
FROM employee e
left JOIN department d USING(dept_id)

--부서배치를 받지 않은 사원의 이름, 부서명을 검색한다면?

SELECT e.emp_name
		,d.dept_name
FROM employee e
left JOIN department d ON(e.DEPT_ID = d.dept_id)
WHERE dept_name IS NULL;

-- self recursive relationship
-- 사원이름과 사수의 이름을 검색

SELECT e.EMP_name
		,m.EMP_name
		,s.EMP_name
FROM employee e
left jOIN employee m ON(e.MGR_ID = m.EMP_ID)
left jOIN employee s ON(m.MGR_ID = s.EMP_ID)


-- SQL DAY4 subquery

/*
서브쿼리란?
- 하나의 쿼리가 다른 쿼리를 포함하는 구조
- 유형(유형에 따라서 사용할수 있는 연산자가 다름)
	- 단일행(단인열, 다중열): 일반연산자들 사용가능
	- 다중행(단인열, 다중열): ANY. ALL, IN
- 위치
	- SELECT (SCALAR SUBQUERY). FROM(INLINE VIEW), WHERE (SUBQUERY)
*/

USE lgcns

SELECT student_name
FROM tb_student s
WHERE ABSENSE_YN = 'Y'
AND DEPARTMENT_NO(
						SELECT DEPARTMENT_NO
						FROM TB_DEPARTMENT
						WHERE DEPARTMENT_NAME ='국어국문학과'
						)
AND SUBSTRING(S.STUDNET_SSN, 8, 1) = '2';

SELECT *
FROM EMPLOYEE

SELECT *
FROM department

-- 나승원 사원과 같은 직급이고 나승원 사원보다 많은 급여를 받는 사원의 정보를 겁색한다면?



-- 단일행/단일열
SELECT *
FROM employee
WHERE job_id = ( 
					  SELECT job_id
					  FROM employee
					  WHERE emp_name = '나승원'
					)
AND salary> (	
					SELECT salary
					FROM employee
					WHERE emp_name = '나승원'
				)
				
-- 부서별 급여총합이 가장 높은 부서의 부서명과 급여총합을 검색한다면?

SELECT
FROM department d, employee e 
WHERE
GROUP BY e.DEPT_ID



SELECT d.DEPT_NAME,SUM(e.SALARY) AS 'suss', MAX(suss)
FROM employee e 
JOIN department d ON (e.DEPT_ID = d.DEPT_ID)
GROUP BY d.DEPT_NAME
ORDER BY 2 DESC;

-- inline table = virtual table
SELECT v.dept_name, MAX(v.sum)
FROM ( 
		SELECT d.DEPT_NAME,SUM(e.SALARY) AS 'sum'
		FROM employee e 
		JOIN department d ON (e.DEPT_ID = d.DEPT_ID)
		GROUP BY d.DEPT_NAME
		) V
	
	
-- 다중행 
-- 서브쿼리의 반환결과가 1개가 아니라 여러개이기때문에 any all in을 사용하여야 함.
SELECT D.DEPT_NAME, SUM(e.SALARY)
FROM employee e 
JOIN department d ON (e.DEPT_ID = d.DEPT_ID)
GROUP BY dept_name
HAVING SUM(e.salary) = (
								SELECT MAX(v.total)
								FROM (
										select SUM(salary) AS 'total' 
										FROM employee e
										JOIN department d ON (e.DEPT_ID = d.DEPT_ID)
										GROUP BY dept_name
										) v
								) 


	
-- limit을 활용한 TOP-N 분석 방식						
SELECT D.DEPT_NAME, SUM(e.SALARY)
FROM employee e 
JOIN department d ON (e.DEPT_ID = d.DEPT_ID)
GROUP BY dept_name
ORDER BY SUM(e.SALARY) DESC
LIMIT 1



SELECT *
FROM employee
WHERE salary = ( SELECT MIN(salary) 
						FROM employee);



SELECT	G.STUDENT_NO
         ,S.STUDENT_NAME
FROM        tb_grade G
JOIN        tb_student S USING(STUDENT_NO)
GROUP BY    STUDENT_NO
HAVING G.STUDENT_NO IN (
    								SELECT    S.STUDENT_NO
    								FROM        tb_student S
    								WHERE        S.DEPARTMENT_NO = (
        																		SELECT    DEPARTMENT_NO
        																		FROM        TB_DEPARTMENT
        																		WHERE    DEPARTMENT_NAME = '국어국문학과'
    																			)
								)
ORDER BY AVG(G.POINT) DESC
LIMIT 1;





SELECT D.DEPT_NAME, SUM(E.SALARY)
FROM department D
JOIN employee E ON (D.DEPT_ID = E.DEPT_ID)
GROUP BY DEPT_NAME
HAVING SUM(E.SALARY) = (
                                SELECT MAX(SUM_SALARY)
                                FROM (
                                            SELECT SUM(SALARY) AS 'SUM_SALARY'
                                            FROM department D
                                            JOIN employee E ON (D.DEPT_ID = E.DEPT_ID) 
                                            GROUP BY DEPT_NAME
                                      ) V
                            );
                            
                            
SELECT v.STUDENT_NO
		,v.STUDENT_NAME
FROM	(    
			SELECT	s.STUDENT_NO
						,s.STUDENT_NAME
						,RANK() OVER (ORDER BY AVG(g.POINT) DESC) AS rank
         FROM	TB_GRADE g
         JOIN  TB_STUDENT s ON g.STUDENT_NO = s.STUDENT_NO
         JOIN  TB_DEPARTMENT d ON s.DEPARTMENT_NO = d.DEPARTMENT_NO
         WHERE d.DEPARTMENT_NAME = '국어국문학과'
         GROUP BY s.STUDENT_NO, s.STUDENT_NAME ) v
WHERE v.rank = 1;



--ERROR
SELECT E.*
		,MIN(salary)
FROM employee E;

-- 다중열 case
-- 부서별 최소금액을 받는 사원의 이름, 부서명, 급여를 검색한다면?


SELECT emp_name
		,dept_id
		,salary
FROM employee
WHERE (dept_id, salary) in (SELECT dept_id, MIN(salary)
									FROM employee e
									GROUP BY e.dept_id)
									

SELECT sub.emp_name
		,sub.dept_name
		,sub.salary
FROM(
		SELECT e.emp_name
				,d.dept_name
				,e.salary
				,RANK() OVER(PARTITION BY e.dept_id ORDER BY e.salary) AS 'rnk'
		FROM employee e
		JOIN department d ON(e.dept_id  =d.DEPT_ID)
		) sub
WHERE sub.rnk =1;


SELECT *
FROM  employee

SELECT *
FROM job

-- 과장 직급 급여확인
SELECT e.EMP_NAME,e.SALARY
FROM employee e
JOIN job j ON(e.job_id = j.JOB_ID)
WHERE j.JOB_title = '과장';

-- 대리 직급 급여확인
SELECT e.EMP_NAME,e.SALARY
FROM employee e
JOIN job j ON(e.job_id = j.JOB_ID)
WHERE j.JOB_title = '대리';


-- 다중행 서브쿼리(any, all)
-- >any, <any (박스 안 상한보다 작은거, 하한보다 큰거)
-- >all, <all (박스 밖 상한보다 큰거, 하한보다 작은거)
-- 과장직급보다 많은 급여를 받는 대리직급의 사원정보를 검색한다면?
				
SELECT e.EMP_NAME,
       e.SALARY
FROM employee e
JOIN job j ON e.job_id = j.JOB_ID
WHERE j.JOB_title = '대리'
AND e.SALARY < All (
    SELECT e2.SALARY
    FROM employee e2
    JOIN job j2 ON e2.job_id = j2.JOB_ID
    WHERE j2.JOB_title = '과장'
);


-- top -N query === limit (없는 벤더사 제품이 있을수 있음)
-- 정렬된 서브쿼리를 바탕으로 where c.순위 = 1;
-- limit offset()

USE lgcns

SELECT E.DEPT_ID, SUM(salary)
FROM employee e
GROUP BY dept_id
ORDER BY 2 DESC;


-- offset: n+1 번지부터 n번지까지의 범위검색
SELECT e.DEPT_ID, SUM(salary)
FROM employee e
GROUP BY dept_id
ORDER BY 2 DESC
LIMIT 3 OFFSET 0;
		
		
-- 상관관계 서브쿼리
-- 자기직급의 평균 급여를 받는 직원의 이름, 직급명, 급여를 검색한다면?
-- 계산의 편의를 위해서 급여는 정수 5자리까지 절삭(truncate)

--서브쿼리로 쓰고 싶은데 JOB_ID가 전달이 안되면 문제가 생김


-- CASE 01
SELECT e.EMP_NAME
		,j.JOB_TITLE
		,e.SALARY
FROM job j
JOIN employee e ON (j.JOB_ID = e.JOB_ID)
WHERE (e.job_id, e.SALARY) IN (
										SELECT JOB_ID, TRUNCATE(AVG(SALARY),-5)
											FROM employee
											GROUP BY job_id	
									);

-- 잘못된 예시
SELECT e.EMP_NAME
		,j.JOB_TITLE
		,e.SALARY
FROM job j
JOIN employee e ON (j.JOB_ID = e.JOB_ID)
WHERE (e.SALARY) IN (
									SELECT TRUNCATE(AVG(SALARY),-5)
									FROM employee
									GROUP BY job_id
									) v

-- CASE 02

SELECT	E.EMP_NAME,
			J.JOB_TITLE,
			E.SALARY					
FROM	(	SELECT	JOB_ID, 
						TRUNCATE(AVG(SALARY), -5) AS `JOBAVG`
			FROM		employee 
			GROUP BY JOB_ID ) V
JOIN	employee E ON ( V.JOB_ID = E.JOB_ID AND V.JOBAVG = E.SALARY ) 
JOIN	job		J ON ( E.JOB_ID = J.JOB_ID) ;

-- case 03 상관관계 쿼리 
SELECT e.EMP_NAME
		,j.JOB_TITLE
		,e.SALARY
FROM job j
JOIN employee e ON (j.JOB_ID = e.JOB_ID)
WHERE e.SALARY = (
						SELECT TRUNCATE(AVG(SALARY),-5)
						FROM employee ee
						WHERE ee.job_id = e.job_id);
					
					
-- explain 존재
EXPLAIN
SELECT e.EMP_NAME
		,j.JOB_TITLE
		,e.SALARY
FROM job j
JOIN employee e ON (j.JOB_ID = e.JOB_ID)
WHERE e.SALARY = (
						SELECT TRUNCATE(AVG(SALARY),-5)
						FROM employee ee
						WHERE ee.job_id = e.job_id);
					

-- DDL 물리적 저장공간인 테이블 생성과 논리적 저장공간 VIEW
-- DDL: CREATE, ALTER, DROP
-- CONSTRAIN : PK, FK, NOT NULL, UNIQUE, CHECK
-- DEFAULT : DML NULL 값이 입력될때 DEFALUT로 선언된 값이 자동으로 할당

/*
CREATE TABLE TABLE_NAME(
	COLUMN_NAME DATETYPE(SIZE) [DEFAULT][CONSTRAIN TYPE] (옴션)
	[CONSTRAIN TYPE] 테이블 레벨의 제약조건
)

OBJECT: TABLE, VIEW, INDEX. SEQUENCE, ETC..
*/

DROP DATABASE IF EXISTS TABLEDBtb_blogtb_blog;
CREATE DATABASE TABLEDB;

USE TABLEDB;

DROP TABLE IF EXISTS TB_blog;

-- CONSTRAIN은 관리용도
-- CONSTRAINT TB_BLOG_ID_PK PRIMARY KEY 
-- 보통 테이블 이름_칼럼명_속성으로 이름만듬

CREATE TABLE TB_BLOG(
 -- BLOG_ID VARCHAR(50) PRIMARY KEY. --COLUMN CONSTRAINT TB_BLOG_ID_PK 
 BLOG_ID VARCHAR(50),
 TITLE 	VARCHAR(100),
 CONTENT VARCHAR(100),
 EMAIL 	VARCHAR(100),
 CATEGORY VARCHAR(100) CHECK(CATEGORY IN ('전체','취미','일상','코딩')), -- COLUMN 레벨 제약
 CREATE_AT DATE DEFAULT SYSDATE(),
 PRIMARY KEY (BLOG_ID) -- TABLE 레벨 CONSTRAINT
);


SELECT *
FROM tb_blog;


--DML
INSERT INTO tb_blog(BLOG_ID, CATEGORY)
VALUES ('1','전체');


