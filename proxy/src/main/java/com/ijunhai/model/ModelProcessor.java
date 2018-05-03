package com.ijunhai.model;

import com.ijunhai.dao.DaoType;
import com.ijunhai.dao.MysqlDao;
import com.ijunhai.dao.ParallelDao;
import com.ijunhai.model.metrics.*;
import com.ijunhai.model.parsers.*;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.ResultSet;
import java.util.*;

import static com.ijunhai.constant.ProxyConstants.longMetric;
import static com.ijunhai.dao.DaoType.*;

public class ModelProcessor {
    private MysqlDao mysqlConn = MysqlDao.getInstance();
    private List<Pair<DaoType, String>> sqlList;
    private String granularity;
    private List<Metric> metricList;
    private ResultParser resultParser;
    private ParallelDao conn;
    private QueryModel model;
    private List<String> metricNameLists;
    private DateTime startTime;
    private DateTime endTime;
    private DateTime startOfDay;
    public static DateTimeFormatter formatA = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter formatB = DateTimeFormat.forPattern("yyyy-MM-dd");

    public ModelProcessor(QueryModel model) {
        this.granularity = model.getGranularity();
        this.metricList = model.getMetrics();
        this.conn = ParallelDao.getInstance();
        this.resultParser = new ResultParser();
        this.model = model;
        this.sqlList = new ArrayList<>();
        this.metricNameLists = new ArrayList<>();
        this.startOfDay = new DateTime().withTimeAtStartOfDay();

        //传入参数的处理
        Condition conditions = model.getConditions();
        String start = conditions.getStart().trim();
        String end = conditions.getEnd().trim();
        if (start.trim().contains(" ")) {
            DateTimeFormatter formatA = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            startTime = DateTime.parse(start, formatA);
            endTime = DateTime.parse(end, formatA);
        } else {
            DateTimeFormatter formatB = DateTimeFormat.forPattern("yyyy-MM-dd");
            startTime = DateTime.parse(start, formatB);
            endTime = DateTime.parse(end, formatB);
        }

    }

    public List<Map<String, String>> process() throws Exception {
        for (Metric metric : metricList) {
            String metricName = metric.getName();
            if (metricName.contains("complex")) {
                complexMetric(metric);
                continue;
            }
            buildSql(metric, UNKNOW);
        }

        List<ResultSet> ResultList = conn.execQuery(sqlList);
        //遍历所有resultSet
        //所有维度值加入finalDimensionMap Map<String,Map<String,String>>，key为value整条md5
        //所有指标值加入finalMetricMap Map<String,List<Map<String,String>>>,key和finalDimensionMap的key一样
        resultParser.resultSetParse(metricNameLists,ResultList);
        //合并相同的finalDemensionMap的key，同时合并对应key的finalMetricMap的所有相同的value值
        return resultParser.finalParse(model);
    }

