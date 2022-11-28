package com.yixihan.yibot;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yixihan.yibot.constant.PatternConstants;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

import javax.annotation.Nullable;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/23 23:35
 */
public class MatcherTest {

    @Test
    public void test() {
        String pattern = "^消息记录 群聊 \\.+$";
        String testStr1 = "消息记录 群聊 802086225";
        System.out.println (testStr1.matches (pattern));
    }

    @Test
    public void test2() {
        String pattern = "\\d{4}-\\d{2}-\\d{2}";
        String testStr1 = "2022-10-31";
        System.out.println (testStr1.matches (pattern));
    }

    @Test
    public void test3() {
        String pattern = "^消息记录 群聊 " + PatternConstants.GROUP_ID_PATTERN + ".*$";
        String testStr1 = "消息记录 群聊 802086225 2022-10-31 2022-11-14";
        System.out.println ("pattern : " + pattern);
        System.out.println ("str : " + testStr1);
        System.out.println (testStr1.matches (pattern));
    }

    @Test
    public void test4() {
        String pattern = "^(色色|瑟瑟|涩涩).*$";
        String testStr1 = "色色 萝莉";
        String testStr2 = "涩涩 萝莉";
        String testStr3 = "瑟瑟 萝莉";
        System.out.println (testStr1.matches (pattern));
        System.out.println (testStr2.matches (pattern));
        System.out.println (testStr3.matches (pattern));
    }

    @Test
    public void test5() {
        String pattern = "^(色色|瑟瑟|涩涩).*$";
        String testStr1 = "色色";
        String testStr2 = "涩涩";
        String testStr3 = "瑟瑟";
        System.out.println (testStr1.matches (pattern));
        System.out.println (testStr2.matches (pattern));
        System.out.println (testStr3.matches (pattern));
    }
    
    @Test
    public void test7() {
        String tests = "0/2 * * * * ? *";
        System.out.println (CronExpression.isValidExpression (tests));
    
    }
    
    
    @Test
    public void testGetImage() {
        System.out.println (getImage (null));
    }

    private String getImage(@Nullable String val) {
        String[] tag = new String[]{val};
        String body = JSONUtil.toJsonStr (tag);
        HttpResponse response = HttpRequest.post ("https://api.lolicon.app/setu/v2").header ("Content-Type", "application/json").body (body).execute ();

        if (response.isOk ()) {
            JSONObject obj = JSONUtil.parseObj (response.body ());
            System.out.println ("obj : " + obj);
            if (StrUtil.isBlank (obj.getStr ("error"))) {
                return JSONUtil.parseObj (JSONUtil.parseObj (JSONUtil.parseArray (obj.get ("data")).get (0)).get ("urls")).getStr ("original");
            } else {
                return obj.getStr ("error");
            }
        }

        return null;
    }

}
