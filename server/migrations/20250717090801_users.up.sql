-- Add up migration script here

CREATE TABLE users (
    id INTEGER PRIMARY KEY ,  -- 自增整数主键
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    verified INTEGER NOT NULL DEFAULT 0,  -- SQLite 用 0/1 表示布尔值
    password TEXT NOT NULL,
    verification_token TEXT,
    token_expires_at INTEGER,  -- 存储 ISO8601 格式时间字符串
    role TEXT NOT NULL DEFAULT 'user' CHECK (role IN ('admin', 'user')),  -- 用 CHECK 约束替代 ENUM
    created_at INTEGER DEFAULT (datetime('now')),  -- 当前时间
    updated_at INTEGER DEFAULT (datetime('now'))
);

-- 保留邮箱索引
CREATE INDEX users_email_idx ON users (email);