CREATE TABLE crossword (
    id      UUID PRIMARY KEY,
    date    DATE NOT NULL,
    payload TEXT,
    CONSTRAINT uc_crossword_date UNIQUE (date)
);