    public void complexMetric(Metric metric) throws Exception {
        String metricName = metric.getName();
        List<Integer> valuesList = metric.getValues();
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startTime = format.parseDateTime(model.getConditions().getStart().split(" ")[0]);
        DateTime endTime = format.parseDateTime(model.getConditions().getEnd().split(" ")[0]);
        String startDate = startTime.toString("yyyy-MM-dd");
        String endDate = endTime.toString("yyyy-MM-dd");


        List<String> timeList = new ArrayList<>();
        timeList.add(startTime.toString("yyyy-MM-dd"));
        //用第三个变量来记录开始时间
        DateTime startTimeTmp = startTime;
        while (!startTimeTmp.equals(endTime)) {
            //如果开始的日期，不等于结束的日期，就一知执行，取出时间段的每一天，将取出的每一天加入list
            startTimeTmp = startTimeTmp.plusDays(1);
            timeList.add(startTimeTmp.toString("yyyy-MM-dd"));
        }
        //kylin
        //计算1日前的2留，2日前的3留
        HashMap<DateTime, Integer> computeMap = new HashMap<>();
        if (endTime.compareTo(startOfDay) == 0 && startTime.compareTo(startOfDay.minusDays(7)) == 1) {
            List<Integer> yet = Arrays.asList(1, 2, 3, 4, 5, 6);
            List<Integer> ret = Arrays.asList(2, 3, 4, 5, 6, 7);
            if (valuesList.containsAll(yet) || valuesList.containsAll(ret)) {
                DateTime today = new DateTime().withTimeAtStartOfDay();
                switch (metricName) {
                    case "complex_yet_pay_nuv":
                        metric = new OrderYetPayNuv(startDate, endDate, 0);
                        break;
                    case "complex_nu_yet_pay_amount":
                        metric = new OrderNuYetPayAmount(startDate, endDate, 0);
                        break;
                    case "complex_retention_uv":
                        metric = new LoginRetentionUv(startDate, endTime);
                        break;
                    case "complex_first_pay_retention_nuv":
                        metric = new LoginFirstPayRetentionNuv(startDate, endTime);
                        break;
                    case "complex_first_pay_retention_uv":
                        metric = new LoginFirstPayRetentionUv(startDate, endTime);
                        break;
                }
                buildSql(metric, KYLIN);
            } else {
                for (int days : valuesList) {
                    int d = Days.daysBetween(startTime, endTime).getDays();
                    if (d + 1 < days) {
                        continue;
                    }
                    computeMap.put(endTime.minusDays(days - 1), days);
                }

                for (Map.Entry<DateTime, Integer> e : computeMap.entrySet()) {
                    Integer value = e.getValue();
                    String key = e.getKey().toString("yyyy-MM-dd");
                    switch (metricName) {
                        //留存kylin算到7日
                        case "complex_retention_uv":
                            metric = new LoginRetentionUv(key, new ArrayList<Integer>() {{
                                add(value);
                            }});
                            break;
                        case "complex_first_pay_retention_nuv":
                            metric = new LoginFirstPayRetentionNuv(key, new ArrayList<Integer>() {
                                {
                                    add(value);
                                }
                            });
                            break;
                        //Ltv实时算到6日
                        case "complex_first_pay_retention_uv":
                            metric = new LoginFirstPayRetentionUv(key, new ArrayList<Integer>() {{
                                add(value);
                            }});
                            break;
                        case "complex_nu_yet_pay_amount":
                            if (value == 7) {
                                continue;
                            }
                            metric = new OrderNuYetPayAmount(e.getKey().minusDays(1).toString("yyyy-MM-dd"), endTime.toString("yyyy-MM-dd"), value);

                    }
                }
            }
        }

    }

    public void buildSql(Metric metric, DaoType type) throws Exception {
        metricNameLists.add(metric.getName());
        String time = granularity == null ? "" : granularity;
        if (metric.getFuction(MYSQL) != null && !time.equals("hour") && time.equals("minute")) {
            sqlList.add(Pair.of(MYSQL, build(new MysqlParser(model, metric))));
        }

        if (longMetric.contains(metric.getName())) {
            //startTime大于当前日期(startOfDay-7) 且 endTime大于startOfDay则查ky实时数据
            if (type.equals(KYLIN)) {
                sqlList.add(Pair.of(KYLIN, build(new KylinParser(model, metric))));
            } else {
                sqlList.add(Pair.of(GP, build(new GPParser(model, metric))));
            }
        } else {
            //其他指标
            //startTime大于startOfDay实时ky表，endTime小于startOfDay历史GP数据，否则两块合并
            if (endTime.compareTo(startOfDay) == -1) {
                sqlList.add(Pair.of(GP, build(new GPParser(model, metric))));
            } else if (startTime.compareTo(startOfDay) >= 0) {
                sqlList.add(Pair.of(KYLIN, build(new KylinParser(model, metric))));
            } else {
                SqlParser gp = new GPParser(model, metric);
                gp.setEndTime(startOfDay.minusDays(1));
                sqlList.add(Pair.of(GP, build(gp)));

                SqlParser ky = new KylinParser(model, metric);
                ky.setStartTime(startOfDay);
                sqlList.add(Pair.of(KYLIN, build(ky)));
            }
        }


    }

    private String build(SqlParser sqlParser) throws Exception {
        //第一步先构建每个段的sql语句
        sqlParser.build();
        //第二步将构建好的sql语句段在合并起来
        String sql =
                sqlParser.getSelectSQL() + sqlParser.getTableName()
                        + sqlParser.getWhereSQL() + sqlParser.getGroupBySql();
        //添加大蓝海外数据
//        String sql1 = sql;
//        if (sqlParser.getTableName().contains("rpt") && !sqlParser.getTableName().contains("import")) {
//            addDLHW(Pair.of(sqlParser.getTableName(), sql1));
//        }
        return sql;

    }

    private void addDLHW(Pair<String, String> pair) {


    }
}
