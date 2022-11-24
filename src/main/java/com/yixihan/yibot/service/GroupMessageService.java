package com.yixihan.yibot.service;

import com.yixihan.yibot.pojo.GroupMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 群聊消息记录表 服务类
 * </p>
 *
 * @author yixihan
 * @since 2022-11-23
 */
public interface GroupMessageService extends IService<GroupMessage> {


    /**
     * 获取指定群聊的消息
     *
     * @param groupId 群号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息列表
     */
    List<String> getGroupMessages (Long groupId, Date startTime, Date endTime);

}
