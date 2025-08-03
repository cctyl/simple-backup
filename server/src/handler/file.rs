use std::any::Any;

use crate::utils::id;
use crate::{
    app::{
        database::CONTEXT,
        error::HttpError,
        response::{FailRespExt, OkRespExt, R, RR},
    },
    dao::file::FileDao,
};
use axum::body::Bytes;
use axum::{
    Router, debug_handler,
    extract::{Json, Multipart},
};
use log::{error, info};
use rbatis::plugin::object_id::ObjectId;
use rbs::value;
use serde::{Deserialize, Serialize};
use tokio::fs::File;
use tokio::io::AsyncWriteExt;
pub fn create_router() -> Router {
    Router::new()
        .route("/compare", axum::routing::post(compare))
        .route("/upload", axum::routing::post(upload))
        .route("/test", axum::routing::get(test))
}
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct FileDto {
    pub relative_path: String,
    pub md5: String,
    pub id: i64,
    pub is_directory: bool,
}

#[debug_handler]
async fn compare(Json(params): Json<Vec<FileDto>>) -> RR<Vec<FileDto>> {
    let collect: Vec<String> = params
        .iter()
        .map(|item: &FileDto| item.relative_path.clone())
        .collect();

    let db_list = FileDao::select_by_relative_path_in(collect).await?;

    let result: Vec<FileDto> = params
        .into_iter()
        .filter(|item| {
            //如果这个文件不存在，那自然是需要上传
            let find = db_list
                .iter()
                .find(|i| i.relative_path == item.relative_path);

            match find {
                Some(i) => {
                    //如果已存在，则判定md5是否相同
                    //不同，则需要更新

                    i.md5 != item.md5
                }
                None => true,
            }
        })
        .collect();

    RR::success(result)
}

#[debug_handler]
async fn upload(mut multipart: Multipart) -> RR<()> {
    let mut name = None;
    let mut tree_uri = None;
    let mut doc_id = None;
    let mut md5 = None;
    let mut relative_path = None;
    let mut is_directory = false;
    let mut check_md5 = false;

    while let Some(field) = multipart.next_field().await.unwrap() {
        let name_str = field.name().unwrap_or("").to_string();
        let data = field.bytes().await;

        let data = match data {
            Ok(i) => i,
            Err(e) => {
                error!("{:#?}", e);

                return RR::fail(HttpError::ServerError(e.to_string()));
            }
        };

        match name_str.as_str() {
            "name" => name = Some(String::from_utf8_lossy(&data).to_string()),
            "treeUri" => tree_uri = Some(String::from_utf8_lossy(&data).to_string()),
            "docId" => doc_id = Some(String::from_utf8_lossy(&data).to_string()),
            "md5" => md5 = Some(String::from_utf8_lossy(&data).to_string()),
            "relativePath" => relative_path = Some(String::from_utf8_lossy(&data).to_string()),
            "checkMd5" => {
                check_md5 = String::from_utf8_lossy(&data)
                    .to_string()
                    .parse::<bool>()
                    .unwrap_or(false);
            }
            "isDirectory" => {
                is_directory = String::from_utf8_lossy(&data)
                    .to_string()
                    .parse::<bool>()
                    .unwrap_or(false)
            }
            "file" => {
                let mut path = relative_path.clone().ok_or(HttpError::BadRequest(
                    "必须提供releactivePath！".to_string(),
                ))?;
                if path == "/" {
                    return RR::success(());
                }

                if path.starts_with("/") {
                    path = path[1..].to_string();
                }

                let path = format!("./upload/{}", path);

                info!("path={path}");
                let path = std::path::Path::new(&path);
                if let Some(parent) = path.parent() {
                    info!("父目录不存在，创建中");
                    // 递归创建所有缺失的父目录
                    tokio::fs::create_dir_all(parent).await?;
                }

                if !check_md5 {
                    info!("无需校验md5");
                    let write = tokio::fs::write(path, &data).await?;
                } else {
                    //再次校验md5
                    let recheck_md5 = calculate_md5(&data);

                    info!("md5={md5:?},recheck_md5={recheck_md5} ");

                    //比较前后md5是否相同
                    if md5.clone().unwrap() == recheck_md5 {
                        info!("md5校验通过");
                        let write = tokio::fs::write(path, &data).await?;
                    } else {
                        error!("md5校验失败，删除已上传的文件");

                        return RR::fail(HttpError::Custom(
                            700,
                            "md5校验失败，请重新上传！".to_string(),
                        ));
                    }
                }
            }
            _ => {}
        }
    }

    //删除relative_path相同的文件
    let data = crate::entity::models::File::delete_by_map(
        &CONTEXT.rb,
        value! {
            "relative_path":relative_path.clone().unwrap()
        },
    )
    .await?;

    info!("del: {data}");

    //保存新的
    let file = crate::entity::models::File {
        id: id::next_id(),
        name: name.unwrap(),
        doc_id: doc_id.unwrap(),
        relative_path: relative_path.unwrap(),
        is_directory,
        md5: md5.unwrap(),
    };

    crate::entity::models::File::insert(&CONTEXT.rb, &file).await?;

    RR::success(())
}

fn calculate_md5(data: &[u8]) -> String {
    let digest = md5::compute(data);
    format!("{:x}", digest)
}

#[debug_handler]
async fn test() -> RR<String> {
    RR::success("服务端连接成功！".to_string())
}

#[test]
fn test_qr_code() {
    use qrcode::QrCode;
    use qrcode::render::unicode;
  let code = QrCode::new(br#"{
"addr":"192.168.31.151:8082",
"secret":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9s"
}"#).unwrap();
    let image = code.render::<unicode::Dense1x2>()
        .dark_color(unicode::Dense1x2::Light)
        .light_color(unicode::Dense1x2::Dark)
        .build();
    println!("{}", image);
}
