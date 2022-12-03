package com.yixihan.yibot.plugin;

import com.mikuac.shiro.annotation.MessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

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

    @MessageHandler
    public void getMsg(@NotNull Bot bot, @NotNull AnyMessageEvent event) {
        log.info ("sender : {}, message : {}", event.getSender ().getUserId (), event.getMessage ());
    }
}
