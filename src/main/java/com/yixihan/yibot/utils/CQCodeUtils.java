package com.yixihan.yibot.utils;

import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.yixihan.yibot.enums.CQCodeEnums;
import com.yixihan.yibot.dto.message.MessageNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CQ code 工具类
 *
 * @author yixihan
 * @date 2022/11/25 19:38
 */
@Slf4j
public class CQCodeUtils {

    public static CQCodeEnums getCQCodeEnum(String type) {
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
     * @param val  信息
     * @param type CQ code type
     */
    public static String extracted(String val, CQCodeEnums type) {
        ArrayMsg msb = new ArrayMsg ();
        msb.setType (type.getType ());
        HashMap<String, String> map = new HashMap<> ();
        if (CQCodeEnums.image.equals (type) || CQCodeEnums.video.equals (type)) {
            map.put ("file", val);
        } else if (CQCodeEnums.at.equals (type) || CQCodeEnums.poke.equals (type)) {
            map.put ("qq", val);
        }
        msb.setData (map);
        return ShiroUtils.jsonToCode (msb);
    }
    
    /**
     * 组装合并转发消息
     */
    public static List<Map<String, Object>> generateForwardMsg(List<MessageNode> contents) {
        ArrayList<Map<String, Object>> nodes = new ArrayList<> ();
        contents.forEach((msg) -> {
            HashMap<String, Object> node = new HashMap<String, Object>(16) {
                {
                    this.put("type", "node");
                    this.put("data", new HashMap<String, Object>(16) {
                        {
                            this.put("name", msg.getUserName ());
                            this.put("uin", msg.getUserId ());
                            this.put("content", msg.getMessage ());
                        }
                    });
                }
            };
            nodes.add(node);
        });
        return nodes;
    }


}
