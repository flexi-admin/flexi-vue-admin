package io.github.zmxckj.flexiadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "flexi.multi-tenancy")
public class MultiTenancyConfig {
    
    private boolean enabled = true;
    
    private String tenantIdColumn = "tenant_id";
    
    private List<String> ignoreTables = new ArrayList<>(Arrays.asList(
            "sys_tenant",
            "sys_config"
    ));

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }
}