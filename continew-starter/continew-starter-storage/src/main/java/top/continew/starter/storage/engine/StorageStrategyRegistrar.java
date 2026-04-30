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

package top.continew.starter.storage.engine;

import top.continew.starter.storage.strategy.StorageStrategy;

import java.util.List;

/**
 * 存储策略注册
 * <p>
 * 主要针对配置文件
 * <p/>
 *
 * @author echo
 * @since 2.14.0
 */
public interface StorageStrategyRegistrar {

    /**
     * 注册策略到列表
     */
    void register(List<StorageStrategy> strategies);

}