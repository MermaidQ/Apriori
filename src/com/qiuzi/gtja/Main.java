package com.qiuzi.gtja;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.qiuzi.gtja.config.Config;
import com.qiuzi.gtja.entity.ParamSetObject;
import com.qiuzi.gtja.entity.User;
import com.qiuzi.gtja.util.Apriori;
import com.qiuzi.gtja.util.DatabaseUtil;
import com.qiuzi.gtja.util.FileUtil;
import com.qiuzi.gtja.util.HibernateUtil;
import com.qiuzi.gtja.util.Preprocessor;

public class Main {

    private static List<User> user;

    public static void main(String[] args) {
        
        long starTime = System.currentTimeMillis();
        final String rootPath = new File("").getAbsolutePath();
        /*
         * final String csvFilepath = rootPath + "\\data\\bank.csv"; final
         * String resultFilepath = rootPath + "\\data\\result.csv";
         */
        final String csvFilepath = rootPath + "\\data\\bank-full.csv";
        final String resultFilepath = rootPath + "\\data\\result-full.csv";
        // final String testsetFilepath = rootPath + "\\data\\result-full.csv";

        final String TABLENAME = "roi_user";
        final float SUPPORT_THRESHOLD = (float) 0.05;
        final String[] PARAM_NAME = { "age", "job", "marital", "education", "credit", "balance", "housing", "loan",
                "duration", "poutcome" };

        user = new FileUtil().readCsvFile(csvFilepath);
        long readTime = System.currentTimeMillis();
        
        // HibernateUtil.getSessionFactory();
        long midTime = 0;
        try {
            final String standardConfigFilepath = rootPath + "\\config\\categorizeStandardsConfig.txt";
            Preprocessor preprocess = new Preprocessor(user, standardConfigFilepath);

            //清空数据库中已有数据，并置id的其实值为1
            //HibernateUtil.excuteBySql("truncate table "+TABLENAME);
            //HibernateUtil.excuteBySql("alter table "+TABLENAME+" AUTO_INCREMENT=1");
            //DatabaseUtil.add(user);
            
            Apriori apriori = new Apriori(PARAM_NAME, SUPPORT_THRESHOLD, TABLENAME, user.size());
            apriori.initializeResultset();
            apriori.generateObjectList();
            
            midTime = System.currentTimeMillis();
            
            FileUtil.writeCsvFile(resultFilepath, apriori.getObjectList());
        }
        // apriori.printObjectList();
        // FileUtil.writeFile(resultFilepath);
        finally {
            long endTime = System.currentTimeMillis();
            System.out.println("数据录入花时：" + (readTime - starTime) / 1000 + "s");
            System.out.println("模型训练花时：" + (midTime - readTime) / 1000 + "s");
            System.out.println("写入训练结果花时：" + (endTime - midTime) / 1000 + "s");
        }
        // System.out.println("筛选项集花时："+(endTime-starTime)/1000+"s");
        
        System.exit(-1);
    }
}
