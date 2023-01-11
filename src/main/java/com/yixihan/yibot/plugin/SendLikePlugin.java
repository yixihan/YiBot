package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.constant.PatternConstants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 点赞插件
 *
 * @author yixihan
 * @date 2023/1/11 20:41
 */
@Slf4j
@Component
public class SendLikePlugin extends BotPlugin {
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String message = event.getMessage ();
        String sendMessage = null;
        if (message.matches (PatternConstants.SEND_LIKE_PATTERN)) {
            int num;
            try {
                message = message.replaceAll ("[^-|0-9]", "");
                log.info ("message : {}", message);
                num = Integer.parseInt (message);
       
                if (num <= 0) {
                    sendMessage = MsgUtils.builder ()
                            .reply (event.getMessageId ())
                            .text ("要我踩你两jio?")
                            .build ();
                } else {
                    ActionRaw actionRaw = bot.sendLike (event.getUserId (), num);
                    log.info ("actionRaw : {}", actionRaw);
                    sendMessage = MsgUtils.builder ()
                            .reply (event.getMessageId ())
                            .text ("点赞成功")
                            .build ();
                }
                
            } catch (NumberFormatException e) {
                sendMessage = MsgUtils.builder ()
                        .reply (event.getMessageId ())
                        .text ("?")
                        .build ();
            }
        }
        if (StrUtil.isNotBlank (sendMessage)) {
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        }
        return super.onGroupMessage (bot, event);
    }
}
