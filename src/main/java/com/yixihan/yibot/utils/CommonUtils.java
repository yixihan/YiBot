package com.yixihan.yibot.utils;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;

/**
 * 通用工具类
 *
 * @author yixihan
 * @date 2022/11/28 20:16
 */
public class CommonUtils {
  
  public static String toString (AnyMessageEvent event) {
    return new StringBuilder ("\n").append ("messageId : ").append (event.getMessageId ()).append ("\n")
            .append ("subType : ").append (event.getSubType ()).append ("\n")
            .append ("groupId : ").append (event.getGroupId ()).append ("\n")
            .append ("message : ").append (event.getMessage ()).append ("\n")
            .append ("sender : ").append (event.getSender ()).append ("\n")
            .append ("anonymous : ").append (event.getAnonymous ())
            .toString ();
  }
}
