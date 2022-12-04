package com.yixihan.yibot.plugin;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yixihan.yibot.constant.RedisKeyConstants.GROUP_RECORD_KEY;
import static com.yixihan.yibot.constant.RedisKeyConstants.PRIVATE_RECORD_KEY;

/**
 * 消息记录插件
 *
 * @author yixihan
 * @date 2022/11/23 23:28
 */
@Slf4j
@Shiro
@Component
public class MessageRecordPlugin {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @GroupMessageHandler
    public void groupMessageRecord(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        log.info ("消息来源 : 群消息, group : {}, sender : {}, message : {}", event.getGroupId (), event.getSender ().getUserId (), event.getMessage ());
        
        String key = String.format (GROUP_RECORD_KEY, event.getGroupId ());
        redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (event));
    }
    
    @PrivateMessageHandler
    public void privateMessageRecord(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        log.info ("消息来源 : 群消息, group : {}, sender : {}, message : {}", event.getGroupId (), event.getSender ().getUserId (), event.getMessage ());
        
        String key = String.format (PRIVATE_RECORD_KEY, event.getGroupId ());
        redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (event));
    }
}
