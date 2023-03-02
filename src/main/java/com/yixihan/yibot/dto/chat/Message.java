package com.yixihan.yibot.dto.chat;

import lombok.Data;

/**
 * ChatGPT3 Message
 *
 * @author yixihan
 * @date 2023/3/2 19:26
 */
@Data
public class Message {
    
    private String role;
    
    private String content;
}
