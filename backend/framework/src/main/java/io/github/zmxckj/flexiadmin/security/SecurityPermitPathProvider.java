package io.github.zmxckj.flexiadmin.security;

import java.util.List;

/**
 * 安全开放路径提供者接口
 * 其他项目可以实现此接口来添加开放路径
 */
public interface SecurityPermitPathProvider {
    
    /**
     * 获取开放路径列表
     * @return 开放路径列表
     */
    List<String> getPermitPaths();
}
