package com.yixihan.yibot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 群权限控制配置类
 *
 * @author yixihan
 * @date 2023/1/12 19:16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "group")
public class GroupConfig {
    
    /**
     * 通用插件权限
     */
    private List<Long> commonList = new ArrayList<> ();
    
    /**
     * 瑟图权限
     */
    private List<Long> setuList = new ArrayList<> ();
    
    /**
     * 词云权限
     */
    private List<Long> wordCloudList = new ArrayList<> ();
    
    /**
     * 自动签到权限
     */
    private List<Long> autoCheckList = new ArrayList<> ();
}
