package com.yixihan.yibot.plugin;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.pojo.MessageNode;
import com.yixihan.yibot.utils.CQCodeUtils;
import com.yixihan.yibot.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;

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
    
    @GroupMessageHandler
    public void groupMessageRecord(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        event.getArrayMsg ().forEach ((o) -> {
            String message;
            if (CQCodeEnums.image.getType ().equals (o.getType ())) {
                String imgUrl = CommonUtils.getMsgImgUrl (o);
                message = CQCodeUtils.extracted (imgUrl, CQCodeEnums.image);
            } else {
                message = ShiroUtils.jsonToCode (o);
            }
            
            log.info ("消息来源 : 群消息, 群号 : {}, 发送者QQ : {}, 消息 : {}",
                    event.getGroupId (), event.getSender ().getUserId (), message);
        
            String key = String.format (GROUP_MESSAGE_RECORD_KEY, event.getGroupId ());
            MessageNode node = new MessageNode (
                    event.getUserId (),
                    event.getSender ().getNickname (),
                    (long) event.getMessageId (),
                    message
            );
            redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (node));
        });
    }
    
    @PrivateMessageHandler
    public void privateMessageRecord(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        event.getArrayMsg ().forEach ((o) -> {
            String message;
            if (CQCodeEnums.image.getType ().equals (o.getType ())) {
                String imgUrl = CommonUtils.getMsgImgUrl (o);
                message = CQCodeUtils.extracted (imgUrl, CQCodeEnums.image);
            } else {
                message = ShiroUtils.jsonToCode (o);
            }
            
            log.info ("消息来源 : 私聊消息, 发送者QQ : {}, 消息 : {}",
                    event.getUserId (), event.getMessage ());
            String key = String.format (PRIVATE_MESSAGE_RECORD_KEY, event.getUserId ());
            MessageNode node = new MessageNode (
                    event.getUserId (),
                    event.getPrivateSender ().getNickname (),
                    (long) event.getMessageId (),
                    message
            );
            redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (node));
        });
        
    }
}
