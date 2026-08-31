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

-- 15
SELECT s.STUDENT_NO,
       s.STUDENT_NAME,
       d.DEPARTMENT_NAME,
       g.AVG_POINT
FROM tb_student s
JOIN tb_department d
  ON s.DEPARTMENT_NO = d.DEPARTMENT_NO
JOIN (
    SELECT STUDENT_NO,
           AVG(POINT) AS AVG_POINT
    FROM tb_grade
    GROUP BY STUDENT_NO
    HAVING AVG(POINT) > 4.0
) g
  ON s.STUDENT_NO = g.STUDENT_NO;

-- 16
SELECT c.CLASS_NAME AS '과목명',
       ROUND(AVG(g.POINT), 1) AS '평점'
FROM TB_CLASS c
JOIN TB_DEPARTMENT d
    ON c.DEPARTMENT_NO = d.DEPARTMENT_NO
JOIN TB_GRADE g
    ON c.CLASS_NO = g.CLASS_NO
WHERE d.DEPARTMENT_NAME = '환경조경학과'
GROUP BY c.CLASS_NO, c.CLASS_NAME;
-- 17
SELECT s.STUDENT_NAME,
       s.STUDENT_ADDRESS
FROM TB_STUDENT s
WHERE s.DEPARTMENT_NO = (
    SELECT DEPARTMENT_NO
    FROM TB_STUDENT
    WHERE STUDENT_NAME = '최경희'
);


-- 18
SELECT s.STUDENT_NAME,
       s.STUDENT_NO
FROM TB_STUDENT s
JOIN TB_DEPARTMENT d
    ON s.DEPARTMENT_NO = d.DEPARTMENT_NO
JOIN TB_GRADE g
    ON s.STUDENT_NO = g.STUDENT_NO
WHERE d.DEPARTMENT_NAME = '국어국문학과'
GROUP BY s.STUDENT_NO, s.STUDENT_NAME
ORDER BY AVG(g.POINT) DESC
LIMIT 1;

-- 19
SELECT d.DEPARTMENT_NAME AS '계열 학과명',
       ROUND(AVG(g.POINT), 1) AS '전공평점'
FROM TB_DEPARTMENT d
JOIN TB_CLASS c
    ON d.DEPARTMENT_NO = c.DEPARTMENT_NO
JOIN TB_GRADE g
    ON c.CLASS_NO = g.CLASS_NO
WHERE d.CATEGORY = (
    SELECT CATEGORY
    FROM TB_DEPARTMENT
    WHERE DEPARTMENT_NAME = '환경조경학과'
)
GROUP BY d.DEPARTMENT_NO, d.DEPARTMENT_NAME;

-- ddl
USE lgcns

-- 1
CREATE TABLE TB_CATEGORY(
	NAME VARCHAR(10),
	USE_YN CHAR(1) DEFAULT 'Y'
)

-- 2
CREATE TABLE TB_CLASS_TYPE(
	NO VARCHAR(5) PRIMARY KEY,
	NAME VARCHAR(10)
)

-- 3
ALTER TABLE TB_CATEGORY
ADD CONSTRAINT PRIMARY KEY(NAME)

-- 4
ALTER TABLE TB_CLASS_TYPE
MODIFY NAME VARCHAR(10) NOT NULL

--5
ALTER TABLE TB_CATEGORY
MODIFY NAME VARCHAR(20);

ALTER TABLE TB_CLASS_TYPE
MODIFY no VARCHAR(10) 

--6
ALTER TABLE TB_CATEGORY
RENAME COLUMN NAME TO CATEGORY_NAME;

ALTER TABLE TB_CLASS_TYPE
RENAME COLUMN no TO CLASS_TYPE_NO

--7
ALTER TABLE TB_CATEGORY
DROP CONSTRAINT PRIMARY KEY,
ADD CONSTRAINT PK_CATEGORY_NAME PRIMARY KEY (CATEGORY_NAME);

ALTER TABLE TB_CLASS_TYPE
DROP CONSTRAINT PRIMARY KEY,
ADD CONSTRAINT PK_CLASS_TYPE_NO PRIMARY KEY (CLASS_TYPE_NO);

-- 8
INSERT INTO tb_category
VALUES
('공학','Y'),
('자연과학','Y'),
('의학','Y'),
('예체능','Y'),
('인문사회','Y');

-- 9
ALTER TABLE TB_DEPARTMENT
ADD CONSTRAINT FK_TB_DEPARTMENT_CATEGORY
FOREIGN KEY(category) 
REFERENCES tb_category(category_name) ;

-- 10
CREATE OR REPLACE VIEW
    VW_학생일반정보(학번, 학생이름, 주소)
