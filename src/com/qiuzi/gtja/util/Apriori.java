package com.qiuzi.gtja.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.qiuzi.gtja.entity.ParamSetObject;
import com.qiuzi.gtja.entity.User;

//根据预处理好的数据集，形成项集集合objectList，并打印
public class Apriori{

    protected static String[] param;// 属性名称数组
    protected static double SUPPORT_THRESHOLD;// 支持度阈值

    protected static List<User> users;
    protected static Map<String, Set<Integer>> userinParamGroup;
    
    protected String TABLENAME = null;// 操作的表名称
    protected int usersetSize;// 用户集合大小
    protected int maxParamCount;// 属性个数
    
    private Map<Integer, List<ParamSetObject>> objectList;// objectList为结果集合，key对应属性个数k，value对应有k个属性的不同组合及其对应的ROI

    public Apriori() {
    }

    /*
     * @description 初始化Apriori，将支持度大于阈值的单属性加入结果集合
     * 
     * @param param:属性名数组
     * 
     * @param supportThreshold:支持度阈值
     * 
     * @param usersetSize：用户集合大小
     */
    public Apriori(List<User> users, String[] param, double supportThreshold, String TABLENAME) {

        Apriori.users = users;
        Apriori.param = param;
        Apriori.SUPPORT_THRESHOLD = supportThreshold;
        Apriori.userinParamGroup = new HashMap<String, Set<Integer>>();

        this.TABLENAME = TABLENAME;
        this.usersetSize = users.size();
        this.maxParamCount = param.length;
        this.objectList = new HashMap<Integer, List<ParamSetObject>>();// k项集集合的列表，其中的每个元素都是一个k频繁项集及其对应置信度
    }

    private void initwithDB() {

        List<ParamSetObject> paramSetList = new ArrayList<ParamSetObject>();

        // 对于每一个属性名
        for (int i = 0; i < param.length; i++) {

            // System.out.println("Analyzing param "+param[i]);
            String paramsql = "select distinct " + param[i] + " from " + TABLENAME;
            List<Object> paramValueList = HibernateUtil.query(paramsql, null);// 记录不同的属性取值

            for (Object o : paramValueList) {// 对于每一个属性值，构造1项集

                String paramValue = String.valueOf(o);
                Set<String> set = new HashSet<String>();
                set.add(param[i]+"="+paramValue);

                String sql = getSql(set);
                double total = getTotalCount(sql);// 有该属性值的对象个数

                double support = total / usersetSize;
                if (support >= SUPPORT_THRESHOLD) {// 支持度大于阈值
                    double yes = getYesCount(sql);
                    double confidence = yes / total;// 属性值对应的置信度
                    paramSetList.add(ParamSetObject.getParamSetObject(param[i], paramValue, support, confidence));
                }
            }
        }
        objectList.put(1, paramSetList);
    }

