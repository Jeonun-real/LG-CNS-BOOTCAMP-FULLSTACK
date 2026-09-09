CREATE TABLE flyway_blogs_tbl (
    blog_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    category	VARCHAR(50)  DEFAULT '전체' CHECK( CATEGORY IN ('전체', '개발', '생활', '취미', '일상')),
    email       VARCHAR(100) NOT NULL,

    -- created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT  
        FOREIGN KEY (email) REFERENCES flyway_users_tbl(email) 
        -- ON DELETE CASCADE
);




