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
import com.yixihan.yibot.constant.CommonConstants;
import com.yixihan.yibot.dto.message.MessageNode;
import com.yixihan.yibot.dto.weather.FutureWeather;
import com.yixihan.yibot.dto.weather.HourlyWeather;
import com.yixihan.yibot.dto.weather.NowWeather;
import com.yixihan.yibot.dto.weather.WeatherCity;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.properties.WeatherProperties;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.yixihan.yibot.constant.NumberConstants.*;

/**
 * 天气查询插件
 *
 * @author yixihan
 * @date 2022/12/5 15:38
 */
@Slf4j
@Component
public class WeatherPlugin extends BotPlugin {
    
    @Resource
    private WeatherProperties prop;
    
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String[] queryParams = event.getMessage ().split (" ");
    
        if (!CommonConstants.WEATHER_QUERY_FIRST.equals (queryParams[0])) {
            return MESSAGE_IGNORE;
        }
        if (Arrays.stream(CommonConstants.WEATHER_QUERY_SECOND).noneMatch ((o) -> o.equals (queryParams[1]))) {
            messageOutput (bot, event, "错误指令");
            return MESSAGE_IGNORE;
        }
        
        if (CommonConstants.WEATHER_QUERY_SECOND[0].equals (queryParams[1])) {
            // 实时天气查询
            realTimeWeatherQuery (bot, event, queryParams);
        } else if (CommonConstants.WEATHER_QUERY_SECOND[1].equals (queryParams[1])) {
            // 未来天气查询
            futureWeatherQuery (bot, event, queryParams);
        } else if (CommonConstants.WEATHER_QUERY_SECOND[2].equals (queryParams[1])) {
            // 逐小时天气查询
            hourlyWeatherQuery (bot, event, queryParams);
        } else if (CommonConstants.WEATHER_QUERY_SECOND[3].equals (queryParams[1])) {
            // 帮助菜单
            helperWeather (bot, event);
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
        WeatherCity city;
        if (len == THREE) {
            // 天气查询 实时天气 xxx
            city = citySearch (null, queryParams[2]);
        } else if (len == FOUR) {
            city = citySearch (queryParams[2], queryParams[3]);
        } else {
            log.warn ("天气查询-实时天气查询-错误传参");
            messageOutput (bot, event, "错误传参");
            return;
        }
    
        if (city == null) {
            log.warn ("天气查询-实时天气查询-城市搜索不到");
            messageOutput (bot, event, "城市搜索不到");
            return;
        }
    
        String url = prop.REAL_TIME_WEATHER_URL
                + "?key=" + prop.KEY
                + "&location=" + city.getLocationId ();
    
        HttpResponse execute = HttpRequest.get (url).execute ();
        if (execute.isOk ()) {
            JSONObject body = JSONUtil.parseObj (execute.body ());
            if (!"200".equals (body.getStr ("code"))) {
                log.warn ("天气查询-实时天气查询-天气查询失败");
                messageOutput (bot, event, "天气查询失败");
                return;
            }
            NowWeather data = JSONUtil.toBean (body.getStr ("now"), NowWeather.class);
            data.setCity (city);
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
        int len = queryParams.length;
        WeatherCity city;
        if (len == FOUR) {
            // 天气查询 实时天气 xxx
            city = citySearch (null, queryParams[2]);
        } else if (len == FIVE) {
            city = citySearch (queryParams[2], queryParams[3]);
        } else {
            log.warn ("天气查询-实时天气查询-错误传参");
            messageOutput (bot, event, "错误传参");
            return;
        }
    
        if (city == null) {
            log.warn ("天气查询-实时天气查询-城市搜索不到");
            messageOutput (bot, event, "城市搜索不到");
            return;
        }
        if (Arrays.stream (CommonConstants.FUTURE_WEATHER_PARAMS).noneMatch ((o) -> o.toString ().equals (queryParams[queryParams.length - 1]))) {
            log.warn ("天气查询-实时天气查询-查询天数错误");
            messageOutput (bot, event, "查询天数错误");
            return;
        }
    
        String url = String.format (prop.FUTURE_WEATHER_URL, queryParams[queryParams.length - 1])
                + "?key=" + prop.KEY
                + "&location=" + city.getLocationId ();
    
        HttpResponse execute = HttpRequest.get (url).execute ();
        if (execute.isOk ()) {
            JSONObject body = JSONUtil.parseObj (execute.body ());
            if (!"200".equals (body.getStr ("code"))) {
                log.warn ("天气查询-实时天气查询-天气查询失败");
                messageOutput (bot, event, "天气查询失败");
                return;
            }
            List<FutureWeather> dataList = JSONUtil.toList (JSONUtil.parseArray (body.getStr ("daily")), FutureWeather.class);
            List<MessageNode> messageList = new ArrayList<> ();
            MessageNode node = new MessageNode ();
            node.setMessage ("下面是 " + city.getCityName () + " 未来 " + queryParams[queryParams.length - 1] + " 天天气预报");
            node.setUserId (event.getUserId ());
            node.setUserName (event.getSender ().getNickname ());
            messageList.add (node);
            for (FutureWeather data : dataList) {
                node = new MessageNode ();
                node.setMessage (data.toString ());
                node.setUserId (event.getUserId ());
                node.setUserName (event.getSender ().getNickname ());
                messageList.add (node);
            }
            List<Map<String, Object>> forwardMsg = CQCodeUtils.generateForwardMsg (messageList);
            bot.sendGroupForwardMsg (event.getGroupId (), forwardMsg);
    
    
        } else {
            log.warn ("天气查询-实时天气查询-天气查询失败");
            messageOutput (bot, event, "天气查询失败");
        }
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
        int len = queryParams.length;
        WeatherCity city;
        if (len == FOUR) {
            // 天气查询 实时天气 xxx
            city = citySearch (null, queryParams[2]);
        } else if (len == FIVE) {
            city = citySearch (queryParams[2], queryParams[3]);
        } else {
            log.warn ("天气查询-实时天气查询-错误传参");
            messageOutput (bot, event, "错误传参");
            return;
        }
    
        if (city == null) {
            log.warn ("天气查询-实时天气查询-城市搜索不到");
            messageOutput (bot, event, "城市搜索不到");
            return;
        }
        if (Arrays.stream (CommonConstants.HOURLY_WEATHER_PARAMS).noneMatch ((o) -> o.toString ().equals (queryParams[queryParams.length - 1]))) {
            log.warn ("天气查询-实时天气查询-查询天数错误");
            messageOutput (bot, event, "查询天数错误");
            return;
        }
    
        String url = String.format (prop.HOURLY_WEATHER_URL, queryParams[queryParams.length - 1])
                + "?key=" + prop.KEY
                + "&location=" + city.getLocationId ();
    
        HttpResponse execute = HttpRequest.get (url).execute ();
        if (execute.isOk ()) {
            JSONObject body = JSONUtil.parseObj (execute.body ());
            if (!"200".equals (body.getStr ("code"))) {
                log.warn ("天气查询-实时天气查询-天气查询失败");
                messageOutput (bot, event, "天气查询失败");
                return;
            }
            List<HourlyWeather> dataList = JSONUtil.toList (JSONUtil.parseArray (body.getStr ("hourly")), HourlyWeather.class);
            List<MessageNode> messageList = new ArrayList<> ();
            MessageNode node = new MessageNode ();
            node.setMessage ("下面是 " + city.getCityName () + " 未来 " + queryParams[queryParams.length - 1] + " 小时天气预报");
            node.setUserId (event.getUserId ());
            node.setUserName (event.getSender ().getNickname ());
            messageList.add (node);
            for (HourlyWeather data : dataList) {
                node = new MessageNode ();
                node.setMessage (data.toString ());
                node.setUserId (event.getUserId ());
                node.setUserName (event.getSender ().getNickname ());
                messageList.add (node);
            }
            List<Map<String, Object>> forwardMsg = CQCodeUtils.generateForwardMsg (messageList);
            bot.sendGroupForwardMsg (event.getGroupId (), forwardMsg);
        
        
        } else {
            log.warn ("天气查询-实时天气查询-天气查询失败");
            messageOutput (bot, event, "天气查询失败");
        }
    }
    
    /**
     * 天气查询-帮助
     *
     * @param bot bot
     * @param event event
     */
    private void helperWeather(Bot bot, GroupMessageEvent event) {
        String message = CQCodeUtils.extracted (
                event.getSender ().getUserId (),
                CQCodeEnums.at)
                + "天气查询 帮助系统\n"
                + "实时天气查询 :\n"
                + "天气查询 实时天气 [城市的上级行政区划] 地区\n"
                + "未来天气查询 :\n"
                + "天气查询 未来天气 [城市的上级行政区划] 地区 (3/7)"
                + "逐小时天气查询 : \n"
                + "天气查询 逐小时天气 [城市的上级行政区划] 地区 (24)\n"
                + "tips : [] 为可选参数, () 为必选参数";
        bot.sendGroupMsg (event.getGroupId (), message, false);
    }
    
    
    /**
     * 城市搜索, 返回 locationId
     *
     * @param adm 城市的上级行政区划
     * @param location 查询地区的名称
     * @return locationId
     */
    private WeatherCity citySearch (@Nullable String adm, @NotNull String location) {
        String url = prop.LOCATION_URL
                + "?key=" + prop.KEY
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
