package com.yixihan.yibot.mapper;

import com.yixihan.yibot.pojo.GroupMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 群聊消息记录表 Mapper 接口
 * </p>
 *
 * @author yixihan
 * @since 2022-11-23
 */
@Mapper
public interface GroupMessageMapper extends BaseMapper<GroupMessage> {

}
