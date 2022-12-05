package com.yixihan.yibot.pojo;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 逐小时天气
 *
 * @author yixihan
 * @date 2022/12/5 21:22
 */
@Data
public class HourlyWeather {
  
  @JSONField(name = "fxTime", label = "预报时间")
  private String fxTime;
  
  @JSONField(name = "temp", label = "温度")
  private String temp;
  
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
  
  @JSONField(name = "pop", label = "逐小时预报降水概率")
  private String pop;
  
  @JSONField(name = "precip", label = "当前小时累计降水量")
  private String precip;
  
  @JSONField(name = "pressure", label = "大气压强")
  private String pressure;
  
  @JSONField(name = "cloud", label = "云量")
  private String cloud;
  
  @JSONField(name = "dew", label = "露点温度")
  private String dew;
  
  public String toString() {
    LocalDateTime date = LocalDateTime.parse (fxTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    return "预报时间 : " + DateUtil.format (date, "yyyy-MM-dd HH:mm:ss") + "\n"
            + "温度 : " + temp + " 度\n"
            + "天气状况 : " + text + "\n"
            + "风向 : " + windDir + "\n"
            + "风力等级 : " + windScale + "\n"
            + "风速 : " + windSpeed + " 公里/小时\n"
            + "相对湿度 : " + humidity + "%\n"
            + "当前小时累计降水量 : " + precip + " 毫米\n"
            + "逐小时预报降水概率 : " + pop + " %\n"
            + "大气压强 : " + pressure + " 百帕\n"
            + "云量 : " + cloud + "%\n"
            + "露点温度 : " + dew + " 度";
  }
}
