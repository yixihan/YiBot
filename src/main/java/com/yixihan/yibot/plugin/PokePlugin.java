package com.yixihan.yibot.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 戳一戳插件
 *
 * @author yixihan
 * @date 2022/11/28 20:12
 */
@Slf4j
@Component
public class PokePlugin extends BotPlugin {
    
    private static final Map<Integer, String> MESSAGE_MAP = new HashMap<> ();
    
    private static final Random RD = new Random ();
    
    private static final Integer MAX_CNT = 6;
    
    
    static {
        MESSAGE_MAP.put (0, "憋载这理发店(╬▔皿▔)╯");
        MESSAGE_MAP.put (1, "戳坏了你得赔哦( ͡• ͜ʖ ͡• )");
        MESSAGE_MAP.put (2, "再戳我就生气啦ヽ（≧□≦）ノ");
        MESSAGE_MAP.put (3, "这么喜欢戳我,一定是因为喜欢易老师吧(￣_,￣ )");
        MESSAGE_MAP.put (4, "再戳我就祝你期末考59分<( ￣^￣)(θ(θ☆( >_<<( ￣^￣)(θ(θ☆( >_<");
        MESSAGE_MAP.put (5, "请到易老师床上发癫捏");
    }
    
    /**
     * 群聊戳一戳事件
     */
    @Override
    public int onGroupPokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        // 如果是自己戳自己或戳的别人则不执行
        if (event.getUserId () == event.getSelfId () || event.getTargetId () != event.getSelfId ()) {
            return MESSAGE_IGNORE;
        }
        String message = CQCodeUtils.extracted (
                String.valueOf (event.getUserId ()),
                CQCodeEnums.poke
        );
        bot.sendGroupMsg (event.getGroupId (), message, false);
        bot.sendGroupMsg (event.getGroupId (), getRandomMessage (), false);
        return super.onGroupPokeNotice (bot, event);
    }
    
    
    /**
     * 私聊戳一戳事件
     */
    @Override
    public int onPrivatePokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        // 如果是自己戳自己或戳的别人则不执行
        if (event.getUserId () == event.getSelfId () || event.getTargetId () != event.getSelfId ()) {
            return MESSAGE_IGNORE;
        }
        String message = CQCodeUtils.extracted (
                String.valueOf (event.getUserId ()),
                CQCodeEnums.poke
        );
        bot.sendPrivateMsg (event.getUserId (), message, false);
        bot.sendPrivateMsg (event.getUserId (), getRandomMessage (), false);
        return super.onPrivatePokeNotice (bot, event);
        
    }
    
    /**
     * 获取随机消息
     */
    private String getRandomMessage () {
        return MESSAGE_MAP.get (RD.nextInt (MAX_CNT));
    }
}
