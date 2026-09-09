CREATE TABLE flyway_comments_tbl (
    comment_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    blog_id     BIGINT          NOT NULL,
    email       VARCHAR(50)     NOT NULL,
    comment     VARCHAR(1000)   NOT NULL,
    -- created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_blog
        FOREIGN KEY (blog_id) REFERENCES flyway_blogs_tbl(blog_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
        FOREIGN KEY (email) REFERENCES flyway_users_tbl(email)
        ON DELETE CASCADE
);