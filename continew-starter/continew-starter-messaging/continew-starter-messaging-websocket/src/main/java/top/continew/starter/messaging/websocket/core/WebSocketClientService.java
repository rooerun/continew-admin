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

package top.continew.starter.messaging.websocket.core;

import org.springframework.http.server.ServletServerHttpRequest;

/**
 * WebSocket 客户端服务
 *
 * @author Charles7c
 * @since 2.1.0
 */
public interface WebSocketClientService {

    /**
     * 获取当前客户端 ID
     *
     * @param request 请求对象
     * @return 当前客户端 ID
     */
    String getClientId(ServletServerHttpRequest request);
}
