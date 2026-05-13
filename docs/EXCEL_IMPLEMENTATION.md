# Excel导入导出使用文档

## 概述

本框架基于 EasyExcel 3.3.3 实现了 Excel 导入导出功能，提供简洁易用的 API 供业务模块使用。

## 添加依赖

框架已内置 easyexcel 依赖，无需额外添加。

## EasyExcel 原生注解使用

### 核心注解详解

#### 1. @ExcelProperty
用于指定 Excel 列的属性配置。

**参数说明**：
- `value`: 列标题，支持多标题，例如 `@ExcelProperty({"标题1", "标题2"})`
- `index`: 列索引，从 0 开始，指定字段在 Excel 中的位置
- `order`: 列排序，数字越小越靠前
- `converter`: 指定自定义转换器，用于数据类型转换

**示例**：
```java
// 基本用法
@ExcelProperty(value = "用户名", index = 0)
private String username;

// 多标题
@ExcelProperty(value = {"用户信息", "手机号"}, index = 1)
private String phone;

// 自定义转换器
@ExcelProperty(value = "状态", index = 4, converter = StatusConverter.class)
private Boolean status;
```

#### 2. @ExcelIgnore
用于忽略某个字段，导入导出时不会处理该字段。

**示例**：
```java
@ExcelIgnore
private Long tenantId;

@ExcelIgnore
private String password;
```

#### 3. @ExcelIgnoreUnannotated
用于类上，忽略所有没有加 `@ExcelProperty` 注解的字段。

**示例**：
```java
@Data
@ExcelIgnoreUnannotated
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    private String username;
    
    // 此字段会被忽略
    private String password;
}
```

### 格式注解

#### 4. @DateTimeFormat
用于日期/时间格式化，导入导出时对日期类型进行格式化。

**参数说明**：
- `value`: 日期格式，如 `yyyy-MM-dd HH:mm:ss`、`yyyy-MM-dd` 等

**示例**：
```java
// 完整日期时间
@ExcelProperty(value = "创建时间", index = 5)
@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
private LocalDateTime createTime;

// 仅日期
@ExcelProperty(value = "生日", index = 6)
@DateTimeFormat("yyyy-MM-dd")
private LocalDate birthday;

// 仅时间
@ExcelProperty(value = "打卡时间", index = 7)
@DateTimeFormat("HH:mm:ss")
private LocalTime checkInTime;
```

#### 5. @NumberFormat
用于数字格式化，导入导出时对数字类型进行格式化。

**参数说明**：
- `value`: 数字格式，如 `#.##`、`#.00`、`#,##0.00` 等

**示例**：
```java
// 保留两位小数
@ExcelProperty(value = "金额", index = 3)
@NumberFormat("#.##")
private BigDecimal amount;

// 百分比
@ExcelProperty(value = "折扣", index = 4)
@NumberFormat("#.##%")
private BigDecimal discount;

// 千分位分隔符
@ExcelProperty(value = "销售额", index = 5)
@NumberFormat("#,##0.00")
private BigDecimal salesAmount;
```

### 样式注解

#### 6. @ColumnWidth
用于设置列宽度。

**参数说明**：
- `value`: 列宽数值

**示例**：
```java
@ExcelProperty(value = "用户名", index = 0)
@ColumnWidth(15)
private String username;

@ExcelProperty(value = "邮箱", index = 3)
@ColumnWidth(25)
private String email;

@ExcelProperty(value = "描述", index = 4)
@ColumnWidth(40)
private String description;
```

#### 7. @ContentRowHeight
用于设置内容行高度，作用于类上。

**参数说明**：
- `value`: 行高数值

**示例**：
```java
@Data
@ContentRowHeight(25)
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    private String username;
}
```

#### 8. @HeadRowHeight
用于设置表头行高度，作用于类上。

**参数说明**：
- `value`: 行高数值

**示例**：
```java
@Data
@HeadRowHeight(40)
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    private String username;
}
```

#### 9. @HeadFontStyle
用于设置表头字体样式，作用于类或字段上。

