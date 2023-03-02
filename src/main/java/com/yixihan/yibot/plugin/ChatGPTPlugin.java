package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.constant.CommonConstants;
import com.yixihan.yibot.dto.chat.ChatGPTBody;
import com.yixihan.yibot.properties.ChatGPTProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yixihan.yibot.constant.NumberConstants.FIVE;
import static com.yixihan.yibot.constant.NumberConstants.FOUR;

/**
 * chatGPT 插件
 *
 * @author yixihan
 * @date 2022/12/10 15:53
 */
@Slf4j
@Component
public class ChatGPTPlugin extends BotPlugin {
    
    @Resource
    private ChatGPTProperties prop;
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String eventMessage = event.getMessage ();
        String sendMessage;
    
        if (!eventMessage.startsWith (CommonConstants.CHAT_WORD_ONE)) {
            return MESSAGE_IGNORE;
        }
        
        if ("chat 获取当前模型".equals (eventMessage)) {
            sendMessage = MsgUtils.builder ()
                    .reply (event.getMessageId ())
                    .text (prop.getModel ())
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        }
    
        if (eventMessage.length () < FIVE || eventMessage.charAt (FOUR) != ' ') {
            sendMessage = MsgUtils.builder ()
                    .reply (event.getMessageId ())
                    .text ("?")
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
            return MESSAGE_IGNORE;
        }
    
        String param = eventMessage.substring (FIVE);
        if (StrUtil.isBlank (param)) {
            sendMessage = MsgUtils.builder ()
                    .reply (event.getMessageId ())
                    .text ("?")
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
            return MESSAGE_IGNORE;
        }
        
        String answer = chatGPT (param);
        if (StrUtil.isNotBlank (answer)) {
            sendMessage = MsgUtils.builder ()
                    .reply (event.getMessageId ())
                    .text (answer)
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        } else {
            sendMessage = MsgUtils.builder ()
                    .reply (event.getMessageId ())
                    .text ("我布吉岛捏~")
                    .build ();
            bot.sendGroupMsg (event.getGroupId (), sendMessage, false);
        }
        
        return super.onGroupMessage (bot, event);
    }
    
    private String chatGPT (String question) {
    
        ChatGPTBody chatGPTBody = new ChatGPTBody ();
        chatGPTBody.setModel (prop.getModel ());
        chatGPTBody.setPrompt (question);
        chatGPTBody.setTemperature (0);
        chatGPTBody.setMax_tokens (4096);
        String params = JSONUtil.parse (chatGPTBody).toStringPretty ();
        
        try {
            HttpResponse response = HttpRequest.post (prop.getApiUrl ())
                    .header ("Authorization", "Bearer " + prop.getApiKey ())
                    .header ("Content-Type", "application/json")
                    .body (params)
                    .execute ();
        
            if (response.isOk ()) {
                JSONObject body = JSONUtil.parseObj (response.body ());
                String text = JSONUtil.parseObj (JSONUtil.parseArray (body.getStr ("choices")).get (0)).getStr ("text");
                log.info ("text : {}", text);
                return text;
            }
        } catch (Exception e) {
            log.info ("出错捏");
            return "出错捏";
        }
        return null;
    }
}
