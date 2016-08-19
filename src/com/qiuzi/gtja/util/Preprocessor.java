package com.qiuzi.gtja.util;

import java.util.List;
import java.util.Map;

import com.qiuzi.gtja.config.Config;
import com.qiuzi.gtja.entity.User;

public class Preprocessor {

    public static String standardConfigFilepath;

    // 本部分用于对数据进行预处理并存储于数据库
    public static void preprocess(List<User> user) {

        Map<String, int[]> categorizeStandards = Config.getCategorizeStandardConfig(standardConfigFilepath);
        for (User u : user) {
            try {
                changeDataformat(u, categorizeStandards);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void preprocess(User user) {

        Map<String, int[]> categorizeStandards = Config.getCategorizeStandardConfig(standardConfigFilepath);
        try {
            changeDataformat(user, categorizeStandards);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将Object对象转为int
    private static int toInt(Object object) {
        return Integer.parseInt(String.valueOf(object));
    }

    // 将Object数组转为int[]
    @SuppressWarnings("unused")
    private static int[] toInt(Object[] object) {

        int result[] = new int[object.length];
        for (int i = 0; i < object.length; i++) {
            result[i] = Integer.parseInt(String.valueOf(object[i]));
        }
        return result;
    }

    private static String getCategorizedValue(String attributeName, Object attributeValue,
            Map<String, int[]> categorizeStandards) {

        int[] valueRange = categorizeStandards.get(attributeName);

        int attriValue = toInt(attributeValue);
        for (int i = 0; i < valueRange.length; i++) {
            if (attriValue < valueRange[i]) {
                return String.valueOf(i);
            }
        }
        return String.valueOf(valueRange.length);
    }

    /*
     * @description:格式化字段
     * @param categorizeStandards:字段及字段对应的取值标准
     */
    private static void changeDataformat(User user, Map<String, int[]> categorizeStandards)
            throws IllegalArgumentException, IllegalAccessException {

        // ��ɢ��������age��balance��duration
        user.setAge(getCategorizedValue("age", user.getAge(), categorizeStandards));
        user.setBalance(getCategorizedValue("balance", user.getBalance(), categorizeStandards));
        user.setDuration(getCategorizedValue("duration", user.getDuration(), categorizeStandards));
    }
}