**参数说明**：
- `fontName`: 字体名称，如 "宋体"、"Microsoft YaHei" 等
- `fontHeightInPoints`: 字体大小
- `italic`: 是否斜体
- `strikeout`: 是否删除线
- `color`: 字体颜色
- `typeOffset`: 偏移量
- `underline`: 下划线
- `charset`: 编码
- `bold`: 是否粗体

**示例**：
```java
@Data
@HeadFontStyle(fontName = "Microsoft YaHei", fontHeightInPoints = 12, bold = true)
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    @HeadFontStyle(fontName = "Microsoft YaHei", fontHeightInPoints = 14)
    private String username;
}
```

#### 10. @ContentFontStyle
用于设置内容字体样式，作用于类或字段上。

**参数说明**：
与 `@HeadFontStyle` 相同。

**示例**：
```java
@Data
@ContentFontStyle(fontName = "Microsoft YaHei", fontHeightInPoints = 11)
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    @ContentFontStyle(fontName = "Microsoft YaHei", fontHeightInPoints = 12)
    private String username;
}
```

#### 11. @HeadStyle
用于设置表头单元格样式，作用于类或字段上。

**参数说明**：
- `dataFormat`: 数据格式
- `hidden`: 是否隐藏
- `locked`: 是否锁定
- `quotePrefix`: 是否前面有引号
- `horizontalAlignment`: 水平对齐方式
- `wrapped`: 是否换行
- `verticalAlignment`: 垂直对齐方式
- `rotation`: 旋转
- `indent`: 缩进
- `borderLeft`: 左边框
- `borderRight`: 右边框
- `borderTop`: 上边框
- `borderBottom`: 下边框
- `leftBorderColor`: 左边框颜色
- `rightBorderColor`: 右边框颜色
- `topBorderColor`: 上边框颜色
- `bottomBorderColor`: 下边框颜色
- `fillPatternType`: 填充类型
- `fillBackgroundColor`: 填充背景色
- `fillForegroundColor`: 填充前景色
- `shrinkToFit`: 是否自动缩小
- `automatic`: 是否自动样式

**示例**：
```java
@Data
@HeadStyle(fillForegroundColor = FillPatternType.SOLID_FOREGROUND, 
           fillBackgroundColor = IndexedColors.LIGHT_YELLOW.index)
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    @HeadStyle(fillForegroundColor = FillPatternType.SOLID_FOREGROUND, 
               fillBackgroundColor = IndexedColors.LIGHT_GREEN.index)
    private String username;
}
```

#### 12. @ContentStyle
用于设置内容单元格样式，作用于类或字段上。

**参数说明**：
与 `@HeadStyle` 相同。

**示例**：
```java
@Data
@ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
              verticalAlignment = VerticalAlignment.CENTER)
public class UserExcelEntity {
    @ExcelProperty(value = "用户名", index = 0)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    private String username;
}
```

#### 13. @ContentLoopMerge
用于内容循环合并，相邻的相同内容会自动合并。

**参数说明**：
- `eachRow`: 每多少行合并一次
- `columnIndex`: 列索引

**示例**：
```java
@ExcelProperty(value = "部门", index = 0)
@ContentLoopMerge(eachRow = 2)
private String department;
```

#### 14. @OnceAbsoluteMerge
用于一次性绝对合并单元格。

**参数说明**：
- `firstRowIndex`: 起始行索引
- `lastRowIndex`: 结束行索引
- `firstColumnIndex`: 起始列索引
- `lastColumnIndex`: 结束列索引

**示例**：
```java
@Data
@OnceAbsoluteMerge(firstRowIndex = 1, lastRowIndex = 1, firstColumnIndex = 0, lastColumnIndex = 3)
public class UserExcelEntity {
    // 合并第一行的第0-3列
}
```

### 完整示例

