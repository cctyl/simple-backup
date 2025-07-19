use std::any::Any;

use crate::{
    app::{
        database::CONTEXT, error::HttpError, response::{FailRespExt, OkRespExt, R, RR}
    },
    dao::file::FileDao,
};
use axum::{
    debug_handler, extract::{Json, Multipart}, Router
};
use  rbatis::plugin::object_id::ObjectId;
use log::{error, info};
use serde::{Deserialize, Serialize};
use tokio::fs::File;
use tokio::io::AsyncWriteExt;
use crate::utils::id;
pub fn create_router() -> Router {
    Router::new()
        .route("/compare", axum::routing::post(compare))
        .route("/upload", axum::routing::post(upload))
}
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct FileDto {
    pub relative_path: String,
    pub md5: String,
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
            "releactivePath" => relative_path = Some(String::from_utf8_lossy(&data).to_string()),
            "isDirectory" => {
                is_directory = String::from_utf8_lossy(&data)
                    .to_string()
                    .parse::<bool>()
                    .unwrap_or(false)
            }
            "file" => {
                 let path = relative_path.clone().ok_or(HttpError::BadRequest("必须提供releactivePath！".to_string()))?;
                    info!("path={path}");
                let path = std::path::Path::new(&path);
                if let Some(parent) = path.parent() {
                    info!("父目录不存在，创建中");
                    // 递归创建所有缺失的父目录
                    tokio::fs::create_dir_all(parent).await?; 
                }
               
             
                let write = tokio::fs::write(path, &data).await?;
            }
            _ => {}
        }
    }



    let file = crate::entity::models::File{
        id: id::next_id(),
        name:name.unwrap(),
        doc_id:doc_id.unwrap(),
        relative_path:relative_path.unwrap(),
        is_directory,
        md5:md5.unwrap()


    };

    crate::entity::models::File::insert(&CONTEXT.rb, &file).await?;

    RR::success(())
}
