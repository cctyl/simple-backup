#[derive(Debug, Clone)]
pub struct Config {
    pub secret: String,
    pub port: u16,
}

impl Config {
    pub fn init() -> Config {
        let port = std::env::var("PORT").map_or(8080, |s| s.parse::<u16>().unwrap());
        let secret = std::env::var("SECRET").expect("必须设置密钥");

        Config {
            secret,
            port,
        }
    }
}
