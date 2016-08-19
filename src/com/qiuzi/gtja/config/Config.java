package com.qiuzi.gtja.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.qiuzi.gtja.util.FileUtil;

public class Config {

    private Properties property;
    private FileInputStream inputFile;
    private FileOutputStream outputFile;

    /**
     * @description 读入离散化数据的配置文件
     * @param configfilePath 配置文件地址
     * @return 返回一个map，key为需要离散化的属性名，value为数组形式的区间标签
     */
    public static Map<String, int[]> getCategorizeStandardConfig(String configfilePath) {

        Map<String, int[]> map = new HashMap<String, int[]>();
        List<String> list = FileUtil.readFile(configfilePath);
        for (String s : list) {

            String[] a = s.split(" ");
            String attributeName = a[0];
            String[] attributeRange = a[1].split(",");
            int[] categorizeStandard = new int[attributeRange.length];
            // ����񻯱�׼תΪint
            for (int i = 0; i < attributeRange.length; i++) {
                categorizeStandard[i] = Integer.parseInt(attributeRange[i]);
            }
            map.put(attributeName, categorizeStandard);
        }
        return map;
    }

    /**
     * 初始化Configuration类
     */
    public Config() {
        property = new Properties();
    }

    /**
     * 初始化Configuration类
     * @param filePath
     *            要读取的配置文件的路径+名称
     */
    public Config(String filePath) {
        property = new Properties();
        try {
            inputFile = new FileInputStream(filePath);
            property.load(inputFile);
            inputFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("装载文件--->失败!");
            ex.printStackTrace();
        }
    }// end

    /**
     * 重载函数，得到key的值
     * @param key 取得其值的键
     * @return key的值
     */
    public String getValue(String key) {
        if (property.containsKey(key)) {
            String value = property.getProperty(key);// 得到某一属性的值
            return value;
        } else
            return "";
    }// end getValue()

    /**
     * 重载函数，得到key的值
     * 
     * @param fileName
     *            properties文件的路径+文件名
     * @param key
     *            取得其值的键
     * @return key的值
     */
    public String getValue(String fileName, String key) {
        try {
            String value = "";
            inputFile = new FileInputStream(fileName);
            property.load(inputFile);
            inputFile.close();
            if (property.containsKey(key)) {
                value = property.getProperty(key);
                return value;
            } else
                return value;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }// end
     // getValue()

    
    /**
     * 清除properties文件中所有的key和其值
     */
    public void clear() {
        property.clear();
    }// end clear();

    
    /**
     * 改变或添加一个key的值，当key存在于properties文件中时该key的值被value所代替， 当key不存在时，该key的值是value
     * 
     * @param key
     *            要存入的键
     * @param value
     *            要存入的值
     */
    public void setValue(String key, String value) {
        property.setProperty(key, value);
    }// end
     // setValue()

    
    /**
     * 将更改后的文件数据存入指定的文件中，该文件可以事先不存在。
     * 
     * @param fileName
     *            文件路径+文件名称
     * @param description
     *            对该文件的描述
     */
    public void saveFile(String fileName, String description) {
        try {
            outputFile = new FileOutputStream(fileName);
            property.store(outputFile, description);
            outputFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }// end
     // saveFile()

    public void configExample(String[] args) {
        Config rc = new Config(".\\config\\test.properties");// 相对路径

        String ip = rc.getValue("ipp");// 以下读取properties文件的值
        String host = rc.getValue("host");
        String tab = rc.getValue("tab");

        System.out.println("ip = " + ip + "ip-test leng = " + "ip-test".length());// 以下输出properties读出的值
        System.out.println("ip's length = " + ip.length());
        System.out.println("host = " + host);
        System.out.println("tab = " + tab);

        Config cf = new Config();
        String ipp = cf.getValue(".\\config\\test.properties", "ip");
        System.out.println("ipp = " + ipp);
        // cf.clear();
        cf.setValue("min", "999");
        cf.setValue("max", "1000");
        cf.saveFile(".\\config\\save.perperties", "test");

        // Configuration saveCf = new Configuration();
        // saveCf.setValue("min", "10");
        // saveCf.setValue("max", "1000");
        // saveCf.saveFile(".\config\save.perperties");

    }
}
