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

package top.continew.admin.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.continew.admin.system.enums.NoticeMethodEnum;
import top.continew.admin.system.enums.NoticeStatusEnum;
import top.continew.admin.system.mapper.NoticeMapper;
import top.continew.admin.system.model.entity.NoticeDO;
import top.continew.admin.system.service.NoticeService;
import top.continew.starter.core.util.CollUtils;
import top.continew.starter.extension.tenant.annotation.TenantIgnore;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告发布任务
 *
 * @author Charles7c
 * @since 2025/5/11 22:19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NoticePublishJob {

    /**
     * 定时发布公告
     */
    @TenantIgnore
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice() {
        log.info("定时任务 [公告发布] 开始执行。");
        publishNoticeInternal();
        log.info("定时任务 [公告发布] 执行结束。");
    }

    /**
     * 发布公告内部方法
     */
    private static void publishNoticeInternal() {
        NoticeMapper noticeMapper = SpringUtil.getBean(NoticeMapper.class);
        // 查询待发布公告
        List<NoticeDO> list = noticeMapper.lambdaQuery()
            .eq(NoticeDO::getStatus, NoticeStatusEnum.PENDING)
            .le(NoticeDO::getPublishTime, LocalDateTime.now())
            .list();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 筛选需要发送消息的公告并发送
        List<NoticeDO> needSendMessageList = list.stream()
            .filter(notice -> CollUtil.isNotEmpty(notice.getNoticeMethods()))
            .filter(notice -> notice.getNoticeMethods().contains(NoticeMethodEnum.SYSTEM_MESSAGE.getValue()))
            .toList();
        if (CollUtil.isNotEmpty(needSendMessageList)) {
            // 发送消息
            NoticeService noticeService = SpringUtil.getBean(NoticeService.class);
            needSendMessageList.parallelStream().forEach(noticeService::publish);
        }
        // 更新状态
        noticeMapper.lambdaUpdate()
            .set(NoticeDO::getStatus, NoticeStatusEnum.PUBLISHED)
            .in(NoticeDO::getId, CollUtils.mapToList(list, NoticeDO::getId))
            .update();
    }
}
