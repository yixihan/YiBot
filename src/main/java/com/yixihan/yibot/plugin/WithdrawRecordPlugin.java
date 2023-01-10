package com.yixihan.yibot.plugin;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupMsgDeleteNoticeEvent;
import com.mikuac.shiro.dto.event.notice.PrivateMsgDeleteNoticeEvent;
import com.yixihan.yibot.dto.message.MessageNode;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yixihan.yibot.constant.CommonConstants.MASTER_ID;
import static com.yixihan.yibot.constant.RedisKeyConstants.*;

/**
 * 撤回消息记录插件
 *
 * @author yixihan
 * @date 2022/12/4 17:15
 */
@Slf4j
@Component
public class WithdrawRecordPlugin extends BotPlugin {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 群消息撤回记录
     */
    @Override
    public int onGroupMsgDeleteNotice(@NotNull Bot bot, @NotNull GroupMsgDeleteNoticeEvent event) {
        MessageNode node = saveMessageNode (event.getMsgId (), event.getGroupId (), true);
        
        // 发给易老师
        List<MessageNode> list = new ArrayList<> ();
        list.add (new MessageNode (MASTER_ID, "易老师", null, "消息类型 : 群消息"));
        list.add (new MessageNode (MASTER_ID, "易老师", null, "消息来源 : " + event.getGroupId ()));
        list.add (node);
        List<Map<String, Object>> forwardMsg = CQCodeUtils.generateForwardMsg (list);
        bot.sendPrivateForwardMsg (MASTER_ID, forwardMsg);
        return super.onGroupMsgDeleteNotice (bot, event);
    }
    
    /**
     * 私聊消息撤回记录
     */
    @Override
    public int onPrivateMsgDeleteNotice(@NotNull Bot bot, @NotNull PrivateMsgDeleteNoticeEvent event) {
        MessageNode node = saveMessageNode (event.getMsgId (), event.getUserId (), false);
        
        // 发给易老师
        List<MessageNode> list = new ArrayList<> ();
        list.add (new MessageNode (MASTER_ID, "易老师", null, "消息类型 : 私聊消息"));
        list.add (new MessageNode (MASTER_ID, "易老师", null, "消息来源 : " + event.getUserId ()));
        list.add (node);
        List<Map<String, Object>> forwardMsg = CQCodeUtils.generateForwardMsg (list);
        bot.sendPrivateForwardMsg (MASTER_ID, forwardMsg);
        return super.onPrivateMsgDeleteNotice (bot, event);
    }
    
    /**
     * 获取消息列表
     *
     * @param key redis key
     */
    private List<MessageNode> getMessageList(String key) {
        List<Object> list = redisTemplate.opsForList ().range (key, 0, -1);
        return list == null ? Collections.emptyList () : list.stream ().map ((o) -> JSONUtil.toBean (o.toString (), MessageNode.class)).collect (Collectors.toList ());
    }
    
    /**
     * 保存消息
     *
     * @param messageId 消息 id
     * @param id        groupId | userId
     * @param flag      群消息 : true | 私聊消息 : false
     */
    @NotNull
    private MessageNode saveMessageNode(Long messageId, Long id, boolean flag) {
        // 获取撤回消息信息
        String key = String.format (flag ? GROUP_MESSAGE_RECORD_KEY : PRIVATE_MESSAGE_RECORD_KEY, id);
        List<MessageNode> messageList = getMessageList (key);
        MessageNode node = messageList.stream ().filter ((o) -> o.getMessageId ().equals (messageId)).findFirst ().orElse (new MessageNode ());
        
        // 存储进 redis
        key = String.format (flag ? GROUP_WITHDRAW_RECORD_KEY : PRIVATE_WITHDRAW_RECORD_KEY, id);
        redisTemplate.opsForList ().rightPush (key, JSONUtil.toJsonStr (node));
        return node;
    }
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearWithdrawMessage() {
    
    }
}
