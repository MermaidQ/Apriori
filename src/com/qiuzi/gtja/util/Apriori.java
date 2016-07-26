package com.qiuzi.gtja.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qiuzi.gtja.entity.ParamSetObject;

//根据预处理好的数据集，形成项集集合objectList，并打印
public class Apriori {

    private String[] param;// 属性名称数组
    private float SUPPORT_THRESHOLD;// 支持度阈值
    private String TABLENAME;//操作的表名称
    private int usersetSize;// 用户集合大小
    private int maxParamCount;// 属性个数
    private Map<Integer, List<ParamSetObject>> objectList;// objectList为结果集合，key对应属性个数k，value对应有k个属性的不同组合及其对应的ROI

    /*
     * @description 初始化Apriori，将支持度大于阈值的单属性加入结果集合
     * 
     * @param param:属性名数组
     * 
     * @param supportThreshold:支持度阈值
     * 
     * @param usersetSize：用户集合大小
     */
    public Apriori(String[] param, float supportThreshold, String TABLENAME, int usersetSize) {

        this.param = param;
        this.SUPPORT_THRESHOLD = supportThreshold;
        this.TABLENAME = TABLENAME;
        this.usersetSize = usersetSize;
        this.maxParamCount = param.length;
        this.objectList = new HashMap<Integer, List<ParamSetObject>>();// k项集集合的列表，其中的每个元素都是一个k频繁项集及其对应置信度
    }

    // 产生2至maxParamCount项集
    public void generateObjectList() {

        for (int paramCount = 2; paramCount <= maxParamCount; paramCount++) {
            this.addFrequentSetk(paramCount);
        }
    }

    // 使用树结构产生候选项集
    public void generateObjectListwithTree() {
        
        
    }
    
    /*
     * 初始化频繁项集，初始化1项集。选择出数据集中所有的属性，判断该属性的支持度是否大于阈值SUPPORT_THRESHOLD
     * 若是，则加入1项集。并记录其对应的置信度confidence
     */
    public void initializeResultset() {

        System.out.println("Initializing frequent itemset...");
        List<ParamSetObject> paramSetList = new ArrayList<ParamSetObject>();

        // 对于每一个属性名
        for (int i = 0; i < param.length; i++) {

            // System.out.println("Analyzing param "+param[i]);
            String paramsql = "select distinct " + param[i] + " from "+TABLENAME;
            List<Object> paramValueList = HibernateUtil.query(paramsql, null);// 记录不同的属性取值

            for (Object o : paramValueList) {// 对于每一个属性值，构造1项集

                String paramValue = String.valueOf(o);
                Map<String, String> map = new HashMap<String, String>();
                map.put(param[i], paramValue);

                String sql = getSql(map);
                float total = getTotalCount(sql);// 有该属性值的对象个数

                float support = total / usersetSize;
                if (support >= SUPPORT_THRESHOLD) {// 支持度大于阈值
                    float yes = getYesCount(sql);
                    float confidence = yes / total;// 属性值对应的置信度
                    paramSetList.add(ParamSetObject.getParamSetObject(param[i], paramValue, support, confidence));
                }
            }
        }
        objectList.put(1, paramSetList);
    }

    public void printObjectList() {

        int size = objectList.size();
        for (int i = 1; i <= param.length; i++) {
            if (objectList.containsKey(i)) {
                List<ParamSetObject> list = objectList.get(i);
                int list_size = list.size();
                System.out.println("*****" + i + "项集包含" + list_size + "项*****");
                for (int j = 0; j < list_size; j++) {
                    print(list.get(j));
                }
            }
        }
    }

    private int getOrderNumber(String obj) {

        for (int i = 0; i < param.length; i++) {
            if (param[i].equals(obj)) {
                return i;
            }
        }
        return param.length;
    }

    private boolean isLargerThan(Map<String, String> prevResultSet, Map<String, String> firstResultSet) {

        Set<String> setFirst = firstResultSet.keySet();
        Iterator<String> itFirst = setFirst.iterator();
        String obj = itFirst.next();
        int first = getOrderNumber(obj);

        int largest = 0;
        Set<String> setPrev = prevResultSet.keySet();
        Iterator<String> iPrev = setPrev.iterator();
        while (iPrev.hasNext()) {
            String key = iPrev.next();
            int present = getOrderNumber(key);
            if (present > largest) {
                largest = present;
            }
        }
        return largest < first ? true : false;
    }

