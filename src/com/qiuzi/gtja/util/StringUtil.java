package com.qiuzi.gtja.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.Text;

public class StringUtil {

    /**
     * @description 将String封装为Set<String>
     * @param 字符串s
     * @return 包含字符串s的集合
     */
    public static Set<String> toSet(Text text){
        
        Set<String> set = new HashSet<String>();
        set.add(text.toString());
        return set;
    }
    
    public static String toString(Set<String> set){
        
        StringBuffer stringBuffer = new StringBuffer();
        for(String s:set){
            stringBuffer.append(s+" ");
        }
        return stringBuffer.toString();
    }
    
    public static Set<String> toSet(Text text, String splitCharacter){
        
        String[] strings = text.toString().trim().split(splitCharacter);

        Set<String> set = new HashSet<String>();
        
        for(String s:strings){
            set.add(s);
        }
        return set;
    }
    
    public static String toString(String[] array, int start, int end){
        
        StringBuffer string = new StringBuffer();
        for(int i = start;i<=end;i++){
            string.append(array[i]+" ");
        }
        return string.toString();
    }
    
    /**
     * @param <T> 可操作各种数据类型的数组
     * @description 将数组中，下标在start和end之间的元素放入集合set返回
     * @param array: 要转化为set的数组
     * @param start: 加入set开始下标
     * @param end: 加入set最后元素的下标
     * @return 集合
     */
    public static <T> Set<T> toSet(T[] array, int start, int end){
        
        Set<T> set = new HashSet<T>();
        for(int i = start;i<=end;i++){
            set.add(array[i]);
        }
        return set;
    }
    
    /**
     * @param <T> 可操作各种数据类型的数组
     * @description 将数组中的元素放入集合set返回
     * @param array: 要转化为set的数组
     * @return 集合
     */
    public static <T> Set<T> toSet(T[] array){
        
        Set<T> set = new HashSet<T>();
        int length = array.length;
        for(int i = 0;i < length;i++){
            set.add(array[i]);
        }
        return set;
    }
}
