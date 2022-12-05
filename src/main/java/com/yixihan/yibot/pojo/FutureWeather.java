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
        return "预报日期:" + fxDate + "\n"
                + "日出时间 : " + sunrise + "\n"
                + "日落时间 : " + sunset + "\n"
                + "月升时间 : " + moonrise + "\n"
                + "月落时间 : " + moonset + "\n"
                + "月相名称 : " + moonPhase + "\n"
                + "当天最高温度 : " + tempMax + " 度\n"
                + "当天最低温度 : " + tempMin + " 度\n"
                + "白天天气状况 : " + textDay + "\n"
                + "晚间天气状况 : " + textNight + "\n"
                + "白天风向 : " + windDirDay + "\n"
                + "白天风力等级 : " + windScaleDay + "\n"
                + "白天风速 : " + windSpeedDay + " 公里/小时\n"
                + "夜间风向 : " + windDirNight + "\n"
                + "夜间风力等级 : " + windScaleNight + "\n"
                + "夜间风速 : " + windSpeedNight + " 公里/小时\n"
                + "当天总降水量 : " + precip + " 毫米\n"
                + "紫外线强度指数 : " + uvIndex + "\n"
                + "相对湿度 : " + humidity + " %\n"
                + "大气压强 : " + pressure + " 百帕\n"
                + "能见度 : " + vis + " 公里\n"
                + "云量 : " + cloud;
    }
    
}
