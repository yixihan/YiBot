package com.yixihan.yibot.constant;

/**
 * 天气查询-常量
 *
 * @author yixihan
 * @date 2022/12/5 15:30
 */
public class WeatherConstants {
    
    /**
     * 实时天气查询 url
     */
    public static final String REAL_TIME_WEATHER_URL = "https://devapi.qweather.com/v7/weather/now";
    
    /**
     * 未来天气预报 - (3, 7, 10, 15, 30)
     */
    public static final String DAILY_WEATHER_URL = "https://devapi.qweather.com/v7/weather/%sd";
    
    /**
     * 逐小时天气预报 - (24, 72, 168)
     */
    public static final String HOURLY_WEATHER_URL = "https://devapi.qweather.com/v7/weather/%sh";
    
    /**
     * 城市搜索
     */
    public static final String LOCATION_URL = "https://geoapi.qweather.com/v2/city/lookup";
    
    /**
     * key
     */
    public static final String KEY = "264aeb2154ab4c238c6db8b6b587c30c";
    
    /**
     * 查询 - 1
     */
    public static final String WEATHER_QUERY_FIRST = "天气查询";
    
    /**
     * 查询 - 2
     */
    public static final String[] WEATHER_QUERY_SECOND = {"实时天气", "未来天气", "逐小时天气"};
    
    /**
     * 逐小时天气 - 参数
     */
    public static final Integer[] HOURLY_WEATHER_PARAMS = {24, 72, 168};
    
    /**
     * 未来天气 - 参数
     */
    public static final Integer[] DAILY_WEATHER_PARAMS = {3, 7, 10, 15, 30};
    
}
