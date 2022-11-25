package com.yixihan.yibot.utils;

import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.yixihan.yibot.enums.CQCodeEnums;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

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

    /**
     * 组装 CQ code
     *
     * @param val 信息
     * @param type CQ code type
     */
    public static String extracted(String val, CQCodeEnums type) {
        MsgChainBean msb = new MsgChainBean ();
        msb.setType (type.getType ());
        HashMap<String, String> map = new HashMap<> ();
        if (CQCodeEnums.IMAGE.equals (type) || CQCodeEnums.VIDEO.equals (type)) {
            map.put ("file", val);
        } else if (CQCodeEnums.AT.equals (type)) {
            map.put ("qq", val);
        }
        msb.setData (map);
        return ShiroUtils.jsonToCode (msb);
    }


}
