package com.yixihan.yibot.plugin;

import cn.hutool.core.date.DateUtil;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.yixihan.yibot.constant.PatternConstants;
import com.yixihan.yibot.pojo.GroupMessage;
import com.yixihan.yibot.pojo.PrivateMessage;
import com.yixihan.yibot.service.GroupMessageService;
import com.yixihan.yibot.service.PrivateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/23 23:28
 */
@Slf4j
@Shiro
@Component
public class MessageRecordPlugin {

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private GroupMessageService groupMessageService;

    @PrivateMessageHandler(cmd = "^消息记录 群聊 " + PatternConstants.GROUP_ID_PATTERN + ".*$")
    public void fun1(@NotNull Bot bot, @NotNull PrivateMessageEvent event, @NotNull Matcher matcher) {
        // 获取信息
        String[] splits = event.getMessage ().split (" ");
        Long groupId = Long.parseLong (splits[2]);
        Date endTime = DateUtil.parse (splits.length >= 4 ? splits[3] : DateUtil.now ());
        Date startTime = splits.length >= 5 ? DateUtil.parse (splits[4]) : DateUtil.offsetDay (new Date (), -1);


        List<String> msgList = groupMessageService.getGroupMessages (groupId, startTime, endTime);
        msgList.replaceAll (ShiroUtils::unescape);
        // 构建合并转发消息（selfId为合并转发消息显示的账号，nickname为显示的发送者昵称，msgList为消息列表）
        List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                event.getPrivateSender ().getUserId (),
                event.getPrivateSender ().getNickname (),
                msgList
        );

        // 发送合并转发内容到私聊（userId为要发送的好友账号）
        bot.sendPrivateForwardMsg(event.getPrivateSender ().getUserId (), forwardMsg);
    }
}
