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

