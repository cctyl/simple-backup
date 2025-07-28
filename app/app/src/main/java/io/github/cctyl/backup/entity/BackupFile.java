package io.github.cctyl.backup.entity;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "backup_file")
public class BackupFile {

    @PrimaryKey(autoGenerate = true)
    private Integer id;


    private String name;        //文件名


    @ColumnInfo(name = "tree_uri")
    private Uri treeUri;      // 用于访问该目录内容的Uri

    @ColumnInfo(name = "doc_id")
    private String docId;       // 当前目录的docId（用于构建子目录Uri）
    private long size;          //文件大小，byte
    @ColumnInfo(name = "last_modified")
    private long lastModified;  //最后修改时间，时间戳

    @ColumnInfo(name = "is_directory")
    private boolean isDirectory;//是否是目录

    @ColumnInfo(name = "root_doc_id")
    private String rootDocId;//是否是目录

    @ColumnInfo(name = "relative_path")
    private String relativePath;//是否是目录

    @ColumnInfo(name = "mime_type")
    private String mimeType;//是否是目录

    public String getRootDocId() {
        return rootDocId;
    }

    public void setRootDocId(String rootDocId) {
        this.rootDocId = rootDocId;
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public BackupFile() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Ignore
    public BackupFile(String name, Uri treeUri, String docId, long size, long lastModified, boolean isDirectory, String rootDocId, String relativePath, String mimeType) {
        this.name = name;
        this.treeUri = treeUri;
        this.docId = docId;
        this.size = size;
        this.lastModified = lastModified;
        this.isDirectory = isDirectory;
        this.rootDocId = rootDocId;
        this.relativePath = relativePath;
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getTreeUri() {
        return treeUri;
    }

    public void setTreeUri(Uri treeUri) {
        this.treeUri = treeUri;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }


}