# 定时任务模块从 Snail-Job 迁移到 Quartz 说明

## 概述

本次迁移将 `continew-plugin-schedule` 模块从分布式任务调度平台 Snail-Job 切换到本地任务调度框架 Quartz。

## 主要变更

### 1. 依赖变更

**移除的依赖：**
- `snail-job-client-starter`
- `snail-job-client-retry-core`
- `snail-job-client-job-core`
- `spring-cloud-starter-openfeign`

**新增的依赖：**
- `spring-boot-starter-quartz` - Quartz 任务调度框架
- `mybatis-plus-spring-boot3-starter` - 持久层框架

### 2. 数据库表变更

新增两张表用于存储任务信息和执行日志：

**schedule_job（定时任务表）**
- 存储任务的基本信息、cron 表达式、执行状态等

**schedule_job_log（定时任务日志表）**
- 记录每次任务执行的详细信息，包括执行时间、状态、异常信息等

SQL 脚本位置：`continew-plugin-schedule/src/main/resources/db/schedule_tables.sql`

### 3. 配置变更

**原配置（Snail-Job）：**
```yaml
snail-job:
  enabled: true
  namespace: xxx
  server:
    api:
      url: http://xxx
      username: xxx
      password: xxx
```

**新配置（Quartz）：**
```yaml
schedule:
  enabled: true  # 是否启用定时任务
```

### 4. 代码架构变更

#### 删除的文件
- `SnailJobConfiguration.java` - Snail-Job 配置类
- `FeignRequestInterceptor.java` - Feign 请求拦截器
- `FeignConfiguration.java` - Feign 配置类
- `JobApi.java` - 任务 API 接口
- `JobBatchApi.java` - 任务批次 API 接口
- `JobGroupApi.java` - 任务组 API 接口
- `JobClient.java` - 任务客户端

#### 新增的文件
- `QuartzConfiguration.java` - Quartz 配置类
- `QuartzJobExecutor.java` - Quartz 任务执行器
- `SpringUtils.java` - Spring 工具类
- `ScheduleJobDO.java` - 任务实体类
- `ScheduleJobLogDO.java` - 任务日志实体类
- `ScheduleJobMapper.java` - 任务 Mapper
- `ScheduleJobLogMapper.java` - 任务日志 Mapper

#### 修改的文件
- `JobServiceImpl.java` - 重构为使用 Quartz API
- `JobLogServiceImpl.java` - 重构为使用 MyBatis Plus
- `JobConstants.java` - 更新常量定义
- `ConditionalOnEnabledScheduleJob.java` - 更新配置前缀
- `ConditionalOnDisabledScheduleJob.java` - 更新配置前缀
- `DefaultController.java` - 更新错误提示信息

### 5. 功能对比

| 功能 | Snail-Job | Quartz | 说明 |
|------|-----------|--------|------|
| 任务管理 | ✅ | ✅ | 增删改查 |
| Cron 表达式 | ✅ | ✅ | 支持标准 Cron |
| 任务启停 | ✅ | ✅ | 暂停/恢复 |
| 立即执行 | ✅ | ✅ | 手动触发 |
| 执行日志 | ✅ | ✅ | 记录执行情况 |
| 分布式调度 | ✅ | ❌ | Quartz 为单机版 |
| 任务重试 | ✅ | ❌ | 需自行实现 |
| 失败策略 | ✅ | ⚠️ | Misfire 策略 |
| 并发控制 | ✅ | ✅ | 支持/禁止并发 |

## 使用说明

### 创建定时任务

1. **编写任务类**

```java
@Slf4j
@Component("myTask")  // Bean 名称将作为调用目标的第一部分
public class MyTask {
    
    public void execute() {
        log.info("任务执行成功");
        // 业务逻辑
    }
}
```

2. **通过 API 创建任务**

调用 `/schedule/job` 接口创建任务，参数示例：
```json
{
  "jobName": "测试任务",
  "groupName": "DEFAULT",
  "invokeTarget": "myTask.execute",  // 格式：Bean名称.方法名
  "cronExpression": "0 0/5 * * * ?",  // 每5分钟执行一次
  "misfirePolicy": 3,  // 失败策略：1立即执行 2执行一次 3放弃执行
  "concurrent": 1,  // 是否并发：0允许 1禁止
  "jobStatus": 0,  // 状态：0正常 1暂停
  "remark": "备注信息"
}
```

### 任务调用目标格式

格式：`Bean名称.方法名`

示例：
- `testTask.test` - 调用 testTask Bean 的 test 方法
- `myService.process` - 调用 myService Bean 的 process 方法

**注意：** 任务类必须注册为 Spring Bean（使用 @Component 或其他注解），且方法必须是公共无参方法。

### 查询任务日志

调用 `/schedule/log` 接口查询任务执行日志，支持按以下条件筛选：
- 任务 ID
- 任务名称
- 执行状态
- 执行时间范围

## 注意事项

1. **分布式场景**：Quartz 是单机版任务调度，如果需要分布式调度，需要考虑其他方案（如 Quartz 集群模式或保留 Snail-Job）

2. **任务持久化**：当前实现使用内存存储 Quartz 任务信息，应用重启后需要重新加载数据库中的任务到调度器

3. **失败处理**：Quartz 提供 Misfire 策略处理错过执行时间的任务，但不支持自动重试

4. **监控告警**：Snail-Job 提供的可视化监控和告警功能需要自行实现或集成其他工具

5. **数据迁移**：如果之前使用 Snail-Job，需要手动迁移历史任务和日志数据

## 优势

1. **简化架构**：无需部署额外的 Snail-Job Server
2. **降低依赖**：减少外部组件依赖，降低系统复杂度
3. **易于维护**：Quartz 是成熟稳定的开源框架，社区活跃
4. **性能更好**：本地调用，无网络开销

## 劣势

1. **不支持分布式**：无法在多个实例间协调任务执行
2. **功能相对简单**：缺少可视化管理界面、重试机制等高级功能
3. **扩展性受限**：需要自行实现缺失的功能

## 后续优化建议

1. 添加应用启动时自动加载数据库中的任务到 Quartz 调度器
2. 实现任务执行失败的重试机制
3. 集成可视化管理界面（如 Quartz UI）
4. 添加任务执行监控和告警
5. 考虑使用 Quartz 集群模式支持高可用

## 迁移检查清单

- [x] 更新 pom.xml 依赖
- [x] 创建数据库表
- [x] 创建实体类和 Mapper
- [x] 实现 Quartz 配置
- [x] 重构服务层代码
- [x] 更新配置类和常量
- [x] 删除 Snail-Job 相关代码
- [x] 更新配置文件
- [x] 创建示例任务
- [ ] 执行数据库迁移脚本
- [ ] 测试任务创建、修改、删除
- [ ] 测试任务执行和日志记录
- [ ] 验证应用重启后任务加载