AS SELECT
    S.STUDENT_NO
    , S.STUDENT_NAME
    , S.STUDENT_ADDRESS
FROM tb_student S;

-- 11
CREATE OR REPLACE VIEW VW_지도면담(학생이름, 학과이름, 지도교수이름)
AS
SELECT S.STUDENT_NAME, D.DEPARTMENT_NAME, P.PROFESSOR_NAME
FROM tb_student S
JOIN tb_department D ON(S.DEPARTMENT_NO = D.DEPARTMENT_NO)
LEFT JOIN tb_professor P ON(S.COACH_PROFESSOR_NO = P.PROFESSOR_NO)
ORDER BY D.DEPARTMENT_NAME;

-- 12
CREATE OR REPLACE VIEW VW_학과별학생수(DEPARTMENT_NAME, STUDENT_COUNT)
AS
SELECT D.DEPARTMENT_NAME, COUNT(S.DEPARTMENT_NO)
FROM tb_student S
JOIN tb_department D ON(S.DEPARTMENT_NO = D.DEPARTMENT_NO)
GROUP BY D.DEPARTMENT_NO

-- 13 (단일뷰는 업데니트가 가능한데, join된 뷰는 업데이트가 불가)
UPDATE VW_학생일반정보
SET 학생이름 = '서정운'
WHERE 학번 = 'A213046'

-- 14 (CHECK 옵션을 통해 변경사항을 막을수 있음)
CREATE OR REPLACE VIEW
    VW_학생일반정보(학번, 학생이름, 주소)
AS SELECT
    S.STUDENT_NO
    , S.STUDENT_NAME
    , S.STUDENT_ADDRESS
FROM tb_student S
WHERE ~~~~~ 
WITH CHECK OPTION;

-- 15
SELECT C.CLASS_NO   AS 과목번호,
       C.CLASS_NAME AS 과목이름,
       COUNT(*)      AS `누적수강생수(명)`
FROM TB_CLASS C
JOIN tb_grade G ON (C.CLASS_NO = G.CLASS_NO)
WHERE SUBSTRING(G.TERM_NO, 1, 4) BETWEEN '2007' AND '2009'
GROUP BY C.CLASS_NO,C.CLASS_NAME
ORDER BY COUNT(*) DESC, C.CLASS_NO
LIMIT 3;







SHOW CREATE TABLE TB_CATEGORY;
SHOW CREATE TABLE TB_CLASS_TYPE;
SELECT * FROM TB_CATEGORY;
SELECT * FROM tb_class_type;
SELECT * FROM tb_department;
SELECT * FROM tb_student;


-- 15 2009년 기준 3년


-- 쇼핑몰 ERD
USE sqldb

DROP TABLE product
DROP TABLE customer

-- 1
CREATE TABLE PRODUCT (
	pno INT PRIMARY KEY
	,pname VARCHAR(20) NOT NULL
	,cost INT NOT NULL DEFAULT 0
	,stock INT NOT NULL DEFAULT 0
)


CREATE TABLE customers (
	cno INT PRIMARY key
	,cname VARCHAR(10) NOT null
	,address VARCHAR(50) NOT null
	,email VARCHAR(20) NOT null
	,phone VARCHAR(20) NOT null
)

CREATE TABLE ORDERS(
	orderno INT PRIMARY KEY
	,orderdate DATE NOT NULL DEFAULT SYSDATE()
	,address VARCHAR(50) NOT NULL 
	,phone VARCHAR(20) NOT NULL 
	,STATUS VARCHAR(20) NOT NULL CHECK(status IN ('결제완료','배송중','배송완료'))
	,cno INT NOT NULL
	,FOREIGN KEY(cno) REFERENCES customers(cno) 
)

CREATE TABLE ORDERDETAIL(
	orderno INT 
	,pno INT 
	,qty INT DEFAULT 0
	,cost INT DEFAULT 0
	,PRIMARY KEY(orderno,pno)
	,FOREIGN KEY(orderno) REFERENCES orders(orderno)
	,FOREIGN KEY(pno) REFERENCES PRODUCT(pno)
)

-- 2
INSERT INTO product (pno, pname, cost, stock)
VALUES
	(1001, '삼양라면', 1000, 200)
	,(1002, '새우깡', 1500, 500)
	,(1003, '월드콘', 2000, 350)
	,(1004, '빼빼로', 2000, 700)
	,(1005, '코카콜라', 1800, 550)
	,(1006, '환타', 1600, 300)


