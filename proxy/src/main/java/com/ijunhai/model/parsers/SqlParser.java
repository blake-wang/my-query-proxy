package com.ijunhai.model.parsers;

import org.joda.time.DateTime;

public interface SqlParser {
    void build() throws Exception;
    String getSelectSQL();
    String getTableName();
    String getWhereSQL();
    String getGroupBySql();
    default void setStartTime(DateTime time){}
    default void setEndTime(DateTime time){}
}
