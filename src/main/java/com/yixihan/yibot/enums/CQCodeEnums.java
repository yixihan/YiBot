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
     * QQ 表情
     */
    FACE ("face", "QQ 表情"),

    /**
     * 语言
     */
    RECORD ("record", "语言"),

    /**
     * 短视频
     */
    VIDEO ("video", "短视频"),

    /**
     * at某人
     */
    AT ("at", "@某人"),

    /**
     * 链接分享
     */
    SHARE ("share", "链接分享"),

    /**
     * 音乐分享
     */
    MUSIC ("music", "音乐分享"),

    /**
     * 图片
     */
    IMAGE ("image", "图片"),

    /**
     * 回复
     */
    REPLY ("reply", "回复"),

    /**
     * 戳一戳
     */
    POKE ("poke", "戳一戳"),

    /**
     * 礼物
     */
    GIFT ("gift", "礼物"),

    /**
     * 合并转发
     */
    FORWARD ("forward", "合并转发"),

    /**
     * XML 消息
     */
    XML ("xml", "XML 消息"),

    /**
     * JSON 消息
     */
    JSON ("json", "JSON 消息"),

    /**
     * 装逼大图
     */
    CARDIMAGE ("cardimage", "装逼大图"),

    /**
     * 文本转语音
     */
    TTS ("tts", "文本转语音")
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
