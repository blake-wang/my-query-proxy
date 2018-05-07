package com.ijunhai.dao;

import org.apache.commons.lang3.tuple.Pair;

import javax.security.auth.callback.Callback;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelDao {
    private static final int DEFAULT_AWAIT_SECONDS = 60;


    //线程池
    private final ExecutorService exec;
    private final KylinDao kylinDao;
    private final MysqlDao mysqlDao;
    private final GPDao gpDao;

    private static ParallelDao parallelDao;

    private static class LazyHolder {
        private static final ParallelDao INSTANCE = new ParallelDao();
    }

    ParallelDao() {
        exec = Executors.newFixedThreadPool(25);
        kylinDao = KylinDao.getInstance();
        mysqlDao = MysqlDao.getInstance();
        gpDao = GPDao.getInstance();
    }

    public static ParallelDao getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<ResultSet> execQuery(List<Pair<DaoType, String>> sqls) throws Exception {
        //这个CountDownLatch有什么用
        CountDownLatch latch = new CountDownLatch(sqls.size());
        //为什么要在ResultSet外面包一层Future
        List<Future<ResultSet>> futures = new ArrayList<>();

        for (Pair<DaoType, String> sql : sqls) {
            futures.add(exec.submit(new ResultSetCallback(sql, latch)));
        }
        //这个await方法有什么用？
        latch.await(DEFAULT_AWAIT_SECONDS, TimeUnit.SECONDS);
        List<ResultSet> resultSets = new ArrayList<>();

        boolean isFinish = true;
        //遍历检查每个任务是否执行完，当所有的都执行完了，才取出结果集
        for (Future<ResultSet> future : futures) {
            if (!future.isDone()) {
                isFinish = false;
                future.cancel(true);
            }
        }
        //到这里是所有的都执行完了，遍历取出结果集
        if (isFinish) {
            for (Future<ResultSet> future : futures) {
                try {
                    resultSets.add(future.get());
                } catch (InterruptedException e) {
                    throw new Exception(e);
                }
            }
        } else {
            throw new TimeoutException("query time out in " + DEFAULT_AWAIT_SECONDS + "s");
        }



        return resultSets;
    }

    class ResultSetCallback implements Callable<ResultSet> {
        private Pair<DaoType, String> sql;
        private CountDownLatch latch;

        public ResultSetCallback(Pair<DaoType, String> sql, CountDownLatch latch) {
            this.sql = sql;
            this.latch = latch;
        }

        @Override
        public ResultSet call() throws Exception {
            ResultSet resultSet = null;
            try {
                switch (sql.getLeft()) {
                    //JDK1.8新特性，可以匹配枚举
                    //这里根据不同的数据库，查询对应的sql语句
                    case KYLIN:
                        resultSet = kylinDao.execuQuery(sql.getRight());
                        break;
                    case MYSQL:
                        resultSet = mysqlDao.execQuery(sql.getRight());
                        break;
                    case GP:
                        resultSet = gpDao.executeQuery(sql.getRight());
                        break;
                }
            } catch (Exception ex) {
                throw new Exception("query error", ex);
            } finally {
                latch.countDown();
            }
            return resultSet;
        }

    }


}
