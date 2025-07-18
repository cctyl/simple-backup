


use std::fmt::Debug;

use chrono::prelude::*;
use serde::{Deserialize, Serialize};



#[derive(Debug, Clone, Serialize, Deserialize, sqlx::FromRow,sqlx::Type)]
pub struct File{

    pub id:i64,
    pub name:String,
    pub doc_id:String,
    pub relative_path:String,
    pub is_directory:String,
    pub md5:String,
    

}
