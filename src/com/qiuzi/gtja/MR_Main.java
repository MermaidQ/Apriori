package com.qiuzi.gtja;

import java.io.File;

import com.qiuzi.gtja.util.Preprocessor;
import com.qiuzi.gtja.util.mr_Apriori;

public class MR_Main {
    
    static String rootPath = new File("").getAbsolutePath();
    static String inputPath = rootPath + "/mr_data/input/bank-full.csv";
    static String outputPath = rootPath + "/mr_data/output/";
    static String configPath = rootPath + "/config/categorizeStandardsConfig.txt";
    static double Threshold = 0.05;//确定支持度阈值
    private static String[] param = { "age", "job", "marital", "education", "credit", "balance", "housing", "loan",
            "duration", "poutcome" };
    
    public static void main(String[] args) {
        
        //数据规范化标准文件路径
        Preprocessor.standardConfigFilepath = configPath;
        mr_Apriori mr_apriori = new mr_Apriori(param, Threshold, inputPath, outputPath, configPath);
        
        try {
            //preprocess为生成1项集的map-reduce过程
            mr_apriori.preprocess();
            mr_apriori.setUsersetSize(mr_apriori.getUserlist().size());
            
            //初始化userinParamGroup
            mr_apriori.addtoUserinParamGroup(mr_apriori.getIndexedUsers(mr_apriori.getUserlist()));
            
            //获取k项集的map-reduce过程
            int paramCount = mr_apriori.getMaxParamCount();
            for(int k = 2;k <= paramCount; k++){
                mr_apriori.getFrequentSet(k);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}