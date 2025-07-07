package com.jiawa.train.batch.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.batch.job
 * @BelongsClassName: TestJob
 * @Author: yuanql
 * @CreateTime: 2023-07-24  16:43
 * @Description: test
 * @Version: 1.0
 */


@DisallowConcurrentExecution // 禁用并发执行
public class Test2Job implements Job {

    private void test() {
        // 增加分布式锁，解决集群问题
        System.out.println("SpringBootTestJob  ====   test");
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("TestJob2222222222  ===== test开始");
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("TestJob2222222222222222  ===== test结束");
    }
}
