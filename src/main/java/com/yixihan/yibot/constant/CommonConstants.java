package com.yixihan.yibot.constant;

/**
 * 常用常量
 *
 * @author yixihan
 * @date 2022/12/4 17:19
 */
public class CommonConstants {
    
    
    /**
     * 易老师 QQ 号
     */
    public static final Long MASTER_ID = 3113788997L;
    
    
    //=================================== 自动提醒
    
    public static final String HELP = "帮助";
    
    public static final String ADD = "添加";
    
    public static final String DEL = "删除";
    
    public static final String LIST = "列表";
    
    public static final String RELOAD = "刷新";
    
    //===================================chatGPT常量
    
    public static final String CHAT_WORD_ONE = "chat";
    
    
    //===================================天气查询常量
    
    /**
     * 查询 - 1
     */
    public static final String WEATHER_QUERY_FIRST = "天气查询";
    
    /**
     * 查询 - 2
     */
    public static final String[] WEATHER_QUERY_SECOND = {"实时天气", "未来天气", "逐小时天气", "帮助"};
    
    /**
     * 逐小时天气 - 参数
     */
    public static final Integer[] HOURLY_WEATHER_PARAMS = {24};
    
    /**
     * 未来天气 - 参数
     */
    public static final Integer[] FUTURE_WEATHER_PARAMS = {3, 7};
    
    //==========================================翻译
    
    public static final String TRANSLATE_WORD_ONE = "翻译";
    
    public static final String TRANSLATE_WORD_ONE_ENG = "translate";
    
    //==========================================cordCloud 自动签到
    
    public static final String CORD_CLOUD_WORD_ONE = "cordCloud";
}
