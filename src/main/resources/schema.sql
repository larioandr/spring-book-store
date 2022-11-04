CREATE TABLE reader
(
    id          BIGINT       PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    surname     VARCHAR(255) NOT NULL,
    last_book_taken_date     TIMESTAMP
);

CREATE TABLE book (
    id           BIGINT       PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    publish_date datetime     NOT NULL,
    author       VARCHAR(255) NOT NULL,
    reader_id    BIGINT       NULL,

    FOREIGN KEY (reader_id)
        REFERENCES reader (id)
        ON DELETE SET NULL
);
