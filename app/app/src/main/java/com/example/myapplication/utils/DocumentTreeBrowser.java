package com.example.myapplication.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import com.example.myapplication.entity.DirectoryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DocumentTreeBrowser {
    private final ContentResolver contentResolver;
    private final Stack<Uri> pathStack = new Stack<>(); // 路径历史栈（用于返回上级）

    public DocumentTreeBrowser(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    /**
     * 获取当前层级的子目录列表
     *
     * @param treeUri 当前层级的Uri（如果是根目录，传用户选择的treeUri）
     */
    public List<DirectoryItem> listDirectories(Uri treeUri) {
        List<DirectoryItem> directories = new ArrayList<>();

        // 如果是第一次访问，将根目录压栈
        if (pathStack.isEmpty() && treeUri != null) {
            pathStack.push(treeUri);
        }

        // 获取当前目录的docId
        String currentDocId = DocumentsContract.getTreeDocumentId(treeUri);
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, currentDocId);

        try (Cursor cursor = contentResolver.query(
                childrenUri,
                new String[]{
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                },
                null, null, null)) {

            while (cursor != null && cursor.moveToNext()) {
                String docId = cursor.getString(0);
                String name = cursor.getString(1);
                String mimeType = cursor.getString(2);

                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType)) {
                    // 构建子目录的访问Uri
                    Uri childUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId);
//                    directories.add(new DirectoryItem(name, childUri, docId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return directories;
    }

    /**
     * 进入子目录（将当前Uri压栈，并返回子目录内容）
     */
    public List<DirectoryItem> enterDirectory(Uri childUri) {
        pathStack.push(childUri);
        return listDirectories(childUri);
    }

    /**
     * 返回上级目录
     */

    public List<DirectoryItem> goBack() {
        if (pathStack.size() > 1) {
            pathStack.pop(); // 移除当前目录
            return listDirectories(pathStack.peek()); // 返回上一级内容
        }
        return null; // 已经在根目录
    }

    /**
     * 获取当前目录 URI
     */
    public Uri getCurrentUri() {
        return pathStack.isEmpty() ? null : pathStack.peek();
    }
    /**
     * 获取当前路径栈（用于显示面包屑导航）
     */
    public Stack<Uri> getPathStack() {
        return pathStack;
    }
}