```java
package com.example.entity;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.format.NumberFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentFontStyle;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.ContentStyle;
import cn.idev.excel.annotation.write.style.HeadFontStyle;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.enums.HorizontalAlignment;
import cn.idev.excel.enums.VerticalAlignment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@HeadRowHeight(40)
@ContentRowHeight(25)
@HeadFontStyle(fontName = "Microsoft YaHei", fontHeightInPoints = 12, bold = true)
@ContentFontStyle(fontName = "Microsoft YaHei", fontHeightInPoints = 11)
@ContentStyle(horizontalAlignment = HorizontalAlignment.CENTER,
              verticalAlignment = VerticalAlignment.CENTER)
public class UserExcelEntity {

    @ExcelProperty(value = {"用户信息", "ID"}, index = 0)
    @ColumnWidth(10)
    private Long id;

    @ExcelProperty(value = {"用户信息", "用户名"}, index = 1)
    @ColumnWidth(15)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    private String username;

    @ExcelProperty(value = {"用户信息", "昵称"}, index = 2)
    @ColumnWidth(15)
    private String nickname;

    @ExcelProperty(value = {"联系方式", "手机号"}, index = 3)
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty(value = {"联系方式", "邮箱"}, index = 4)
    @ColumnWidth(25)
    @ContentStyle(horizontalAlignment = HorizontalAlignment.LEFT)
    private String email;

    @ExcelProperty(value = {"财务信息", "金额"}, index = 5)
    @ColumnWidth(15)
    @NumberFormat("#.##")
    private BigDecimal amount;

    @ExcelProperty(value = {"时间信息", "创建时间"}, index = 6)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime createTime;

    @ExcelProperty(value = {"时间信息", "生日"}, index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    @ColumnWidth(12)
    private LocalDate birthday;

    @ExcelIgnore
    private String password;

    @ExcelIgnore
    private Long tenantId;
}
```

## 服务类

### ExcelImportService

Excel 导入服务类，提供多种导入方式。

#### 基本用法

```java
@Autowired
private ExcelImportService excelImportService;
```

#### 表头行数与数据起始行配置

在导入 Excel 时，可以通过 `headRowNumber` 参数指定表头行数，数据行会自动从表头行之后开始读取。

**参数说明**：
- `headRowNumber`: 表头行数，默认为 1（即第 1 行为表头）
- 数据起始行 = headRowNumber + 1

**Excel 行号说明**：
- Excel 文件的行号从 1 开始（不是从 0 开始）
- 如果 `headRowNumber = 1`，表示第 1 行为表头，数据从第 2 行开始
- 如果 `headRowNumber = 2`，表示第 1-2 行为表头（合并表头场景），数据从第 3 行开始

**示例**：

```java
// 默认配置：第 1 行为表头，数据从第 2 行开始
InputStream inputStream = new FileInputStream("data.xlsx");
List<MyEntity> data = excelImportService.importData(inputStream, MyEntity.class);
inputStream.close();

// 指定表头行数为 2：第 1-2 行为表头，数据从第 3 行开始
InputStream inputStream = new FileInputStream("data.xlsx");
List<MyEntity> data = excelImportService.importData(inputStream, MyEntity.class, 2);
inputStream.close();

// 带回调的导入，同时指定表头行数和批处理大小
InputStream inputStream = new FileInputStream("data.xlsx");
excelImportService.importDataWithHandler(inputStream, MyEntity.class, batchData -> {
    myService.saveBatch(batchData);
}, 100, 2);  // 每100条处理一次，表头行数为2
inputStream.close();
```

#### 导入全部数据

```java
InputStream inputStream = new FileInputStream("users.xlsx");
List<UserExcelEntity> users = excelImportService.importData(inputStream, UserExcelEntity.class);
inputStream.close();
```

#### 带回调的导入（分批处理）

```java
InputStream inputStream = new FileInputStream("users.xlsx");
excelImportService.importDataWithHandler(inputStream, UserExcelEntity.class, batchData -> {
    userService.saveBatch(batchData);
}, 100);  // 每100条处理一次
inputStream.close();
```

#### 导入并逐条保存

```java
InputStream inputStream = new FileInputStream("users.xlsx");
int count = excelImportService.importAndSave(inputStream, UserExcelEntity.class, user -> {
    userService.save(user);
});
inputStream.close();
System.out.println("共导入 " + count + " 条数据");
```

### ExcelExportService

Excel 导出服务类，提供多种导出方式。

#### 基本用法

```java
@Autowired
private ExcelExportService excelExportService;
```

#### 导出数据到文件

