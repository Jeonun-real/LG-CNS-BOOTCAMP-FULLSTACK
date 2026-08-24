CREATE TABLE COUNTRY
(
  COUNTRY_ID   CHAR(2),
  COUNTRY_NAME VARCHAR(30) NOT NULL,
  PRIMARY KEY(COUNTRY_ID)
);

CREATE TABLE LOCATION
(
  LOCATION_ID CHAR(2) ,
  COUNTRY_ID  CHAR(2),
  LOC_DESCRIBE VARCHAR(40),
  PRIMARY KEY(LOCATION_ID),
  FOREIGN KEY(COUNTRY_ID) REFERENCES COUNTRY(COUNTRY_ID)
);



CREATE TABLE DEPARTMENT
(
  DEPT_ID        CHAR(2) ,
  DEPT_NAME VARCHAR(30),
  LOC_ID           CHAR(2) NOT NULL,
  PRIMARY KEY(DEPT_ID),
  FOREIGN KEY(LOC_ID) REFERENCES LOCATION(LOCATION_ID)
);


CREATE TABLE JOB
(
  JOB_ID     CHAR(2) ,
  JOB_TITLE  VARCHAR(35),
  MIN_SAL  INT,
  MAX_SAL INT,
  PRIMARY KEY(JOB_ID)
);


CREATE TABLE SAL_GRADE
(
 SLEVEL  CHAR(1) PRIMARY KEY,
 LOWEST  INT,
 HIGHEST INT
);

CREATE TABLE EMPLOYEE
(EMP_ID           CHAR(3) PRIMARY KEY,
 EMP_NAME    VARCHAR(20)  NOT NULL, 
 EMP_NO          CHAR(14) NOT NULL, 
 EMAIL               VARCHAR(25),
 PHONE             VARCHAR(12),
 HIRE_DATE       DATE DEFAULT NOW(),
 JOB_ID             CHAR(2), 
 SALARY             INT,
 BONUS_PCT     FLOAT,
 MARRIAGE       CHAR(1) DEFAULT 'N',
 MGR_ID           CHAR(3) ,
 DEPT_ID          CHAR(2)      REFERENCES DEPARTMENT(DEPT_ID) ,
 FOREIGN KEY(JOB_ID) REFERENCES JOB(JOB_ID)  ,
 CHECK (MARRIAGE IN ('Y','N')),
 UNIQUE(EMP_NO)
);


INSERT into country values ('KO', '한국' );
INSERT into country values ('JP', '일본' );
INSERT into country values ('CH', '중국' );
INSERT into country values ('US', '미국' );
INSERT into country values ('ID', '인도' );

INSERT into location values ('A1', 'KO', '아시아지역1' );
INSERT into location values ('A2', 'JP', '아시아지역2' );
INSERT into location values ('A3', 'CH', '아시아지역3' );
INSERT into location values ('U1', 'US', '미주지역' );
INSERT into location values ('OT', 'ID', '기타지역' );


INSERT into job values ('J1', '대표이사', 200000000, 400000000 );
INSERT into job values ('J2', '부사장', 150000000, 300000000 );
INSERT into job values ('J3', '부장', 70000000, 100000000 );
INSERT into job values ('J4', '차장', 50000000, 80000000 );
INSERT into job values ('J5', '과장', 42000000, 60000000 );
INSERT into job values ('J6', '대리', 38000000, 56000000 );
INSERT into job values ('J7', '사원', 20000000, 40000000 );

INSERT into SAL_GRADE values ('A', 3000000, 9000000 );
INSERT into SAL_GRADE values ('B', 2500000, 2999999 );
INSERT into SAL_GRADE values ('C', 2000000, 2499999 );
INSERT into SAL_GRADE values ('D', 1500000, 1999999 );
INSERT into SAL_GRADE values ('E', 1000000, 1499999 );

INSERT into department values ('20', '회계팀', 'A1' );
INSERT into department values ('10', '본사 인사팀', 'A1' );
INSERT into department values ('50', '해외영업1팀', 'U1' );
INSERT into department values ('60', '기술지원팀', 'OT' );
INSERT into department values ('80', '해외영업2팀', 'A2' );
INSERT into department values ('90', '해외영업3팀', 'A3' );
INSERT into department values ('30', '마케팅팀', 'A1' );


