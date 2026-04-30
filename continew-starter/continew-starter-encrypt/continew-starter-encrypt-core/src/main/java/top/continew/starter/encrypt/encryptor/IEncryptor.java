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

/**
 * 加密器接口
 *
 * @author Charles7c
 * @author lishuyan
 * @since 1.4.0
 */
public interface IEncryptor {

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 加密后的文本
     */
    String encrypt(String plaintext);

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 解密后的文本
     */
    String decrypt(String ciphertext);
}
