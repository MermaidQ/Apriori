package com.qiuzi.gtja.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.qiuzi.gtja.util.StringUtil;


public class FreqSetGenMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws InterruptedException, IOException {

        // 从value中获取到标签列表，将之split后形成params集合，将该集合传递给Reducer
        // FIXME pso初始化需要知道value:Text的形式，来提取数据
        String param[] = value.toString().trim().split(" ");
        String line = StringUtil.toString(param, 0, param.length-3);
        
        context.write(new Text(line), new LongWritable(1));
    }
}
