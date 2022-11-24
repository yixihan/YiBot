package com.yixihan.yibot;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yixihan.yibot.constant.GoodImageConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@WebAppConfiguration
class YiBotApplicationTests {

    @Resource
    private GoodImageConstants constants;

    @Test
    void contextLoads() {

        constants.initList ();
        System.out.println (constants.getUrl ());
        System.out.println (constants.getVal ());
        System.out.println (constants.getWhiteList ());
    }

    @Test
    public void testGetImage () {
        System.out.println (getImage ("萝莉"));
    }

    private String getImage (String val) {
        String[] tag = new String[]{val};
        String body = JSONUtil.toJsonStr (tag);
        HttpResponse response = HttpRequest.post (constants.getUrl ())
                .header ("Content-Type", "application/json")
                .body (body)
                .execute ();

        if (response.isOk ()) {
            try {
                JSONObject obj = JSONUtil.parseObj (response.body ());
                System.out.println ("obj : " + obj);
                if (StrUtil.isBlank (obj.getStr ("error"))) {
                    JSONObject data = JSONUtil.parseObj (obj.get ("data"));
                    System.out.println ("data : " + data);
                    System.out.println ("urls : " + data.get ("urls"));
                    JSONObject urls = JSONUtil.parseObj (data.get ("urls"));
                    System.out.println ("urls : " + urls);
                    return urls.get ("original", String.class);
                } else {
                    return obj.getStr ("error");
                }
            } catch (Exception e) {
                return null;
            }

        }

        return null;
    }

}
