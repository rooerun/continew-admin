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
import org.springframework.stereotype.Service;
import top.continew.admin.schedule.mapper.ScheduleJobLogMapper;
import top.continew.admin.schedule.model.entity.ScheduleJobLogDO;
import top.continew.admin.schedule.model.query.JobLogQuery;
import top.continew.admin.schedule.model.resp.JobLogResp;
import top.continew.admin.schedule.service.JobLogService;
import top.continew.starter.extension.crud.model.resp.PageResp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务日志业务实现
 *
 * @author KAI
 * @author Charles7c
 * @since 2024/6/27 22:54
 */
@Service
@RequiredArgsConstructor
public class JobLogServiceImpl implements JobLogService {

    private final ScheduleJobLogMapper jobLogMapper;

    @Override
    public PageResp<JobLogResp> page(JobLogQuery query) {
        LambdaQueryWrapper<ScheduleJobLogDO> wrapper = new LambdaQueryWrapper<>();
        if (query.getJobId() != null) {
            wrapper.eq(ScheduleJobLogDO::getJobId, query.getJobId());
        }
        if (StrUtil.isNotBlank(query.getJobName())) {
            wrapper.like(ScheduleJobLogDO::getJobName, query.getJobName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ScheduleJobLogDO::getStatus, query.getStatus());
        }
        if (query.getStartTime() != null && query.getEndTime() != null) {
            wrapper.between(ScheduleJobLogDO::getStartTime, query.getStartTime(), query.getEndTime());
        }
        wrapper.orderByDesc(ScheduleJobLogDO::getCreateTime);

        Page<ScheduleJobLogDO> page = jobLogMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        List<JobLogResp> list = page.getRecords().stream().map(this::convertToResp).collect(Collectors.toList());

        PageResp<JobLogResp> result = new PageResp<>();
        result.setList(list);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public boolean stop(Long id) {
        // Quartz 中不支持单独停止某个执行实例，这里可以记录日志或实现其他逻辑
        throw new UnsupportedOperationException("Quartz 不支持此操作");
    }

    @Override
    public boolean retry(Long id) {
        // Quartz 中不支持直接重试，需要重新触发任务
        throw new UnsupportedOperationException("Quartz 不支持此操作");
    }

    /**
     * 保存任务日志
     */
    public void save(ScheduleJobLogDO jobLog) {
        jobLogMapper.insert(jobLog);
    }

    /**
     * 转换为响应对象
     */
    private JobLogResp convertToResp(ScheduleJobLogDO log) {
        JobLogResp resp = new JobLogResp();
        resp.setId(log.getId());
        resp.setJobGroup(log.getJobGroup());
        resp.setInvokeTarget(log.getInvokeTarget());
        resp.setJobMessage(log.getJobMessage());
        resp.setStatus(log.getStatus());
        resp.setExceptionInfo(log.getExceptionInfo());
        resp.setStartTime(log.getStartTime());
        resp.setEndTime(log.getEndTime());
        resp.setCreateTime(log.getCreateTime());
        return resp;
    }
}
