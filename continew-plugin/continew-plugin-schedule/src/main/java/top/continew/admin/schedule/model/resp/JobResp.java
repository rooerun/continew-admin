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

package top.continew.admin.schedule.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务响应参数
 *
 * @author KAI
 * @author Charles7c
 * @since 2024/6/25 17:15
 */
@Data
@Schema(description = "任务响应参数")
public class JobResp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID", example = "1")
    private Long id;

    /**
     * 任务组
     */
    @Schema(description = "任务组", example = "DEFAULT")
    private String groupName;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称", example = "定时任务1")
    private String jobName;

    /**
     * 调用目标字符串（类名.方法名）
     */
    @Schema(description = "调用目标字符串", example = "testTask.test")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @Schema(description = "cron执行表达式", example = "0 0/5 * * * ?")
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @Schema(description = "计划执行错误策略", example = "3")
    private Integer misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Schema(description = "是否并发执行", example = "1")
    private Integer concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Schema(description = "任务状态", example = "0")
    private Integer jobStatus;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "备注信息")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2023-08-08 08:08:00")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", example = "2023-08-08 08:08:00")
    private LocalDateTime updateTime;
}