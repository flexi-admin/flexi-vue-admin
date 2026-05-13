package io.github.zmxckj.flexiadmin.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TenantHandlerImpl implements TenantLineHandler {

    @Autowired
    private MultiTenancyConfig multiTenancyConfig;

    @Override
    public Expression getTenantId() {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        if (tenantId == null) {
            tenantId = 0L;
        }
        return new LongValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return multiTenancyConfig.getTenantIdColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        if (!multiTenancyConfig.isEnabled()) {
            return true;
        }
        return multiTenancyConfig.getIgnoreTables().contains(tableName);
    }
}