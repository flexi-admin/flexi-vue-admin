package com.flexi.app.utils;

/**
 * 雪花算法生成全局唯一ID
 */
public class SnowflakeIdGenerator {
    // 起始时间戳 (2020-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1577836800000L;
    
    // 机器ID位数
    private static final long MACHINE_BIT = 5;
    // 数据中心ID位数
    private static final long DATA_CENTER_BIT = 5;
    // 序列号位数
    private static final long SEQUENCE_BIT = 12;
    
    // 机器ID最大值
    private static final long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    // 数据中心ID最大值
    private static final long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    // 序列号最大值
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    
    // 机器ID左移位数
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    // 数据中心ID左移位数
    private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    // 时间戳左移位数
    private static final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT + DATA_CENTER_BIT;
    
    // 数据中心ID
    private final long dataCenterId;
    // 机器ID
    private final long machineId;
    // 序列号
    private long sequence = 0L;
    // 上一次时间戳
    private long lastTimestamp = -1L;
    
    // 单例实例
    private static SnowflakeIdGenerator instance;
    
    static {
        instance = new SnowflakeIdGenerator(1, 1);
    }
    
    private SnowflakeIdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("DataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("MachineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }
    
    public static SnowflakeIdGenerator getInstance() {
        return instance;
    }
    
    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        
        // 如果当前时间戳小于上一次时间戳，说明时钟回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        // 如果当前时间戳等于上一次时间戳，递增序列号
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号达到最大值，等待下一个时间戳
            if (sequence == 0) {
                timestamp = nextTimestamp(lastTimestamp);
            }
        } else {
            // 如果当前时间戳大于上一次时间戳，重置序列号
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // 组合ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT) |
               (dataCenterId << DATA_CENTER_LEFT) |
               (machineId << MACHINE_LEFT) |
               sequence;
    }
    
    /**
     * 获取下一个时间戳
     */
    private long nextTimestamp(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}