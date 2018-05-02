package com.ijunhai.model.parsers;

import com.ijunhai.dao.DaoType;
import com.ijunhai.model.Condition;
import com.ijunhai.model.QueryModel;
import com.ijunhai.model.metrics.Metric;
import com.ijunhai.util.FieldMapping;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class KylinParser implements SqlParser {
    public final static List<String> complexRetentionMetric = Arrays.asList("complex_retention_uv", "complex_first_pay_retention_uv", "complex_first_pay_retention_nuv");
    private String granularity;
    private Condition conditions;
    private Metric metric;
    private List<String> returnDemensionsList;
    private String selectSQL;
    private String groupBySQL;
    private StringBuffer whereSQL;
    private String tableName;
    private DateTime startTime;
    private DateTime endTime;

    private static final Logger logger = LoggerFactory.getLogger(KylinParser.class);


    public KylinParser(QueryModel model, Metric metric) {
        this.granularity = model.getGranularity();
        this.conditions = model.getConditions();
        this.metric = metric;
        this.returnDemensionsList = model.getReturnDemensions();

        String start = conditions.getStart().trim();
        String end = conditions.getEnd().trim();
        tableName = metric.getTableName(DaoType.KYLIN);
        if (start.contains(" ")) {
            DateTimeFormatter formatA = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            startTime = DateTime.parse(start, formatA);
            endTime = DateTime.parse(end, formatA);
        } else {
            DateTimeFormatter formatB = DateTimeFormat.forPattern("yyyy-MM-dd");
            startTime = DateTime.parse(start, formatB);
            endTime = DateTime.parse(end, formatB);
        }


    }

    @Override
    public void build() throws Exception {
        bulidSelectAndGroupBySQL();
        bulidWhereSQL();
    }

    @Override
    public String getSelectSQL() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public String getWhereSQL() {
        return null;
    }

    @Override
    public String getGroupBySql() {
        return null;
    }


    private void bulidSelectAndGroupBySQL() {
        StringBuffer selectSql = new StringBuffer();
        StringBuffer groupBySql = new StringBuffer();
        groupBySql.append(" group by ");
        selectSql.append("select ").append(metric.getFuction(DaoType.KYLIN)).append(" , ");
        if (!returnDemensionsList.isEmpty()) {
            for (String returnDemension : returnDemensionsList) {
                if (StringUtils.isNoneBlank(FieldMapping.getTimeColumn(returnDemension.toUpperCase())) {
                    logger.error("returnDemension cannot contain " + returnDemension);
                }
                selectSql.append(FieldMapping.getKylin(returnDemension.toUpperCase(), tableName))
                        .append(" as ").append(returnDemension).append(" , ");
                groupBySql.append(FieldMapping.getKylin(returnDemension.toUpperCase(), tableName));
            }
        }
        if (!metric.getName().contains("retention") && !metric.getName().contains("yet")) {
            switch (granularity.toLowerCase()) {
//                case "day"
                //TODO  代码写到这里了哈 2018-05-02 10：21：00！！！！
            }
        }
    }

    private void bulidWhereSQL() {

    }


}
