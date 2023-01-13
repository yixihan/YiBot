package com.yixihan.yibot.dto.cordcloud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * cordCloud 登录实体类
 *
 * @author : yixihan
 * @date : 2022-09-16-13:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    /**
     * cordCloud 登录邮箱
     */
    private String email;

    /**
     * cordCloud 登录密码
     */
    private String passwd;

}
