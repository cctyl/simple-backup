-- Add up migration script here

CREATE TABLE users (
    id INTEGER PRIMARY KEY ,  
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    verified INTEGER NOT NULL DEFAULT 0,  
    password TEXT NOT NULL,
    verification_token boolean,
    token_expires_at text,  
    role TEXT NOT NULL DEFAULT 'user' CHECK (role IN ('admin', 'user')),  
    created_at text   ,  
    updated_at text  
);

-- 保留邮箱索引
CREATE INDEX users_email_idx ON users (email);