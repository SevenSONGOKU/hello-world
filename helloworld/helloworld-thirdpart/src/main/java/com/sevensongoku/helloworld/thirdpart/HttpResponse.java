package com.sevensongoku.helloworld.thirdpart;

import cn.hutool.http.HttpConnection;

import java.nio.charset.Charset;

public class HttpResponse extends cn.hutool.http.HttpResponse {
    protected HttpResponse(HttpConnection httpConnection, Charset charset, boolean isAsync, boolean isIgnoreBody) {
        super(httpConnection, charset, isAsync, isIgnoreBody);
    }
}
