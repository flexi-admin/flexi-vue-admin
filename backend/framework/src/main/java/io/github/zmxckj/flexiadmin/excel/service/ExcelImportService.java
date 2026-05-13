package io.github.zmxckj.flexiadmin.excel.service;

import cn.idev.excel.FastExcel;
import cn.idev.excel.read.listener.PageReadListener;
import io.github.zmxckj.flexiadmin.excel.listener.ExcelDataListener;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class ExcelImportService {

    public <T> List<T> importData(InputStream inputStream, Class<T> clazz) {
        return importData(inputStream, clazz, 1);
    }

    public <T> List<T> importData(InputStream inputStream, Class<T> clazz, int headRowNumber) {
        ExcelDataListener<T> listener = new ExcelDataListener<>(data -> {}, Integer.MAX_VALUE, true);
        FastExcel.read(inputStream, clazz, listener)
                .headRowNumber(headRowNumber)
                .sheet()
                .doRead();
        return listener.getDataList();
    }

    public <T> void importDataWithHandler(InputStream inputStream, Class<T> clazz, Consumer<List<T>> dataHandler) {
        importDataWithHandler(inputStream, clazz, dataHandler, 100, 1);
    }

    public <T> void importDataWithHandler(InputStream inputStream, Class<T> clazz, Consumer<List<T>> dataHandler, int batchSize) {
        importDataWithHandler(inputStream, clazz, dataHandler, batchSize, 1);
    }

    public <T> void importDataWithHandler(InputStream inputStream, Class<T> clazz, Consumer<List<T>> dataHandler, int batchSize, int headRowNumber) {
        ExcelDataListener<T> listener = new ExcelDataListener<>(dataHandler, batchSize, false);
        FastExcel.read(inputStream, clazz, listener)
                .headRowNumber(headRowNumber)
                .sheet()
                .doRead();
    }

    public <T> void importDataWithPageHandler(InputStream inputStream, Class<T> clazz, Consumer<List<T>> dataHandler, int batchSize) {
        importDataWithPageHandler(inputStream, clazz, dataHandler, batchSize, 1);
    }

    public <T> void importDataWithPageHandler(InputStream inputStream, Class<T> clazz, Consumer<List<T>> dataHandler, int batchSize, int headRowNumber) {
        PageReadListener<T> listener = new PageReadListener<>(dataHandler);
        FastExcel.read(inputStream, clazz, listener)
                .headRowNumber(headRowNumber)
                .sheet()
                .doRead();
    }

    public <T> int importAndSave(InputStream inputStream, Class<T> clazz, Consumer<T> saveHandler) {
        return importAndSave(inputStream, clazz, saveHandler, 1);
    }

    public <T> int importAndSave(InputStream inputStream, Class<T> clazz, Consumer<T> saveHandler, int headRowNumber) {
        List<T> allData = importData(inputStream, clazz, headRowNumber);
        int count = 0;
        for (T data : allData) {
            saveHandler.accept(data);
            count++;
        }
        return count;
    }

    public <T> int importAndBatchSave(InputStream inputStream, Class<T> clazz, Consumer<List<T>> batchSaveHandler) {
        return importAndBatchSave(inputStream, clazz, batchSaveHandler, 1);
    }

    public <T> int importAndBatchSave(InputStream inputStream, Class<T> clazz, Consumer<List<T>> batchSaveHandler, int headRowNumber) {
        final int[] count = {0};
        importDataWithHandler(inputStream, clazz, data -> {
            batchSaveHandler.accept(data);
            count[0] += data.size();
        }, 100, headRowNumber);
        return count[0];
    }
}
