package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description
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

    @GroupMessageHandler(at = AtEnum.OFF, cmd = "^(色色|瑟瑟|涩涩).*$")
    public void getImage (@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        if (!constants.getVal ().contains (String.valueOf (event.getGroupId ()))) {
            return;
        }
        String[] splits = event.getMessage ().split (" ");
        String tag = splits.length >= 2 ? splits[1] : null;
        List<String> msgList = getImage (tag);
        String message;
        if (msgList == null) {
            message = extracted (
                    event.getSender () != null ? event.getSender ().getUserId () : String.valueOf (event.getAnonymous ().getId ()),
                    "at") + "搜不到" + tag + ", 似乎不能涩涩了捏";
            bot.sendGroupMsg (event.getGroupId (), message, false);
        } else {
            message = extracted (msgList.get (0), "image");
            msgList.add (message);
            msgList.remove (0);
            List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg(
                    Long.parseLong (event.getSender ().getUserId ()),
                    event.getSender ().getNickname (),
                    msgList
            );
            bot.sendGroupForwardMsg (event.getGroupId (), forwardMsg);

            message = extracted (
                    event.getSender () != null ? event.getSender ().getUserId () : String.valueOf (event.getAnonymous ().getId ()),
                    "at") + "可以涩涩!";

            bot.sendGroupMsg (event.getGroupId (), message, false);
        }

    }


    private static String extracted(String val, String type) {
        MsgChainBean msb = new MsgChainBean ();
        msb.setType (type);
        HashMap<String, String> map = new HashMap<> ();
        if ("image".equals (type)) {
            map.put ("file", val);
        } else if ("at".equals (type)) {
            map.put ("qq", val);
        }
        msb.setData (map);
        return ShiroUtils.jsonToCode (msb);
    }

    private List<String> getImage (@Nullable String val) {
        String body = JSONUtil.toJsonStr (new RequestBody (new String[]{val}));
        log.info ("body : {}", body);
        HttpResponse response = HttpRequest.post (constants.getUrl ())
                .header ("Content-Type", "application/json")
                .body (body)
                .execute ();

        if (response.isOk ()) {
            try {
                JSONObject obj = JSONUtil.parseObj (response.body ());
                System.out.println ("obj : " + obj);
                if (StrUtil.isBlank (obj.getStr ("error"))) {
                    JSONObject data = JSONUtil.parseObj (JSONUtil.parseArray (obj.get ("data")).get (0));
                    List<String> list = new ArrayList<> ();
                    list.add (JSONUtil.parseObj (data.get ("urls")).getStr ("original"));
                    list.add ("pid : " + data.getStr ("pid"));
                    list.add ("uid : " + data.getStr ("uid"));
                    return list;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class RequestBody {

        String[] tag;

    }
}