```java
List<UserExcelEntity> users = userService.list();
OutputStream outputStream = new FileOutputStream("users.xlsx");
excelExportService.export(outputStream, users, UserExcelEntity.class);
outputStream.close();
```

#### 指定 Sheet 名称

```java
excelExportService.export(outputStream, users, UserExcelEntity.class, "用户数据");
```

#### 导出空模板

```java
OutputStream outputStream = new FileOutputStream("template.xlsx");
excelExportService.exportEmpty(outputStream, UserExcelEntity.class);
outputStream.close();
```

#### 多 Sheet 导出

```java
List<ExcelExportService.SheetData<UserExcelEntity>> sheets = new ArrayList<>();
sheets.add(new ExcelExportService.SheetData<>("用户列表", users, UserExcelEntity.class));
sheets.add(new ExcelExportService.SheetData<>("部门列表", depts, DeptExcelEntity.class));

OutputStream outputStream = new FileOutputStream("data.xlsx");
excelExportService.exportMultipleSheets(outputStream, sheets);
outputStream.close();
```

#### 填充模板导出

```java
Map<String, Object> data = new HashMap<>();
data.put("title", "用户统计报表");
data.put("date", LocalDate.now().toString());

OutputStream outputStream = new FileOutputStream("report.xlsx");
excelExportService.fillTemplate(outputStream, data, "template.xlsx");
outputStream.close();
```

## Controller 接口示例

```java
package com.example.controller;

import com.example.entity.UserExcelEntity;
import com.example.service.UserService;
import io.github.zmxckj.flexiadmin.excel.service.ExcelExportService;
import io.github.zmxckj.flexiadmin.excel.service.ExcelImportService;
import io.github.zmxckj.flexiadmin.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserExcelController {

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private UserService userService;

    @PostMapping("/import")
    public R<String> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            int count = excelImportService.importAndSave(
                file.getInputStream(),
                UserExcelEntity.class,
                userExcelEntity -> {
                    User user = convertToUser(userExcelEntity);
                    userService.save(user);
                }
            );
            return R.ok("成功导入 " + count + " 条数据");
        } catch (IOException e) {
            return R.fail("导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) {
        try {
            List<UserExcelEntity> users = userService.list().stream()
                .map(this::convertToExcelEntity)
                .toList();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=users.xlsx");

            excelExportService.export(response.getOutputStream(), users, UserExcelEntity.class);
        } catch (IOException e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    private User convertToUser(UserExcelEntity excelEntity) {
        User user = new User();
        user.setUsername(excelEntity.getUsername());
        user.setNickname(excelEntity.getNickname());
        user.setPhone(excelEntity.getPhone());
        user.setEmail(excelEntity.getEmail());
        user.setStatus(excelEntity.getStatus());
        user.setCreateTime(excelEntity.getCreateTime());
        return user;
    }

    private UserExcelEntity convertToExcelEntity(User user) {
        UserExcelEntity excelEntity = new UserExcelEntity();
        excelEntity.setUsername(user.getUsername());
        excelEntity.setNickname(user.getNickname());
        excelEntity.setPhone(user.getPhone());
        excelEntity.setEmail(user.getEmail());
        excelEntity.setStatus(user.getStatus());
        excelEntity.setCreateTime(user.getCreateTime());
        return excelEntity;
    }
}
```

## 注意事项

1. **文件流关闭**：使用完 InputStream/OutputStream 后务必关闭，建议使用 try-with-resources。

2. **大数据量导入**：建议使用 `importDataWithHandler` 分批处理，避免内存溢出。

3. **日期格式**：使用 `@DateTimeFormat` 注解确保日期正确解析。

4. **列索引**：`@ExcelProperty` 的 `index` 从 0 开始，与 Excel 列对应。

5. **Boolean类型**：导出时Boolean类型会显示为"true"或"false"，如需显示其他格式需自定义转换器。

6. **文件大小**：Spring Boot 默认文件上传大小限制为 1MB，需在配置文件中调整：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
```

## 文件结构

```
io.github.zmxckj.flexiadmin.excel
├── listener
│   └── ExcelDataListener.java  # 数据监听器
└── service
    ├── ExcelImportService.java # 导入服务
    └── ExcelExportService.java # 导出服务
```
