package com.qiuzi.gtja.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;

import com.qiuzi.gtja.entity.User;
import com.qiuzi.gtja.mapreduce.FreqSetGenMapper;
import com.qiuzi.gtja.mapreduce.FreqSetGenReducer;
import com.qiuzi.gtja.mapreduce.PreprocessMapper;
import com.qiuzi.gtja.mapreduce.PreprocessReducer;

public class mr_Apriori extends Apriori{

    private static String inputPath;
    private static String outputPath;
    
    private static Set<Set<String>> initFreqSets;
    
    public mr_Apriori() {}
    
    public mr_Apriori(String[] param, double supportThreshold, String inputPath, String outputPath, String configPath) {
        
        super(new ArrayList<User>(), param, supportThreshold, null);
        
        mr_Apriori.inputPath = inputPath;
        mr_Apriori.outputPath = outputPath;
        Preprocessor.standardConfigFilepath = configPath;
        
        initFreqSets = new HashSet<Set<String>>();
    }
    
    public static void addUser(User user){
        users.add(user);
    }
    
    public Set<Set<String>> getInitFreqSets() {
        return initFreqSets;
    }
    
    public List<User> getUserlist() {
        return users;
    }
    
    public void addtoUserinParamGroup(Map<String, Set<Integer>> map){
        userinParamGroup.putAll(map);
    }

    public boolean getFrequentSet(int k) throws IOException, ClassNotFoundException, InterruptedException{

        Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf);
        job.setJobName("FreqSetGen"+k);
        
        //设置整个job所用的那些类在哪个jar包
        job.setJarByClass(mr_Apriori.class);
        
        //本job使用的mapper和reducer的类
        job.setMapperClass(FreqSetGenMapper.class);
        job.setReducerClass(FreqSetGenReducer.class);
        
        //指定reduce的输出数据kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
       
        //指定mapper的输出数据kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        
        //指定要处理的输入数据存放路径
        FileInputFormat.setInputPaths(job,outputPath+"frequentSet"+(k-1));
        
        //指定处理结果的输出数据存放路径
        FileOutputFormat.setOutputPath(job, new Path(outputPath+"frequentSet"+k));
        
        //将job提交给集群运行 
        return job.waitForCompletion(true);
    }
    
    public void preprocess() throws IOException, ClassNotFoundException, InterruptedException{
        
        Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf);
        job.setJobName("Preprocess");
        
        //设置整个job所用的那些类在哪个jar包
        job.setJarByClass(mr_Apriori.class);
        
        //本job使用的mapper和reducer的类
        job.setMapperClass(PreprocessMapper.class);
        job.setReducerClass(PreprocessReducer.class);
        
        //指定reduce的输出数据kv类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        
        //指定mapper的输出数据kv类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        
        //指定要处理的输入数据存放路径
        FileInputFormat.setInputPaths(job,inputPath);
        
        //指定处理结果的输出数据存放路径
        FileOutputFormat.setOutputPath(job, new Path(outputPath+"frequentSet1"));
        
        //将job提交给集群运行 
        job.waitForCompletion(true);
    }
}