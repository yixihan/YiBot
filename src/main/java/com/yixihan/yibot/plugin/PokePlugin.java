package com.yixihan.yibot.plugin;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
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
    
    private static final Map<Integer, String> POKE_MESSAGE_MAP = new HashMap<> ();
    private static final Map<Integer, String> AT_MESSAGE_MAP = new HashMap<> ();
    
    private static final Random RD = new Random ();
    
    private static final Integer MAX_CNT = 6;
    
    
    static {
        POKE_MESSAGE_MAP.put (0, "憋载这理发店(╬▔皿▔)╯");
        POKE_MESSAGE_MAP.put (1, "戳坏了你得赔哦( ͡• ͜ʖ ͡• )");
        POKE_MESSAGE_MAP.put (2, "再戳我就生气啦ヽ（≧□≦）ノ");
        POKE_MESSAGE_MAP.put (3, "这么喜欢戳我,一定是因为喜欢易老师吧(￣_,￣ )");
        POKE_MESSAGE_MAP.put (4, "再戳我就祝你期末考59分<( ￣^￣)(θ(θ☆( >_<<( ￣^￣)(θ(θ☆( >_<");
        POKE_MESSAGE_MAP.put (5, "请到易老师床上发癫捏");
        AT_MESSAGE_MAP.put (0, "at我干嘛捏?");
        AT_MESSAGE_MAP.put (1, "没活儿了可以咬打火机哟~");
        AT_MESSAGE_MAP.put (2,"不去学习在这儿水群???");
        AT_MESSAGE_MAP.put (3,"如果实在太无聊了建议去和易老师私聊捏");
        AT_MESSAGE_MAP.put (4,"没事做我这边建议去玩ChatGPT捏");
        AT_MESSAGE_MAP.put (5,"你是要在我这理发店吗?");
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
        bot.sendGroupMsg (event.getGroupId (), getRandomMessage (CQCodeEnums.poke), false);
        return super.onGroupPokeNotice (bot, event);
    }
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        event.getArrayMsg ().forEach ((o) -> {
            if (CQCodeEnums.at.getType ().equals (o.getType ())) {
                if (o.getData ().get ("qq").equals (String.valueOf (bot.getSelfId ()))) {
                    bot.sendGroupMsg (event.getGroupId (), getRandomMessage (CQCodeEnums.at), false);
                }
            }
        });
        
        return super.onGroupMessage (bot, event);
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
        bot.sendPrivateMsg (event.getUserId (), getRandomMessage (CQCodeEnums.poke), false);
        return super.onPrivatePokeNotice (bot, event);
        
    }
    
    /**
     * 获取随机消息
     *
     * @param type {@link CQCodeEnums}
     */
    private String getRandomMessage (CQCodeEnums type) {
        if (CQCodeEnums.poke.equals (type)) {
            return POKE_MESSAGE_MAP.get (RD.nextInt (AT_MESSAGE_MAP.size ()));
        } else if (CQCodeEnums.at.equals (type)) {
            return AT_MESSAGE_MAP.get (RD.nextInt (AT_MESSAGE_MAP.size ()));
        } else {
            return null;
        }
    }
    
    /**
     * 消息输出
     *
     * @param info 消息
     */
    private void messageOutput(@NotNull Bot bot, @NotNull GroupMessageEvent event, String info) {
        String message;
        message = CQCodeUtils.extracted (event.getSender ().getUserId (),
                CQCodeEnums.at);
        bot.sendGroupMsg (event.getGroupId (), message + info, false);
    }
}