INSERT INTO EMPLOYEE VALUES ('100', '한선기', '621133-1483658', 'sg_ahn@vcc.com', '0199949999', '90/04/01', 'J1', 9000000, 0.2, 'Y', '', '90' );
INSERT INTO EMPLOYEE VALUES ('101', '강중훈', '621136-1006405', 'jh_park@vcc.com', '0193334433', '04/04/30', 'J2', 5500000,null , 'Y', '100', '90' );
INSERT INTO EMPLOYEE VALUES ('102', '최만식', '861011-1940062', 'ms_choi@vcc.com', '0198879908', '95/12/30', 'J2', 3600000,null , 'Y', '101', '90' );
INSERT INTO EMPLOYEE VALUES ('103', '정도연', '631127-2519077', 'sy_kang@vcc.com', '0196654436', '97/06/03', 'J4', 2600000,null , 'Y', '104', '60' );
INSERT INTO EMPLOYEE VALUES ('104', '안석규', '651031-1962810', 'sg_han@vcc.com', '0192347654', '98/07/01', 'J3', 3500000, 0.25, 'Y', '100', '60' );
INSERT INTO EMPLOYEE VALUES ('107', '조재형', '721128-1732822', 'jh_jo@vcc.com', '0193325548', '98/11/23', 'J3', 3800000,null , 'Y', '104', '60' );
INSERT INTO EMPLOYEE VALUES ('124', '정지현', '641231-2269080', 'jih_jeon@vcc.com', '01922976129', '04/07/15', 'J7', 1500000,null , 'N', '141', '50' );
INSERT INTO EMPLOYEE VALUES ('141', '김예수', '651122-2592930', 'hs_kim@vcc.com', '0194087600', '01/03/20', 'J5', 2100000, 0.1, 'Y', '100', '50' );
INSERT INTO EMPLOYEE VALUES ('143', '나승원', '871024-1945881', 'sw_cha@vcc.com', '0197243979', '01/03/20', 'J5', 2300000,null , 'Y', '141', '50' );
INSERT INTO EMPLOYEE VALUES ('144', '김순이', '741122-2515789', 'sm_kim@vcc.com', '0192213306', '99/10/20', 'J3', 3400000, 0.1, 'Y', '141', '50' );
INSERT INTO EMPLOYEE VALUES ('149', '성해교', '640524-2148639', 'hg_song@vcc.com', '01992882295', '03/08/16', 'J7', 1900000,null , 'N', '141', '50' );
INSERT INTO EMPLOYEE VALUES ('174', '전우성', '821121-1660412', 'ws_jeong@vcc.com', '0193243388', '02/07/14', 'J6', 2090000,null , 'Y', '100', '80' );
INSERT INTO EMPLOYEE VALUES ('176', '엄정하', '791217-2230420', 'jh_um@vcc.com', '0194769665', '04/07/21', 'J6', 2420000, 0.2, 'Y', '174', '80' );
INSERT INTO EMPLOYEE VALUES ('178', '심하균', '611121-1673370', 'hk_shin@vcc.com', '0197122111', '04/09/30', NULL, 2300000, 0.3, 'Y', '', NULL );
INSERT INTO EMPLOYEE VALUES ('200', '고승우', '840217-1776881', 'sw_jo@vcc.com', '0193475512', '03/04/11', 'J7', 1500000,null , 'Y', '100', '10' );
INSERT INTO EMPLOYEE VALUES ('201', '박하일', '891225-1069101', 'hi_park@vcc.com', '01951564413', '04/11/10', 'J5', 2600000,null , 'Y', '', '50' );
INSERT INTO EMPLOYEE VALUES ('202', '권상후', '790331-1662986', 'sw_kwon@vcc.com', '0196640090', '01/05/20', 'J6', 3410000, 0.2, 'Y', '200', '10' );
INSERT INTO EMPLOYEE VALUES ('205', '임영애', '790833-2105839', 'jangum_lee@vcc.com', '0191132477', '00/01/31', 'J6', 2640000, 0.15, 'N', '200', '10' );
INSERT INTO EMPLOYEE VALUES ('206', '염정하', '860122-2785746', 'jh_yeum@vcc.com', '01997546623', '03/09/17', 'J7', 1500000,null , 'Y', '', NULL );
INSERT INTO EMPLOYEE VALUES ('207', '김술오', '640226-1358242', 'so_kim@vcc.com', '', '96/10/01', 'J4', 2500000,null , 'Y', '', '20' );
INSERT INTO EMPLOYEE VALUES ('208', '이중기', '790411-1452247', 'jk_lee@vcc.com', '', '04/10/01', 'J4', 2500000,null , 'Y', '', '20' );
INSERT INTO EMPLOYEE VALUES ('210', '감우섭', '700813-1766819', 'manofking@vcc.com', '', '05/07/31', 'J4', 2500000,null , 'Y', '', '20' );
COMMIT ;

-- ALTER TABLE employee
-- ADD CONSTRAINT FK_MGRID FOREIGN KEY (MGR_ID) REFERENCES EMPLOYEE (EMP_ID) ; 




COMMIT;




