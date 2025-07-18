#![cfg_attr(
    debug_assertions,
    allow(dead_code, unused_imports, unused_variables, unused_mut)
)]

mod app;
mod dao;

mod entity;
mod handler;
mod utils;

use crate::app::database::{self, get_db};
use app::config::Config;
use axum::{Extension, Router, extract::DefaultBodyLimit};
use bytesize::ByteSize;
use dotenv::dotenv;
use std::{sync::Arc, time::Duration};
use tokio::{net::TcpListener, runtime::Runtime};
use tower_http::{
    cors::{self, CorsLayer},
};


#[derive(Debug)]
pub struct AppState {
    pub env: Config,
}

fn main() {
    let config = init_config();
    get_db();
    let port = config.port;
    println!(
        "{}",
        format!("🚀 Server is running on http://localhost:{}", port)
    );

    //由于数据库需要异步初始化（里面用了一个异步运行时）
    //所以main函数不能使用#[tokio::main]
    //不然会嵌套运行时
    //因此axum也在一个单独的运行时里面
    tokio::runtime::Builder::new_multi_thread()
        .enable_all()
        .build()
        .unwrap()
        .block_on(async {
            let listener = tokio::net::TcpListener::bind(format!("0.0.0.0:{}", port))
                .await
                .unwrap();
            let app = build_router(config);
            axum::serve(listener, app).await.unwrap();
        });
}

fn init_config() -> Config {
    //从.env 加载环境变量，然后可以被std::env::var读取
    dotenv().ok();
    Config::init()
}

fn build_router(config: Config) -> Router {

    let cors = CorsLayer::new()
        .allow_origin(cors::Any)
        .allow_methods(cors::Any)
        .allow_headers(cors::Any)
        .allow_credentials(false)
        .max_age(Duration::from_secs(3600 * 12));
    let body_limit = DefaultBodyLimit::max(ByteSize::mib(2048).as_u64() as usize);

    let app_state = AppState { env: config };

    handler::create_router()
        .layer(cors)
        .layer(TraceLayer::new_for_http())
        .layer(Extension(Arc::new(app_state)))


}
