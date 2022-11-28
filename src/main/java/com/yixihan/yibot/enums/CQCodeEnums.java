package com.yixihan.yibot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description
 *
 * @author yixihan
 * @date 2022/11/25 19:20
 */
@AllArgsConstructor
@Getter
public enum CQCodeEnums {

    /**
     * qq 表情
     */
    face ("face", "qq 表情"),

    /**
     * 语言
     */
    record ("record", "语言"),

    /**
     * 短视频
     */
    video ("video", "短视频"),

    /**
     * at某人
     */
    at ("at", "@某人"),

    /**
     * 链接分享
     */
    share ("share", "链接分享"),

    /**
     * 音乐分享
     */
    music ("music", "音乐分享"),

    /**
     * 图片
     */
    image ("image", "图片"),

    /**
     * 回复
     */
    reply ("reply", "回复"),

    /**
     * 戳一戳
     */
    poke ("poke", "戳一戳"),

    /**
     * 礼物
     */
    gift ("gift", "礼物"),

    /**
     * 合并转发
     */
    forward ("forward", "合并转发"),

    /**
     * xml 消息
     */
    xml ("xml", "xml 消息"),

    /**
     * json 消息
     */
    json ("json", "json 消息"),

    /**
     * 装逼大图
     */
    cardimage ("cardimage", "装逼大图"),

    /**
     * 文本转语音
     */
    tts ("tts", "文本转语音")
    ;

    /**
     * 类型
     */
    private final String type;

    /**
     * 描述
     */
    private final String description;
}
