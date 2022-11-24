package com.yixihan.yibot.mapper;

import com.yixihan.yibot.pojo.PrivateMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 私聊消息记录表 Mapper 接口
 * </p>
 *
 * @author yixihan
 * @since 2022-11-23
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {

}
