package io.ont.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class TaskExecutorConfig implements SchedulingConfigurer {

    @Value("${sync.threadPoolSize.max}")
    protected int THREADPOOLSIZE_MAX;

    @Value("${sync.threadPoolSize.core}")
    protected int THREADPOOLSIZE_CORE;

    @Value("${sync.threadPoolSize.queue}")
    protected int THREADPOOLSIZE_QUEUE;

    @Value("${sync.threadPoolSize.keepalive}")
    protected int THREADPOOLSIZE_KEEPALIVE_SECOND;

    @Bean(name = "asyncTaskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        log.info("########synTaskExecutor#########");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(THREADPOOLSIZE_MAX);
        executor.setCorePoolSize(THREADPOOLSIZE_CORE);
        executor.setQueueCapacity(THREADPOOLSIZE_QUEUE);
        executor.setThreadNamePrefix("AsyncTaskThread--");
        executor.setKeepAliveSeconds(THREADPOOLSIZE_KEEPALIVE_SECOND);

        // Rejection policies
/*		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				logger.error("###########reject thread....");
				// .....
			}
		});*/
        //调用者的线程会执行该任务,如果执行器已关闭,则丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        return executor;
    }

    /**
     * 并行任务使用策略：多线程处理（配置线程数等）
     *  
     *
     * @return ThreadPoolTaskScheduler 线程池
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.setPoolSize(50);
        //设置线程名开头
        scheduler.setThreadNamePrefix("scheduler task-");
        //等待时常
//        scheduler.setAwaitTerminationSeconds(600);
        //当调度器shutdown被调用时等待当前被调度的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        //设置当任务被取消的同时从当前调度器移除的策略
        scheduler.setRemoveOnCancelPolicy(true);
        return scheduler;
    }

    /**
     * 注册定时任务线程池
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        TaskScheduler taskScheduler = taskScheduler();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
