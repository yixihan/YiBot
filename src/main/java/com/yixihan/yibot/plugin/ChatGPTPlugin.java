package com.yixihan.yibot.plugin;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.constant.ChatGPTConstants;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.pojo.ChatGPTBody;
import com.yixihan.yibot.utils.CQCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yixihan.yibot.constant.ChatGPTConstants.WORD_ONE;
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
    private ChatGPTConstants constants;
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String eventMessage = event.getMessage ();
        
        if (!eventMessage.startsWith (WORD_ONE)) {
            return MESSAGE_IGNORE;
        }
    
        if (eventMessage.length () < FIVE || eventMessage.charAt (FOUR) != ' ') {
            messageOutput (bot, event, "？");
            return MESSAGE_IGNORE;
        }
    
        String param = eventMessage.substring (FIVE);
        if (StrUtil.isBlank (param)) {
            messageOutput (bot, event, "？");
            return MESSAGE_IGNORE;
        }
    
        messageOutput (bot, event, "正在试图解答ing~");
        String answer = chatGPT (param);
        if (StrUtil.isNotBlank (answer)) {
            messageOutput (bot, event, "已经找到答案捏~");
            messageOutput (bot, event, answer);
        } else {
            messageOutput (bot, event, "我布吉岛捏");
        }
        
        return super.onGroupMessage (bot, event);
    }
    
    private String chatGPT (String question) {
    
        ChatGPTBody chatGPTBody = new ChatGPTBody ();
        chatGPTBody.setModel ("text-davinci-003");
        chatGPTBody.setPrompt (question);
        chatGPTBody.setTemperature (0);
        chatGPTBody.setMax_tokens (1024);
        String params = JSONUtil.parse (chatGPTBody).toStringPretty ();
        
        try {
            HttpResponse response = HttpRequest.post (constants.getApiUrl ())
                    .header ("Authorization", "Bearer " + constants.getApiKey ())
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
