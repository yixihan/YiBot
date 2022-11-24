package com.yixihan.yibot.service.impl;

import com.yixihan.yibot.pojo.PrivateMessage;
import com.yixihan.yibot.mapper.PrivateMessageMapper;
import com.yixihan.yibot.service.PrivateMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 私聊消息记录表 服务实现类
 * </p>
 *
 * @author yixihan
 * @since 2022-11-23
 */
@Service
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage> implements PrivateMessageService {

}
