package com.yixihan.yibot.plugin;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.yixihan.yibot.pojo.GroupMessage;
import com.yixihan.yibot.pojo.PrivateMessage;
import com.yixihan.yibot.service.GroupMessageService;
import com.yixihan.yibot.service.PrivateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.regex.Matcher;

/**
 * 示例插件
 *
 * @author yixihan
 * @date 2022/11/23 22:12
 */
@Slf4j
@Shiro
@Component
public class ExamplePlugin {

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private GroupMessageService groupMessageService;

    /**
     * 符合 cmd 正则表达式的消息会被响应
     * @param bot
     * @param event
     * @param matcher
     */
    @PrivateMessageHandler(cmd = "hi")
    public void fun1(@NotNull Bot bot, @NotNull PrivateMessageEvent event, @NotNull Matcher matcher) {
        // 构建消息
        String sendMsg = MsgUtils.builder().face(66).text("Hello, this is shiro demo.").build();
        // 发送私聊消息
        bot.sendPrivateMsg(event.getUserId(), sendMsg, false);
        PrivateMessage message = new PrivateMessage ();
        message.setMessageId ((long) event.getMessageId ());
        message.setSubType (event.getSubType ());
        message.setTmpSource (event.getTempSource ());
        message.setGroupId (event.getPrivateSender ().getGroupId ());
        message.setUserId (event.getPrivateSender ().getUserId ());
        message.setNickname (event.getPrivateSender ().getNickname ());
        message.setMessage (ShiroUtils.escape (event.getMessage ()));
        privateMessageService.save (message);
        log.info (event.toString ());
    }

    /**
     * 如果 at 参数设定为 AtEnum.NEED 则只有 at 了机器人的消息会被响应
     *
     * @param event
     */
    @GroupMessageHandler(at = AtEnum.OFF)
    public void fun2(@NotNull GroupMessageEvent event) {
        // 以注解方式调用可以根据自己的需要来为方法设定参数
        // 例如群组消息可以传递 GroupMessageEvent, Bot, Matcher 多余的参数会被设定为 null
        log.info(event.getMessage());
        log.info (event.toString ());
        GroupMessage message = new GroupMessage ();
        message.setMessageId ((long) event.getMessageId ());
        message.setSubType (event.getSubType ());
        message.setGroupId (event.getGroupId ());
        if (event.getSender () != null) {
            message.setUserId (Long.valueOf (event.getSender ().getUserId ()));
            message.setNickname (event.getSender ().getNickname ());
        } else if (event.getAnonymous () != null) {
            message.setUserId (event.getAnonymous ().getId ());
            message.setNickname (event.getAnonymous ().getName ());
        }
        message.setMessage (ShiroUtils.escape (event.getMessage ()));
        groupMessageService.save (message);
    }

    /**
     * 同时监听群组及私聊消息 并根据消息类型（私聊，群聊）回复
     *
     * @param bot
     * @param event
     */
    @MessageHandler
    public void fun3(@NotNull Bot bot, @NotNull AnyMessageEvent event) {
        bot.sendMsg(event, "hello", false);
    }

}
