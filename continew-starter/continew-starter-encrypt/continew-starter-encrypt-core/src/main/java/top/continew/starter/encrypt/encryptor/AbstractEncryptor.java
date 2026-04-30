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

package top.continew.starter.encrypt.encryptor;

import top.continew.starter.encrypt.context.CryptoContext;

/**
 * 加密器基类
 *
 * @author lishuyan
 * @since 2.13.2
 */
public abstract class AbstractEncryptor implements IEncryptor {

    protected AbstractEncryptor(CryptoContext context) {
        // 配置校验与配置注入
    }

}
