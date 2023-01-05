package com.yixihan.yibot.plugin;

import cn.hutool.core.date.DateUtil;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 时间获取插件
 *
 * @author yixihan
 * @date 2023/1/5 9:25
 */
@Slf4j
@Component
public class DatePlugin extends BotPlugin {
    
    @Override
    public int onAnyMessage(@NotNull Bot bot, @NotNull AnyMessageEvent event) {
        if (event.getMessage ().equals ("date")) {
            String now = DateUtil.format (new Date (), "yyyy-MM-dd HH:mm:ss");
            bot.sendMsg (event, now, true);
        }
        
        return super.onAnyMessage (bot, event);
    }
}
