package com.yixihan.yibot.plugin;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.yixihan.yibot.dto.message.MessageNode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.Set;

import static com.yixihan.yibot.constant.RedisKeyConstants.GROUP_MESSAGE_RECORD_KEY;
import static com.yixihan.yibot.constant.RedisKeyConstants.PRIVATE_MESSAGE_RECORD_KEY;

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
    
    private static final Set<String> redisKeySet = new HashSet<> ();
    
    @GroupMessageHandler
    public void groupMessageRecord(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String message = event.getMessage ();
        String key = String.format (GROUP_MESSAGE_RECORD_KEY, event.getGroupId ());
        MessageNode node = new MessageNode (event.getUserId (),
                event.getSender ().getNickname (),
                (long) event.getMessageId (),
                message);
        redisKeySet.add (key);
        redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (node));
    }
    
    @PrivateMessageHandler
    public void privateMessageRecord(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        String message = event.getMessage ();
        String key = String.format (PRIVATE_MESSAGE_RECORD_KEY, event.getUserId ());
        MessageNode node = new MessageNode (event.getUserId (),
                event.getPrivateSender ().getNickname (),
                (long) event.getMessageId (),
                message);
        redisKeySet.add (key);
        redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (node));
    }
    
    @Scheduled(cron = "0 0 0 ? * 7")
    public void clearMessageRecord () {
        redisKeySet.forEach ((key) -> redisTemplate.delete (key));
    }
}
