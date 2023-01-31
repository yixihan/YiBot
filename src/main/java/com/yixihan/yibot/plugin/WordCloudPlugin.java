package com.yixihan.yibot.plugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.Word;
import cn.hutool.extra.tokenizer.engine.mmseg.MmsegEngine;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.OneBotMedia;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yixihan.yibot.config.GroupConfig;
import com.yixihan.yibot.constant.RedisKeyConstants;
import com.yixihan.yibot.properties.WordCloudProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 词云插件
 *
 * @author yixihan
 * @date 2023/1/10 19:25
 */
@Slf4j
@Component
public class WordCloudPlugin extends BotPlugin {
    
    
    @Resource
    private GroupConfig config;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private WordCloudProperties prop;
    @Resource
    private BotContainer botContainer;
    
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        //自动根据用户引入的分词库的jar来自动选择使用的引擎
        TokenizerEngine engine = new MmsegEngine ();
        if (!config.getWordCloudList ().contains (event.getGroupId ())) {
            return super.onGroupMessage (bot, event);
        }
        
        String dailyKey = String.format (RedisKeyConstants.DAILY_GROUP_WORD_CLOUD, event.getGroupId ());
        String weekKey = String.format (RedisKeyConstants.WEEK_GROUP_WORD_CLOUD, event.getGroupId ());
        
        ShiroUtils.stringToMsgChain (event.getMessage ()).stream ()
                .filter ((o) -> "text".equals (o.getType ()))
                .forEach ((o) -> {
                    Result result = engine.parse (o.getData ().get ("text"));
                    
                    CollUtil.newArrayList ((Iterable<Word>) result)
                            .forEach ((item) -> {
                                String text = item.getText ();
                                String dailyVal = Optional.ofNullable (redisTemplate.opsForHash ()
                                        .get (dailyKey, text))
                                        .orElse ("0").toString ();
                                redisTemplate.opsForHash ().put (dailyKey, text, Integer.parseInt (dailyVal) + 1);
                                
                                
                                String weekVal = Optional.ofNullable (redisTemplate.opsForHash ()
                                        .get (weekKey, text))
                                        .orElse ("0").toString ();
                                redisTemplate.opsForHash ().put (weekKey, text, Integer.parseInt (weekVal) + 1);
                    });
        });
        return super.onGroupMessage (bot, event);
    }
    
    @Scheduled(cron = "0 58 23 ? * 1,2,3,4,5,6")
    private void cleanDailyWord() {
        for (Long group : config.getWordCloudList ()) {
            ThreadUtil.execAsync (() -> {
                String dailyKey = String.format (RedisKeyConstants.DAILY_GROUP_WORD_CLOUD, group);
                List<WordFrequency> wordFrequencyList = getWordFrequencies (dailyKey);
                if (CollectionUtil.isNotEmpty (wordFrequencyList)) {
                    String file = getWordCloud (wordFrequencyList, group);
                    String msg = MsgUtils.builder ()
                            .text ("摸鱼的一天结束啦, 让我来看看大家今天都讨论了啥吧")
                            .img (OneBotMedia.builder ().file (file))
                            .build ();
                    getBot ().sendGroupMsg (group, msg, false);
                }
                redisTemplate.delete (dailyKey);
            });
        }
    }
    
    @Scheduled(cron = "0 58 23 ? * 7")
    private void cleanWeekWord() {
        for (Long group : config.getWordCloudList ()) {
            ThreadUtil.execAsync (() -> {
                String weekKey = String.format (RedisKeyConstants.WEEK_GROUP_WORD_CLOUD, group);
                String dailyKey = String.format (RedisKeyConstants.DAILY_GROUP_WORD_CLOUD, group);
                
                List<WordFrequency> wordFrequencyList = getWordFrequencies (weekKey);
                if (CollectionUtil.isNotEmpty (wordFrequencyList)) {
                    String file = getWordCloud (wordFrequencyList, group);
                    String msg = MsgUtils.builder ()
                            .text ("摸鱼的一周结束啦, 让我来看看大家今天都讨论了啥吧")
                            .img (OneBotMedia.builder ().file (file))
                            .build ();
                    getBot ().sendGroupMsg (group, msg, false);
                }
                
                redisTemplate.delete (weekKey);
                redisTemplate.delete (dailyKey);
            });
        }
    }
    
    private Bot getBot() {
        // 机器人账号
        long botId = 2535774265L;
        // 通过机器人账号取出 Bot 对象
        return botContainer.robots.get (botId);
    }
    
    private List<WordFrequency> getWordFrequencies(String key) {
        Map<Object, Object> map = redisTemplate.opsForHash ().entries (key);
        int size = map.size ();
        return map.entrySet ().stream ().map ((entry) -> {
            String word = entry.getKey ().toString ();
            int count = Integer.parseInt (entry.getValue ().toString ());
            return new WordFrequency (word, count);
        }).filter ((o) ->  {
            if (size <= 100) {
                return o.getFrequency () >= 0;
            } else if (size <= 300) {
                return o.getFrequency () >= 1;
            } else if (size <= 500) {
                return o.getFrequency () >= 2;
            } else if (size <= 800) {
                return o.getFrequency () >= 3;
            } else if (size <= 1000) {
                return o.getFrequency () >= 4;
            } else {
                return o.getFrequency () >= 5;
            }
        }).collect (Collectors.toList ());
    }
    
    public String getWordCloud(List<WordFrequency> wordFrequencyList, Long group) {
        //此处不设置会出现中文乱码
        java.awt.Font font = new java.awt.Font ("STSong-Light", Font.ITALIC, 18);
        //设置图片分辨率
        Dimension dimension = new Dimension (500, 500);
        //此处的设置采用内置常量即可，生成词云对象
        WordCloud wordCloud = new WordCloud (dimension, CollisionMode.PIXEL_PERFECT);
        //设置边界及字体
        wordCloud.setPadding (2);
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground (new CircleBackground (255));
        wordCloud.setFontScalar (new SqrtFontScalar (12, 42));
        //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
        wordCloud.setColorPalette (new LinearGradientColorPalette (
                Color.getHSBColor (69, 168, 234),
                Color.getHSBColor (60, 212, 244),
                Color.getHSBColor (84, 215, 240),
                30, 30)
        );
        wordCloud.setKumoFont (new KumoFont (font));
        wordCloud.setBackgroundColor (new Color (255, 255, 255));
        //因为我这边是生成一个圆形,这边设置圆的半径
        wordCloud.setBackground (new CircleBackground (255));
        wordCloud.build (wordFrequencyList);
        //生成词云图路径
        String path = FileUtil.isWindows () ? prop.getWinPath () : prop.getLinuxPath ();
        String outFileName = group + "-" + UUID.fastUUID () + ".png";
        wordCloud.writeToFile (path + "/" + outFileName);
        return outFileName;
    }
    
    
}


