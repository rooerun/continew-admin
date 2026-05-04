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

package top.continew.admin.schedule.job;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import top.continew.admin.schedule.model.entity.ScheduleJobLogDO;
import top.continew.admin.schedule.service.JobLogService;
import top.continew.admin.schedule.utils.SpringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Quartz 任务执行器
 *
 * @author Charles7c
 * @since 2025/5/2 10:00
 */
@Slf4j
@Component
public class QuartzJobExecutor implements Job {

    private final JobLogService jobLogService;

    public QuartzJobExecutor(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        Long jobId = context.getMergedJobDataMap().getLong("jobId");
        String jobName = context.getMergedJobDataMap().getString("jobName");
        String jobGroup = context.getMergedJobDataMap().getString("jobGroup");
        String invokeTarget = context.getMergedJobDataMap().getString("invokeTarget");

        ScheduleJobLogDO jobLog = new ScheduleJobLogDO();
        jobLog.setJobId(jobId);
        jobLog.setJobName(jobName);
        jobLog.setJobGroup(jobGroup);
        jobLog.setInvokeTarget(invokeTarget);
        jobLog.setStartTime(LocalDateTime.now());

        try {
            // 解析调用目标（格式：类名.方法名）
            String[] targets = invokeTarget.split("\\.");
            String className = targets[0];
            String methodName = targets[1];

            // 获取 Bean
            Object bean = SpringUtils.getBean(className);

            // 获取方法
            Method method = bean.getClass().getMethod(methodName);

            // 执行方法
            method.invoke(bean);

            jobLog.setStatus(0);
            jobLog.setJobMessage("执行成功");
            log.info("定时任务执行成功，任务ID：{}，任务名称：{}", jobId, jobName);
        } catch (Exception e) {
            jobLog.setStatus(1);
            jobLog.setExceptionInfo(StrUtil.subPre(e.getMessage(), 2000));
            jobLog.setJobMessage("执行失败：" + e.getMessage());
            log.error("定时任务执行失败，任务ID：{}，任务名称：{}", jobId, jobName, e);
        } finally {
            jobLog.setEndTime(LocalDateTime.now());
            jobLogService.save(jobLog);
        }
    }
}
