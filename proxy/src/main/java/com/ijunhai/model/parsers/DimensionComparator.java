package com.ijunhai.model.parsers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DimensionComparator implements Comparator<String> {

    private Map<String, Map<String, String>> keyDimensionHashMap;
    private Map<String, List<Map<String, String>>> keyMetricMap;
    private int num = 1;
    private DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
    private DateTimeFormatter mysqlFormat = DateTimeFormat.forPattern("yyyyMMdd");

    private String granularity;
    private List<String> orderByList;


    public DimensionComparator(
            Map<String, List<Map<String, String>>> keyMetricMap,
            Map<String, Map<String, String>> keyDimensionHashMap,
            String granularity,
            List<String> orderByList) {
        this.keyDimensionHashMap = keyDimensionHashMap;
        this.keyMetricMap = keyMetricMap;
        this.granularity = granularity;
        this.orderByList = orderByList;

    }

//    @Override
//    public int compare(String o1, String o2) {
//        return 0;
//    }


    //Comparator<String>，这里的泛型声明的是String，表示传进来比较的是String类型，compare的两个参数，就是要进行比较的String
    public int compare(String a, String b) {
        //指定orderByList排序，如果选择日期的话，比如 day desc
        if (orderByList.size() != 0 && orderByList != null) {
            int i = 0;
            num = compareTo(a, b, i);
            //没有指定orderByList，默认按照granularity排序
        } else if (orderByList.size() == 0 && granularity.length() != 0) {
            DateTime timeC;
            DateTime timeD;
            if (!keyDimensionHashMap.get(a).get("date").contains("-")) {
                timeC = DateTime.parse(keyDimensionHashMap.get(a).get("date"), mysqlFormat);
            } else {
                timeC = DateTime.parse(keyDimensionHashMap.get(a).get("date"), format);
            }
            if (!keyDimensionHashMap.get(b).get("date").contains("-")) {
                timeD = DateTime.parse(keyDimensionHashMap.get(b).get("date"), mysqlFormat);
            } else {
                timeD = DateTime.parse(keyDimensionHashMap.get(b).get("date"), format);
            }
            if (timeC.compareTo(timeD) <= 0) {
                num = 1;
            } else {
                num = -1;
            }

        }
        return num;
    }


    //第二个参数i，只是为了标记orderByList的角标
    public int compareTo(String a, String b, int i) {

        //取出第一个day desc 中的date
        String[] split = orderByList.get(i).split(" ", 2);
        //day
        String orderByKey = split[0].toLowerCase();

        if (!orderByKey.equals("day") && !orderByKey.equals("hour") && !orderByKey.equals("minute")) {
            String orderByValueA = keyDimensionHashMap.get(a).get(orderByKey);
            String orderByValueB = keyDimensionHashMap.get(b).get(orderByKey);
            if (orderByValueA == null && orderByValueB == null) {
                StringBuffer tmpa = new StringBuffer();
                StringBuffer tmpb = new StringBuffer();
                keyMetricMap.get(a).forEach(map -> map.keySet().forEach(key -> {
                    if (orderByKey.equals(key)) {
                        tmpa.append(map.get(key));
                    }
                }));
                keyMetricMap.get(b).forEach(map -> map.keySet().forEach(key -> {
                    if (orderByKey.equals(key)) {
                        tmpb.append(map.get(key));
                    }
                }));
                orderByValueA = tmpa.toString();
                orderByValueB = tmpb.toString();
            }

            int numA = 0;
            int numB = 0;

            try {
                if (orderByValueA != null && !orderByValueA.isEmpty() && !orderByValueA.equals("error") && !orderByValueA.equals("null")) {
                    numA = Integer.parseInt(orderByValueA);
                }
                if (!orderByValueB.isEmpty() && !orderByValueB.equals("error") && !orderByValueB.equals("null")) {
                    numB = Integer.parseInt(orderByValueB);
                }
            } catch (NumberFormatException e) {
                if (orderByValueA.compareTo(orderByValueB) < 0) {
                    num = -1;
                } else if (orderByValueA.compareTo(orderByValueB) > 0) {
                    num = 1;
                } else if (orderByValueA.compareTo(orderByValueB) == 0 && i + 1 < orderByList.size()) {
                    i++;
                    compareTo(a, b, i);
                }
            }
            if(split.length ==2 && split[1].equals("asc") || split.length==1){
                if(numA<numB){
                    num =-1;
                }else if(numA>numB){
                    num =1;
                }else if(numA == numB && i+1 < orderByList.size()){
                    i++;
                    compareTo(a,b,i);

                }
            }else{
                if(numA<numB){
                    num =1;
                }else if(numA > numB){
                    num=-1;
                }else if(numA == numB && i+1<orderByList.size()){
                    i++;
                    compareTo(a,b,i);
                }
            }


        }else if(orderByKey.equals("day") || orderByKey.equals("hour") || orderByKey.equals("minute")){//根据day进行排序
            //取出维度集合中的date:2018-05-04
            DateTime timeA = DateTime.parse(keyDimensionHashMap.get(a).get("date"), format);
            //取出维度集合中的date:2018-05-04
            DateTime timeB = DateTime.parse(keyDimensionHashMap.get(b).get("date"), format);
            //<<这里默认就是按date asc进行排序>>，如果date相等，继续按hour排序，如果hour相等，继续按minute排序
            if (split.length == 2 && split[1].equals("asc") || split.length == 1) {
                if (timeA.compareTo(timeB) < 0) {
                    //compareTo返回-1，timeA<timeB，但是返回-1，正序排序 timeA,timeB
                    num = -1;
                } else if (timeA.compareTo(timeB) > 0) {
                    //compareTO返回1,timeA>timeB，但是返回1，正序排序 timeA,timeB
                    num = 1;
                } else if (timeA.compareTo(timeB) == 0 && orderByKey.equals("day") && i + 1 < orderByList.size()) {//当两条数据日期相等，时，递归调用，继续比较
                    //如果这里只是day desc，则不会进入到这里，因为默认就是按day进行排序的
                    i++;
                    compareTo(a, b, i);

                } else if (timeA.compareTo(timeB) == 0 && (orderByKey.equals("hour") || orderByKey.equals("minute"))) {
                    //当按date没有排出大小，继续找是否有hour或者minute，有就继续按hour或者minute排序
                    int hourA;
                    int hourB;

                    hourA = Integer.parseInt(keyDimensionHashMap.get(a).get("hour"));
                    hourB = Integer.parseInt(keyDimensionHashMap.get(b).get("hour"));
                    if (hourA < hourB) {
                        num = -1;
                    } else if (hourA > hourB) {
                        num = 1;
                    } else if (hourA == hourB && orderByKey.equals("hour") && i + 1 < orderByList.size()) {
                        i++;
                        compareTo(a, b, i);
                    } else if (hourA == hourB && orderByKey.equals("minute")) {
                        int minuteA;
                        int minuteB;

                        minuteA = Integer.parseInt(keyDimensionHashMap.get(a).get("minute"));
                        minuteB = Integer.parseInt(keyDimensionHashMap.get(b).get("minute"));
                        if (minuteA < minuteB) {
                            num = -1;
                        } else if (minuteA > minuteB) {
                            num = 1;
                        } else if (minuteA == minuteB && i + 1 < orderByList.size()) {
                            i++;
                            compareTo(a, b, i);
                        }
                    }
                }


            } else {//这里是按date desc排序，如果date相等，按hour排序，如果hour也相等，按minute排序
                if (timeA.compareTo(timeB) < 0) {
                    //compareTo返回-1，说明timeA<timeB ，但是num=1，因此是倒序排序，timeB,timeA
                    num = 1;
                } else if (timeA.compareTo(timeB) > 0) {
                    //compareTo返回1，说明timeA>timeB，但是num=-1，因此是倒序排序，timeB,timeA
                    num = -1;
                } else if (timeA.compareTo(timeB) == 0 && i + 1 < orderByList.size()) {
                    i++;
                    compareTo(a, b, i);
                } else if (timeA.compareTo(timeB) == 0 && (orderByKey.equals("hour") || orderByKey.equals("minute"))) {
                    int hourA;
                    int hourB;

                    hourA = Integer.parseInt(keyDimensionHashMap.get(a).get("hour"));
                    hourB = Integer.parseInt(keyDimensionHashMap.get(b).get("hour"));
                    if (hourA < hourB) {
                        num = 1;
                    } else if (hourA > hourB) {
                        num = -1;
                    } else if (hourA == hourB && orderByKey.equals("hour") && i + 1 < orderByList.size()) {
                        i++;
                        compareTo(a, b, i);
                    } else if (hourA == hourB && orderByKey.equals("minute")) {
                        int minuteA;
                        int minuteB;
                        minuteA = Integer.parseInt(keyDimensionHashMap.get(a).get("minute"));
                        minuteB = Integer.parseInt(keyDimensionHashMap.get(b).get("minute"));
                        if (minuteA < minuteB) {
                            num = 1;
                        } else if (minuteA > minuteB) {
                            num = -1;
                        } else if (minuteA == minuteB && i + 1 < orderByList.size()) {
                            i++;
                            compareTo(a, b, i);
                        }
                    }
                }
            }
        }


        return num;
    }
}
