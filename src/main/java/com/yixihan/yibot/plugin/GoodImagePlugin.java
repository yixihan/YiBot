package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.yixihan.yibot.constant.GoodImageConstants;
import com.yixihan.yibot.enums.CQCodeEnums;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 色图插件
 *
 * @author yixihan
 * @date 2022/11/24 23:05
 */
@Slf4j
@Shiro
@Component
public class GoodImagePlugin {

    @Resource
    private GoodImageConstants constants;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GroupMessageHandler(at = AtEnum.OFF, cmd = "^(色色|瑟瑟|涩涩|sese).*$")
    public void getImage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        if (!constants.getVal ().contains (String.valueOf (event.getGroupId ()))) {
            return;
        }
        String message;
        if (!getToken (event.getGroupId ())) {
            message = extracted (
                    event.getSender () != null ?
                            event.getSender ().getUserId () :
                            String.valueOf (event.getAnonymous ().getId ()), CQCodeEnums.AT) + "别冲啦, 对弟弟好点儿(￣_￣|||)";
            bot.sendGroupMsg (event.getGroupId (), message, false);
            return;
        }
        String[] splits = event.getMessage ().split (" ");
        String tag = splits.length >= 2 ? splits[1] : null;

        List<String> msgList = getImage (tag);

        if (msgList == null) {
            message = extracted (
                    event.getSender () != null ?
                            event.getSender ().getUserId () :
                            String.valueOf (event.getAnonymous ().getId ()), CQCodeEnums.AT) + "搜不到" + tag + ", 似乎不能涩涩了捏";
            bot.sendGroupMsg (event.getGroupId (), message, false);
        } else {
            message = extracted (msgList.get (0), CQCodeEnums.IMAGE);
            msgList.add (message);
            msgList.remove (0);
            List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                    Long.parseLong (event.getSender ().getUserId ()),
                    event.getSender ().getNickname (),
                    msgList
            );
            bot.sendGroupForwardMsg (event.getGroupId (), forwardMsg);

            message = extracted (event.getSender () != null ?
                    event.getSender ().getUserId () :
                    String.valueOf (event.getAnonymous ().getId ()), CQCodeEnums.AT) + "可以涩涩!";

            bot.sendGroupMsg (event.getGroupId (), message, false);
        }

    }


    private static String extracted(String val, CQCodeEnums type) {
        MsgChainBean msb = new MsgChainBean ();
        msb.setType (type.getType ());
        HashMap<String, String> map = new HashMap<> ();
        if (CQCodeEnums.IMAGE.equals (type)) {
            map.put ("file", val);
        } else if (CQCodeEnums.AT.equals (type)) {
            map.put ("qq", val);
        }
        msb.setData (map);
        return ShiroUtils.jsonToCode (msb);
    }

    private List<String> getImage(@Nullable String val) {
        String body = JSONUtil.toJsonStr (new RequestBody (new String[]{val}));
        try {
            log.info ("body : {}", body);
            HttpResponse response = HttpRequest.post (constants.getUrl ())
                    .header ("Content-Type", "application/json")
                    .body (body)
                    .execute ();
            if (response.isOk ()) {

                JSONObject obj = JSONUtil.parseObj (response.body ());
                System.out.println ("obj : " + obj);
                if (StrUtil.isBlank (obj.getStr ("error"))) {
                    JSONObject data = JSONUtil.parseObj (JSONUtil.parseArray (obj.get ("data")).get (0));
                    List<String> list = new ArrayList<> ();
                    list.add (JSONUtil.parseObj (data.get ("urls")).getStr ("original"));
                    list.add ("pid : " + data.getStr ("pid"));
                    list.add ("uid : " + data.getStr ("uid"));
                    list.add ("tags : " + JSONUtil.parseArray (data.get ("tags")));
                    return list;
                }

            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    @Scheduled (cron = "0 * * * * ?")
    @PostConstruct
    public void setnx () {
        log.info ("开始重置限流key...");

        for (String groupId : constants.getVal ().split (", ")) {
            log.info ("重置群 : {}",groupId);
            String key = String.format (constants.getSetnxKey (), groupId);
            redisTemplate.opsForValue ().set (key,
                    constants.getSetnxCnt (),
                    constants.getSetnxTime (),
                    TimeUnit.MINUTES);
        }
    }

    private synchronized boolean getToken (Long groupId) {
        String key = String.format (constants.getSetnxKey (), groupId);
        int cnt = Integer.parseInt (Optional.ofNullable (redisTemplate.opsForValue ().get (key)).orElse ("-1").toString ());

        if (cnt <= 0) {
            return false;
        } else {
            cnt--;
            redisTemplate.opsForValue ().set (key, cnt);
            return true;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class RequestBody {

        String[] tag;

    }
}
