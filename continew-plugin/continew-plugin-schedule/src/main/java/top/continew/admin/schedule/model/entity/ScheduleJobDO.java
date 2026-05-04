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

package top.continew.admin.schedule.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.continew.admin.common.base.model.entity.BaseDO;

import java.io.Serial;

/**
 * 定时任务实体
 *
 * @author Charles7c
 * @since 2025/5/2 10:00
 */
@Data
@TableName("schedule_job")
public class ScheduleJobDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 调用目标字符串（类名.方法名）
     */
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    private Integer misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    private Integer concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
