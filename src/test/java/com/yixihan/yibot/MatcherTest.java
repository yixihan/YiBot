package com.yixihan.yibot;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.constant.PatternConstants;
import com.yixihan.yibot.pojo.NowWeather;
import com.yixihan.yibot.pojo.WeatherCity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

import javax.annotation.Nullable;

import static com.yixihan.yibot.constant.NumberConstants.FOUR;
import static com.yixihan.yibot.constant.NumberConstants.THREE;
import static com.yixihan.yibot.constant.WeatherConstants.*;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/23 23:35
 */
@Slf4j
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
    
    @Test
    public void test9 () {
        realTimeWeatherQuery ("天气查询 实时天气 四川 双流".split (" "));
    }
    
    private WeatherCity citySearch (@Nullable String adm, @NotNull String location) {
        String url = LOCATION_URL
                + "?key=" + KEY
                + (StrUtil.isEmpty (adm) ? "" : "&adm=" + adm)
                + "&location=" + location;
        HttpResponse execute = HttpRequest.get (url).execute ();
        if (execute.isOk ()) {
            JSONObject body = JSONUtil.parseObj (execute.body ());
            if (!"200".equals (body.getStr ("code"))) {
                log.warn ("地区查询失败");
                return null;
            }
            JSONArray jsonArray = JSONUtil.parseArray (body.get ("location"));
            if (jsonArray.isEmpty ()) {
                return null;
            }
            WeatherCity city = new WeatherCity ();
            JSONObject data = JSONUtil.parseObj (jsonArray.get (0));
            city.setLocationId (data.getStr ("id"));
            city.setCityName (data.getStr ("country") + "-"
                    + data.getStr ("adm1") + "-"
                    + data.getStr ("adm2") + "-"
                    + data.getStr ("name"));
            
            return city;
        
        }
    
        return null;
    }
    
    private void realTimeWeatherQuery (String[] queryParams) {
        int len = queryParams.length;
        WeatherCity city;
        if (len == THREE) {
            // 天气查询 实时天气 xxx
            city = citySearch (null, queryParams[2]);
        } else if (len == FOUR) {
            city = citySearch (queryParams[2], queryParams[3]);
        } else {
            log.warn ("天气查询-实时天气查询-错误传参");
            return;
        }
        
        if (city == null) {
            log.warn ("天气查询-实时天气查询-城市搜索不到");
            return;
        }
        
        String url = REAL_TIME_WEATHER_URL
                + "?key=" + KEY
                + "&location=" + city.getLocationId ();
        
        HttpResponse execute = HttpRequest.get (url).execute ();
        if (execute.isOk ()) {
            JSONObject body = JSONUtil.parseObj (execute.body ());
            if (!"200".equals (body.getStr ("code"))) {
                log.warn ("天气查询-实时天气查询-天气查询失败");
                return;
            }
            NowWeather data = JSONUtil.toBean (body.getStr ("now"), NowWeather.class);
            data.setCity (city);
            log.info (data.toString ());
        } else {
            log.warn ("天气查询-实时天气查询-天气查询失败");
        }
    }

}
