package com.yixihan.yibot.plugin;

import cn.hutool.core.collection.CollectionUtil;
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
import com.yixihan.yibot.dto.chat.ChatGPT3Body;
import com.yixihan.yibot.dto.chat.Message;
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
            return MESSAGE_IGNORE;
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
    
    private String chatGPT ( String question) {
    
        ChatGPT3Body chatGPTBody = new ChatGPT3Body ();
        chatGPTBody.setModel (prop.getModel ());
        Message message = new Message ();
        message.setContent (question);
        message.setRole ("user");
        
        chatGPTBody.setMessage (CollectionUtil.newArrayList (message));
        String params = JSONUtil.parse (chatGPTBody).toString ();
        
        try {
            HttpResponse response = HttpRequest.post (prop.getApiUrl ())
                    .header ("Authorization", "Bearer " + prop.getApiKey ())
                    .header ("Content-Type", "application/json")
                    .body (params)
                    .execute ();
            log.info ("body : {}", response.body ());
        
            if (response.isOk ()) {
                JSONObject body = JSONUtil.parseObj (response.body ());
                Message ans = JSONUtil.parseObj (JSONUtil.parseArray (body.getStr ("choices"))
                        .get (0)).getBean ("message", Message.class);
                log.info ("ans : {}", ans);
                return ans.getContent ();
            }
        } catch (Exception e) {
            log.error ("出错捏", e);
            return "出错捏";
        }
        return null;
    }
}
