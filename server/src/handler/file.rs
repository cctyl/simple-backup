use axum::{Router, debug_handler, extract::Json};
use serde::{Deserialize, Serialize};

use crate::{
    app::response::{OkRespExt, R, RR},
    dao::file::FileDao,
};

pub fn create_router() -> Router {
    Router::new().route("/compare", axum::routing::post(compare))
    // .route("/upload", axum::routing::post(upload))
}
#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct FileDto {
    pub name: String,
    pub doc_id: String,
    pub relative_path: String,
    pub is_directory: String,
    pub md5: String,
    pub tree_uri: String,
}

#[debug_handler]
async fn compare(Json(params): Json<Vec<FileDto>>) -> RR<Vec<FileDto>> {

    println!("params: {:#?}",params);
    let collect: Vec<String> = params
        .iter()
        .map(|item: &FileDto| item.relative_path.clone())
        .collect();
    println!("collect: {:#?}",collect);
    let compare: Vec<String> = FileDao::select_by_relative_path_in(collect)
        .await?
        .into_iter()
        .map(|f| f.relative_path)
        .collect();
    println!("compare: {:#?}",compare);
    let result: Vec<FileDto> = params
        .into_iter()
        .filter(|item| !compare.contains(&item.relative_path))
        .collect();


    RR::success(result)
}
