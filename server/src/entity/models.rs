


use std::fmt::Debug;

use chrono::prelude::*;
use serde::{Deserialize, Serialize};


#[derive(Debug, Clone, Serialize, Deserialize,Copy, PartialEq, sqlx::Type)]
#[sqlx(type_name = "user_role", rename_all = "lowercase")]
pub enum UserRole{
    Admin,
    User,
}

impl UserRole {
    pub fn to_str(&self)-> &str{
        match self {
            UserRole::Admin => "admin",
            UserRole::User => "user",
        }
    }
    
}

#[derive(Debug, Clone, Serialize, Deserialize, sqlx::FromRow,sqlx::Type)]
pub struct User{

    pub id:i64,
    pub name:String,
    pub email:String,
    pub password:String,
    pub role :UserRole,
    pub verified:bool,
    pub verification_token: Option<String>,
    pub token_expires_at: Option<DateTime<Utc>>,
    #[serde(rename = "createdAt")]
    pub created_at: Option<DateTime<Utc>>,
    #[serde(rename = "updatedAt")]
    pub updated_at: Option<DateTime<Utc>>,

}
