package io.github.cctyl.backup.entity;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "select_dir")
public class SelectDir {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;        //文件名
    private Uri treeUri;      // 用于访问该目录内容的Uri
    private String docId;       // 当前目录的docId（用于构建子目录Uri）

    private String rootDocId;//根目录的docId
    private String relativePath;//相对路径

    @ColumnInfo(name = "last_modified")
    private long lastModified;  //最后修改时间，时间戳

    @ColumnInfo(name = "is_directory")
    private boolean isDirectory;//是否是目录


    @ColumnInfo(name = "mime_type")
    private String mimeType;//文件类型


    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public SelectDir() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRootDocId() {
        return rootDocId;
    }

    public void setRootDocId(String rootDocId) {
        this.rootDocId = rootDocId;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }


    @Override
    @Ignore
    public String toString() {
        return "SelectDir{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", treeUri=" + treeUri +
                ", docId='" + docId + '\'' +
                ", rootDocId='" + rootDocId + '\'' +
                ", relativePath='" + relativePath + '\'' +
                '}';
    }




}