package com.qiuzi.gtja.util;

import java.util.List;
import java.util.Map;

import com.qiuzi.gtja.config.Config;
import com.qiuzi.gtja.entity.User;

public class Preprocessor {

    private List<User> user;
    private Map<String, int[]> categorizeStandards;// ������������ɢ����׼��ʹ��map�洢��Ҫ��ɢ�������������Ӧ��ɢ��׼
    
    // 本部分用于对数据进行预处理并存储于数据库
    public Preprocessor(List<User> user, String standardConfigFilepath) {
        
        this.user = user;
        this.categorizeStandards = Config.getCategorizeStandardConfig(standardConfigFilepath);
        
        for (User u : user) {
            try {
                this.changeDataformat(u);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ��object����תΪint
    private int toInt(Object object) {
        return Integer.parseInt(String.valueOf(object));
    }

    // ��object����תΪint[]
    private int[] toInt(Object[] object) {

        int result[] = new int[object.length];
        for (int i = 0; i < object.length; i++) {
            result[i] = Integer.parseInt(String.valueOf(object[i]));
        }
        return result;
    }

    private String getCategorizedValue(String attributeName, Object attributeValue) {

        int[] valueRange = categorizeStandards.get(attributeName);

        int attriValue = toInt(attributeValue);
        for (int i = 0; i < valueRange.length; i++) {
            if (attriValue < valueRange[i]) {
                return String.valueOf(i);
            }
        }
        return String.valueOf(valueRange.length);
    }

    // �ı�ԭʼ���ݣ�ʹ֮��ʽ��Ϊ�ɴ���
    private void changeDataformat(User user) throws IllegalArgumentException, IllegalAccessException {

        // ��ɢ��������age��balance��duration
        user.setAge(getCategorizedValue("age", user.getAge()));
        user.setBalance(getCategorizedValue("balance", user.getBalance()));
        user.setDuration(getCategorizedValue("duration", user.getDuration()));
    }
}
