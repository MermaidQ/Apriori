package com.qiuzi.gtja.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiuzi.gtja.entity.ParamSetObject;
import com.qiuzi.gtja.entity.User;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class FileUtil {
    /**
     * ����ǰ�е�����ӳ�䵽����ĸ�������
     * 
     * @param line:��ǰ������
     * @return ���ݵ�ǰ�����ݷ�װ�Ķ���
     */
    public User getUser(String line) {

        User user = new User();

        String[] attributes = line.replaceAll("\"", "").split(";");
        user.setAge(attributes[0]);
        user.setJob(attributes[1]);
        user.setMarital(attributes[2]);
        user.setEducation(attributes[3]);
        user.setCredit(attributes[4]);
        user.setBalance(attributes[5]);
        user.setHousing(attributes[6]);
        user.setLoan(attributes[7]);
        user.setDuration(attributes[11]);
        user.setPoutcome(attributes[15]);
        user.setY(attributes[16]);

        return user;
    }

    // ���ļ������ļ��е����ݰ��зֿ�����list����
    public List<String> readFile(String filePath) {

        List<String> list = new ArrayList<String>();

        if (filePath != null) {
            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileInputStream = new FileInputStream(filePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String line = null;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    list.add(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * ��excel�ļ�
     * 
     * @param filePath���ļ�·��
     * @return ����user�����б�
     */
    public List<User> readCsvFile(String filePath) {

        List<User> userList = new ArrayList<User>();

        if (filePath != null) {
            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileInputStream = new FileInputStream(filePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String line = null;
            try {
                bufferedReader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    // System.out.println(line);

                    User user = this.getUser(line);
                    if (user != null) {
                        userList.add(user);
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return userList;
    }

    public static void writeCsvFile(String filePath, Map<Integer, List<ParamSetObject>> objectList) {

        try {
            // 打开文件
            System.out.println("Starting to output the result to " + filePath);
            WritableWorkbook book = Workbook.createWorkbook(new File(filePath));
            // 生成名为“第一页”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet("第一页", 0);
            // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
            // 以及单元格内容为test
            Label label1 = new Label(0, 0, "项集");
            Label label2 = new Label(1, 0, "支持度");
            Label label3 = new Label(2, 0, "置信度");
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label1);
            sheet.addCell(label2);
            sheet.addCell(label3);
            
            /*jxl.write.Number number = new jxl.write.Number(1, 0, 789.123);
            sheet.addCell(number);
            jxl.write.Label s = new jxl.write.Label(1, 2, "三十三");
            sheet.addCell(s);*/

            int line = 1;
            int size = objectList.size();
            for (int i = 1; i <= size; i++) {
                if (objectList.containsKey(i) && objectList.get(i) != null) {
                    
                    List<ParamSetObject> list = objectList.get(i);
                    int list_size = list.size();
                    // System.out.println("*****" + i + "项集包含" + list_size +"项*****");
                    for (int j = 0; j < list_size; j++) {
                        
                        ParamSetObject pso = list.get(j);
                        jxl.write.Number support = new jxl.write.Number(1, line, pso.getSupport());
                        sheet.addCell(support);
                        jxl.write.Number confidence = new jxl.write.Number(2, line, pso.getConfidence());
                        sheet.addCell(confidence);

                        Map<String, String> paramSetObject = pso.getparamSetObject();
                        String item = "";
                        for (String key : paramSetObject.keySet()) {
                            item += "(" + key + "," + paramSetObject.get(key) + ") ";
                        }
                        Label Item = new Label(0, line++, item);
                        sheet.addCell(Item);
                    }
                }
            }
            // 写入数据并关闭文件
            book.write();
            book.close();
        } catch (

        Exception e) {
            System.out.println(e);
        }

    }
    

    public void writeTxtFile(String filePath, HashMap<Integer, List<ParamSetObject>> objectList) {

        try {
            String content = "";
            for (int i = 1; i <= objectList.size(); i++) {
                if (objectList.containsKey(i)) {
                    List<ParamSetObject> list = objectList.get(i);
                    int list_size = list.size();
                    content += "*****" + i + "项集包含" + list_size + "项*****"+System.getProperty("line.separator"); ;
                    for (int j = 0; j < list_size; j++) {
                        
                        ParamSetObject pso = list.get(j);
                        
                        Map<String, String> paramSetObject = pso.getparamSetObject();
                        for (String key : paramSetObject.keySet()) {
                            content += "(" + key + "," + paramSetObject.get(key) + ") ";
                        }
                        content += pso.getSupport() + " ";
                        content += pso.getConfidence();
                        content += System.getProperty("line.separator");
                    }
                }
            }

            File file = new File(filePath);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            else{
                file.delete();
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
