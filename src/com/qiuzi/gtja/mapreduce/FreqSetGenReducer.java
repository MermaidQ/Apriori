package com.qiuzi.gtja.mapreduce;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import com.qiuzi.gtja.util.StringUtil;
import com.qiuzi.gtja.util.mr_Apriori;
import com.qiuzi.gtja.entity.ParamSetObject;

public class FreqSetGenReducer extends Reducer<Text, LongWritable, Text, LongWritable>{

    @Override
    public void reduce(Text text, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException{
        
        Set<String> set = StringUtil.toSet(text, " ");
        
        mr_Apriori mr_apriori = new mr_Apriori();
        List<ParamSetObject> list = mr_apriori.getCorrObjects(set, mr_apriori.getInitFreqSets());
        
        for(ParamSetObject pso:list){
            //将集合转为一个字符串
            Set<String> params = pso.getparamSetObject();
            context.write(new Text(StringUtil.toString(params)+new DoubleWritable(pso.getSupport()).toString()+" "+new DoubleWritable(pso.getConfidence()).toString()), new LongWritable(1));
        }
    }
}