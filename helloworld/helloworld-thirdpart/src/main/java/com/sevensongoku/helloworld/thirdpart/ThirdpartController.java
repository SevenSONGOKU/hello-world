package com.sevensongoku.helloworld.thirdpart;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.sevensongoku.helloworld.thirdpart.HttpRequest.BOUNDARY;
import static com.sevensongoku.helloworld.thirdpart.HttpRequest.CONTENT_TYPE_MULTIPART_PREFIX;

@RestController
@RequestMapping(produces = {"application/json;charset=utf-8"})
public class ThirdpartController {
    private static final String ENV = "dev-curtfirstmp";
    private static final String PATH_ROOT = "images/";

    @GetMapping("/access-token")
    public String getAccessToken() {
        Console.log("access........");
        String appid = "wx35981e14e71c9ed8";
        String secret = "32dea815cc9665562568aafdd9f2d88a";
        String result = HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret+"");
        return result;
    }

    @PostMapping("/upload-file")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        Console.log("upload file....");
        File newFile = new File(file.getOriginalFilename());
        try {
            FileOutputStream output = new FileOutputStream(newFile);
            output.write(file.getBytes());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "failed";
        }

        String accessToken = JSONUtil.parseObj(getAccessToken()).getStr("access_token");
        Console.log(accessToken);
        String[] split = file.getContentType().split("/");
        String type = "." + split[split.length - 1];
        String path = PATH_ROOT + System.currentTimeMillis() + "." + SecureUtil.md5(newFile) + "." + newFile.getName() + type;
        Console.log(path);
        JSONObject params = new JSONObject();
        params.put("env", ENV);
        params.put("path", path);
        String result = HttpUtil.post("https://api.weixin.qq.com/tcb/uploadfile?access_token=" + accessToken, params.toString());
        Console.log(result);

        JSONObject obj = JSONUtil.parseObj(result);
        HashMap<String, Object> formParams = new HashMap<>();
        formParams.put("key", path);
        formParams.put("Signature", obj.getStr("authorization"));
        formParams.put("x-cos-security-token", obj.getStr("token"));
        formParams.put("x-cos-meta-fileid", obj.getStr("cos_file_id"));
        formParams.put("file", newFile);
//        String multipartResult = HttpUtil.post(obj.getStr("url"), formParams);
        String multipartResult = HttpRequest.post(obj.getStr("url"))
                .header(Header.CONTENT_TYPE, CONTENT_TYPE_MULTIPART_PREFIX + BOUNDARY, true)
                .form(formParams)
                .timeout(60000)
                .execute()
                .body();
        Console.log(multipartResult);
        Console.log("end....");

        return obj.getStr("file_id");
    }

    @PostMapping("/insert-data")
    public String uploadGoods(@RequestBody JSONObject params) {
        Console.log("insert data...");
        Console.log(params.toString());
        String accessToken = JSONUtil.parseObj(getAccessToken()).getStr("access_token");
        String url = StrUtil.format("https://api.weixin.qq.com/tcb/invokecloudfunction?access_token={}&env={}&name=databaseadd", accessToken, ENV);
        String result = HttpUtil.post(url, params.toString(), 60000);
        Console.log(result);
        return result;
    }

    @PostMapping("/get-list")
    public String getGoodsList(@RequestBody JSONObject params) {
        Console.log("get list...");
        Console.log(params.toString());
        String accessToken = JSONUtil.parseObj(getAccessToken()).getStr("access_token");
        String url = StrUtil.format("https://api.weixin.qq.com/tcb/invokecloudfunction?access_token={}&env={}&name=databaseget", accessToken, ENV);
        String result = HttpUtil.post(url, params.toString(), 60000);
        Console.log(result);
        return result;
    }
}