-- 3
INSERT INTO customers (cno, cname, address, email, phone)
VALUES
	(101, '김철수', '서울 강남구', 'cskim@naver.com', '899-6666')
	,(102, '이영희', '부산 서면', 'yhlee@empal.com', '355-8882')
	,(103, '최진국', '제주 동광양', 'jkchoi@gmail.com', '852-5764')
	,(104, '강준호', '강릉 홍제동', 'jhkang@hanmail.com', '559-7777')
	,(105, '민병국', '대전 전민동', 'bgmin@hotmail.com', '559-8741')
	,(106, '오민수', '광주 북구', 'msoh@microsoft.com', '542-9988');

-- 4
INSERT INTO orders(orderno, orderdate, address, phone, status, cno)
VALUES
(
	1
	,SYSDATE()-INTERVAL 3 DAY
	,'서울 강남구'
	,'899-6666'
	,'결제완료'
	,(SELECT cno FROM customers WHERE cname = '김철수')
)



INSERT INTO ORDERDETAIL(orderno, pno, qty, cost)
VALUES
(
	1
	,(SELECT pno FROM product WHERE pname = '삼양라면')
	,'50'
	,'1000'
)

-- 5
UPDATE product 
SET stock = 150
WHERE pno = 1001

-- 6
INSERT into orders(orderno, orderdate, address, phone, status, cno)
VALUE(
	2,
	SYSDATE() - INTERVAL 2 DAY,
	'부산 수영구',
	'337-5000',
	'결제완료',
	(SELECT cno
	FROM customers
	WHERE cname = '이영희')
);

INSERT into orderdetail(orderno, pno, qty, cost)
VALUE(
	2,
	(SELECT pno
	FROM product
	WHERE pname = '새우깡'),
	100,
	1500
),
(
	2,
	(SELECT pno
	FROM product
	WHERE pname = '월드콘'),
	150,
	2000
);

-- 7
UPDATE product 
SET stock = 400
WHERE pno = 1002;

UPDATE product 
SET stock = 200
WHERE pno = 1003

--8
INSERT INTO orders
    (orderno, orderdate, address, phone, status, cno)
VALUES
(
    3,
    SYSDATE() - INTERVAL 1 DAY,
    '광주 북구',
    '652-2277',
    '결제완료',
    (SELECT cno
     FROM customers
     WHERE cname = '오민수')
);

INSERT INTO orderdetail
    (orderno, pno, qty, cost)
VALUES
(
    3,
    (SELECT pno
     FROM product
     WHERE pname = '빼빼로'),
    100,
    2000
),
(
    3,
    (SELECT pno
     FROM product
     WHERE pname = '코카콜라'),
    50,
    1800
);

-- 9
UPDATE product 
SET stock = 600
WHERE pno = 1004;

UPDATE product 
SET stock = 500
WHERE pno = 1005

-- 10
SELECT o.orderdate,
       c.cname,
       o.address,
       o.phone,
       o.status,
       p.pname,
       od.cost,
       od.qty,
       od.cost * od.qty AS 'cost*qty'
FROM orders o
JOIN customers c
    ON o.cno = c.cno
JOIN orderdetail od
    ON o.orderno = od.orderno
JOIN product p
    ON od.pno = p.pno
ORDER BY o.orderno, p.pno;

-- 11
SELECT o.orderdate,
       SUM(od.cost * od.qty) AS 'sum(cost*qty)'
FROM orders o
JOIN orderdetail od
    ON o.orderno = od.orderno
GROUP BY o.orderdate
ORDER BY o.orderdate;

--12
INSERT INTO product (pno, pname, cost, stock)
VALUES (1007, '목캔디', 3000, 500);

--13
INSERT INTO orders
    (orderno, orderdate, address, phone, status, cno)
VALUES
(
    4,
    SYSDATE(),
    '제주 동광양',
    '352-4657',
    '결제완료',
    (SELECT cno
     FROM customers
     WHERE cname = '최진국')
);

INSERT INTO orderdetail
    (orderno, pno, qty, cost)
VALUES
(
    4,
    (SELECT pno
     FROM product
     WHERE pname = '목캔디'),
    200,
    3000
);

UPDATE product
SET stock = stock - 200
WHERE pno = 1007;

SELECT o.orderdate,
       c.cname,
       o.address,
       o.phone,
       o.status,
       p.pname,
       od.cost,
       od.qty,
       od.cost * od.qty AS 'cost*qty'
FROM orders o
JOIN customers c
    ON o.cno = c.cno
JOIN orderdetail od
    ON o.orderno = od.orderno
JOIN product p
    ON od.pno = p.pno
ORDER BY o.orderno, p.pno;

desc PRODUCT;
DESC customers;
DESC orders;
DESC ORDERDETAIL;

SELECT * FROM PRODUCT;
SELECT * FROM customers;
SELECT * FROM orders;
SELECT * FROM ORDERDETAIL;







