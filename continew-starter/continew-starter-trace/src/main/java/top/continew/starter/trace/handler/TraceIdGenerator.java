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

package top.continew.starter.trace.handler;

import com.yomahub.tlog.id.TLogIdGenerator;
import com.yomahub.tlog.id.snowflake.UniqueIdGenerator;

/**
 * TLog ID 生成器
 *
 * @author Jasmine
 * @author Charles7c
 * @since 1.3.0
 */
public class TraceIdGenerator extends TLogIdGenerator {
    @Override
    public String generateTraceId() {
        return String.valueOf(UniqueIdGenerator.generateId());
    }
}