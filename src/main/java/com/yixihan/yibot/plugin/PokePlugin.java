package com.yixihan.yibot.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 戳一戳插件
 *
 * @author yixihan
 * @date 2022/11/28 20:12
 */
@Slf4j
@Component
public class PokePlugin extends BotPlugin {
    
    /**
     * 群聊戳一戳事件
     */
    @Override
    public int onGroupPokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        // 如果是自己戳自己或戳的别人则不执行
        if (event.getUserId () == event.getSelfId () || event.getTargetId () != event.getSelfId ()) {
            return MESSAGE_IGNORE;
        }
        String message = CQCodeUtils.extracted (
                String.valueOf (event.getUserId ()),
                CQCodeEnums.poke
        );
        bot.sendGroupMsg (event.getGroupId (), message, false);
        return super.onGroupPokeNotice (bot, event);
    }
    
    /**
     * 私聊戳一戳事件
     */
    @Override
    public int onPrivatePokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        // 如果是自己戳自己或戳的别人则不执行
        if (event.getUserId () == event.getSelfId () || event.getTargetId () != event.getSelfId ()) {
            return MESSAGE_IGNORE;
        }
        String message = CQCodeUtils.extracted (
                String.valueOf (event.getUserId ()),
                CQCodeEnums.poke
        );
        bot.sendPrivateMsg (event.getUserId (), message, false);
        return super.onPrivatePokeNotice (bot, event);
        
    }
}
