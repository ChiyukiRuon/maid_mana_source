package com.chiyukiruon.maid_mana_source.util;

import net.minecraft.client.resources.language.I18n;

import java.util.ArrayList;
import java.util.List;
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
    public static String getRandomLangKey(String baseKey) {
        List<String> candidates = new ArrayList<>();
        int index = 1;

        while (true) {
            String testKey = baseKey + "." + index;
            if (I18n.exists(testKey)) {
                candidates.add(testKey);
                index++;
            } else {
                break;
            }
        }

        if (candidates.isEmpty()) {
            return baseKey;
        }

        return candidates.get(RANDOM.nextInt(candidates.size()));
    }
}
