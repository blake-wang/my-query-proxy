package com.ijunhai.model.parsers;

import com.ijunhai.model.QueryModel;
import com.ijunhai.model.metrics.Metric;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static com.ijunhai.model.ModelProcessor.formatA;
import static com.ijunhai.model.ModelProcessor.formatB;

public class ResultParser {
    private Map<String, List<Map<String, String>>> finalMetricMap;
    private Map<String, Map<String, String>> finalDimensionMap;
    private static MessageDigest messageDigest;
    private DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public ResultParser() {
        this.finalMetricMap = new HashMap<>();
        this.finalDimensionMap = new HashMap<>();
    }

    public void resultSetParse(List<String> metricNameLists, List<ResultSet> ResultSets) throws SQLException {
        //遍历每一个结果集
        for (ResultSet resultSet : ResultSets) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, String> kylinMap = new HashMap<>();
            //遍历结果集中的每一行
            while (resultSet.next()) {//遍历每一行
                if (columnCount == 1 && StringUtils.isEmpty(resultSet.getString(1))) {
                    break;
                }
                //dimensionMap存放的是维度名称和值，date->2018-05-01
                Map<String, String> dimensionMap = new TreeMap<>();
                //metricMap存放的是指标字段名称和值，active_uv->4412
                Map<String, String> metricMap = new HashMap<>();

                //声明这个标记，是为了等下对异常日期做过滤
                int flag = 0;

                //遍历每一行的每个字段
                //因为数据库resultSet中的字段编号是从1开始的，所以这里i=1方便一点
                for (int i = 1; i <= columnCount; i++) {
                    //获取字段名
                    String key = metaData.getColumnLabel(i);
                    //获取字段值
                    String value = resultSet.getString(i);
                    if (value == null) {
                        continue;
                    }
                    //因为group by之后，select字段也会有group by的维度数据，因此这里是把date这个字段对应的值变成日期
                    if (key.equals("date") && !value.contains("-")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(value);
                        //这里是将20180401这个时间变成2018-04-01
                        sb.insert(6, "-").insert(4, "-");
                        value = sb.toString();
                    }
                    //这里为什么要做判断？
                    if ((key.toLowerCase().contains("ret")
                            || key.toLowerCase().contains("yet")
                            || metricNameLists.contains(key.toLowerCase())) && !key.equals("_m")) {
                        metricMap.put(key.toLowerCase(), value.split("\\.")[0]);
                        if (!metricMap.containsKey(key.toLowerCase() + "_revision")) {
                            metricMap.put(key.toLowerCase() + "_revision", "0");
                        }
                    } else if (key.contains("_m")) {//这里是为了找出哪些指标字段是带_m的，找出来后，将这个指标名称替换为带_revision，比如active_uv_m替换为active_uv_revision
                        key = key.substring(0, key.length() - 2);
                        //这里将从mysql中查出来的带_m的字段，拆成了两个，active_uv,active_uv_revision，并分别作为key存入metricMap,值是相同的值
                        metricMap.put(key.toLowerCase(), value.split("\\.")[0]);
                        metricMap.put(key.toLowerCase() + "_revision", value.split("\\.")[0]);

                    } else {
                        //这里这个try抓异常，是因为如果出现2018-04-31这样的日期，
                        try {
                            if (key.toLowerCase().equals("date")) {
                                DateTime.parse(value, format);
                            }
                        } catch (Exception e) {
                            flag = 1;
                            break;
                        }
                        //便利后的日期信息，加入map
                        dimensionMap.put(key.toLowerCase(), value);
                    }
                }//遍历每一行的每个字段结束

                //当遇到异常日期值的时候，当前这条数据就跳过了，不处理
                if (flag == 1) {
                    continue;
                }

                //对整个dimensionMap进行Base64加密，生成key
                String key = Base64.getEncoder().encodeToString(messageDigest.digest(dimensionMap.toString().getBytes()));
                //用dimensionMap生成的key和dimensionMap作为value，存入finalDimensionMap中
                if (!finalDimensionMap.containsKey(key)) {
                    finalDimensionMap.put(key, dimensionMap);
                }

                //kylin结果处理
                if (metricMap.containsKey("kylin_yet_pay_amount") || metricMap.containsKey("kylin_yet_pay_nuv") || metricMap.containsKey("kylin_retention_uv")
                        || metricMap.containsKey("kylin_first_pay_retention_nuv") || metricMap.containsKey("kylin_first_pay_retention_uv")) {
                    int days = Days.daysBetween(DateTime.parse(dimensionMap.get("date"), formatB), new DateTime().withTimeAtStartOfDay()).getDays() + 1;
                    Map<String, String> kylinMetricMap = new HashMap<>();
                    for (Map.Entry e : metricMap.entrySet()) {
                        String tmpKey = e.getKey().toString();
                        String value = e.getValue().toString();
                        String revision = tmpKey.contains("_revision") ? "_revision" : "";
                        if (tmpKey.contains("yet")) {
                            kylinMetricMap.put(days + tmpKey.substring(6) + revision, value);
                        } else {
                            kylinMetricMap.put(days + tmpKey.substring(6) + revision, value);
                        }
                    }

                    if (!kylinMetricMap.isEmpty()) {
                        if (!finalMetricMap.containsKey(key)) {
                            finalMetricMap.put(key, new ArrayList<Map<String, String>>() {{
                                add(kylinMetricMap);
                            }});
                        } else {
                            finalMetricMap.get(key).add(kylinMetricMap);
                        }
                    }

                } else {
                    //如果不包含这个key，这个key就是刚才对整个dimensionMap进行Base64加密，生成的key，加入finalMetricMap
                    //值为metricMap,此处的metricMap应该包含两个kv对,active_uv->4412,active_uv_revision->4412
                    if (!finalMetricMap.containsKey(key)) {
                        finalMetricMap.put(key, new ArrayList<>());
                        finalMetricMap.get(key).add(metricMap);
                    } else if (finalMetricMap.containsKey(key)) {
                        finalMetricMap.get(key).forEach(map -> map.keySet().forEach(k -> {
                            if (metricMap.keySet().contains(k) && !k.contains("_revision") && metricMap.get(k) != null && map.get(k) != null) {
                                int a = (int) Double.parseDouble(map.get(k) + (int) Double.parseDouble(metricMap.get(k)));
                                map.put(k, a + "");
                                metricMap.remove(k);
                            }
                        }));

                        if (!metricMap.isEmpty()) {
                            finalMetricMap.get(key).add(metricMap);
                        }
                    }
                }

            }
        }

    }

    public List<Map<String, String>> finalParse(QueryModel model) {
        String granularity = model.getGranularity();
        List<Metric> metricList = model.getMetrics();
        List<String> orderByList = model.getOrderByFields();
        String limit = model.getLimit() == null ? "2000" : model.getLimit();
        //根据granularity(默认),orderByList(指定)给finalDimensionMap的值排序

        //DimensionComparator里面定义了排序的规则
        DimensionComparator bvc = new DimensionComparator(finalMetricMap, finalDimensionMap, granularity, orderByList);
        //
        Map<String, Map<String, String>> keyDimensionTreeMap = new TreeMap<>(bvc);
        keyDimensionTreeMap.putAll(finalDimensionMap);

        List<Map<String, String>> result = new ArrayList<>();
        Set<Map.Entry<String, Map<String, String>>> set = keyDimensionTreeMap.entrySet();
        for (Map.Entry<String, Map<String, String>> i : set) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.putAll(i.getValue());

            finalMetricMap.get(i.getKey()).forEach(metricMap -> metricMap.forEach(resultMap::put));
            for (Metric metric : metricList) {
                String value = resultMap.get(metric.getName());
                if (!metric.getName().contains("retention") && !metric.getName().contains("yet")) {
                    if ((!resultMap.keySet().contains(metric.getName()) || StringUtils.isEmpty(value))) {
                        resultMap.put(metric.getName(), "");
                        resultMap.put(metric.getName() + "_revision", "0");
                    } else {
                        resultMap.put(metric.getName(), metric.format(value));
                    }
                }
            }
            result.add(resultMap);
        }

        //返回要求获取的条目
        if (!limit.isEmpty()) {
            int lm = Integer.parseInt(limit);
            if (result.size() > lm) {
                result = result.subList(0, lm);
            }
        }
        return result;
    }
}
