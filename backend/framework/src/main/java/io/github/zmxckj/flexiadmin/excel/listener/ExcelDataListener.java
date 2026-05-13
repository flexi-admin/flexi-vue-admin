package io.github.zmxckj.flexiadmin.excel.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelDataConvertException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExcelDataListener<T> extends AnalysisEventListener<T> {

    private final List<T> dataList = new ArrayList<>();
    private final List<T> allDataList = new ArrayList<>();
    private final Consumer<List<T>> dataHandler;
    private final int batchSize;
    private final boolean collectAllData;
    private int currentRow = 0;

    public ExcelDataListener(Consumer<List<T>> dataHandler, int batchSize) {
        this(dataHandler, batchSize, false);
    }

    public ExcelDataListener(Consumer<List<T>> dataHandler, int batchSize, boolean collectAllData) {
        this.dataHandler = dataHandler;
        this.batchSize = batchSize;
        this.collectAllData = collectAllData;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        currentRow++;
        dataList.add(data);
        if (collectAllData) {
            allDataList.add(data);
        }
        if (dataList.size() >= batchSize) {
            flushData();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!dataList.isEmpty()) {
            flushData();
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        if (exception instanceof ExcelDataConvertException) {
            Integer rowIndex = ((ExcelDataConvertException) exception).getRowIndex();
            Integer columnIndex = ((ExcelDataConvertException) exception).getColumnIndex();
            throw new RuntimeException("Excel解析失败，行: " + (rowIndex + 1) + "，列: " + (columnIndex + 1) + "，错误: " + exception.getMessage());
        }
        throw new RuntimeException("Excel解析异常: " + exception.getMessage());
    }

    private void flushData() {
        dataHandler.accept(new ArrayList<>(dataList));
        dataList.clear();
    }

    public List<T> getDataList() {
        return collectAllData ? allDataList : dataList;
    }

    public int getCurrentRow() {
        return currentRow;
    }
}
