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
     * group.commonList
     * 这里list名需要和application.properties中的参数一致
     */
    private List<Long> commonList = new ArrayList<> ();
    
    
    private List<Long> setuList = new ArrayList<> ();
    
    
    private List<Long> wordCloudList = new ArrayList<> ();
}
