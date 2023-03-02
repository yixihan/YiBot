package com.yixihan.yibot.dto.chat;

import lombok.Data;

import java.util.List;

/**
 * chatGPT 请求体
 *
 * @author yixihan
 * @date 2022/12/10 16:24
 */
@Data
public class ChatGPT3Body {
    
    private String model;
    
    private List<Message> message;
}
