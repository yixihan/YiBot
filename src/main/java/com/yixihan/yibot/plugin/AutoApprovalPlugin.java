package com.yixihan.yibot.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.action.response.GroupMemberInfoResp;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 加群自动审批
 *
 * @author yixihan
 * @date 2022/11/28 22:59
 */
@Slf4j
@Component
public class AutoApprovalPlugin extends BotPlugin {
    
    /**
     * qq bot 群
     */
    private static final Long BOT_GROUP = 715932474L;
    
    /**
     * 后花园群
     */
    public static final Long GARDEN_GROUP = 604675398L;
    
    /**
     * 技术群
     */
    private static final Long TECHNOLOGY_GROUP = 903839576L;
    
    private List<GroupMemberInfoResp> memberList;
    
    /**
     * 入群自动审批
     */
    @Override
    public int onGroupAddRequest(@NotNull Bot bot, @NotNull GroupAddRequestEvent event) {
        
        if (event.getGroupId () != BOT_GROUP) {
            return MESSAGE_IGNORE;
        }
    
        initUserList (bot);
        long count = memberList.stream ().filter (groupMemberInfoResp -> groupMemberInfoResp.getUserId () == event.getUserId ()).count ();
        if (count > 0) {
            bot.sendGroupMsg (event.getGroupId (), "是老朋友捏", false);
            bot.setGroupAddRequest (event.getFlag (), event.getSubType (), true, "");
        }
        
        return super.onGroupAddRequest (bot, event);
    }
    
    private void initUserList(@NotNull Bot bot) {
        memberList = new ArrayList<> ();
        memberList.addAll (bot.getGroupMemberList (GARDEN_GROUP).getData ());
        log.info ("memberList : " + memberList);
        memberList.addAll (bot.getGroupMemberList (TECHNOLOGY_GROUP).getData ());
        log.info ("memberList : " + memberList);
    }
}
