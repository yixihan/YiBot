package com.yixihan.yibot.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.dto.action.response.FriendInfoResp;
import com.mikuac.shiro.dto.action.response.GroupInfoResp;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自动处理请求插件
 *
 * <li>加好友</li>
 * <li>邀请入群</li>
 *
 * @author yixihan
 * @date 2022/12/3 15:26
 */
@Slf4j
@Component
public class AutoAddRequestPlugin extends BotPlugin {
    
    private static final String INVITE = "invite";
    
    private static final Long MASTER_ID = 3113788997L;
    
    List<Long> groupIdList = new ArrayList<> ();
    
    List<GroupMemberInfoResp> groupNumberIdList = new ArrayList<> ();
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 处理加好友请求
     */
    @Override
    public int onFriendAddRequest(@NotNull Bot bot, @NotNull FriendAddRequestEvent event) {
        log.info ("收到好友添加请求, userId : {}", event.getUserId ());
        if (getBlackList ().stream ()
                .anyMatch ((o) -> Long.parseLong (o.toString ()) == event.getUserId ())) {
            log.info ("此人在黑名单中，拒绝加好友请求");
            bot.setFriendAddRequest (event.getFlag (), false, null);
            return MESSAGE_IGNORE;
        }
        getGroupNumberList (bot);
        if (groupNumberIdList.stream ().anyMatch ((user) -> user.getUserId () == event.getUserId ())) {
            log.info ("是已在群的群友, 同意加好友请求");
            bot.setFriendAddRequest (event.getFlag (), true, null);
        } else {
            log.info ("未知人物, 拒绝加好友请求");
            bot.setFriendAddRequest (event.getFlag (), false, null);
        }
        return super.onFriendAddRequest (bot, event);
    }
    
    @Override
    public int onGroupAddRequest(@NotNull Bot bot, @NotNull GroupAddRequestEvent event) {
        if (!INVITE.equals (event.getSubType ())) {
            return MESSAGE_IGNORE;
        }
        log.info ("收到加群邀请, 邀请人 : {}", event.getUserId ());
        if (event.getUserId () == MASTER_ID) {
            log.info ("是易老师的邀请, 同意");
            bot.setGroupAddRequest (event.getFlag (), event.getSubType (), true, "");
        } else {
            log.info ("未知人的要求, 不同意");
            ActionRaw actionRaw = bot.setGroupAddRequest (event.getFlag (), event.getSubType (), false, "易老师给我说, 不能同意陌生人的邀请捏");
            if ("failed".equals (actionRaw.getStatus ())) {
                log.info ("已经被拉入群捏, 赶紧退出");
                bot.setGroupLeave (event.getGroupId (), false);
                List<FriendInfoResp> data = bot.getFriendList ().getData ();
                FriendInfoResp friendInfo = data.stream ()
                        .filter ((o) -> o.getUserId () == event.getUserId ()).findFirst ().orElse (null);
                if (friendInfo == null) {
                    log.info ("非好友, 不处理");
                } else {
                    log.info ("是好友, 删好友, 进黑名单");
                    bot.sendPrivateMsg (event.getUserId (), "乱拉小易入群, 大坏蛋, 进黑名单吧你!", false);
                    bot.deleteFriend (friendInfo.getUserId ());
                }
                addBlacklist (event.getUserId ());
            }
        }
        return super.onGroupAddRequest (bot, event);
    }
    
    /**
     * 获取 QQ bot 群列表
     */
    private void getGroupIdList(@NotNull Bot bot) {
        groupIdList = bot.getGroupList ().getData ().stream ().map (GroupInfoResp::getGroupId).collect (Collectors.toList ());
    }
    
    /**
     * 获取 QQ bot 所在所有群的所有群成员信息
     */
    private void getGroupNumberList(@NotNull Bot bot) {
        getGroupIdList (bot);
        for (Long groupId : groupIdList) {
            groupNumberIdList.addAll (bot.getGroupMemberList (groupId).getData ());
        }
    }
    
    /**
     * 添加黑名单
     */
    private void addBlacklist (Long userId) {
        redisTemplate.opsForList ().rightPush ("blacklist", String.valueOf (userId));
    }
    
    /**
     * 获取黑名单列表
     */
    private List<Object> getBlackList () {
        return redisTemplate.opsForList ().range ("blacklist", 0, -1);
    }
}
