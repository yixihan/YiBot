package com.yixihan.yibot.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 天气查询 配置参数
 *
 * @author yixihan
 * @date 2022/12/5 15:30
 */
@Component
@Getter
public class WeatherProperties {
    
    /**
     * 实时天气查询 url
     */
    @Value("${weather.real-time-weather-url}")
    public String REAL_TIME_WEATHER_URL;
    
    /**
     * 未来天气预报 - (3, 7, 10, 15, 30)
     */
    @Value("${weather.future-weather-url}")
    public String FUTURE_WEATHER_URL;
    
    /**
     * 逐小时天气预报 - (24, 72, 168)
     */
    @Value("${weather.hourly-weather-url}")
    public String HOURLY_WEATHER_URL;
    
    /**
     * 城市搜索
     */
    @Value("${weather.location-url}")
    public String LOCATION_URL;
    
    /**
     * key
     */
    @Value("${weather.key}")
    public String KEY;
}
