package com.yixihan.yibot.utils;

import com.yixihan.yibot.enums.CQCodeEnums;
import lombok.extern.slf4j.Slf4j;

/**
 * CQ code 工具类
 *
 * @author yixihan
 * @date 2022/11/25 19:38
 */
@Slf4j
public class CQCodeUtils {

    public static CQCodeEnums getCQCodeEnum (String type) {
        CQCodeEnums enums = null;
        try {
            enums = CQCodeEnums.valueOf (type);
        } catch (IllegalArgumentException e) {
            log.warn ("CQ type 转换失败!");
        }
        return enums;
    }


}
