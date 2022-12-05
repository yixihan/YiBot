package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.pojo.NowWeather;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.yixihan.yibot.constant.NumberConstants.FOUR;
import static com.yixihan.yibot.constant.NumberConstants.THREE;
import static com.yixihan.yibot.constant.WeatherConstants.*;

/**
 * 天气查询插件
 *
 * @author yixihan
 * @date 2022/12/5 15:38
 */
@Slf4j
@Component
public class WeatherPlugin extends BotPlugin {
    
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String[] queryParams = event.getMessage ().split (" ");
    
        if (!WEATHER_QUERY_FIRST.equals (queryParams[0])) {
            return MESSAGE_IGNORE;
        }
        if (Arrays.stream(WEATHER_QUERY_SECOND).noneMatch ((o) -> o.equals (queryParams[1]))) {
            messageOutput (bot, event, "错误指令");
            return MESSAGE_IGNORE;
        }
        
        if (WEATHER_QUERY_SECOND[0].equals (queryParams[1])) {
            // 实时天气查询
            realTimeWeatherQuery (bot, event, queryParams);
        } else if (WEATHER_QUERY_SECOND[1].equals (queryParams[2])) {
            // 未来天气查询
            futureWeatherQuery (bot, event, queryParams);
        } else if (WEATHER_QUERY_SECOND[2].equals (queryParams[1])) {
            // 逐小时天气查询
            hourlyWeatherQuery (bot, event, queryParams);
        } else {
            messageOutput (bot, event, "错误指令");
        }
        return super.onGroupMessage (bot, event);
    }
    
    /**
     * 实时天气查询
     *
     * @param bot bot
     * @param event event
     * @param queryParams 查询参数
     */
    private void realTimeWeatherQuery (@NotNull Bot bot,
                                       @NotNull GroupMessageEvent event,
                                       String[] queryParams) {
        int len = queryParams.length;
        String locationId;
        if (len == THREE) {
            // 天气查询 实时天气 xxx
            locationId = citySearch (null, queryParams[2]);
        } else if (len == FOUR) {
            locationId = citySearch (queryParams[2], queryParams[3]);
        } else {
            log.warn ("天气查询-实时天气查询-错误传参");
            messageOutput (bot, event, "错误传参");
            return;
        }
    
        String url = REAL_TIME_WEATHER_URL
                + "?key=" + KEY
                + "&location=" + locationId;
    
        HttpResponse execute = HttpRequest.get (url).execute ();
        if (execute.isOk ()) {
            JSONObject body = JSONUtil.parseObj (execute.body ());
            if (!"200".equals (body.getStr ("code"))) {
                log.warn ("天气查询-实时天气查询-天气查询失败");
                messageOutput (bot, event, "天气查询失败");
                return;
            }
            NowWeather data = JSONUtil.toBean (body.getStr ("now"), NowWeather.class);
            messageOutput (bot, event, data.toString ());
        } else {
            log.warn ("天气查询-实时天气查询-天气查询失败");
            messageOutput (bot, event, "天气查询失败");
        }
    }
    
    /**
     * 未来天气查询
     *
     * @param bot bot
     * @param event event
     * @param queryParams 查询参数
     */
    private void futureWeatherQuery (@NotNull Bot bot,
                                     @NotNull GroupMessageEvent event,
                                     String[] queryParams) {
        messageOutput (bot, event, "还没有做捏, 别着急啦!");
    }
    
    /**
     * 逐小时天气查询
     *
     * @param bot bot
     * @param event event
     * @param queryParams 查询参数
     */
    private void hourlyWeatherQuery (@NotNull Bot bot,
                                     @NotNull GroupMessageEvent event,
                                     String[] queryParams) {
        messageOutput (bot, event, "还没有做捏, 别着急啦!");
    }
    
    
    /**
     * 城市搜索, 返回 locationId
     *
     * @param adm 城市的上级行政区划
     * @param location 查询地区的名称
     * @return locationId
     */
    private String citySearch (@Nullable String adm, @NotNull String location) {
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
            return JSONUtil.parseObj (jsonArray.get (0)).getStr ("id");
    
        }
        
        return null;
    }
    
    /**
     * 消息输出
     *
     * @param info 消息
     */
    private static void messageOutput(@NotNull Bot bot, @NotNull GroupMessageEvent event, String info) {
        String message;
        message = CQCodeUtils.extracted (event.getSender ().getUserId (),
                CQCodeEnums.at);
        bot.sendGroupMsg (event.getGroupId (), message + info, false);
    }
}
