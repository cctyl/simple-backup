use crate::{
    app::{database::get_db, response::R},
    entity::models::File,
};

#[derive(Debug)]
pub struct FileDao;

impl FileDao {
    pub async fn select_by_md5_in(md5_list: Vec<String>) -> R<Vec<File>> {
        let join = md5_list.join(",");
        let db = get_db();
        let files = sqlx::query_as!(
            File,
            r#"
                select id, name, doc_id, relative_path, is_directory, md5
                from file
                where md5 in ($1)
            "#,
            join
        )
        .fetch_all(db)
        .await?;
        Ok(files)
    }


    pub async fn select_by_relative_path_in(relative_path_list: Vec<String>) -> R<Vec<File>> {
        let join = relative_path_list
        .into_iter()
        .map(|s|  format!("'{}'",s) )
        .collect::<Vec<String>>()
        .join(",");
        println!("relative_path_list: {}", join);
        let db = get_db();
        let files = sqlx::query_as!(
            File,
            r#"
                select id, name, doc_id, relative_path, is_directory, md5
                from file
                where relative_path in ($1)
            "#,
            join
        )
        .fetch_all(db)
        .await?;
        Ok(files)
    }
}
