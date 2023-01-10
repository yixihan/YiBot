package com.yixihan.yibot.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * description
 *
 * @author yixihan
 * @date 2022/12/4 17:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageNode implements Serializable {
    
    
    /**
     * QQ 号
     */
    private Long userId;
    
    /**
     * QQ 昵称
     */
    private String userName;
    
    /**
     * 消息 ID
     */
    private Long messageId;
    
    /**
     * 消息内容
     */
    private String message;
    
    public MessageNode(long userId, long messageId) {
        this(userId, null, messageId, null);
    }
}
