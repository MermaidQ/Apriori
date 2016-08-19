package com.qiuzi.gtja;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import com.qiuzi.gtja.entity.User;
import com.qiuzi.gtja.util.Apriori;
import com.qiuzi.gtja.util.FileUtil;
import com.qiuzi.gtja.util.Preprocessor;

public class Main {

    final static String rootPath = new File("").getAbsolutePath();
    final static String csvFilepath = rootPath + "\\data\\bank-full.csv";
    static String resultFilepath;
    // final String testsetFilepath = rootPath + "\\data\\result-full.csv";

    final static String TABLENAME = "roi_user";
    final static double SUPPORT_THRESHOLD = (double) 0.05;
    final static String[] PARAM_NAME = { "age", "job", "marital", "education", "credit", "balance", "housing", "loan",
            "duration", "poutcome" };
    
    public static void trainwithPercentage(){
        
        resultFilepath = rootPath + "\\data\\result-full.csv";
        try {
            
            List<User> users = FileUtil.readCsvFile(csvFilepath, false);
            
            Preprocessor.preprocess(users);
            
            //清空数据库中已有数据，并置id的其实值为1
            //HibernateUtil.getSessionFactory();
            //HibernateUtil.excuteBySql("truncate table "+TABLENAME);
            //HibernateUtil.excuteBySql("alter table "+TABLENAME+" AUTO_INCREMENT=1");
            //DatabaseUtil.add(user);
            
            Apriori apriori = new Apriori(users, PARAM_NAME, SUPPORT_THRESHOLD, TABLENAME);
            apriori.initResultset();
            apriori.generateObjectList();
            
            FileUtil.writeCsvFile(resultFilepath, apriori.getObjectList());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void trainwithRandomValue(){
        
        resultFilepath = rootPath + "\\result\\resultWithRandomValue.csv";
        List<User> users = FileUtil.readCsvFile(csvFilepath, true);
        
        Preprocessor.preprocess(users);
        
        for(double i = new BigDecimal(Double.toString(0.05)).doubleValue();i <= 1;i= new BigDecimal(Double.toString(i)).add(new BigDecimal(Double.toString(0.05))).doubleValue()){

        long start = System.currentTimeMillis(); 
        Apriori apriori = new Apriori(users, PARAM_NAME, i, null);
        apriori.initResultset();
        apriori.generateObjectList();
        System.out.println(i+" "+(System.currentTimeMillis()-start));
        //FileUtil.writeCsvFile(resultFilepath, apriori.getObjectList());
        }
    }
    
    public static void main(String[] args) {

        final String standardConfigFilepath = rootPath + "\\config\\categorizeStandardsConfig.txt";
        Preprocessor.standardConfigFilepath = standardConfigFilepath;
        
        // apriori.printObjectList();
        // FileUtil.writeFile(resultFilepath);
        trainwithRandomValue();
        System.exit(-1);
    }
}
