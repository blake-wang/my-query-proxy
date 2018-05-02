package com.ijunhai.model.parsers;

import com.ijunhai.Proxy;
import com.ijunhai.constant.ProxyConstants;
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

import java.util.List;

import static com.ijunhai.dao.DaoType.MYSQL;

public class MysqlParser implements SqlParser {
    private String granularity;
    private Condition conditions;
    private Metric metric;
    private List<String> returnDemensionList;
    private String selectSQL;
    private String groupBySQL;
    private StringBuffer whereSQL;
    private String tableName;
    private DateTime startTime;
    private DateTime endTime;
    private static final Logger logger = LoggerFactory.getLogger(MysqlParser.class);


    public MysqlParser(QueryModel model, Metric metric) {
        this.granularity = model.getGranularity();
        this.conditions = model.getConditions();
        this.metric = metric;
        this.returnDemensionList = model.getReturnDemensions();
        String start = conditions.getStart().trim();
        String end = conditions.getEnd().trim();
        tableName = metric.getTableName(MYSQL);
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
        buildSelectAndGroupBySQL();
        buildWhereSQL();
    }


    @Override
    public String getSelectSQL() {
        return selectSQL;
    }

    @Override
    public String getTableName() {
        return " from "+ ProxyConstants.MYSQL_TABLENAME;
    }

    @Override
    public String getWhereSQL() {
        return whereSQL.toString();
    }

    @Override
    public String getGroupBySql() {
        return groupBySQL;
    }


    private void buildSelectAndGroupBySQL() {
        StringBuilder selectSql = new StringBuilder();
        StringBuilder groupBySql = new StringBuilder();
        groupBySql.append(" group by ");
        selectSql.append("select ").append(metric.getFuction(MYSQL)).append(",");
        if (!returnDemensionList.isEmpty()) {
            for (String returnDemension : returnDemensionList) {
                if (StringUtils.isNoneBlank(FieldMapping.getTimeColumn(returnDemension.toUpperCase()))) {
                    logger.error("returnDemension cannot contain " + returnDemension);
                }
                selectSql.append(FieldMapping.getMysql(returnDemension.toUpperCase()))
                        .append(" as ").append(returnDemension).append(",");
                groupBySql.append(FieldMapping.getMysql(returnDemension.toUpperCase())).append(",");
            }
        }
        if (!granularity.isEmpty()) {
            groupBySql.append(" date ");
            selectSql.append(" date ");
        }
        if (groupBySql.toString().trim().equals("group by")) {
            groupBySQL = "";
        } else {
            if (groupBySql.charAt(groupBySql.length() - 1) == ',') {
                groupBySql.deleteCharAt(groupBySql.length() - 1);
            }
            groupBySQL = groupBySql.toString();
        }
        if (selectSql.charAt(selectSql.length() - 1) == ',') {
            selectSql.deleteCharAt(selectSql.length() - 1);
        }
        selectSQL = selectSql.toString();
    }

    private void buildWhereSQL() {
        whereSQL = new StringBuffer();
        whereSQL.append(" where date between '").append(startTime.toString("yyyyMMdd"))
                .append("' and '").append(endTime.toString("yyyyMMdd")).append("' ")
                .append(conditionParse(conditions.getChannelId(), "channelId"))
                .append(conditionParse(conditions.getChannelId(), "gameChannelId"))
                .append(conditionParse(conditions.getOsType(), "osType"))
                .append(conditionParse(conditions.getCompanyId(), "companyId"))
                .append(conditionParse(conditions.getGameId(), "gameId"));
    }

    private String conditionParse(List list, String conditionName) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            sb.append(" and ");
            sb.append(FieldMapping.getMysql(conditionName.toUpperCase())).append(" in ('").append(list.get(0)).append("'");
            for (int i = 1; i < list.size(); i++) {
                sb.append(", '").append(list.get(i)).append("'");
            }
            sb.append(") ");
        }

        return sb.toString();
    }


}
