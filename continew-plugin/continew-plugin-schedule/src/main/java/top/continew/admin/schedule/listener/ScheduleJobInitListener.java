/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.continew.admin.schedule.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import top.continew.admin.schedule.job.QuartzJobExecutor;
import top.continew.admin.schedule.mapper.ScheduleJobMapper;
import top.continew.admin.schedule.model.entity.ScheduleJobDO;

import java.util.List;

/**
 * 应用启动任务初始化监听器
 *
 * @author Charles7c
 * @since 2025/5/2 10:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleJobInitListener {

    private final Scheduler scheduler;
    private final ScheduleJobMapper jobMapper;

    /**
     * 应用启动完成后，加载数据库中的任务到 Quartz 调度器
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            log.info("开始初始化定时任务...");

            // 查询所有状态为正常的任务
            List<ScheduleJobDO> jobs = jobMapper
                .selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ScheduleJobDO>()
                    .eq(ScheduleJobDO::getStatus, 0));

            if (jobs == null || jobs.isEmpty()) {
                log.info("没有需要初始化的定时任务");
                return;
            }

            // 清除现有的任务
            scheduler.clear();

            // 添加任务到调度器
            for (ScheduleJobDO job : jobs) {
                try {
                    addJobToScheduler(job);
                    log.info("成功加载定时任务：{}, 任务组：{}", job.getJobName(), job.getJobGroup());
                } catch (Exception e) {
                    log.error("加载定时任务失败：{}, 任务ID：{}", job.getJobName(), job.getId(), e);
                }
            }

            log.info("定时任务初始化完成，共加载 {} 个任务", jobs.size());
        } catch (Exception e) {
            log.error("定时任务初始化失败", e);
        }
    }

    /**
     * 将任务添加到 Quartz 调度器
     */
    private void addJobToScheduler(ScheduleJobDO job) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(QuartzJobExecutor.class)
            .withIdentity(String.valueOf(job.getId()), job.getJobGroup())
            .usingJobData("jobId", job.getId())
            .usingJobData("jobName", job.getJobName())
            .usingJobData("jobGroup", job.getJobGroup())
            .usingJobData("invokeTarget", job.getInvokeTarget())
            .storeDurably()
            .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger_" + job.getId(), job.getJobGroup())
            .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
