package com.yixihan.yibot.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * chatGPT 配置参数
 *
 * @author yixihan
 * @date 2022/12/10 15:57
 */
@Getter
@Component
public class ChatGPTProperties {
    
    @Value ("${chatGPT.api-url}")
    private String apiUrl;
    
    @Value ("${chatGPT.api-key}")
    private String apiKey;
}
