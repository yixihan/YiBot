package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.yixihan.yibot.pojo.RepeatNode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 复读插件
 *
 * @author yixihan
 * @date 2022/11/24 21:00
 */
@Slf4j
@Shiro
@Component
public class RepeatPlugin {
    private static final int MAX_CNT = 3;
    private final Map<Long, RepeatNode> repeatNodeMap = new ConcurrentHashMap<> ();

    @GroupMessageHandler(at = AtEnum.OFF)
    public void GroupRepeatMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        RepeatNode node = repeatNodeMap.getOrDefault (event.getGroupId (), new RepeatNode ());

        if (StrUtil.isBlank (node.getLastMessage ())) {
            node.setLastMessage (event.getMessage ());
            node.setFlag (false);
            node.setCnt (1);
        } else {
            if (node.getLastMessage ().equals (event.getMessage ())) {
                node.setCnt (node.getCnt () + 1);
                log.info ("message : {}, cnt : {}", event.getMessage (), node.getCnt ());
                if (node.getCnt () >= MAX_CNT && !node.isFlag ()) {
                    bot.sendGroupMsg (event.getGroupId (), event.getMessage (), false);
                    node.setFlag (true);
                }
            } else {
                node.setFlag (false);
                node.setCnt (0);
                node.setLastMessage (event.getMessage ());
            }
        }
        repeatNodeMap.put (event.getGroupId (), node);
    }
}
