package com.yixihan.yibot.dto.cordcloud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * cordCloud 自动签到 实体类
 *
 * @author : yixihan
 * @date : 2022-09-16-8:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CordCloud {

    /**
     * cordCloud 用户
     */
    private User user;

    /**
     * cordCloud Cookie
     */
    private volatile String cookie;
    
    /**
     * 是否启用自动签到
     */
    private Boolean isCheckIn;
    
    /**
     * 是否每日发送邮件
     */
    private Boolean isSendEmail;
    
    /**
     * cordCloud 签到记录接受者 (邮箱接收)
     */
    private String sendEmail;
    
    /**
     * 是否每日发送 QQ 消息
     */
    private Boolean isSendQQMsg;
    
    /**
     * cordCloud 签到记录接受者 (QQ接收)
     */
    private Long sendQQ;
    

    public CordCloud(User user) {
        this.user = user;
    }
}
