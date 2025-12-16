package com.chiyukiruon.maid_mana_source.util;

import java.util.Random;

public class I18nUtil {
    private static final Random RANDOM = new Random();

    /**
     * 返回指定前缀下的随机语言键
     *
     * @param baseKey 语言键的前缀
     * @return 随机语言键
     * @author ChiyukiRuon
     * */
    public static String getRandomLangKey(String baseKey, int count) {
        if (count <= 0) {
            return baseKey;
        }
        int index = RANDOM.nextInt(count);
        return baseKey + "." + index;
    }
}
