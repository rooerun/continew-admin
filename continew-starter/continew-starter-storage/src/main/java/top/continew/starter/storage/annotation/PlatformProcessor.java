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

package top.continew.starter.storage.annotation;

import java.lang.annotation.*;

/**
 * 平台处理器注解
 * <p>
 * 该注解用于标记文件前置处理器类，以指定其适用的平台范围。
 * 主要用于实现平台特定的文件处理逻辑，如文件名生成、路径转换、格式适配等。
 * <p>
 *
 * @author echo
 * @since 2.14.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlatformProcessor {
    /**
     * 适用的平台列表
     */
    String[] platforms();
}