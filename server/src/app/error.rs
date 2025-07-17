use std::fmt::Display;

use axum::{
    Json,
    http::StatusCode,
    response::{IntoResponse, Response},
};

use thiserror::Error;
use validator::ValidationErrors;

use crate::app::response::Resp;

#[derive(Debug, PartialEq)]
pub enum ErrorMessage {
    EmptyPassword,
    ExceededMaxPasswordLength(usize),
    HashingError,
    InvalidToken,
    ServerError,
    WrongCredentials,
    EmailExist,
    UserNoLongerExist,
    TokenNotProvided,
    PermissionDenied,
    UserNotAuthenticated,
    InvalidHashFormat,
}
impl ErrorMessage {
    fn to_str(&self) -> String {
        match self {
            ErrorMessage::EmptyPassword => "Password cannot be empty".to_string(),
            ErrorMessage::ExceededMaxPasswordLength(max_length) => {
                format!("Password exceeds maximum length of {}", max_length)
            }
            ErrorMessage::HashingError => "Error hashing password".to_string(),
            ErrorMessage::InvalidToken => "Invalid token".to_string(),
            ErrorMessage::ServerError => "Internal server error".to_string(),
            ErrorMessage::WrongCredentials => "Wrong email or password".to_string(),
            ErrorMessage::EmailExist => "Email already exists".to_string(),
            ErrorMessage::UserNoLongerExist => "User no longer exists".to_string(),
            ErrorMessage::TokenNotProvided => "Token not provided".to_string(),
            ErrorMessage::PermissionDenied => "Permission denied".to_string(),
            ErrorMessage::UserNotAuthenticated => "User not authenticated".to_string(),
            ErrorMessage::InvalidHashFormat => "Invalid hash format".to_string(),
        }
    }
}
impl ToString for ErrorMessage {
    fn to_string(&self) -> String {
        self.to_str().to_owned()
    }
}

#[derive(Debug, Error)]
pub enum HttpError {
    #[error("系统异常:{0}")]
    ServerError(String),

    #[error("请求错误:{0}")]
    BadRequest(String),

    //unique_constraint_violation
    #[error("唯一约束错误:{0}")]
    UniqueConstraintViolation(String),

    //unauthorized
    #[error("未授权:{0}")]
    Unauthorized(String),

    #[error("{0}")]
    Biz(String),

    #[error(transparent)]
    OtherError(#[from] anyhow::Error),
}


impl From<uuid::Error> for HttpError {
    fn from(e: uuid::Error) -> Self {
        HttpError::Unauthorized(e.to_string())
    }
}

impl From<sqlx::Error> for HttpError{
    fn from(e: sqlx::Error) -> Self {
    
        HttpError::ServerError(e.to_string())
    }
}

impl From<jsonwebtoken::errors::Error> for HttpError {
    fn from(e: jsonwebtoken::errors::Error) -> Self {
        HttpError::Unauthorized(ErrorMessage::InvalidToken.to_string())
    }
}
impl From<ValidationErrors> for HttpError {
    fn from(e: ValidationErrors) -> Self {
        HttpError::Unauthorized(ErrorMessage::InvalidToken.to_string())
    }
}




impl HttpError {
    pub fn status_code(&self) -> StatusCode {
        match self {
            HttpError::ServerError(_) | HttpError::OtherError(_) => StatusCode::INTERNAL_SERVER_ERROR,
            HttpError::BadRequest(_) => StatusCode::BAD_REQUEST,
            HttpError::UniqueConstraintViolation(_) => StatusCode::CONFLICT,
            HttpError::Unauthorized(_) => StatusCode::UNAUTHORIZED,
            HttpError::Biz(_) => StatusCode::OK,
            
        }
    }
}

impl IntoResponse for HttpError {
    fn into_response(self) -> Response {
        let status_code = self.status_code();

        let json = Json(Resp::<()> {
            status: status_code.as_u16(),
            message: self.to_string(),
            data: None,
        });

        (status_code, json).into_response()
    }
}
