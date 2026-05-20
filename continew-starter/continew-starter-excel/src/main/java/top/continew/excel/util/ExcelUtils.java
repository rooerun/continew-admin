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

package top.continew.excel.util;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.read.listener.PageReadListener;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import top.continew.excel.converter.ExcelBigNumberConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Excel 工具类（基于 FastExcel）
 *
 * @author Charles7c
 * @since 1.0.0
 */
public class ExcelUtils {

    private static final String XLSX = ".xlsx";

    private ExcelUtils() {
    }

    // ==================== 导入 ====================

    /**
     * 读取 Excel 文件并转换为对象列表
     *
     * @param file  文件
     * @param clazz 对象类型
     * @return 对象列表
     */
    public static <T> List<T> readFile(File file, Class<T> clazz) {
        return FastExcelFactory.read(file, clazz, new PageReadListener<>(dataList -> {})).sheet().doReadSync();
    }

    /**
     * 读取上传的 Excel 文件并转换为对象列表
     *
     * @param clazz 对象类型
     * @return 对象列表
     */
    public static <T> List<T> readMultipartFile(InputStream in, Class<T> clazz) throws IOException {
        return FastExcelFactory.read(in, clazz, new PageReadListener<>(dataList -> {})).sheet().doReadSync();
    }

    // ==================== 导出 ====================

    /**
     * 导出
     *
     * @param list     导出数据集合
     * @param clazz    导出数据类型
     * @param outputStream 响应对象
     */
    public static <T> void export(List<T> list,  Class<T> clazz, OutputStream outputStream) throws IOException {
        export(list, "Sheet1", Collections.emptySet(), clazz, outputStream);
    }

    /**
     * 导出
     *
     * @param list                    导出数据集合
     * @param sheetName               工作表名称
     * @param excludeColumnFieldNames 排除字段
     * @param clazz                   导出数据类型
     * @param outputStream                响应对象
     */
    public static <T> void export(List<T> list,
                                  String sheetName,
                                  Set<String> excludeColumnFieldNames,
                                  Class<T> clazz,
                                  OutputStream outputStream) throws IOException {
        FastExcelFactory.write(outputStream, clazz)
            .autoCloseStream(false)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .registerConverter(new ExcelBigNumberConverter())
            .sheet(sheetName)
            .excludeColumnFieldNames(excludeColumnFieldNames)
            .doWrite(list);
    }

    /**
     * 导出到本地文件
     *
     * @param filePath 文件父路径（如：D:/doc/excel/）
     * @param fileName 文件名称（不带尾缀）
     * @param list     导出数据
     * @param clazz    导出数据类型
     * @return 本地文件
     */
    public static <T> File exportFile(String filePath, String fileName, List<T> list, Class<T> clazz) {
        File dirFile = new File(filePath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String separator = filePath.endsWith("/") ? "" : "/";
        String fullPath = filePath + separator + fileName + XLSX;
        File file = new File(fullPath);
        FastExcelFactory.write(file, clazz)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .registerConverter(new ExcelBigNumberConverter())
            .sheet("Sheet1")
            .doWrite(list);
        return file;
    }

    /**
     * 多 Sheet 页导出
     *
     * @param outputStream 响应对象
     * @param fileName 文件名
     * @param sheetMap 多 Sheet 数据（key 为 sheet 名称，value 为数据列表）
     * @param clazz    导出数据类型
     */
    public static <T> void exportManySheet(OutputStream outputStream,
                                           String fileName,
                                           Map<String, List<T>> sheetMap,
                                           Class<T> clazz){
        ExcelWriter excelWriter = FastExcelFactory.write(outputStream, clazz)
            .autoCloseStream(false)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .registerConverter(new ExcelBigNumberConverter())
            .build();
        for (Map.Entry<String, List<T>> entry : sheetMap.entrySet()) {
            WriteSheet writeSheet = FastExcelFactory.writerSheet(entry.getKey()).build();
            excelWriter.write(entry.getValue(), writeSheet);
        }
        excelWriter.finish();
    }


    /**
     * 导出（内部方法，支持空数据列表）
     */
    private static void export(OutputStream outputStream,
                               String sheetName,
                               Set<String> excludeColumnFieldNames,
                               List<?> list) {
        FastExcelFactory.write(outputStream, null)
            .autoCloseStream(false)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .registerConverter(new ExcelBigNumberConverter())
            .sheet(sheetName)
            .excludeColumnFieldNames(excludeColumnFieldNames)
            .doWrite(list);
    }
}
