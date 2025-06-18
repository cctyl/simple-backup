package com.example.myapplication.utils;

import fi.iki.elonen.NanoHTTPD;

public class MyHttpServer extends NanoHTTPD {

    public MyHttpServer(int port) {
        super(port);
    }


    @Override
    public Response serve(IHTTPSession session) {
        return super.serve(session);
    }
}
