package io.github.zmxckj.flexiadmin.config;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import io.github.zmxckj.flexiadmin.security.SecurityUtils;
import io.github.zmxckj.flexiadmin.service.UserDeptService;
import io.github.zmxckj.flexiadmin.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Slf4j
public class DataPermissionHandlerImpl implements MultiDataPermissionHandler {

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 在此处编写自定义数据权限逻辑
        System.out.println(table.getName());
        System.out.println(mappedStatementId);
        System.out.println(where);
        return null;
    }
}