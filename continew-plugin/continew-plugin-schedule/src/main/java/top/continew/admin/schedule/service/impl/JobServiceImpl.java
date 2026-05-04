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

package top.continew.admin.schedule.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.schedule.job.QuartzJobExecutor;
import top.continew.admin.schedule.mapper.ScheduleJobMapper;
import top.continew.admin.schedule.model.entity.ScheduleJobDO;
import top.continew.admin.schedule.model.query.JobQuery;
import top.continew.admin.schedule.model.req.JobReq;
import top.continew.admin.schedule.model.req.JobStatusReq;
import top.continew.admin.schedule.model.req.JobTriggerReq;
import top.continew.admin.schedule.model.resp.JobResp;
import top.continew.admin.schedule.service.JobService;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务业务实现
 *
 * @author KAI
 * @author Charles7c
 * @since 2024/6/25 17:25
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final ScheduleJobMapper jobMapper;
    private final Scheduler scheduler;

    @Override
    public PageResp<JobResp> page(JobQuery query) {
        LambdaQueryWrapper<ScheduleJobDO> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getGroupName())) {
            wrapper.eq(ScheduleJobDO::getJobGroup, query.getGroupName());
        }
        if (StrUtil.isNotBlank(query.getJobName())) {
            wrapper.like(ScheduleJobDO::getJobName, query.getJobName());
        }
        if (query.getJobStatus() != null) {
            wrapper.eq(ScheduleJobDO::getStatus, query.getJobStatus());
        }
        wrapper.orderByDesc(ScheduleJobDO::getId);

        Page<ScheduleJobDO> page = jobMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        List<JobResp> list = page.getRecords().stream().map(this::convertToResp).collect(Collectors.toList());

        PageResp<JobResp> result = new PageResp<>();
        result.setList(list);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(JobReq req) {
        ScheduleJobDO job = new ScheduleJobDO();
        job.setJobName(req.getJobName());
        job.setJobGroup(req.getGroupName());
        job.setInvokeTarget(req.getInvokeTarget());
        job.setCronExpression(req.getCronExpression());
        job.setMisfirePolicy(req.getMisfirePolicy());
        job.setConcurrent(req.getConcurrent());
        job.setStatus(req.getJobStatus());
        job.setRemark(req.getRemark());

        jobMapper.insert(job);

        // 添加到 Quartz 调度器
        addJobToScheduler(job);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(JobReq req, Long id) {
        ScheduleJobDO job = jobMapper.selectById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        job.setJobName(req.getJobName());
        job.setJobGroup(req.getGroupName());
        job.setInvokeTarget(req.getInvokeTarget());
        job.setCronExpression(req.getCronExpression());
        job.setMisfirePolicy(req.getMisfirePolicy());
        job.setConcurrent(req.getConcurrent());
        job.setStatus(req.getJobStatus());
        job.setRemark(req.getRemark());

        jobMapper.updateById(job);

        // 更新 Quartz 调度器中的任务
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(id), job.getJobGroup());
            scheduler.deleteJob(jobKey);
            if (job.getStatus() == 0) {
                addJobToScheduler(job);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("更新定时任务失败", e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(JobStatusReq req, Long id) {
        ScheduleJobDO job = jobMapper.selectById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        job.setStatus(req.getJobStatus());
        jobMapper.updateById(job);

        // 更新 Quartz 调度器中的任务状态
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(id), job.getJobGroup());
            if (req.getJobStatus() == 0) {
                // 恢复任务
                scheduler.resumeJob(jobKey);
            } else {
                // 暂停任务
                scheduler.pauseJob(jobKey);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("更新任务状态失败", e);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        ScheduleJobDO job = jobMapper.selectById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        jobMapper.deleteById(id);

        // 从 Quartz 调度器中删除任务
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(id), job.getJobGroup());
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException("删除定时任务失败", e);
        }
        return true;
    }

    @Override
    public boolean trigger(JobTriggerReq req) {
        ScheduleJobDO job = jobMapper.selectById(req.getJobId());
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }

        // 立即执行一次任务
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(job.getId()), job.getJobGroup());
            scheduler.triggerJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            throw new RuntimeException("执行任务失败", e);
        }
    }

    @Override
    public List<String> listGroup() {
        LambdaQueryWrapper<ScheduleJobDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ScheduleJobDO::getJobGroup).groupBy(ScheduleJobDO::getJobGroup);
        return jobMapper.selectList(wrapper)
            .stream()
            .map(ScheduleJobDO::getJobGroup)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 将任务添加到 Quartz 调度器
     */
    private void addJobToScheduler(ScheduleJobDO job) {
        try {
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

            if (job.getStatus() == 1) {
                scheduler.pauseJob(JobKey.jobKey(String.valueOf(job.getId()), job.getJobGroup()));
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("添加定时任务失败", e);
        }
    }

    /**
     * 转换为响应对象
     */
    private JobResp convertToResp(ScheduleJobDO job) {
        JobResp resp = new JobResp();
        resp.setId(job.getId());
        resp.setJobName(job.getJobName());
        resp.setGroupName(job.getJobGroup());
        resp.setInvokeTarget(job.getInvokeTarget());
        resp.setCronExpression(job.getCronExpression());
        resp.setMisfirePolicy(job.getMisfirePolicy());
        resp.setConcurrent(job.getConcurrent());
        resp.setJobStatus(job.getStatus());
        resp.setRemark(job.getRemark());
        resp.setCreateTime(job.getCreateTime());
        resp.setUpdateTime(job.getUpdateTime());
        return resp;
    }
}