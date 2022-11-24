package com.yixihan.yibot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yixihan.yibot.pojo.GroupMessage;
import com.yixihan.yibot.mapper.GroupMessageMapper;
import com.yixihan.yibot.service.GroupMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<String> getGroupMessages(Long groupId, Date startTime, Date endTime) {
        QueryWrapper<GroupMessage> wrapper = new QueryWrapper<> ();
        wrapper.eq (GroupMessage.GROUP_ID, groupId)
                .between (GroupMessage.CREATE_TIME, startTime, endTime);
        return baseMapper.selectList (wrapper).stream ().map (GroupMessage::getMessage).collect(Collectors.toList());
    }
}
