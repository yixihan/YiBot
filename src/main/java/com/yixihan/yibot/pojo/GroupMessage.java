package com.yixihan.yibot.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
*
* 群聊消息记录表
*
* @author yixihan
* @since 2022-11-23
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "GroupMessage对象", description = "群聊消息记录表")
public class GroupMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息 id")
    private Long messageId;

    @ApiModelProperty(value = "消息子类型 (正常消息 normal, 匿名消息 anonymous, 系统提示 notice)")
    private String subType;

    @ApiModelProperty(value = "群号")
    private Long groupId;

    @ApiModelProperty(value = "发送者 QQ 号")
    private Long userId;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "消息内容")
    private String message;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "乐观锁")
    @Version
    private Integer version;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    private Integer delFlag;


    public static final String ID = "id";

    public static final String MESSAGE_ID = "message_id";

    public static final String SUB_TYPE = "sub_type";

    public static final String GROUP_ID = "group_id";

    public static final String USER_ID = "user_id";

    public static final String NICKNAME = "nickname";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String VERSION = "version";

    public static final String DEL_FLAG = "del_flag";

}
