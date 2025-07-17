use std::sync::{LazyLock, OnceLock};

use sqlx::{SqlitePool, sqlite::SqlitePoolOptions};
use tokio::runtime::Runtime;

static POOL: OnceLock<sqlx::Pool<sqlx::Sqlite>> = OnceLock::new();

pub fn get_db() -> &'static sqlx::Pool<sqlx::Sqlite> {
    POOL.get_or_init(|| {
        let database_url = std::env::var("DATABASE_URL").expect("数据库链接不能为空");
        let rt = tokio::runtime::Builder::new_current_thread()
            .enable_all()
            .build()
            .unwrap();
        rt.block_on(async {
            match SqlitePoolOptions::new()
                .max_connections(10)
                .connect(&database_url)
                .await
            {
                Ok(pool) => {
                    println!("数据库连接成功");
                    pool
                }
                Err(err) => {
                    eprintln!("数据库连接失败: {}", err);
                    std::process::exit(1);
                }
            }
        })
    })
}
