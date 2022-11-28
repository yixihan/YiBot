package com.yixihan.yibot.plugin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.constant.PatternConstants;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.CronExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自动提醒插件
 *
 * @author yixihan
 * @date 2022/11/24 19:37
 */
@Slf4j
@Component
public class AutoRemindPlugin extends BotPlugin {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private BotContainer botContainer;
    
    private static final String HELP = "帮助";
    
    private static final String ADD = "添加";
    
    private static final String DEL = "删除";
    
    private static final String LIST = "列表";
    
    private static final String RELOAD = "刷新";
    
    
    /**
     * 自动提醒
     */
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        if (event.getSender ().getUserId ().equals (String.valueOf (event.getSelfId ())) || !event.getMessage ().startsWith (PatternConstants.AUTO_REMIND)) {
            return MESSAGE_IGNORE;
        }
        
        String[] splits = event.getMessage ().split (" ");
        
        String message = CQCodeUtils.extracted (event.getSender ().getUserId (), CQCodeEnums.at);
        
        if (splits.length == 2 && LIST.equals (splits[1])) {
            // 自动提醒 列表
            Map<Object, Object> autoRemind = getAutoRemind (event.getUserId ());
            
            if (CollectionUtil.isEmpty (autoRemind)) {
                bot.sendGroupMsg (event.getGroupId (), message + "你现在没有自动提醒内容哦~", false);
                return MESSAGE_IGNORE;
            }
            StringBuilder sb = new StringBuilder ();
            sb.append ("自动提醒内容如下:\n");
            for (Map.Entry<Object, Object> entry : autoRemind.entrySet ()) {
                sb.append ("corn表达式 : ").append (entry.getKey ()).append (", ").append ("提醒内容 : ").append (entry.getValue ()).append ("\n");
            }
            bot.sendGroupMsg (event.getGroupId (), message + sb, false);
            
            return MESSAGE_IGNORE;
        } else if (splits.length == 2 && HELP.equals (splits[1])) {
            // 自动提醒 帮助
            bot.sendGroupMsg (event.getGroupId (), message + autoRemindHelp (), false);
            return MESSAGE_IGNORE;
        } else if (splits.length == 2 && RELOAD.equals (splits[1])) {
            initAutoRemind ();
            bot.sendGroupMsg (event.getGroupId (), message + "刷新成功!", false);
            return MESSAGE_IGNORE;
        } else if (splits.length < 3) {
            return MESSAGE_IGNORE;
        }
        
        String corn = event.getMessage ().substring (event.getMessage ().indexOf ("<") + 1, event.getMessage ().lastIndexOf (">"));
        String autoMindMsg = event.getMessage ().substring (event.getMessage ().lastIndexOf (">") + 1);
        
        // 自动提醒 添加
        if (ADD.equals (splits[1]) && CronExpression.isValidExpression (corn)) {
            saveAutoRemid (event.getUserId (), corn, autoMindMsg);
            addAutoRemind (bot, event.getUserId (), corn, autoMindMsg);
            bot.sendGroupMsg (event.getGroupId (), message + "添加成功!", false);
            return MESSAGE_IGNORE;
        }
        
        // 自动提醒 删除
        if (DEL.equals (splits[1]) && CronExpression.isValidExpression (corn)) {
            delAutoRemind (event.getUserId (), corn);
            initAutoRemind ();
            bot.sendGroupMsg (event.getGroupId (), message + "删除成功!", false);
            return MESSAGE_IGNORE;
        }
        
        
        return super.onGroupMessage (bot, event);
    }
    
    private String autoRemindHelp() {
        return "自动提醒 帮助系统\n" + "添加自动提醒 : 自动提醒 添加 <[corn表达式]> [提醒语句]\n" + "删除自动提醒 : 自动提醒 删除 <[corn表达式]>\n" + "查看自动提醒列表 : 自动提醒 列表\n" + "tip : corn表达式帮助(https://cron.qqe2.com/)";
    }
    
    /**
     * 将自动提醒内容存入 redis
     *
     * @param userId  用户 QQ
     * @param corn    corn 表达式
     * @param message 提醒内容
     */
    private void saveAutoRemid(Long userId, String corn, String message) {
        String key = String.format ("autoRemind:%s", userId);
        redisTemplate.opsForHash ().put (key, corn, message);
        redisTemplate.opsForHash ().put ("autoRemind", String.valueOf (userId), String.valueOf (userId));
    }
    
    /**
     * 获取用户所有的自动签到内容
     *
     * @param userId 用户 QQ
     */
    private Map<Object, Object> getAutoRemind(Long userId) {
        String key = String.format ("autoRemind:%s", userId);
        return redisTemplate.opsForHash ().entries (key);
    }
    
    /**
     * 获取所有自动提醒的 key
     */
    private List<Object> getAutoRemindKey() {
        return new ArrayList<> (redisTemplate.opsForHash ().entries ("autoRemind").keySet ());
    }
    
    /**
     * 添加自动提醒 定时任务
     *
     * @param bot     机器人
     * @param userId  用户 QQ
     * @param corn    corn 表达式
     * @param message 提醒内容
     */
    private void addAutoRemind(@NotNull Bot bot, Long userId, String corn, String message) {
        CronUtil.schedule (userId + corn, corn, (Task) () -> bot.sendPrivateMsg (userId, message, false));
        // 支持秒级别定时任务
        CronUtil.setMatchSecond (true);
    }
    
    private void delAutoRemind(Long userId, String corn) {
        String key = String.format ("autoRemind:%s", userId);
        redisTemplate.opsForHash ().delete (key, corn);
    }
    
    private Bot getBot() {
        // 机器人账号
        long botId = 2535774265L;
        // 通过机器人账号取出 Bot 对象
        return botContainer.robots.get (botId);
    }
    
    /**
     * 启动项目, 插入保存的自动提醒
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void initAutoRemind() {
        Bot bot = getBot ();
        
        CronUtil.restart ();
        
        log.info (String.valueOf (bot.getSelfId ()));
        
        for (Object obj : getAutoRemindKey ()) {
            long userId = Long.parseLong (obj.toString ());
            Map<Object, Object> autoRemind = getAutoRemind (userId);
            for (Map.Entry<Object, Object> entry : autoRemind.entrySet ()) {
                addAutoRemind (bot, userId, entry.getKey ().toString (), entry.getValue ().toString ());
            }
        }
    }
}
