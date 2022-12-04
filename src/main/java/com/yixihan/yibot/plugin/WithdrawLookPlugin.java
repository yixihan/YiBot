package com.yixihan.yibot.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupMsgDeleteNoticeEvent;
import com.mikuac.shiro.dto.event.notice.PrivateMsgDeleteNoticeEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 撤回消息查看插件
 *
 * @author yixihan
 * @date 2022/12/4 17:15
 */
@Slf4j
@Component
public class WithdrawLookPlugin extends BotPlugin {
    
    @Override
    public int onGroupMsgDeleteNotice(@NotNull Bot bot, @NotNull GroupMsgDeleteNoticeEvent event) {
        return super.onGroupMsgDeleteNotice (bot, event);
    }
    
    @Override
    public int onPrivateMsgDeleteNotice(@NotNull Bot bot, @NotNull PrivateMsgDeleteNoticeEvent event) {
        return super.onPrivateMsgDeleteNotice (bot, event);
    }
}
