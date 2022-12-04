package com.yixihan.yibot.plugin;

import cn.hutool.json.JSONUtil;
import com.mikuac.shiro.annotation.MessageHandler;
import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.pojo.MessageNode;
import com.yixihan.yibot.utils.CQCodeUtils;
import com.yixihan.yibot.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.yixihan.yibot.constant.RedisKeyConstants.GROUP_MESSAGE_RECORD_KEY;

/**
 * description
 *
 * @author yixihan
 * @date 2022/12/4 18:53
 */
@Slf4j
@Shiro
@Component
public class ExamplePlugin {
    
    @MessageHandler
    public void test (@NotNull Bot bot, @NotNull AnyMessageEvent event) {
    }
    
}