    // 迭代根据k-1项集和1项集产生k项集
    private <T> void addFrequentSetk(int k) {

        // printObjectList(k - 1);
        List<ParamSetObject> prevResultSetList = this.objectList.get(k - 1);
        List<ParamSetObject> firstResultSetList = this.objectList.get(1);

        List<ParamSetObject> frequentSetkList = new ArrayList<ParamSetObject>();

        int count = 1;
        List<String> totalSql = new ArrayList<String>();
        List<String> yesSql = new ArrayList<String>();
        List<Map<String, String>> resultSet = new ArrayList<Map<String, String>>(); 
        
        for (ParamSetObject prs : prevResultSetList) {
            for (ParamSetObject frs : firstResultSetList) {

                Map<String, String> prevResultSet = prs.getparamSetObject();
                Map<String, String> firstResultSet = frs.getparamSetObject();

                // 若prevResultSet的最后一个元素小于firstResultSet的元素
                if (isLargerThan(prevResultSet, firstResultSet)) {

                    Map<String, String> resultSetK = new HashMap<String, String>();
                    resultSetK.putAll(prevResultSet);
                    resultSetK.putAll(firstResultSet);
                    resultSet.add(resultSetK);
                    
                    String sql = getSql(resultSetK);
                    /*float total = getTotalCount(sql);
                    float yes = getYesCount(sql);*/
                    
                    totalSql.add(sql);
                    yesSql.add(sql+" and y = 'yes'");
                    
                    if(count%1000==0){
                        
                        List<List<T>> totalList = HibernateUtil.query(totalSql, null);
                        List<List<T>> yesList = HibernateUtil.query(yesSql, null);
                        
                        for(int i = 0;i < resultSet.size();i++){
                            int total = (int)totalList.get(i).get(0);
                            float support =  total/ usersetSize;
                            if (support >= SUPPORT_THRESHOLD) {
                                int yes = (int) yesList.get(i).get(0);
                                float confidence = yes / total;
                                frequentSetkList.add(new ParamSetObject(resultSet.get(i), support, confidence));
                            }
                        }
                        totalList.clear();
                        yesList.clear();
                        resultSet.clear();
                    }
                    count ++;
                }
            }
        }
        objectList.put(k, frequentSetkList);
    }

    /*
     * @description 判断mapk是否包含的map1,若包含则返回true，否则返回false
     */
    private boolean contains(Map<String, String> mapk, Map<String, String> map1) {

        Set<String> set1 = map1.keySet();
        Iterator<String> it = set1.iterator();
        Object obj = it.next();
        return mapk.containsKey(obj);
    }

    private String getSql(Map<String, String> resultSetK) {

        String sql = "select count(*) from "+TABLENAME+" where ";

        Set<String> set = resultSetK.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {

            String paramName = iterator.next();
            String paramValue = resultSetK.get(paramName);
            if (paramName.equals("age") || paramName.equals("balance") || paramName.equals("duration"))
                sql += paramName + " = " + paramValue + " and ";
            else
                sql += paramName + " = '" + paramValue + "' and ";
        }
        return sql.substring(0, sql.length() - 4);
    }

    private int getTotalCount(String sql) {
        return Integer.parseInt(String.valueOf(HibernateUtil.query(sql, null).get(0)));
    }

    private int getYesCount(String sql) {
        sql += " and y = 'yes'";
        return Integer.parseInt(String.valueOf(HibernateUtil.query(sql, null).get(0)));
    }

    private void print(ParamSetObject pso) {

        System.out.print(pso.getSupport() + " ");
        System.out.print(pso.getConfidence() + " ");
        Map<String, String> paramSetObject = pso.getparamSetObject();
        for (String key : paramSetObject.keySet()) {
            System.out.print("(" + key + ", " + paramSetObject.get(key) + "), ");
        }
        System.out.println();
    }

    public Map<Integer, List<ParamSetObject>> getObjectList() {
        return objectList;
    }
}
