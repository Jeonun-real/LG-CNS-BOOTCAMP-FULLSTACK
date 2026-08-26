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

-- 5
SELECT S.STUDENT_NO,
       S.STUDENT_NAME
FROM tb_student S
WHERE YEAR(S.ENTRANCE_DATE)
      - CAST(CONCAT('19', LEFT(S.STUDENT_SSN, 2)) AS INT) > 19;

-- 6
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

-- 10
SELECT *
FROM tb_grade

SELECT department_no AS '학과번호'
		,COUNT(*) AS '학생수(명)'
FROM tb_student s
GROUP BY s.department_no


-- 12
SELECT SUBSTRING(term_no,1,4) AS '년도'
		, round(AVG(POINT),1) AS '년도 별 평점' 
FROM tb_grade g
WHERE g.student_no = 'A112113'
GROUP BY SUBSTRING(term_no,1,4)





--13
SELECT *
FROM tb_student

SELECT s.department_no AS '학과코드명'
		, SUM(case when S.ABSENCE_YN = 'Y' THEN 1 ELSE 0 END) AS '휴학생 수'
FROM tb_student s
GROUP BY s.department_no
ORDER BY 1


--14
SELECT s.student_name AS '동일이름'
		, COUNT(*) AS '동명인 수'
FROM tb_student s
GROUP BY 1
HAVING COUNT(*) >=2
order BY 1

--15
SELECT SUBSTRING(g.TERM_NO,1,4) AS '년도'
		,SUBSTRING(g.TERM_NO,5,6) AS '학기'
		,ROUND(AVG(POINT), 1) AS '평점'
FROM tb_grade g 
WHERE g.student_no = 'A112113'
GROUP BY SUBSTRING(g.TERM_NO,1,4),SUBSTRING(g.TERM_NO,5,6) WITH ROLLUP;

-- OPTION 실습(1~14)

USE lgcns

-- 1
SELECT *
FROM tb_student;

SELECT *
FROM tb_professor;

SELECT *
FROM tb_grade

SELECT *
FROM tb_department;

SELECT *
FROM tb_class

SELECT *
FROM tb_class_professor

SELECT s.student_name
		, s.student_address
FROM tb_student s
ORDER BY 1

-- 2

SELECT s.student_name
		, s.student_ssn
FROM tb_student s
WHERE s.absence_yn = 'y'
ORDER BY 2 DESC;

-- 3
SELECT  s.student_name AS '이름'
		, s.student_no AS '학번'
		, s.student_address AS '주소지'
FROM tb_student s
WHERE SUBSTRING(s.student_address,1,3) IN ('경기도','강원도') AND LEFT(student_no, 1) = '9'
ORDER BY 1

-- 4 
SELECT p.professor_name, p.professor_ssn
FROM tb_professor p
JOIN tb_department d ON(d.department_no = p.department_no)
WHERE d.DEPARTMENT_NAME = '법학과'
ORDER BY 2

-- 5
SELECT g.student_no, POINT, g.TERM_NO
FROM tb_grade g
WHERE g.TERM_NO = '200402' AND g.CLASS_NO = 'c3118100'
ORDER BY 2 DESC, 1

-- 6
SELECT s.student_no, s.student_name, d.department_name
FROM tb_student s
JOIN tb_department d ON(s.department_no = d.department_no)
ORDER BY 1,2,3

-- 7
SELECT c.class_name, d.department_name
FROM tb_class c
JOIN tb_department d ON(c.department_no = d.department_no)

-- 8
SELECT c.CLASS_NAME, p.PROFESSOR_NAME
FROM tb_class c
JOIN tb_class_professor cp ON(cp.class_no = c.class_no)
JOIN tb_professor p ON(cp.professor_no = p.professor_no)

-- 9
SELECT c.CLASS_NAME, p.PROFESSOR_NAME
FROM tb_class c
JOIN tb_class_professor cp ON(cp.class_no = c.class_no)
JOIN tb_professor p ON(cp.professor_no = p.professor_no)
JOIN tb_department d ON(d.department_no = p.department_no)
WHERE d.CATEGORY = '인문사회'

-- 10
SELECT s.STUDENT_NO, s.STUDENT_NAME, round(AVG(g.POINT),1)
FROM tb_student s
JOIN tb_grade g ON(g.student_no = s.student_no)
JOIN tb_department d ON(d.DEPARTMENT_NO = s.DEPARTMENT_NO)
WHERE d.DEPARTMENT_NAME = '음악학과'
GROUP BY s.STUDENT_NO

-- 11
SELECT d.DEPARTMENT_NAME, s.STUDENT_NAME, p.PROFESSOR_NAME
FROM tb_student s
JOIN tb_department d ON(d.DEPARTMENT_NO = s.DEPARTMENT_NO)
JOIN tb_professor p ON(s.COACH_PROFESSOR_NO = p.PROFESSOR_NO)
WHERE s.STUDENT_NO = 'A313047'

-- 12
SELECT s.STUDENT_NAME, g.TERM_NO
FROM tb_student s
JOIN tb_grade g ON(g.STUDENT_NO = s.STUDENT_NO)
JOIN tb_class c ON(g.CLASS_NO=c.CLASS_NO)
WHERE c.CLASS_NAME = '인간관계론' AND SUBSTRING(g.TERM_NO,1,4) = '2007'

-- 13
SELECT c.CLASS_NAME
       ,d.DEPARTMENT_NAME
FROM TB_CLASS c
JOIN TB_DEPARTMENT d ON (c.DEPARTMENT_NO = d.DEPARTMENT_NO)
LEFT JOIN TB_CLASS_PROFESSOR cp ON (c.CLASS_NO = cp.CLASS_NO)
WHERE d.CATEGORY = '예체능' AND cp.PROFESSOR_NO IS NULL;

-- 14
SELECT s.STUDENT_NAME AS '학생이름',
       IFNULL(p.PROFESSOR_NAME, '지도교수 미지정') AS '지도교수'
FROM TB_STUDENT s
JOIN TB_DEPARTMENT d ON (s.DEPARTMENT_NO = d.DEPARTMENT_NO)
LEFT JOIN TB_PROFESSOR p ON (s.COACH_PROFESSOR_NO = p.PROFESSOR_NO)
WHERE d.DEPARTMENT_NAME = '서반아어학과'
ORDER BY s.STUDENT_NO DESC;




