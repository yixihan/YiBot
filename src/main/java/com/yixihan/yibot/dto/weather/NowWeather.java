package com.yixihan.yibot.dto.weather;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 实时天气 返回实体类
 *
 * @author yixihan
 * @date 2022/12/5 16:35
 */
@Data
public class NowWeather {
    
    @JSONField(name = "obsTime", label = "数据观测时间")
    private String obsTime;
    
    @JSONField(name = "temp", label = "温度")
    private String temp;
    
    @JSONField(name = "feelsLike", label = "体感温度")
    private String feelsLike;
    
    @JSONField(name = "text", label = "天气状况")
    private String text;
    
    @JSONField(name = "windDir", label = "风向")
    private String windDir;
    
    @JSONField(name = "windScale", label = "风力等级")
    private String windScale;
    
    @JSONField(name = "windSpeed", label = "风速")
    private String windSpeed;
    
    @JSONField(name = "humidity", label = "相对湿度")
    private String humidity;
    
    @JSONField(name = "precip", label = "当前小时累计降水量")
    private String precip;
    
    @JSONField(name = "pressure", label = "大气压强")
    private String pressure;
    
    @JSONField(name = "vis", label = "能见度")
    private String vis;
    
    @JSONField(name = "cloud", label = "云量")
    private String cloud;
    
    @JSONField(name = "dew", label = "露点温度")
    private String dew;
    
    private WeatherCity city;
    
    public String toString() {
        LocalDateTime date = LocalDateTime.parse (obsTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return "下面是 " + city.getCityName () + " 实时天气情况\n"
                + "温度 : " + temp + " 度\n"
                + "体感温度 : " + feelsLike + " 度\n"
                + "天气状况 : " + text + "\n"
                + "风向 : " + windDir + "\n"
                + "风力等级 : " + windScale + "\n"
                + "风速 : " + windSpeed + " 公里/小时\n"
                + "相对湿度 : " + humidity + "%\n"
                + "当前小时累计降水量 : " + precip + " 毫米\n"
                + "大气压强 : " + pressure + " 百帕\n"
                + "能见度 : " + vis + " 公里\n"
                + "云量 : " + cloud + "%\n"
                + "露点温度 : " + dew + " 度\n"
                + "数据观测时间 : " + DateUtil.format (date, "yyyy-MM-dd HH:mm:ss");
    }
}
