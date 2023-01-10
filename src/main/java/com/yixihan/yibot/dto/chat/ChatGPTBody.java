package com.yixihan.yibot.dto.chat;

import lombok.Data;

/**
 * chatGPT 请求体
 *
 * @author yixihan
 * @date 2022/12/10 16:24
 */
@Data
public class ChatGPTBody {
    
    private String model;
    
    private String prompt;
    
    private Integer temperature;
    
    private Integer max_tokens;
}
