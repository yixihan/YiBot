package com.yixihan.yibot.plugin.check;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.config.GroupConfig;
import com.yixihan.yibot.constant.NumberConstants;
import com.yixihan.yibot.dto.cordcloud.CordCloud;
import com.yixihan.yibot.dto.cordcloud.CordCloudMsg;
import com.yixihan.yibot.dto.cordcloud.User;
import com.yixihan.yibot.properties.CordCloudProperties;
import com.yixihan.yibot.serivce.MailSendService;
import com.yixihan.yibot.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.yixihan.yibot.constant.CommonConstants.CORD_CLOUD_WORD_ONE;

/**
 * cordCloud 自动签到插件
 *
 * @author yixihan
 * @date 2023/1/13 23:42
 */
@Slf4j
@Component
public class CordCloudPlugin extends BotPlugin {
    
    @Resource
    private GroupConfig config;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private BotContainer botContainer;
    
    @Resource
    private CordCloudProperties prop;
    
    @Resource
    private MailSendService mailSendService;
    
    public Bot getBot() {
        // 机器人账号
        long botId = 2535774265L;
        // 通过机器人账号取出 Bot 对象
        return botContainer.robots.get (botId);
    }
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        // 全校校验
        if (!config.getAutoCheckList ().contains (event.getGroupId ())) {
            return super.onGroupMessage (bot, event);
        }
        String message = event.getMessage ();
        String sendMessage;
        if (message.startsWith (CORD_CLOUD_WORD_ONE + " help")) {
            // 帮助
            bot.sendGroupMsg (event.getGroupId (), getHelpMsg (event.getMessageId ()), false);
        } else if (message.startsWith (CORD_CLOUD_WORD_ONE + " modify")) {
            // 添加用户
            String result = modifyUser (message.split (" "));
            sendMessage = MsgUtils
                    .builder ()
                    .reply (event.getMessageId ())
                    .text (result)
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        } else if (message.startsWith (CORD_CLOUD_WORD_ONE + " del")) {
            // 删除用户
            String result = delUser (message.split (" "));
            sendMessage = MsgUtils
                    .builder ()
                    .reply (event.getMessageId ())
                    .text (result)
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        } else if (message.equals (CORD_CLOUD_WORD_ONE + " list")) {
            // 展示用户列表
            String result = listUser ();
            sendMessage = MsgUtils
                    .builder ()
                    .reply (event.getMessageId ())
                    .text (result)
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        } else if (message.startsWith (CORD_CLOUD_WORD_ONE + " check")) {
            String result = checkNow (message.split (" "));
            sendMessage = MsgUtils
                    .builder ()
                    .reply (event.getMessageId ())
                    .text (result)
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        }
        return super.onGroupMessage (bot, event);
    }
    
    /**
     * 列表展示 cordCloud 用户
     *
     * @return 返回消息
     */
    private String listUser() {
        StringBuilder sb = new StringBuilder ();
        sb.append ("登录邮箱 | 启动自动签到 | 自动邮箱提醒 | 提醒邮箱 | 自动QQ提醒 | 提醒QQ\n");
        Set<Map.Entry<Object, Object>> entrySet = redisTemplate.opsForHash ().entries (prop.getCordCloudName ()).entrySet ();
        if (CollectionUtil.isEmpty (entrySet)) {
            return "没有用户捏~";
        }
        for (Map.Entry<Object, Object> entry : entrySet) {
            CordCloud cordCloud = JSONUtil.toBean (entry.getValue ().toString (), CordCloud.class);
            sb.append (cordCloud.getUser ().getEmail ()).append (" | ")
                    .append (cordCloud.getIsCheckIn ()).append (" | ")
                    .append (cordCloud.getIsSendEmail ()).append (" | ")
                    .append (cordCloud.getSendEmail ()).append (" | ")
                    .append (cordCloud.getIsSendQQMsg ()).append (" | ")
                    .append (cordCloud.getSendQQ ()).append ("\n");
        }
        
        return sb.toString ();
    }
    
    /**
     * 删除 cordCloud 用户
     *
     * @param params 参数
     * @return 返回消息
     */
    private String delUser(@NotNull String[] params) {
        String email = params[NumberConstants.TWO];
        if (StrUtil.isBlank (email)) {
            return "传的什么玩意儿? 看看帮助";
        }
        
        redisTemplate.opsForHash ().delete (prop.getCordCloudName (), email);
        return "删除账户成功!";
    }
    
    /**
     * 修改 cordCloud 用户
     *
     * @param params 参数
     * @return 返回消息
     */
    private String modifyUser(@NotNull String[] params) {
        try {
            if (params.length == 4) {
                if (!"false".equals (params[3])) {
                    return "传的什么玩意儿? 看看帮助";
                }
                // 设置不自动签到
                User user = new User ();
                user.setEmail (params[2]);
                CordCloud cordCloud = new CordCloud (user);
                cordCloud.setIsCheckIn (false);
                JSON json = JSONUtil.parse (cordCloud);
                redisTemplate.opsForHash ().put (prop.getCordCloudName (), params[2], json);
                return "修改成功";
            } else if (params.length == 7) {
                if (!"true".equals (params[3]) || !"false".equals (params[5]) || !"false".equals (params[6])) {
                    return "传的什么玩意儿? 看看帮助";
                }
                // 设置自动签到 邮箱不提醒 qq不提醒
                User user = new User ();
                user.setEmail (params[2]);
                user.setPasswd (params[4]);
                CordCloud cordCloud = new CordCloud (user);
                cordCloud.setIsCheckIn (true);
                CordCloudMsg msg = loginCordCloud (user);
                if (msg.getRet () == 0) {
                    return msg.getMsg ();
                }
                JSON json = JSONUtil.parse (cordCloud);
                redisTemplate.opsForHash ().put (prop.getCordCloudName (), params[2], json);
                return "修改成功";
            } else if (params.length == 8) {
                if ("true".equals (params[3]) && "true".equals (params[5]) && "false".equals (params[7])) {
                    // 设置自动签到 邮箱提醒 qq不提醒
                    User user = new User ();
                    user.setEmail (params[2]);
                    user.setPasswd (params[4]);
                    CordCloud cordCloud = new CordCloud (user);
                    cordCloud.setIsCheckIn (true);
                    cordCloud.setIsSendEmail (true);
                    cordCloud.setSendEmail (params[6]);
                    CordCloudMsg msg = loginCordCloud (user);
                    if (msg.getRet () == 0) {
                        return msg.getMsg ();
                    }
                    JSON json = JSONUtil.parse (cordCloud);
                    redisTemplate.opsForHash ().put (prop.getCordCloudName (), params[2], json);
                    return "修改成功";
                } else if ("true".equals (params[3]) && "false".equals (params[5]) && "true".equals (params[6])) {
                    // 设置自动签到 邮箱不提醒 qq提醒
                    User user = new User ();
                    user.setEmail (params[2]);
                    user.setPasswd (params[4]);
                    CordCloud cordCloud = new CordCloud (user);
                    cordCloud.setIsCheckIn (true);
                    cordCloud.setIsSendQQMsg (true);
                    cordCloud.setSendQQ (Long.valueOf (params[7]));
                    CordCloudMsg msg = loginCordCloud (user);
                    if (msg.getRet () == 0) {
                        return msg.getMsg ();
                    }
                    JSON json = JSONUtil.parse (cordCloud);
                    redisTemplate.opsForHash ().put (prop.getCordCloudName (), params[2], json);
                    return "修改成功";
                } else {
                    return "传的什么玩意儿? 看看帮助";
                }
            } else if (params.length == 9) {
                if ("true".equals (params[3]) && "true".equals (params[5]) && "true".equals (params[7])) {
                    // 设置自动签到 邮箱提醒 qq提醒
                    User user = new User ();
                    user.setEmail (params[2]);
                    user.setPasswd (params[4]);
                    CordCloud cordCloud = new CordCloud (user);
                    cordCloud.setIsCheckIn (true);
                    cordCloud.setIsSendEmail (true);
                    cordCloud.setSendEmail (params[6]);
                    cordCloud.setIsSendQQMsg (true);
                    cordCloud.setSendQQ (Long.valueOf (params[8]));
                    CordCloudMsg msg = loginCordCloud (user);
                    if (msg.getRet () == 0) {
                        return msg.getMsg ();
                    }
                    JSON json = JSONUtil.parse (cordCloud);
                    redisTemplate.opsForHash ().put (prop.getCordCloudName (), params[2], json);
                    return "修改成功";
                } else {
                    return "传的什么玩意儿? 看看帮助";
                }
            } else {
                return "传的什么玩意儿? 看看帮助";
            }
        } catch (Exception e) {
            log.warn ("出现错误 : {}", e.getMessage ());
            return e.getMessage ();
        }
    }
    
    /**
     * 登录 cordCloud
     *
     * @param user cordCloud 登录用户信息
     * @return cordCloud 通用返回信息
     * @throws RuntimeException 响应状态码不为 200 时抛出
     */
    private CordCloudMsg loginCordCloud(@NotNull User user) {
        String form = JSONUtil.toJsonStr (JSONUtil.parseObj (user, false, true));
        log.info (form);
        HttpResponse response = HttpRequest.post (prop.getCordCloudLogin ()).body (form).execute ();
        if (response.getStatus () == 200) {
            return JSONUtil.toBean (JSONUtil.parseObj (response.body ()), CordCloudMsg.class);
        } else {
            log.warn ("未知错误!");
            log.warn (response.body ());
            throw new RuntimeException ("登录失败, 遇到未知错误, 请联系管理员!");
        }
    }
    
    /**
     * 立即签到
     *
     * @param params 参数
     * @return 返回消息
     */
    private String checkNow(@NotNull String[] params) {
        if (params.length == 2) {
            autoCheckInCordCloud ();
            return "操作成功";
        } else {
            return "传的什么玩意儿? 看看帮助";
        }
    }
    
    /**
     * 构建帮助菜单信息
     */
    private String getHelpMsg(int messageId) {
        return MsgUtils.builder ()
                .reply (messageId)
                .text ("cordCloud 自动签到帮助菜单:\n"
                        + "唤起帮助菜单: cordCloud help\n"
                        + "修改用户 : cordCloud modify [cordCloud登录邮箱] [是否自动签到(true|false)] <cordCloud登录密码> "
                        + "[是否接受自动邮件提醒(true|false)] <自动提醒邮箱> [是否接受bot自动QQ消息提醒(true|false)] <自动提醒QQ>\n"
                        + "eg: cordCloud modify 123@163.com false\n"
                        + "eg: cordCloud modify 123@163.com true 123456 false false\n"
                        + "eg: cordCloud modify 123@163.com true 123456 true 123@163.com false\n"
                        + "eg: cordCloud modify 123@163.com true 123456 false true 123456789\n"
                        + "eg: cordCloud modify 123@163.com true 123456 true 123@163.com true 123456789\n"
                        + "ps: QQ消息提醒需加Bot好友\n"
                        + "删除用户 : cordCloud del [cordCloud登录邮箱]\n"
                        + "立即签到 : cordCloud check [cordCloud登录邮箱]")
                .build ();
        
    }
    
    
    /**
     * cordCloud 自动签到
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void autoCheckInCordCloud() {
        Map<Object, Object> map = redisTemplate.opsForHash ().entries (prop.getCordCloudName ());
        Bot bot = getBot ();
        for (Map.Entry<Object, Object> entry : map.entrySet ()) {
            ThreadUtil.execute (() -> {
                CordCloud cordCloud = BeanUtil.toBean (entry.getValue (), CordCloud.class);
                log.info ("用户 " + cordCloud.getUser ().getEmail () + " 是否已开启自动签到 : " + cordCloud.getIsCheckIn ());
                if (cordCloud.getIsCheckIn ()) {
                    log.info ("开始自动签到用户 " + cordCloud.getSendEmail () + " ...");
                    checkInCordCloud (cordCloud, bot);
                    log.info ("用户 " + cordCloud.getSendEmail () + " 自动签到完成...");
                }
            });
        }
    }
    
    /**
     * 签到 cordCloud
     *
     * @param cordCloud cordCloud 对象
     * @param bot qq bot
     */
    private void checkInCordCloud(@NotNull CordCloud cordCloud, @NotNull Bot bot) {
        try {
            // 登录, 设置 cookie
            String cookie = loginCordCloud (cordCloud, bot);
            if (cookie == null) {
                return;
            }
            cordCloud.setCookie (cookie);
        
            // 签到
            HttpResponse response = HttpRequest.post (prop.getCordCloudCheckIn ())
                    .header ("cookie", cordCloud.getCookie ())
                    .execute ();
        
            JSONObject msg = JSONUtil.parseObj (response.body ());
            if (response.getStatus () == 200) {
                String message;
                if (msg.getInt ("ret") == 0) {
                    message = msg.getStr ("msg");
                } else {
                    String str = StringUtils.decodeUnicode (msg.getStr ("msg"));
                    StringBuilder sb = new StringBuilder ();
                    for (char c : str.toCharArray ()) {
                        if (c >= '0' && c <= '9') {
                            sb.append (c);
                        }
                    }
                    String cordCloudAvgCnt = StrUtil.toStringOrNull (redisTemplate.opsForValue ()
                            .get (prop.getCordCloudAvgCntName ()));
                    double cnt = cordCloudAvgCnt == null ? 1 : Double.parseDouble (cordCloudAvgCnt);
                    String cordCloudAvgSum = StrUtil.toStringOrNull (redisTemplate.opsForValue ()
                            .get (prop.getCordCloudAvgSumName ()));
                    double sum = cordCloudAvgSum == null ? 1 : Double.parseDouble (cordCloudAvgSum);
                    double avg = NumberUtil.div (sum, cnt);
                    double thisSum = Integer.parseInt (sb.toString ());
                    cnt++;
                    sum += thisSum;
                    BigDecimal percentage = NumberUtil.round (NumberUtil.mul (NumberUtil.div (thisSum, avg), 100), 2);
                    redisTemplate.opsForValue ().set (prop.getCordCloudAvgCntName (), cnt);
                    redisTemplate.opsForValue ().set (prop.getCordCloudAvgSumName (), sum);
                    if (avg != 0) {
                        msg.set ("avg", "本次签到获得流量是平均签到流量的 " + percentage + "%");
                    }
                    message = buildMessage (msg);
                    
                }
                log.info ("message : {}", message);
                if (cordCloud.getIsSendEmail () != null && cordCloud.getIsSendEmail ()) {
                    mailSendService.sendMail (message, cordCloud.getSendEmail ());
                    log.info ("邮件发送成功");
                }
                if (cordCloud.getIsSendQQMsg () != null && cordCloud.getIsSendQQMsg ()) {
                    bot.sendPrivateMsg (cordCloud.getSendQQ (), message, false);
                    log.info ("QQ消息发送成功");
                }
            } else {
                log.warn ("签到失败!");
                log.warn (msg.getStr ("msg"));
                String errMsg = "自动签到签到失败, 失败原因 : " + msg.getStr ("msg");
                mailSendService.sendMail (errMsg, cordCloud.getSendEmail ());
                bot.sendPrivateMsg (cordCloud.getSendQQ (), errMsg, false);
            }
        } catch (Exception e) {
            log.error ("出现异常 : {}", e.getMessage (), e);
            checkInCordCloud (cordCloud, bot);
        }
    }
    
    /**
     * 登录 cordCloud, 并返回 cookie
     *
     * @param cordCloud cordCloud
     * @param bot qq bot
     * @return cookie
     * @throws RuntimeException 响应状态码不为 200 时抛出 | 登录用户信息错误时抛出
     */
    private String loginCordCloud(@NotNull CordCloud cordCloud, @NotNull Bot bot) {
        String form = JSONUtil.toJsonStr (JSONUtil.parseObj (cordCloud.getUser (), false, true));
        
        HttpResponse response = HttpRequest.post (prop.getCordCloudLogin ()).body (form).execute ();
        if (response.getStatus () == 200) {
            CordCloudMsg msg = JSONUtil.toBean (JSONUtil.parseObj (response.body ()), CordCloudMsg.class);
            if (msg.getRet () == 1) {
                String cookie = setCookie (response.getCookies ());
                log.info ("登录成功, 正在签到...");
                log.info ("cookie :");
                log.info (cookie);
                return cookie;
            } else {
                log.warn ("登录失败!");
                log.warn (msg.getMsg ());
                String errMsg = "自动签到登录失败, 失败原因 : " + msg.getMsg ();
                if (cordCloud.getIsSendEmail ()) {
                    mailSendService.sendMail (errMsg, cordCloud.getSendEmail ());
                }
                if (cordCloud.getIsSendQQMsg ()) {
                    bot.sendPrivateMsg (cordCloud.getSendQQ (), errMsg, false);
                }
                return null;
            }
        } else {
            log.warn ("登录失败, 遇到未知错误, 请联系管理员!!");
            log.warn (response.body ());
            return null;
        }
    }
    
    /**
     * 设置 cookie
     *
     * @param cookies response 中的 cookies
     * @return cookie
     */
    private String setCookie(@NotNull List<HttpCookie> cookies) {
        StringBuilder sb = new StringBuilder ();
        
        for (HttpCookie cookie : cookies) {
            sb.append (cookie).append ("; ");
        }
        
        return sb.toString ();
    }
    
    /**
     * 构建输出信息
     *
     * @param msg 签到返回 body
     * @return 构建的消息
     */
    private String buildMessage(@NotNull JSONObject msg) {
        StringBuilder sb = new StringBuilder ();
        JSONObject trafficInfo = JSONUtil.parseObj (msg.getStr ("trafficInfo"));
        sb.append ("签到成功! ")
                .append (StringUtils.decodeUnicode (msg.getStr ("msg"))).append ("\n")
                .append (msg.getStr ("avg")).append ("\n")
                .append ("当前套餐总流量 : ").append (msg.getStr ("traffic")).append ("\n")
                .append ("今日已用流量 : ").append (trafficInfo.getStr ("todayUsedTraffic")).append ("\n")
                .append ("过去已用流量 : ").append (trafficInfo.getStr ("lastUsedTraffic")).append ("\n")
                .append ("剩余流量 : ").append (trafficInfo.getStr ("unUsedTraffic"));
        return sb.toString ();
    }
}
