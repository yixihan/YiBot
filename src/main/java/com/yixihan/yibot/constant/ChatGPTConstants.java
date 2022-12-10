package com.yixihan.yibot.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * chatGPT 常量
 *
 * @author yixihan
 * @date 2022/12/10 15:57
 */
@Getter
@Component
public class ChatGPTConstants {
    
    public static final String WORD_ONE = "chat";
    
    @Value ("${chatGPT.api-url}")
    private String apiUrl;
    
    @Value ("${chatGPT.api-key}")
    private String apiKey;
}
