package com.example.myapplication.utils;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;

public class PortFinder {

    private static final String TAG = "PortFinder";

    public static int findAvailablePort(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.close();
                return port;
            } catch (IOException e) {
                // 端口被占用，尝试下一个
            }
        }

        // 如果没有可用端口，返回0并记录错误
        Log.e(TAG, "No available ports between " + startPort + " and " + endPort);
        return 0;
    }
}