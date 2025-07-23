package io.github.cctyl.backup.entity;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectoryItem {
    private String name;        //文件名
    private Uri treeUri;      // 用于访问该目录内容的Uri
    private String docId;       // 当前目录的docId（用于构建子目录Uri）
    private long size;          //文件大小，byte
    private long lastModified;  //最后修改时间，时间戳
    private boolean isDirectory;//是否是目录
    private String rootDocId;//是否是目录
    private String relativePath;//是否是目录
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

    public DirectoryItem(String name, Uri treeUri, String docId, long size, long lastModified, boolean isDirectory, String rootDocId, String relativePath, String mimeType) {
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

    //    public static JSONObject toJson(DirectoryItem item) {
//        JSONObject parentObj = new JSONObject();
//        try {
//            parentObj.put("name", item.getName());
//            parentObj.put("docId", item.getDocId());
//            parentObj.put("treeUri", item.getTreeUri().toString());
//            parentObj.put("size", item.getSize());
//            parentObj.put("lastModified", item.getLastModified());
//            parentObj.put("isDirectory", item.isDirectory());
//            parentObj.put("rootDocId", item.getRootDocId());
//            return parentObj;
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static DirectoryItem fromJson(JSONObject jsonObject) {
//        try {
//            String name = jsonObject.getString("name");
//            String docId = jsonObject.getString("docId");
//            Uri treeUri = Uri.parse(jsonObject.getString("treeUri"));
//            long size = jsonObject.getLong("size");
//            long lastModified = jsonObject.getLong("lastModified");
//            boolean isDirectory = jsonObject.getBoolean("isDirectory");
//            String rootDocId = jsonObject.getString("rootDocId");
//            return new DirectoryItem(name, treeUri, docId, size, lastModified, isDirectory,rootDocId);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
}