package com.sevensongoku.helloworld.thirdpart;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.BytesResource;
import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.MultiFileResource;
import cn.hutool.core.io.resource.MultiResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.*;
import cn.hutool.http.*;
import cn.hutool.http.cookie.GlobalCookieManager;
import cn.hutool.http.ssl.SSLSocketFactoryBuilder;
import cn.hutool.json.JSON;
import cn.hutool.log.StaticLog;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.Proxy;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpRequest extends HttpBase<HttpRequest> {
    public static final int TIMEOUT_DEFAULT = -1;
    public static final String BOUNDARY = "--------------------Hutool_" + RandomUtil.randomString(16);
    private static final byte[] BOUNDARY_END;
    private static final String CONTENT_DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"\r\n\r\n";
    private static final String CONTENT_DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n";
    public static final String CONTENT_TYPE_MULTIPART_PREFIX = "multipart/form-data; boundary=";
    private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: {}\r\n\r\n";
    private String url;
    private URLStreamHandler urlHandler;
    private Method method;
    private int connectionTimeout;
    private int readTimeout;
    private Map<String, Object> form;
    private Map<String, Resource> fileForm;
    private String cookie;
    private HttpConnection httpConnection;
    private boolean isDisableCache;
    private boolean encodeUrlParams;
    private boolean isRest;
    private int redirectCount;
    private int maxRedirectCount;
    private Proxy proxy;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory ssf;

    public static CookieManager getCookieManager() {
        return GlobalCookieManager.getCookieManager();
    }

    public static void setCookieManager(CookieManager customCookieManager) {
        GlobalCookieManager.setCookieManager(customCookieManager);
    }

    public static void closeCookie() {
        GlobalCookieManager.setCookieManager((CookieManager)null);
    }

    public HttpRequest(String url) {
        this.method = Method.GET;
        this.connectionTimeout = -1;
        this.readTimeout = -1;
        Assert.notBlank(url, "Param [url] can not be blank !", new Object[0]);
        this.url = URLUtil.normalize(url, true);
        this.header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8", true);
        this.header(Header.ACCEPT_ENCODING, "gzip", true);
        this.header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8", true);
        this.header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.toString(CharsetUtil.CHARSET_UTF_8), true);
        this.header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.84 Safari/537.36 Hutool", true);
    }

    public static HttpRequest post(String url) {
        return (new HttpRequest(url)).method(Method.POST);
    }

    public static HttpRequest get(String url) {
        return (new HttpRequest(url)).method(Method.GET);
    }

    public static HttpRequest head(String url) {
        return (new HttpRequest(url)).method(Method.HEAD);
    }

    public static HttpRequest options(String url) {
        return (new HttpRequest(url)).method(Method.OPTIONS);
    }

    public static HttpRequest put(String url) {
        return (new HttpRequest(url)).method(Method.PUT);
    }

    public static HttpRequest patch(String url) {
        return (new HttpRequest(url)).method(Method.PATCH);
    }

    public static HttpRequest delete(String url) {
        return (new HttpRequest(url)).method(Method.DELETE);
    }

    public static HttpRequest trace(String url) {
        return (new HttpRequest(url)).method(Method.TRACE);
    }

    public String getUrl() {
        return this.url;
    }

    public HttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpRequest setUrlHandler(URLStreamHandler urlHandler) {
        this.urlHandler = urlHandler;
        return this;
    }

    public Method getMethod() {
        return this.method;
    }

    public HttpRequest setMethod(Method method) {
        return this.method(method);
    }

    public HttpConnection getConnection() {
        return this.httpConnection;
    }

    public HttpRequest method(Method method) {
        if (Method.PATCH == method) {
            this.method = Method.POST;
            this.header("X-HTTP-Method-Override", "PATCH");
        } else {
            this.method = method;
        }

        return this;
    }

    public HttpRequest contentType(String contentType) {
        this.header(Header.CONTENT_TYPE, contentType);
        return this;
    }

    public HttpRequest keepAlive(boolean isKeepAlive) {
        this.header(Header.CONNECTION, isKeepAlive ? "Keep-Alive" : "Close");
        return this;
    }

    public boolean isKeepAlive() {
        String connection = this.header(Header.CONNECTION);
        if (connection == null) {
            return !this.httpVersion.equalsIgnoreCase("HTTP/1.0");
        } else {
            return !connection.equalsIgnoreCase("close");
        }
    }

    public String contentLength() {
        return this.header(Header.CONTENT_LENGTH);
    }

    public HttpRequest contentLength(int value) {
        this.header(Header.CONTENT_LENGTH, String.valueOf(value));
        return this;
    }

    public HttpRequest cookie(HttpCookie... cookies) {
        return ArrayUtil.isEmpty(cookies) ? this.disableCookie() : this.cookie(ArrayUtil.join(cookies, ";"));
    }

    public HttpRequest cookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public HttpRequest disableCookie() {
        return this.cookie("");
    }

    public HttpRequest enableDefaultCookie() {
        return this.cookie((String)null);
    }

    public HttpRequest form(String name, Object value) {
        if (!StrUtil.isBlank(name) && !ObjectUtil.isNull(value)) {
            this.bodyBytes = null;
            if (value instanceof File) {
                return this.form(name, (File)value);
            } else if (value instanceof Resource) {
                return this.form(name, (Resource)value);
            } else {
                if (this.form == null) {
                    this.form = new LinkedHashMap();
                }

                String strValue;
                if (value instanceof List) {
                    strValue = CollectionUtil.join((List)value, ",");
                } else if (ArrayUtil.isArray(value)) {
                    if (File.class == ArrayUtil.getComponentType(value)) {
                        return this.form(name, (File[])((File[])value));
                    }

                    strValue = ArrayUtil.join((Object[])((Object[])value), ",");
                } else {
                    strValue = Convert.toStr(value, (String)null);
                }

                this.form.put(name, strValue);
                return this;
            }
        } else {
            return this;
        }
    }

    public HttpRequest form(String name, Object value, Object... parameters) {
        this.form(name, value);

        for(int i = 0; i < parameters.length; i += 2) {
            name = parameters[i].toString();
            this.form(name, parameters[i + 1]);
        }

        return this;
    }

    public HttpRequest form(Map<String, Object> formMap) {
        if (MapUtil.isNotEmpty(formMap)) {
            Iterator i$ = formMap.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, Object> entry = (Entry)i$.next();
                this.form((String)entry.getKey(), entry.getValue());
            }
        }

        return this;
    }

    public HttpRequest form(String name, File... files) {
        if (1 == files.length) {
            File file = files[0];
            return this.form(name, file, file.getName());
        } else {
            return this.form(name, (Resource)(new MultiFileResource(files)));
        }
    }

    public HttpRequest form(String name, File file) {
        return this.form(name, file, file.getName());
    }

    public HttpRequest form(String name, File file, String fileName) {
        if (null != file) {
            this.form(name, (Resource)(new FileResource(file, fileName)));
        }

        return this;
    }

    public HttpRequest form(String name, byte[] fileBytes, String fileName) {
        if (null != fileBytes) {
            this.form(name, (Resource)(new BytesResource(fileBytes, fileName)));
        }

        return this;
    }

    public HttpRequest form(String name, Resource resource) {
        if (null != resource) {
            if (!this.isKeepAlive()) {
                this.keepAlive(true);
            }

            if (null == this.fileForm) {
                this.fileForm = new HashMap();
            }

            this.fileForm.put(name, resource);
        }

        return this;
    }

    public Map<String, Object> form() {
        return this.form;
    }

    public Map<String, Resource> fileForm() {
        return this.fileForm;
    }

    public HttpRequest body(String body) {
        return this.body(body, (String)null);
    }

    public HttpRequest body(String body, String contentType) {
        this.body(StrUtil.bytes(body, this.charset));
        this.form = null;
        this.contentLength(null != body ? body.length() : 0);
        if (null != contentType) {
            this.contentType(contentType);
        } else {
            contentType = HttpUtil.getContentTypeByRequestBody(body);
            if (null != contentType && ContentType.isDefault(this.header(Header.CONTENT_TYPE))) {
                if (null != this.charset) {
                    contentType = ContentType.build(contentType, this.charset);
                }

                this.contentType(contentType);
            }
        }

        if (StrUtil.containsAnyIgnoreCase(contentType, new CharSequence[]{"json", "xml"})) {
            this.isRest = true;
        }

        return this;
    }

    public HttpRequest body(JSON json) {
        return this.body(json.toString());
    }

    public HttpRequest body(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
        return this;
    }

    public HttpRequest timeout(int milliseconds) {
        this.setConnectionTimeout(milliseconds);
        this.setReadTimeout(milliseconds);
        return this;
    }

    public HttpRequest setConnectionTimeout(int milliseconds) {
        this.connectionTimeout = milliseconds;
        return this;
    }

    public HttpRequest setReadTimeout(int milliseconds) {
        this.readTimeout = milliseconds;
        return this;
    }

    public HttpRequest disableCache() {
        this.isDisableCache = true;
        return this;
    }

    public HttpRequest setEncodeUrlParams(boolean isEncodeUrlParams) {
        this.encodeUrlParams = isEncodeUrlParams;
        return this;
    }

    public HttpRequest setFollowRedirects(boolean isFollowRedirects) {
        return this.setMaxRedirectCount(isFollowRedirects ? 2 : 0);
    }

    public HttpRequest setMaxRedirectCount(int maxRedirectCount) {
        if (maxRedirectCount > 0) {
            this.maxRedirectCount = maxRedirectCount;
        } else {
            this.maxRedirectCount = 0;
        }

        return this;
    }

    public HttpRequest setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public HttpRequest setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public HttpRequest setSSLSocketFactory(SSLSocketFactory ssf) {
        this.ssf = ssf;
        return this;
    }

    public HttpRequest setSSLProtocol(String protocol) {
        if (null == this.ssf) {
            try {
                this.ssf = SSLSocketFactoryBuilder.create().setProtocol(protocol).build();
            } catch (Exception var3) {
                throw new HttpException(var3);
            }
        }

        return this;
    }

    public HttpRequest setRest(boolean isRest) {
        this.isRest = isRest;
        return this;
    }

    public HttpResponse execute() {
        return this.execute(false);
    }

    public HttpResponse executeAsync() {
        return this.execute(true);
    }

    public HttpResponse execute(boolean isAsync) {
        this.urlWithParamIfGet();
        if (this.encodeUrlParams) {
            this.url = HttpUtil.encodeParams(this.url, this.charset);
        }

        this.initConnecton();
        this.send();
        HttpResponse httpResponse = this.sendRedirectIfPosible();
        if (null == httpResponse) {
            httpResponse = new HttpResponse(this.httpConnection, this.charset, isAsync, this.isIgnoreResponseBody());
        }

        return httpResponse;
    }

    public HttpRequest basicAuth(String username, String password) {
        String data = username.concat(":").concat(password);
        String base64 = Base64.encode(data, this.charset);
        this.header("Authorization", "Basic " + base64, true);
        return this;
    }

    private void initConnecton() {
        this.httpConnection = HttpConnection.create(URLUtil.toUrlForHttp(this.url, this.urlHandler), this.proxy).setMethod(this.method).setHttpsInfo(this.hostnameVerifier, this.ssf).setConnectTimeout(this.connectionTimeout).setReadTimeout(this.readTimeout).setCookie(this.cookie).setInstanceFollowRedirects(this.maxRedirectCount > 0).header(this.headers, true);
        GlobalCookieManager.add(this.httpConnection);
        if (this.isDisableCache) {
            this.httpConnection.disableCache();
        }

    }

    private void urlWithParamIfGet() {
        if (Method.GET.equals(this.method) && !this.isRest) {
            if (ArrayUtil.isNotEmpty(this.bodyBytes)) {
                this.url = HttpUtil.urlWithForm(this.url, StrUtil.str(this.bodyBytes, this.charset), this.charset, false);
            } else {
                this.url = HttpUtil.urlWithForm(this.url, this.form, this.charset, false);
            }
        }

    }

    private HttpResponse sendRedirectIfPosible() {
        if (this.maxRedirectCount < 1) {
            return null;
        } else {
            if (this.httpConnection.getHttpURLConnection().getInstanceFollowRedirects()) {
                int responseCode;
                try {
                    responseCode = this.httpConnection.responseCode();
                } catch (IOException var3) {
                    throw new HttpException(var3);
                }

                if (responseCode != 200 && (responseCode == 302 || responseCode == 301 || responseCode == 303)) {
                    this.url = this.httpConnection.header(Header.LOCATION);
                    if (this.redirectCount < this.maxRedirectCount) {
                        ++this.redirectCount;
                        return this.execute();
                    }

                    StaticLog.warn("URL [{}] redirect count more than two !", new Object[]{this.url});
                }
            }

            return null;
        }
    }

    private void send() throws HttpException {
        try {
            if (!Method.POST.equals(this.method) && !Method.PUT.equals(this.method) && !Method.DELETE.equals(this.method) && !this.isRest) {
                this.httpConnection.connect();
            } else if (CollectionUtil.isEmpty(this.fileForm)) {
                this.sendFormUrlEncoded();
            } else {
                this.sendMultipart();
            }

        } catch (IOException var2) {
            throw new HttpException(var2.getMessage(), var2);
        }
    }

    private void sendFormUrlEncoded() throws IOException {
        if (StrUtil.isBlank(this.header(Header.CONTENT_TYPE))) {
            this.httpConnection.header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.toString(this.charset), true);
        }

        if (ArrayUtil.isNotEmpty(this.bodyBytes)) {
            IoUtil.write(this.httpConnection.getOutputStream(), true, this.bodyBytes);
        } else {
            String content = HttpUtil.toParams(this.form, this.charset);
            IoUtil.write(this.httpConnection.getOutputStream(), this.charset, true, new Object[]{content});
        }

    }

    private void sendMultipart() throws IOException {
        this.setMultipart();
        OutputStream out = this.httpConnection.getOutputStream();

        try {
            this.writeForm(out);
            this.writeFileForm(out);
            this.formEnd(out);
        } catch (IOException var6) {
            throw var6;
        } finally {
            IoUtil.close(out);
        }

    }

    private void writeForm(OutputStream out) throws IOException {
        if (CollectionUtil.isNotEmpty(this.form)) {
            StringBuilder builder = StrUtil.builder();
            Iterator i$ = this.form.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, Object> entry = (Entry)i$.next();
                builder.append("--").append(BOUNDARY).append("\r\n");
                builder.append(StrUtil.format("Content-Disposition: form-data; name=\"{}\"\r\n\r\n", new Object[]{entry.getKey()}));
                builder.append(entry.getValue()).append("\r\n");
            }

            IoUtil.write(out, this.charset, false, new Object[]{builder});
        }

    }

    private void writeFileForm(OutputStream out) throws IOException {
        Iterator i$ = this.fileForm.entrySet().iterator();

        while(i$.hasNext()) {
            Entry<String, Resource> entry = (Entry)i$.next();
            this.appendPart((String)entry.getKey(), (Resource)entry.getValue(), out);
        }

    }

    private void appendPart(String formFieldName, Resource resource, OutputStream out) {
        if (resource instanceof MultiResource) {
            Iterator i$ = ((MultiResource)resource).iterator();

            while(i$.hasNext()) {
                Resource subResource = (Resource)i$.next();
                this.appendPart(formFieldName, subResource, out);
            }
        } else {
            StringBuilder builder = StrUtil.builder().append("--").append(BOUNDARY).append("\r\n");
            String fileName = resource.getName();
            builder.append(StrUtil.format("Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n", new Object[]{formFieldName, ObjectUtil.defaultIfNull(fileName, formFieldName)}));
            builder.append(StrUtil.format("Content-Type: {}\r\n\r\n", new Object[]{HttpUtil.getMimeType(fileName)}));
            IoUtil.write(out, this.charset, false, new Object[]{builder});
            InputStream in = null;

            try {
                in = resource.getStream();
                IoUtil.copy(in, out);
            } finally {
                IoUtil.close(in);
            }

            IoUtil.write(out, this.charset, false, new Object[]{"\r\n"});
        }

    }

    private void formEnd(OutputStream out) throws IOException {
        out.write(BOUNDARY_END);
        out.flush();
    }

    private void setMultipart() {
        this.httpConnection.header(Header.CONTENT_TYPE, "multipart/form-data; boundary=" + BOUNDARY, true);
    }

    private boolean isIgnoreResponseBody() {
        return Method.HEAD == this.method || Method.CONNECT == this.method || Method.OPTIONS == this.method || Method.TRACE == this.method;
    }

    static {
        BOUNDARY_END = StrUtil.format("--{}--\r\n", new Object[]{BOUNDARY}).getBytes();
    }
}

