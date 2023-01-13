package com.yixihan.yibot.dto.cordcloud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * cordCloud 请求返回实体类
 *
 * @author : yixihan
 * @date : 2022-09-16-14:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CordCloudMsg {

    /**
     * 返回状态码
     */
    private int ret;

    /**
     * 返回信息
     */
    private String msg;
}


