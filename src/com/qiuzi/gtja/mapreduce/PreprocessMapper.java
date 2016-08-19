package com.qiuzi.gtja.mapreduce;

import com.qiuzi.gtja.util.Preprocessor;
import com.qiuzi.gtja.util.mr_Apriori;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.qiuzi.gtja.entity.User;
import com.qiuzi.gtja.util.FileUtil;

public class PreprocessMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
    
    private class ReturnFormat{
        
        String line;
        LongWritable yes;
        
        public String getLine() {
            return line;
        }
        public void setLine(String line) {
            this.line = line;
        }
        public LongWritable getYes() {
            return yes;
        }
        public void setYes(LongWritable yes) {
            this.yes = yes;
        }
    }
    
    private ReturnFormat reflect(User user){
        
        ReturnFormat rf = null;
        
        String line = null;
        Class<? extends User> cls = user.getClass();  
        Field[] fields = cls.getDeclaredFields();
        
        for(int i=0; i<fields.length; i++){  
            
            fields[i].setAccessible(true);
            try {
                String key = fields[i].getName();
                String value = (String) fields[i].get(user);
                if(key.equals("y")){
                    rf = new ReturnFormat();
                    rf.setYes(new LongWritable(value.equals("yes")?1:0));
                }
                else{
                    line += fields[i].getName() + ":" + fields[i].get(user) + " ";
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        rf.setLine(line);
        return rf;
    }
    
    /**
     * @description 按行读入user数据，将其封装为user对象，按“属性名：属性值”的形式将其各个属性拼接成字符串作为key，value为它是否为yes。将数据集的大小加1
     * @description get record of user by line, and encapsulate it as an object of User. Return format: key is a string that is
     * composed of "paramname:paramvalue", value is 1 if the param 'y' is yes and 0 elsewhere. 
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
        
        //从value中读取数据形成User对象，将user规范化
        User user = FileUtil.getUser(value.toString());
        Preprocessor.preprocess(user);
        
        mr_Apriori.addUser(user);
        
        //将user对象的各个属性按照某个格式写入context
        ReturnFormat rf = this.reflect(user);
        String line = rf.getLine().trim();
        
        String paramValues[] = line.split(" ");
        for(String param:paramValues){
            context.write(new Text(param), rf.getYes());
        }
    }
}
