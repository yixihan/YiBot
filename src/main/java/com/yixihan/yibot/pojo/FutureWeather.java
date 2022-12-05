package com.yixihan.yibot.pojo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * 未来天气
 *
 * @author yixihan
 * @date 2022/12/5 20:20
 */
@Data
public class FutureWeather {
    
    @JSONField(name = "fxDate", label = "预报日期")
    private String fxDate;
    
    @JSONField(name = "sunrise", label = "日出时间")
    private String sunrise;
    
    @JSONField(name = "sunset", label = "日落时间")
    private String sunset;
    
    @JSONField(name = "moonrise", label = "月升时间")
    private String moonrise;
    
    @JSONField(name = "moonset", label = "月落时间")
    private String moonset;
    
    @JSONField(name = "moonPhase", label = "月相名称")
    private String moonPhase;
    
    @JSONField(name = "tempMax", label = "当天最高温度")
    private String tempMax;
    
    @JSONField(name = "tempMin", label = "当天最低温度")
    private String tempMin;
    
    @JSONField(name = "textDay", label = "白天天气状况文字描述")
    private String textDay;
    
    @JSONField(name = "textNight", label = "晚间天气状况文字描述")
    private String textNight;
    
    @JSONField(name = "windDirDay", label = "白天风向")
    private String windDirDay;
    
    @JSONField(name = "windScaleDay", label = "白天风力等级")
    private String windScaleDay;
    
    @JSONField(name = "windSpeedDay", label = "白天风速")
    private String windSpeedDay;
    
    @JSONField(name = "windDirNight", label = "夜间风向")
    private String windDirNight;
    
    @JSONField(name = "windScaleNight", label = "夜间风力等级")
    private String windScaleNight;
    
    @JSONField(name = "windSpeedNight", label = "夜间风速")
    private String windSpeedNight;
    
    @JSONField(name = "precip", label = "当天总降水量")
    private String precip;
    
    @JSONField(name = "uvIndex", label = "紫外线强度指数")
    private String uvIndex;
    
    @JSONField(name = "humidity", label = "相对湿度")
    private String humidity;
    
    @JSONField(name = "pressure", label = "大气压强")
    private String pressure;
    
    @JSONField(name = "vis", label = "能见度")
    private String vis;
    
    @JSONField(name = "cloud", label = "云量")
    private String cloud;
    
    public String toString () {
        StringBuilder sb = new StringBuilder ();
        sb.append ("预报日期:").append (fxDate).append ("\n")
                .append ("日出时间 : ").append (sunrise).append ("\n")
                .append ("日落时间 : ").append (sunset).append ("\n")
                .append ("月升时间 : ").append (moonrise).append ("\n")
                .append ("月落时间 : ").append (moonset).append ("\n")
                .append ("月相名称 : ").append (moonPhase).append ("\n")
                .append ("当天最高温度 : ").append (tempMax).append (" 度\n")
                .append ("当天最低温度 : ").append (tempMin).append (" 度\n")
                .append ("白天天气状况 : ").append (textDay).append ("\n")
                .append ("晚间天气状况 : ").append (textNight).append ("\n")
                .append ("白天风向 : ").append (windDirDay).append ("\n")
                .append ("白天风力等级 : ").append (windScaleDay).append ("\n")
                .append ("白天风速 : ").append (windSpeedDay).append (" 公里/小时\n")
                .append ("夜间风向 : ").append (windDirNight).append ("\n")
                .append ("夜间风力等级 : ").append (windScaleNight).append ("\n")
                .append ("夜间风速 : ").append (windSpeedNight).append (" 公里/小时\n")
                .append ("当天总降水量 : ").append (precip).append (" 毫米\n")
                .append ("紫外线强度指数 : ").append (uvIndex).append ("\n")
                .append ("相对湿度 : ").append (humidity).append (" %\n")
                .append ("大气压强 : ").append (pressure).append (" 百帕\n")
                .append ("能见度 : ").append (vis).append (" 公里\n")
                .append ("云量 : ").append (cloud);
                
                
        return sb.toString ();
    }
    
}
