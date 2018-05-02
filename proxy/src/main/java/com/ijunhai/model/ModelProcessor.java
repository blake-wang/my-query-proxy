package com.ijunhai.model;

import com.ijunhai.dao.DaoType;
import com.ijunhai.dao.MysqlDao;
import com.ijunhai.dao.ParallelDao;
import com.ijunhai.model.metrics.*;
import com.ijunhai.model.parsers.MysqlParser;
import com.ijunhai.model.parsers.ResultParser;
import com.ijunhai.model.parsers.SqlParser;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ijunhai.dao.DaoType.MYSQL;

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

    public List<Map<String, String>> process() {
        for (Metric metric : metricList) {
            String metricName = metric.getName();
            if (metricName.contains("complex")) {
                complexMetric(metric);
            }
        }

        return null;
    }

    public void complexMetric(Metric metric) throws Exception {
        String metricName = metric.getName();
        List<Integer> valuesList = metric.getValues();
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startTime = format.parseDateTime(model.getConditions().getStart().split(" ")[0]);
        DateTime endTime = format.parseDateTime(model.getConditions().getEnd().split(" ")[0]);
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
        if (startTime.compareTo(startOfDay.minusDays(7)) == 1 && endTime.compareTo(startOfDay) >= 0) {
            //按一天一天来处理
            for (String date : timeList) {
                DateTime today = new DateTime().withTimeAtStartOfDay();
                switch (metricName) {
                    case "complex_retention_uv":
                        metric = new LoginRetentionUv(date, valuesList);
                        break;
                    case "complex_first_pay_retention_nuv":
                        metric = new LoginFirstPayRetentionNuv(date, valuesList);
                        break;
                    case "complex_first_pay_retention_uv":
                        metric = new LoginFirstPayRetentionUv(date, valuesList);
                        break;
                    case "complex_yet_pay_nuv":
                        if (valuesList == null) {
                            metric = new OrderYetPayNuv(date, today.toString("yyyy-MM-dd"), 0);
                            buildSql(metric);
                        } else {

                        }
                }
            }
        }

    }

    public void buildSql(Metric metric) throws Exception {
        metricNameLists.add(metric.getName());
        String time = granularity == null ? "" : granularity;
        if(metric.getFuction(MYSQL) != null && !time.equals("hour") && time.equals("minute")){
            sqlList.add(Pair.of(MYSQL,build(new MysqlParser(model,metric))));
        }
        //TODO 代码写到这里了  8:25

    }

    private String build(SqlParser sqlParser) throws Exception {
        sqlParser.build();
        String sql = sqlParser.getSelectSQL()+sqlParser.getTableName()+sqlParser.getWhereSQL()+sqlParser.getGroupBySql();
        //添加大蓝海外数据
        String sql1 = sql;
        if(sqlParser.getTableName().contains("rpt") && !sqlParser.getTableName().contains("import") ){
            addDLHW(Pair.of(sqlParser.getTableName(),sql1));
        }
        return sql;

    }

    private void addDLHW(Pair<String, String> pair) {


    }
}
