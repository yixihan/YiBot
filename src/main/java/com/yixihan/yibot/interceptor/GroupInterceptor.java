package com.yixihan.yibot.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.yixihan.yibot.config.GroupConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 群权限控制
 *
 * @author yixihan
 * @date 2023/1/12 19:42
 */
@Slf4j
@Component
public class GroupInterceptor implements BotMessageEventInterceptor {
    
    @Resource
    private GroupConfig config;
    
    
    @Override
    public boolean preHandle(Bot bot, MessageEvent sourceEvent) {
        String sendMessage;
        if (sourceEvent instanceof GroupMessageEvent) {
            GroupMessageEvent event = BeanUtil.toBean (sourceEvent, GroupMessageEvent.class);
            if (event.getMessage ().matches ("^(色色|瑟瑟|涩涩).*$")
                    && !config.getSetuList ().contains (event.getGroupId ())) {
                sendMessage = MsgUtils.builder ()
                        .reply (event.getMessageId ())
                        .text ("不准色色！")
                        .face (38)
                        .build ();
                bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
                return false;
            } else {
                return config.getCommonList ().contains (event.getGroupId ());
            }
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(Bot bot, MessageEvent event) {
    
    }
}
