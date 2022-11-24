package com.yixihan.yibot.plugin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.yixihan.yibot.pojo.GroupMessage;
import com.yixihan.yibot.pojo.PrivateMessage;
import com.yixihan.yibot.service.GroupMessageService;
import com.yixihan.yibot.service.PrivateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;

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

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private GroupMessageService groupMessageService;

    @MessageHandler
    public void testCQCode (@NotNull Bot bot, @NotNull AnyMessageEvent event) {
        log.info (toString (event));
        if (!"3113788997".equals (event.getSender ().getUserId ())) {
            return;
        }
        List<String> msgImgUrlList = ShiroUtils.getMsgImgUrlList (event.getArrayMsg ());
        List<String> msgList = new ArrayList<> ();
        if (!CollectionUtil.isEmpty (msgImgUrlList)) {
            for (String msgImgUrl : msgImgUrlList) {
                MsgChainBean msb = new MsgChainBean ();
                msb.setType ("image");
                HashMap<String, String> map = new HashMap<> ();
                map.put ("file", msgImgUrl);
                msb.setData (map);
                msgList.add (ShiroUtils.jsonToCode (msb));
            }
            List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                    Long.parseLong (event.getSender ().getUserId ()),
                    event.getSender ().getNickname (),
                    msgList
            );
            bot.sendPrivateForwardMsg (
                    Long.parseLong (event.getSender ().getUserId ()),
                    forwardMsg
            );
        }

    }


    private String toString (AnyMessageEvent event) {
        return new StringBuilder ("\n").append ("messageId : ").append (event.getMessageId ()).append ("\n")
                .append ("subType : ").append (event.getSubType ()).append ("\n")
                .append ("groupId : ").append (event.getGroupId ()).append ("\n")
                .append ("message : ").append (event.getMessage ()).append ("\n")
                .append ("sender : ").append (event.getSender ()).append ("\n")
                .append ("anonymous : ").append (event.getAnonymous ())
                .toString ();
    }

}
