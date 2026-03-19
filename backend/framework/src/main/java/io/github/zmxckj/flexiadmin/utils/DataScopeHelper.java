package io.github.zmxckj.flexiadmin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataScopeHelper
{
    public static final Logger log = LoggerFactory.getLogger(DataScopeHelper.class);

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void set(String value)
    {
        log.info("Set Data Scope SQL: {}", value);
        CONTEXT_HOLDER.set(value);
    }

    public static String get()
    {
        return CONTEXT_HOLDER.get();
    }

    public static void clear()
    {
        log.info("Clear Data Scope SQL");
        CONTEXT_HOLDER.remove();
    }
}
