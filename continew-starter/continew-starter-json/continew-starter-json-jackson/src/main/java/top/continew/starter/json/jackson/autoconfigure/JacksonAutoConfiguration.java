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

package top.continew.starter.json.jackson.autoconfigure;

import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;

import cn.hutool.core.date.DatePattern;
import top.continew.starter.core.enums.BaseEnum;
import top.continew.starter.core.util.GeneralPropertySourceFactory;
import top.continew.starter.json.jackson.serializer.BaseEnumDeserializer;
import top.continew.starter.json.jackson.serializer.BaseEnumSerializer;
import top.continew.starter.json.jackson.serializer.SimpleDeserializersWrapper;

/**
 * Jackson 自动配置
 *
 * @author Charles7c
 * @author Jasmine
 * @since 1.0.0
 */
@AutoConfiguration
@PropertySource(value = "classpath:default-json-jackson.yml", factory = GeneralPropertySourceFactory.class)
public class JacksonAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JacksonAutoConfiguration.class);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            JavaTimeModule javaTimeModule = this.javaTimeModule();
            SimpleModule baseEnumModule = this.baseEnumModule();
            SimpleModule bigNumberModule = this.bigNumberModule();

            builder.timeZone(TimeZone.getDefault());
            builder.modules(javaTimeModule, baseEnumModule, bigNumberModule);
            log.debug("[ContiNew Starter] - Auto Configuration 'Jackson' completed initialization.");
        };
    }

    /**
     * 日期时间序列化及反序列化配置
     *
     * @return {@link JavaTimeModule}
     * @since 1.0.0
     */
    private JavaTimeModule javaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 针对时间类型：LocalDateTime 的序列化和反序列化处理
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        // 针对时间类型：LocalDate 的序列化和反序列化处理
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // 针对时间类型：LocalTime 的序列化和反序列化处理
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        // 针对时间类型：Instant 的序列化和反序列化处理
        javaTimeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        // 针对时间类型：Duration 的序列化和反序列化处理
        javaTimeModule.addSerializer(Duration.class, DurationSerializer.INSTANCE);
        javaTimeModule.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        return javaTimeModule;
    }

    /**
     * 枚举序列化及反序列化配置
     *
     * @return SimpleModule /
     * @since 2.4.0
     */
    private SimpleModule baseEnumModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BaseEnum.class, BaseEnumSerializer.SERIALIZER_INSTANCE);
        SimpleDeserializersWrapper deserializers = new SimpleDeserializersWrapper();
        deserializers.addDeserializer(BaseEnum.class, new BaseEnumDeserializer());
        simpleModule.setDeserializers(deserializers);
        return simpleModule;
    }

    /**
     * 大数值序列化及反序列化配置
     * <p>
     * 将所有 Long、long、BigInteger 类型统一序列化为 String 类型，避免前端 JavaScript 精度丢失问题
     * </p>
     *
     * @return SimpleModule /
     * @since 2.12.1
     */
    private SimpleModule bigNumberModule() {
        SimpleModule bigNumberModule = new SimpleModule();
        // 统一使用 ToStringSerializer，将所有大数值转为字符串
        bigNumberModule.addSerializer(Long.class, ToStringSerializer.instance);
        bigNumberModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        bigNumberModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        return bigNumberModule;
    }
}
