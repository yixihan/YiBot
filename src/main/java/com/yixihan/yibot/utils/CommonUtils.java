package com.yixihan.yibot.utils;

import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.yixihan.yibot.enums.CQCodeEnums;

import java.util.List;
import java.util.stream.Collectors;

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
  
  public static List<String> getMsgImgUrlList(List<MsgChainBean> arrayMsg) {
    return arrayMsg.stream()
            .filter((it) -> "image".equals(it.getType()))
            .map((it) -> it.getData().get("url"))
            .collect(Collectors.toList());
  }
  
  public static String getMsgImgUrl(MsgChainBean msg) {
    if (CQCodeEnums.image.getType ().equals (msg.getType ())) {
      return msg.getData ().get ("url");
    }
    
    return null;
  }
}
