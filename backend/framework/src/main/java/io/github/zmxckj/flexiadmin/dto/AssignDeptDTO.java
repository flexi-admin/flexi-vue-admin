package io.github.zmxckj.flexiadmin.dto;

import java.util.List;

public class AssignDeptDTO {
    private Long userId;
    private List<Long> deptIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds) {
        this.deptIds = deptIds;
    }
}