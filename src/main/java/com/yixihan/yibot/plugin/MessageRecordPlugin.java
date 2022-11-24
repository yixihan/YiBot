package com.yixihan.yibot.plugin;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.emoji.EmojiUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
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
 * 消息记录插件
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
    public void getGroupMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event, @NotNull Matcher matcher) {
        // 获取信息
        String[] splits = event.getMessage ().split (" ");
        Long groupId = Long.parseLong (splits[2]);
        Date endTime = DateUtil.parse (splits.length >= 4 ? splits[3] : DateUtil.now ());
        Date startTime = splits.length >= 5 ? DateUtil.parse (splits[4]) : DateUtil.offsetDay (new Date (), -1);


        List<String> msgList = groupMessageService.getGroupMessages (groupId, startTime, endTime);
        msgList.replaceAll (ShiroUtils::unescape);
        msgList.replaceAll (EmojiUtil::toUnicode);
        for (String msg : msgList) {
            msg = EmojiUtil.toUnicode (ShiroUtils.unescape (msg));
            // TODO CQ code


        }
        // TODO 需要修改 (自定义工具类)
        // 构建合并转发消息（selfId为合并转发消息显示的账号，nickname为显示的发送者昵称，msgList为消息列表）
        List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                event.getPrivateSender ().getUserId (),
                event.getPrivateSender ().getNickname (),
                msgList
        );

        // 发送合并转发内容到私聊（userId为要发送的好友账号）
        bot.sendPrivateForwardMsg(event.getPrivateSender ().getUserId (), forwardMsg);
    }

    /**
     * 记录群聊信息， 并保存到数据库中
     */
    @GroupMessageHandler(at = AtEnum.OFF)
    public void saveGroupMessage(@NotNull GroupMessageEvent event) {
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
        message.setMessage (EmojiUtil.toAlias (ShiroUtils.escape (event.getMessage ())));
        try {
            groupMessageService.save (message);
        } catch (Exception e) {
            log.warn ("数据库插入失败, 原因 : {}, message : {}", e.getMessage (), e.getMessage ());
        }
    }

    /**
     * 记录私聊信息， 并保存到数据库中
     */
    @PrivateMessageHandler
    public void savePrivateMessage(@NotNull PrivateMessageEvent event) {
        PrivateMessage message = new PrivateMessage ();
        message.setMessageId ((long) event.getMessageId ());
        message.setSubType (event.getSubType ());
        message.setTmpSource (event.getTempSource ());
        message.setGroupId (event.getPrivateSender ().getGroupId ());
        message.setUserId (event.getPrivateSender ().getUserId ());
        message.setNickname (event.getPrivateSender ().getNickname ());
        message.setMessage (EmojiUtil.toAlias (ShiroUtils.escape (event.getMessage ())));
        try {
            privateMessageService.save (message);
        } catch (Exception e) {
            log.warn ("数据库插入失败, 原因 : {}, message : {}", e.getMessage (), e.getMessage ());
        }
    }
}
