package com.yixihan.yibot.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 词云 配置参数
 *
 * @author yixihan
 * @date 2023/1/11 17:53
 */
@Component
@Getter
public class WordCloudProperties {
    
    @Value ("${word-cloud.win-path}")
    private String winPath;
    
    @Value ("${word-cloud.linux-path}")
    private String linuxPath;
}
