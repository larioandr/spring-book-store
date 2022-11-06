CREATE TABLE reader
(
    id                      IDENTITY     PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    last_book_taken_date    DATE NULL
);

CREATE TABLE book (
    id                      IDENTITY     PRIMARY KEY,
    title                   VARCHAR(255) NOT NULL,
    publish_date            DATE         NOT NULL,
    author                  VARCHAR(255) NOT NULL,
    reader_id               BIGINT       NULL,
    reader_updated_date     DATE         NULL,

    FOREIGN KEY (reader_id)
        REFERENCES reader (id)
        ON DELETE SET NULL
);
