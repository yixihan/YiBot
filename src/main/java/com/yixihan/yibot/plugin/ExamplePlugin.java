package com.yixihan.yibot.plugin;

import cn.hutool.core.collection.CollectionUtil;
import com.mikuac.shiro.annotation.MessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.yixihan.yibot.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @MessageHandler
    public void testCQCode(@NotNull Bot bot, @NotNull AnyMessageEvent event) {
        log.info (CommonUtils.toString (event));
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
            List<Map<String, Object>> forwardMsg = ShiroUtils.generateForwardMsg (Long.parseLong (event.getSender ().getUserId ()), event.getSender ().getNickname (), msgList);
            bot.sendPrivateForwardMsg (Long.parseLong (event.getSender ().getUserId ()), forwardMsg);
        }

    }


    

}
