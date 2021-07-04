package com.ylzs.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xuwei
 * @create 2019-08-19 10:16
 * 线程配置
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //此方法返回可用处理器的虚拟机的最大数量; 不小于1
        int core = Runtime.getRuntime().availableProcessors();
        taskExecutor.setCorePoolSize(core + 1);//设置核心线程数
        taskExecutor.setMaxPoolSize(core * 50 + 1);//设置最大线程数
        taskExecutor.setKeepAliveSeconds(30000);//除核心线程外的线程存活时间
        taskExecutor.setQueueCapacity(200);//如果传入值大于0，底层队列使用的是LinkedBlockingQueue,否则默认使用SynchronousQueue
        taskExecutor.setThreadNamePrefix("thread-execute");//线程名称前缀
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//设置拒绝策略
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);////当调度器shutdown被调用时等待当前被调度的任务完成
        return taskExecutor;
    }
}
