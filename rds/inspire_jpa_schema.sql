-- inspire_jpa 테이블 전체 재생성 스크립트
-- 주의: 기존 SPRING_JPA_* 테이블의 데이터가 모두 삭제됩니다.

USE lgcns;

-- 외래키를 참조하는 자식 테이블부터 삭제
DROP TABLE IF EXISTS SPRING_JPA_COMMENT_TBL;
DROP TABLE IF EXISTS SPRING_JPA_BLOG_TBL;
DROP TABLE IF EXISTS SPRING_JPA_USER_TBL;

-- 1. 사용자 테이블
CREATE TABLE SPRING_JPA_USER_TBL (
    email       VARCHAR(50)  NOT NULL,
    password    VARCHAR(255),
    name        VARCHAR(255),
    role        VARCHAR(255),

    CONSTRAINT pk_spring_jpa_user
        PRIMARY KEY (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 게시글 테이블
CREATE TABLE SPRING_JPA_BLOG_TBL (
    blog_id     INT          NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255),
    content     VARCHAR(255),
    category    VARCHAR(20)  DEFAULT '전체',
    email       VARCHAR(50)  NOT NULL,

    CONSTRAINT pk_spring_jpa_blog
        PRIMARY KEY (blog_id),
    CONSTRAINT chk_spring_jpa_blog_category
        CHECK (category IN ('전체', '개발', '생활', '취미', '일상')),
    CONSTRAINT fk_spring_jpa_blog_user
        FOREIGN KEY (email)
        REFERENCES SPRING_JPA_USER_TBL (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 댓글 테이블
CREATE TABLE SPRING_JPA_COMMENT_TBL (
    comment_id  INT          NOT NULL AUTO_INCREMENT,
    comment     VARCHAR(255),
    email       VARCHAR(50),
    blog_id     INT          NOT NULL,

    CONSTRAINT pk_spring_jpa_comment
        PRIMARY KEY (comment_id),
    CONSTRAINT fk_spring_jpa_comment_blog
        FOREIGN KEY (blog_id)
        REFERENCES SPRING_JPA_BLOG_TBL (blog_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_spring_jpa_comment_user
        FOREIGN KEY (email)
        REFERENCES SPRING_JPA_USER_TBL (email)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 생성 결과 확인
SHOW COLUMNS FROM SPRING_JPA_USER_TBL;
SHOW COLUMNS FROM SPRING_JPA_BLOG_TBL;
SHOW COLUMNS FROM SPRING_JPA_COMMENT_TBL;
