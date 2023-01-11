package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.constant.CommonConstants;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.properties.TranslateProperties;
import com.yixihan.yibot.utils.CQCodeUtils;
import com.yixihan.yibot.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 翻译插件
 *
 * @author yixihan
 * @date 2022/12/8 21:05
 */
@Slf4j
@Component
public class TranslatePlugin extends BotPlugin {
    
    @Resource
    private TranslateProperties constants;
    
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String eventMessage = event.getMessage ();
        String param;
    
        if (eventMessage.startsWith (CommonConstants.TRANSLATE_WORD_ONE)) {
            param = eventMessage.substring (CommonConstants.TRANSLATE_WORD_ONE.length () + 1);
        } else if (eventMessage.startsWith (CommonConstants.TRANSLATE_WORD_ONE_ENG)) {
            param = eventMessage.substring (CommonConstants.TRANSLATE_WORD_ONE_ENG.length () + 1);
        } else {
            return super.onGroupMessage (bot, event);
        }
        
        if (StrUtil.isBlank (param)) {
            return MESSAGE_IGNORE;
        }
        if (param.length () > 2000) {
            messageOutput (bot, event, "你咋不把你那毕业论文拿来让我给你翻译捏?");
            return MESSAGE_IGNORE;
        }
        
        String translate = translate (param);
        if (StrUtil.isEmpty (translate)) {
            messageOutput (bot, event, "翻译不来捏");
        } else {
            messageOutput (bot, event, translate);
        }
        return super.onGroupMessage (bot, event);
    }
    
    private String translate(String query) {
        try {
            query = new String (StrUtil.bytes (query, "UTF-8"));
            boolean isChinese = query.matches ("^.*[\\u4e00-\\u9fa5]+.*$");
            String from = "auto";
            String to = isChinese ? "en" : "zh";
            String appid = constants.getAppId ();
            String securityKey = constants.getSign ();
            String salt = String.valueOf (System.currentTimeMillis ());
            String sign = MD5Utils.md5 (appid + query + salt + securityKey);
            query = URLUtil.encode (query);
            String url = "https://fanyi-api.baidu.com/api/trans/vip/translate" + "?q=" + query + "&from=" + from + "&to=" + to + "&appid=" + appid + "&salt=" + salt + "&sign=" + sign;
            HttpResponse execute = HttpRequest.get (url).execute ();
            if (execute.isOk ()) {
                JSONObject body = JSONUtil.parseObj (execute.body ());
                return JSONUtil.parseObj (JSONUtil.parseArray (body.getStr ("trans_result")).get (0)).getStr ("dst");
            }
        } catch (HttpException e) {
            log.info ("出现异常");
        }
        
        return null;
    }
    
    
    /**
     * 消息输出
     *
     * @param info 消息
     */
    private void messageOutput(@NotNull Bot bot, @NotNull GroupMessageEvent event, String info) {
        String message;
        message = CQCodeUtils.extracted (event.getSender ().getUserId (), CQCodeEnums.at);
        bot.sendGroupMsg (event.getGroupId (), message + info, false);
    }
    
}
