package com.qiuzi.gtja.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.qiuzi.gtja.util.StringUtil;
import com.qiuzi.gtja.util.mr_Apriori;

public class PreprocessReducer extends Reducer<Text, LongWritable, Text, LongWritable>{

    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException{
        
        long count = 0, yesCount = 0; 
        
        //遍历value的list，进行累加求和
        for(LongWritable value:values){
            yesCount += value.get();
            count ++;
        }
        
        mr_Apriori mr_apriori = new mr_Apriori();
        
        double SUPPORT_THRESHOLD = mr_Apriori.getSUPPORT_THRESHOLD();
        double support = (double)count/mr_apriori.getUsersetSize()/*conf.getLong("dataset.size", 1)*/;
        
        if(support >= SUPPORT_THRESHOLD){
            
            double confidence = (double)yesCount/count;
            
            DoubleWritable supWritable = new DoubleWritable(support);
            DoubleWritable confWritable = new DoubleWritable(confidence);
            
            String s = key.toString().trim()+" " + supWritable.toString() + " " + confWritable.toString();
            context.write(new Text(s), new LongWritable(1));
            
            //将当前项集添加到一项集集合中
            mr_apriori.getInitFreqSets().add(StringUtil.toSet(key));
            
            /*//将support和confidence封装成对象mr_ReturnFormat
            mr_ReturnFormat rf = new mr_ReturnFormat(new DoubleWritable(support), new DoubleWritable(confidence));*/
            
        }
    }
}