-- Add up migration script here
CREATE TABLE file (
  "id" INTEGER NOT NULL,
  "name" TEXT NOT NULL,
  "doc_id" TEXT NOT NULL,
  "relative_path" TEXT NOT NULL,
  "is_directory" TEXT NOT NULL,
  "md5" TEXT NOT NULL,
  PRIMARY KEY ("id")
);