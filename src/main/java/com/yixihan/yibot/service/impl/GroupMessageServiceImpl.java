package com.yixihan.yibot.service.impl;

import com.yixihan.yibot.pojo.GroupMessage;
import com.yixihan.yibot.mapper.GroupMessageMapper;
import com.yixihan.yibot.service.GroupMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群聊消息记录表 服务实现类
 * </p>
 *
 * @author yixihan
 * @since 2022-11-23
 */
@Service
public class GroupMessageServiceImpl extends ServiceImpl<GroupMessageMapper, GroupMessage> implements GroupMessageService {

}
