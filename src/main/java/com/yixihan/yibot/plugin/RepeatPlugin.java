package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/24 21:00
 */
@Slf4j
@Shiro
@Component
public class RepeatPlugin {

    private Map<Long, RepeatNode> repeatNodeMap = new ConcurrentHashMap<> ();

    private static final int MAX_CNT = 3;

    @GroupMessageHandler(at = AtEnum.OFF)
    public void GroupRepeatMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        RepeatNode node = repeatNodeMap.getOrDefault (event.getGroupId (), new RepeatNode ());

        if (StrUtil.isBlank (node.lastMessage)) {
            node.lastMessage = event.getMessage ();
            node.flag = false;
            node.cnt++;
        } else {
            if (node.lastMessage.equals (event.getMessage ())) {
                node.cnt++;
                log.info ("message : {}, cnt : {}", event.getMessage (), node.cnt);
                if (node.cnt >= MAX_CNT && !node.flag) {
                    bot.sendGroupMsg (event.getGroupId (), event.getMessage (), false);
                    node.flag = true;
                }
            } else {
                node.flag = false;
                node.cnt = 0;
                node.lastMessage = event.getMessage ();
            }
        }
        repeatNodeMap.put (event.getGroupId (), node);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class RepeatNode {

        private String lastMessage;

        private int cnt;

        private boolean flag;
    }
}
