package com.ijunhai.model.parsers;

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

import static com.ijunhai.dao.DaoType.GP;

public class GPParser implements SqlParser {
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
    private List filter;
    private static final Logger logger = LoggerFactory.getLogger(GPParser.class);

    public GPParser(QueryModel model, Metric metric) {
        this.granularity = model.getGranularity();
        this.conditions = model.getConditions();
        this.metric = metric;
        this.returnDemensionsList = model.getReturnDemensions();
        String start = conditions.getStart().trim();
        String end = conditions.getEnd().trim();
        tableName = metric.getTableName(GP);
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

    private void buildWhereSQL() {
        whereSQL = new StringBuffer();
        whereSQL.append(" where company_id='2' and date between '")
                .append(startTime.toString("yyyy-MM-dd")).append("' and '")
                .append(endTime.toString("yyyy-MM-dd")).append("' ");

        whereSQL.append(" ");
        whereSQL.append(conditionParse(conditions.getChannelId(), "channelId"))
                .append(conditionParse(conditions.getGameChannelId(), "gameChannelId"))
                .append(conditionParse(conditions.getOsType(), "osType"))
                .append(conditionParse(conditions.getCompanyId(), "companyId"))
                .append(conditionParse(conditions.getGameId(), "gameId"));

    }

    private String conditionParse(List list, String conditionName) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            sb.append(" and ");
            sb.append(FieldMapping.getGP(conditionName.toUpperCase())).append(" in (");
            for (int i = 0; i < list.size(); i++) {
                sb.append("'").append(list.get(i)).append("',");
            }
            sb.deleteCharAt(sb.length() - 1).append(") ");
        }

        return sb.toString();
    }

    private void buildSelectAndGroupBySQL() {
        StringBuilder selectSql = new StringBuilder();
        StringBuilder groupBySql = new StringBuilder();
        groupBySql.append(" group by ");
        selectSql.append("select ").append(metric.getFuction(GP)).append(",");
        if (!returnDemensionsList.isEmpty()) {
            for (String returnDemension : returnDemensionsList) {
                if (StringUtils.isNoneBlank(FieldMapping.getTimeColumn(returnDemension.toUpperCase()))) {
                    logger.error("returnDemension cannot contain " + returnDemension);
                }
                selectSql.append(FieldMapping.getGP(returnDemension.toUpperCase()))
                        .append(" as ").append(returnDemension).append(" ,");
                groupBySql.append(FieldMapping.getGP(returnDemension.toUpperCase())).append(",");
            }
        }
        if (!granularity.isEmpty()) {
            selectSql.append(" date ");
            groupBySql.append(" date ");
        }
        if (groupBySql.toString().trim().equals("group by")) {
            groupBySQL = "";
        } else {
            if (groupBySql.charAt(groupBySql.length() - 1) == ',') {
                groupBySQL = groupBySql.deleteCharAt(groupBySql.length() - 1).toString();
            } else {
                groupBySQL = groupBySql.toString();
            }
        }
        if (selectSql.charAt(selectSql.length() - 1) == ',') {
            selectSQL = selectSql.deleteCharAt(selectSql.length() - 1).toString();
        } else {
            selectSQL = selectSql.toString();
        }
    }

    @Override
    public String getSelectSQL() {
        return selectSQL;
    }

    @Override
    public String getTableName() {
        return " from " + tableName;
    }

    @Override
    public String getWhereSQL() {
        return whereSQL.toString();
    }

    @Override
    public String getGroupBySql() {
        return groupBySQL;
    }

    @Override
    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;

    }
}
