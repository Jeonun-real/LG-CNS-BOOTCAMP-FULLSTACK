-- 1
SELECT department_name AS '학과 명'
		, category AS '계열'
FROM tb_department;

-- 2 

SELECT CONCAT(department_name,'의 정원은 ',capacity,'명 입니다') AS '학과별 정원'
from tb_department;

-- 3
SELECT STUDENT_NAME
FROM tb_DEPARTMENT D, tb_student s
WHERE s
WHERE department_no = 001 AND student_ssn LIKE '%-2%' and absence_yn = 'Y';

SELECT student_name
FROM tb_student
WHERE department_no =
		(SELECT department_no FROM tb_department WHERE department_name = '국어국문학과')
		AND student_ssn LIKE '%-2%' and absence_yn = 'Y';


-- 4
SELECT STUDENT_NAME
FROM tb_student
WHERE STUDENT_NO IN ('A513079', 'A513090', 'A513091', 'A513110', 'A513119');

-- 5
SELECT DEPARTMENT_NAME, CATEGORY
FROM tb_department
WHERE capacity BETWEEN 20 AND 30;

-- 6
SELECT PROFESSOR_NAME
FROM tb_professor
WHERE department_no IS NULL

-- 7 
SELECT COUNT(*) AS '학생 수'
FROM tb_student
WHERE ISNULL(department_no) 

-- 8
SELECT CLASS_NO
FROM tb_class
WHERE preattending_class_no IS NOT null

-- 9
SELECT DISTINCT CATEGORY
FROM tb_department 

-- 10
SELECT STUDENT_NO
		, STUDENT_NAME
		, STUDENT_SSN
FROM tb_student
WHERE STUDENT_ADDRESS 
		LIKE '%전주%' 
		AND ABSENCE_YN = 'N' 
		AND ENTRANCE_DATE LIKE '2002%'



-- DAY2 

-- 7, 10, 12, 13, 14, 15, 스킵
-- 3번은 현재 년도를 기준



-- 1 
SELECT *
FROM tb_student;

SELECT *
FROM tb_department

SELECT *
FROM tb_professor

SELECT s.student_no 학번
		, s.student_name 이름
		, s.entrance_date 입학년도
FROM tb_student s
WHERE s.DEPARTMENT_NO = 002
ORDER BY 3 ASC;


-- 2
SELECT professor_name
		, professor_ssn
FROM tb_professor p
WHERE CHAR_LENGTH(p.PROFESSOR_NAME) <> 3;

-- 3
SELECT professor_name, YEAR(CURDATE())-(1900+SUBSTRING(p.PROFESSOR_SSN,1,2)) AS '나이'
FROM tb_professor p
WHERE SUBSTRING(P.professor_ssn,INSTR(P.professor_ssn,'-')+1, 1) = '1' 
ORDER BY 2;


-- 4 
SELECT substring(p.PROFESSOR_NAME,2) AS '이름'
FROM tb_professor p

SELECT RIGHT(p.PROFESSOR_NAME, CHAR_LENGTH(p.PROFESSOR_NAME,2) AS '이름'
FROM tb_professor p

--5
SELECT S.STUDENT_NO,
       S.STUDENT_NAME
FROM tb_student S
WHERE YEAR(S.ENTRANCE_DATE)
      - CAST(CONCAT('19', LEFT(S.STUDENT_SSN, 2)) AS INT) > 19;

--6
-- WEEKDAY(0 ~ 월)
-- DAYOFWEEK(1 ~ 일요일)

SELECT DAYNAME('2020-12-25');

SELECT CASE
            WHEN DAYOFWEEK('2020-12-25') = 1 THEN '일요일'
            WHEN DAYOFWEEK('2020-12-25') = 2 THEN '월요일'
            WHEN DAYOFWEEK('2020-12-25') = 3 THEN '화요일'
            WHEN DAYOFWEEK('2020-12-25') = 4 THEN '수요일'
            WHEN DAYOFWEEK('2020-12-25') = 5 THEN '목요일'
            WHEN DAYOFWEEK('2020-12-25') = 6 THEN '금요일'
            ELSE '토요일' 
        END AS '요일';
        
        
-- 8 
SELECT STUDENT_NO, STUDENT_NAME
FROM tb_student
WHERE SUBSTRING(STUDENT_NO, 1, 1) != 'A';

-- 9
SELECT ROUND(AVG(POINT), 1) AS '학점'
FROM tb_grade
WHERE STUDENT_NO = 'A517178';