    /**
     * @description user列表对象转化为索引的形式：每一个“属性名：属性值”作为map中的key，user的id作为value
     * @description transfer user list into an indexed form： take a string of "paramname:paramvalue" as the key in the map, and the id of user as the value
     * @param users a list of objects of the class User
     * @return an indexed map of user ids
     * @see com.qiuzi.gtja.entity.User
     */
    public Map<String, Set<Integer>> getIndexedUsers(List<User> users) {

        Map<String, Set<Integer>> userinParamGroup = new HashMap<String, Set<Integer>>();// 该map的键为属性取值，值为包含该属性取值的user对象编号集合

        for (int i = 0; i < users.size(); i++) {

            User user = users.get(i);
            Class<? extends User> cls = user.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {

                Field f = fields[j];
                f.setAccessible(true);
                String key = f.getName();
                if (!key.contains("purchase") && !key.equals("id") && !key.equals("y")) {
                    try {
                        key += "=" + f.get(user);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    // 若已有当前属性取值，则索引当前对象，否则将该属性值添加到map中
                    if (userinParamGroup.containsKey(key)) {
                        userinParamGroup.get(key).add(i);
                    } else {
                        Set<Integer> set = new HashSet<Integer>();
                        set.add(i);
                        userinParamGroup.put(key, set);
                    }
                }
            }
        }
        /*for(String s:userinParamGroup.keySet()){
            //sum += userinParamGroup.get(s).size();
            System.out.println(s+" "+userinParamGroup.get(s).size()+" "+userinParamGroup.get(s));
        }*/
        return userinParamGroup;
    }

    private void initwithoutDB() {

        List<ParamSetObject> paramSetList = new ArrayList<ParamSetObject>();// 保存1项集的列表

        userinParamGroup = getIndexedUsers(users);// 按索引存储的user编号序列
        Iterator<Entry<String, Set<Integer>>> iterator = userinParamGroup.entrySet().iterator();// 遍历每一个索引，判断其是否能作为1项集
        while (iterator.hasNext()) {
            
            String line = iterator.next().toString();
            //System.out.println(line.split("=")[0]+"="+line.split("=")[1]+":"+line.split("=")[2].split(",").length);
            String array[] = line.split("=");
            String key = array[0] + "=" + array[1];// 索引值，由属性名+" "+属性值构成
            /*
             * for(String i:array[1].substring(1, array[1].length()-1).split(
             * ", ")){ System.out.print(i); }
             */
            String[] userFlags = array[2].substring(1, array[2].length() - 1).split(", ");

            double total = userFlags.length;// 有该属性值的对象个数
            double support = total / usersetSize;

            if (support >= SUPPORT_THRESHOLD) {

                Set<String> params = new HashSet<String>();
                params.add(key);

                double avgSum = 0.0;
                for (String i : userFlags) {

                    User user = users.get(Integer.parseInt(i));
                    int amount = user.getPurchaseAmount();
                    if (amount != 0)
                        avgSum += user.getPurchaseSum() / amount;
                }
              double avgValue = avgSum / total;
                paramSetList.add(new ParamSetObject(params, support, avgValue));
            }
        }
        objectList.put(1, paramSetList);

        System.out.println(1+" "+paramSetList.size());
    }

    /*
     * 初始化频繁项集，初始化1项集。选择出数据集中所有的属性，判断该属性的支持度是否大于阈值SUPPORT_THRESHOLD
     * 若是，则加入1项集。并记录其对应的置信度confidence
     */
    public void initResultset() {

        System.out.println("Initializing frequent itemset...");

        if (this.TABLENAME == null) {
            initwithoutDB();
        } else {
            initwithDB();
        }
    }

    public void printObjectList() {

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

        for (int i = 0; i < Apriori.param.length; i++) {
            if (Apriori.param[i].equals(obj)) {
                return i;
            }
        }
        return Apriori.param.length;
    }

    private boolean isLargerThan(Set<String> firstResultSet, Set<String> prevResultSet) {
        
        Iterator<String> itFirst = firstResultSet.iterator();
        String obj = itFirst.next();
        int first = getOrderNumber(obj.split("=")[0]);

        int largest = 0;
        Iterator<String> iPrev = prevResultSet.iterator();
        while (iPrev.hasNext()) {
            String key = iPrev.next();
            int present = getOrderNumber(key.split("=")[0]);
            if (present > largest) {
                largest = present;
            }
        }
        return largest < first ? true : false;
    }

    /**
     * @description resultSetK为一个项集的标签集合，通过userinParamGroup确定每个标签对应的用户id
     * 取交集获取有整个标签组合的用户，判断其支持度，将支持度大于某阈值的加入
     * @param resultSetK 某个项集集合
     * @return 
     */
    private ParamSetObject getParamSetObject(Set<String> resultSetK) {

        if (TABLENAME == null) {// 当数据库表名为空，则不调用数据库进行支持度和置信度的计算

            Iterator<String> iterResultSetK = resultSetK.iterator();

            String originKey = iterResultSetK.next();
            
            Set<Integer> originSet = new HashSet<Integer>(userinParamGroup.get(originKey));
            //int size = originSet.size();
            while (iterResultSetK.hasNext()) {
                String key = iterResultSetK.next();
                Set<Integer> set = userinParamGroup.get(key);
                //int size1 = set.size();
                originSet.retainAll(set);
            }
            double total = originSet.size();
            double support = total / usersetSize;
            if (support >= SUPPORT_THRESHOLD) {
                double avgPurchase = getAvgPurchase(originSet);
                return new ParamSetObject(resultSetK, support, avgPurchase);
            }
        } else {
            String sql = getSql(resultSetK);
            double total = getTotalCount(sql);

            double support = total / usersetSize;
            if (support >= SUPPORT_THRESHOLD) {
                double yes = getYesCount(sql);
                double confidence = yes / (double) total;
                return new ParamSetObject(resultSetK, support, confidence);
            }
        }
        return null;
    }

    // 迭代根据k-1项集和1项集产生k项集
    private <T> List<ParamSetObject> addFrequentSetk(List<ParamSetObject> prevResultSetList, List<ParamSetObject> firstResultSetList ) {
        //
        // printObjectList(k - 1);

        List<ParamSetObject> frequentSetkList = new ArrayList<ParamSetObject>();
        
        for (ParamSetObject prs : prevResultSetList) {
            for (ParamSetObject frs : firstResultSetList) {

                Set<String> prevResultSet = prs.getparamSetObject();
                Set<String> firstResultSet = frs.getparamSetObject();

                // 若prevResultSet的最后一个元素小于firstResultSet的元素
                if (isLargerThan(firstResultSet, prevResultSet)) {

                    Set<String> resultSetK = new HashSet<String>();
                    resultSetK.addAll(prevResultSet);
                    resultSetK.addAll(firstResultSet);

                    frequentSetkList.add(getParamSetObject(resultSetK));
                }
            }
        }
        return frequentSetkList;
        //System.out.println(k+" "+frequentSetkList.size());
    }
    
    /**
     * @description 用一个k-1项集与每一个一项集进行组合得到k项集，计算其支持度和置信度。将支持度大于阈值的项集封装成ParamSetObject，加入到list中返回
     * @description take the union of the k-1 frequent set and every 1 frequent set as the k frequent set. 
     * if the value of 'support' is larger than the Threshold, turn the format of the set into a ParamSetObject object
     * and return as a list. 
     * @param prevResultSet an element of the k-1 frequent set
     * @param firstResultSets collection of the 1 frequent sets
     * @return 
     * @see com.qiuzi.gtja.entity.ParamSetObject
     */
    public List<ParamSetObject> getCorrObjects(Set<String> prevResultSet, Set<Set<String>> firstResultSets) {

        // printObjectList(k - 1);
        List<ParamSetObject> frequentSetkList = new ArrayList<ParamSetObject>();

        for (Set<String> firstResultSet : firstResultSets) {
            
            // 若prevResultSet的最后一个元素小于firstResultSet的元素
            if (isLargerThan(firstResultSet, prevResultSet)) {

                Set<String> resultSetK = new HashSet<String>();
                resultSetK.addAll(prevResultSet);
                resultSetK.addAll(firstResultSet);

                frequentSetkList.add(getParamSetObject(resultSetK));
            }
        }

        return frequentSetkList;
        // System.out.println(k+" "+frequentSetkList.size());
        // System.out.println(k+" "+frequentSetkList.size());
    }

    // 产生2至maxParamCount项集
    public void generateObjectList() {
        for (int paramCount = 2; paramCount <= maxParamCount; paramCount++) {
            List<ParamSetObject> frequentSetkList = this.addFrequentSetk(this.objectList.get(paramCount - 1),this.objectList.get(1));
            objectList.put(paramCount, frequentSetkList);
            System.out.println(paramCount+" "+objectList.get(paramCount).size());
        }
    }

    // 遍历集合中每个user对象，求其购置的平均价值，得到集合的平均价值
    private double getAvgPurchase(Set<Integer> originSet) {
        if (originSet.size() == 0)
            return 0;
        double avgPurchase = 0;
        Iterator<Integer> iterOringinSet = originSet.iterator();
        while (iterOringinSet.hasNext()) {
            User user = users.get(iterOringinSet.next());
            int amount = user.getPurchaseAmount();
            if (amount != 0) {
                avgPurchase += user.getPurchaseSum() / amount;
            }
        }
        return avgPurchase / originSet.size();
    }

    /*
     * @description 判断mapk是否包含的map1,若包含则返回true，否则返回false
     */
    /*private boolean contains(Map<String, String> mapk, Map<String, String> map1) {

        Set<String> set1 = map1.keySet();
        Iterator<String> it = set1.iterator();
        Object obj = it.next();
        return mapk.containsKey(obj);
    }*/

    private String getSql(Set<String> resultSetK) {

        String sql = "select count(*) from " + TABLENAME + " where ";

        Iterator<String> iterator = resultSetK.iterator();
        while (iterator.hasNext()) {

            String param[] = iterator.next().split("=");
            String paramName = param[0];
            String paramValue = param[1];
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
        Set<String> paramSetObject = pso.getparamSetObject();
        for (String key : paramSetObject) {
            String param[] = key.split("=");
            System.out.print("(" + param[0] + ", " + param[1] + "), ");
        }
        System.out.println();
    }

    public Map<Integer, List<ParamSetObject>> getObjectList() {
        return objectList;
    }

    public static double getSUPPORT_THRESHOLD() {
        return SUPPORT_THRESHOLD;
    }

    public int getUsersetSize() {
        return usersetSize;
    }

    public void setUsersetSize(int usersetSize) {
        this.usersetSize = usersetSize;
    }

    public int getMaxParamCount() {
        return maxParamCount;
    }
}
