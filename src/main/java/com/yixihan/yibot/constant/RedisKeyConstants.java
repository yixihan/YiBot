package com.yixihan.yibot.constant;

/**
 * redis Key
 *
 * @author yixihan
 * @date 2022/12/4 17:18
 */
public class RedisKeyConstants {
    
    /**
     * 群聊消息记录 key
     */
    public static final String GROUP_MESSAGE_RECORD_KEY = "message:group:%s";
    
    /**
     * 私聊消息记录 key
     */
    public static final String PRIVATE_MESSAGE_RECORD_KEY = "message:private:%s";
    
    /**
     * 群聊消息撤回记录 key
     */
    public static final String GROUP_WITHDRAW_RECORD_KEY = "withdraw:group:%s";
    
    /**
     * 私聊消息撤回记录 key
     */
    public static final String PRIVATE_WITHDRAW_RECORD_KEY = "withdraw:private:%s";
    
    
}
