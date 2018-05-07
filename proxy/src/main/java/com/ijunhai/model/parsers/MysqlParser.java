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
        //这里获取的是MYSQL的tablename
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
        return " from " + ProxyConstants.MYSQL_TABLENAME;
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
        //返回字段列表，目前看一般都为空
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
        //granularity在第一次进来的时候，已经做过非null判断，如果是null，就给""
        //因此这里再次判断，只要判断长度是否为0就行了
        if (!granularity.isEmpty()) {
            //group by 后面如果有date字段，那select后面也需要date字段
            groupBySql.append(" date ");
            selectSql.append(" date ");
        }
        //上一步在判断了granularity的长度后，如果掺入为0，则不会追加爱date字段，因此这里groupBySQL 就是""
        if (groupBySql.toString().trim().equals("group by")) {
            groupBySQL = "";
        } else {
            //删除掉group by 最后面的,号
            if (groupBySql.charAt(groupBySql.length() - 1) == ',') {
                groupBySql.deleteCharAt(groupBySql.length() - 1);
            }
            groupBySQL = groupBySql.toString();
        }
        //删除掉select语句最后面的,号
        if (selectSql.charAt(selectSql.length() - 1) == ',') {
            selectSql.deleteCharAt(selectSql.length() - 1);
        }
        selectSQL = selectSql.toString();
    }

    private void buildWhereSQL() {
        //处理where语句，
        whereSQL = new StringBuffer();
        whereSQL.append(" where date between '").append(startTime.toString("yyyyMMdd"))
                .append("' and '").append(endTime.toString("yyyyMMdd")).append("' ")
                .append(conditionParse(conditions.getChannelId(), "channelId"))
                .append(conditionParse(conditions.getGameChannelId(), "gameChannelId"))
                .append(conditionParse(conditions.getOsType(), "osType"))
                .append(conditionParse(conditions.getCompanyId(), "companyId"))
                .append(conditionParse(conditions.getGameId(), "gameId"));
    }

    private String conditionParse(List list, String conditionName) {
        StringBuilder sb = new StringBuilder();
        //这里判断一次是否有这些过滤的条件，如果没有就不添加了
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
