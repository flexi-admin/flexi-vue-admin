package io.github.zmxckj.flexiadmin.excel.service;

import cn.idev.excel.FastExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.WriteTable;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExcelExportService {

    public <T> void export(OutputStream outputStream, List<T> data, Class<T> clazz) {
        FastExcel.write(outputStream, clazz)
                .sheet("Sheet1")
                .doWrite(data);
    }

    public <T> void export(OutputStream outputStream, List<T> data, Class<T> clazz, String sheetName) {
        FastExcel.write(outputStream, clazz)
                .sheet(sheetName)
                .doWrite(data);
    }

    public <T extends OutputStream> void exportWithTable(OutputStream outputStream, List<T> data, Class<T> clazz) {
        ExcelWriter excelWriter = FastExcel.write(outputStream).build();
        WriteSheet writeSheet = FastExcel.writerSheet().build();
        WriteTable writeTable = FastExcel.writerTable().needHead(true).build();
        excelWriter.write(data, writeSheet, writeTable);
        excelWriter.finish();
    }

    public <T> void fillTemplate(OutputStream outputStream, Map<String, Object> data, String templatePath) {
        FastExcel.write(outputStream)
                .withTemplate(templatePath)
                .sheet()
                .doFill(data);
    }

    public <T> void fillTemplateWithList(OutputStream outputStream, Map<String, Object> data, List<?> list, String listName) {
        ExcelWriter excelWriter = FastExcel.write(outputStream).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        WriteSheet writeSheet = FastExcel.writerSheet().build();
        excelWriter.fill(new FillWrapper(listName, list), fillConfig, writeSheet);
        excelWriter.finish();
    }

    public void exportEmpty(OutputStream outputStream, Class<?> clazz) {
        FastExcel.write(outputStream, clazz)
                .sheet("Sheet1")
                .doWrite((Collection<?>) null);
    }

    public <T> void exportMultipleSheets(OutputStream outputStream, List<SheetData<T>> sheets) {
        ExcelWriter excelWriter = FastExcel.write(outputStream).build();
        int sheetNo = 0;
        for (SheetData<T> sheetData : sheets) {
            WriteSheet writeSheet = FastExcel.writerSheet(sheetNo++).sheetName(sheetData.getSheetName()).build();
            excelWriter.write(sheetData.getData(), writeSheet);
        }
        excelWriter.finish();
    }

    public static class SheetData<T> {
        private final String sheetName;
        private final List<T> data;
        private final Class<T> clazz;

        public SheetData(String sheetName, List<T> data, Class<T> clazz) {
            this.sheetName = sheetName;
            this.data = data;
            this.clazz = clazz;
        }

        public String getSheetName() {
            return sheetName;
        }

        public List<T> getData() {
            return data;
        }

        public Class<T> getClazz() {
            return clazz;
        }
    }
}
