# ContiNew Starter 版本管理说明

## 概述

本项目采用灵活的版本管理策略，支持两种模式：
1. **独立版本模式**（默认）：Starter 使用独立的版本号（2.16.0-SNAPSHOT）
2. **统一版本模式**：Starter 与主项目使用相同的版本号（4.2.0-SNAPSHOT）

## 配置位置

### 主项目 pom.xml

```xml
<properties>
    <!-- 主项目版本号 -->
    <revision>4.2.0-SNAPSHOT</revision>
    
    <!-- Starter 版本号（默认使用独立版本） -->
    <continew-starter.version>2.16.0-SNAPSHOT</continew-starter.version>
</properties>
```

所有 Starter 依赖都使用 `${continew-starter.version}` 变量：

```xml
<dependency>
    <groupId>top.continew.starter</groupId>
    <artifactId>continew-starter-core</artifactId>
    <version>${continew-starter.version}</version>
</dependency>
```

## 使用方式

### 方式一：使用独立版本（默认，推荐）

**适用场景**：
- Starter 作为独立库发布到 Maven 中央仓库
- 其他项目也可以引用该 Starter
- Starter 和主项目有各自的版本演进路线

**使用方法**：
```bash
# 直接编译，无需额外参数
mvn clean install
```

此时使用的 Starter 版本为：`2.16.0-SNAPSHOT`

---

### 方式二：使用统一版本（开发调试用）

**适用场景**：
- 本地开发时，Starter 和主项目同时修改
- 需要确保版本完全一致
- 单体仓库开发模式

**使用方法**：
```bash
# 激活 use-local-starter profile
mvn clean install -Puse-local-starter
```

此时使用的 Starter 版本为：`4.2.0-SNAPSHOT`（与主项目一致）

---

### 方式三：永久切换到统一版本

如果您希望默认就使用统一版本，可以修改 `pom.xml`：

```xml
<properties>
    <revision>4.2.0-SNAPSHOT</revision>
    
    <!-- 修改这里，直接使用 ${revision} -->
    <continew-starter.version>${revision}</continew-starter.version>
</properties>
```

然后删除或注释掉 `use-local-starter` profile。

---

## 版本同步流程

### 场景 1：升级主项目版本

```bash
# 1. 修改主项目版本号
# pom.xml: <revision>4.3.0-SNAPSHOT</revision>

# 2. 如果使用统一版本模式，Starter 会自动同步
mvn clean install -Puse-local-starter

# 3. 如果使用独立版本模式，Starter 版本不变
mvn clean install
```

### 场景 2：同时升级 Starter 和主项目

```bash
# 1. 修改 Starter 版本号
# continew-starter/continew-starter-dependencies/pom.xml
# <revision>2.17.0-SNAPSHOT</revision>

# 2. 修改主项目 pom.xml 中的 starter 版本
# <continew-starter.version>2.17.0-SNAPSHOT</continew-starter.version>

# 3. 先安装 Starter
cd continew-starter
mvn clean install

# 4. 再构建主项目
cd ..
mvn clean install
```

---

## 注意事项

### 1. Flatten Maven Plugin

本项目使用了 `flatten-maven-plugin` 来处理 CI 友好的版本号（`${revision}`）。

**作用**：
- 在构建时将 `${revision}` 替换为实际版本号
- 生成的 `.flattened-pom.xml` 包含具体版本号
- 便于发布到 Maven 仓库

**配置**：
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <configuration>
        <updatePomFile>true</updatePomFile>
        <flattenMode>resolveCiFriendliesOnly</flattenMode>
    </configuration>
</plugin>
```

### 2. IDE 配置

**IntelliJ IDEA**：
- 确保启用 "Use plugin registry" 选项
- 重新导入 Maven 项目以识别新的 profile
- 路径：`File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven`

**激活 Profile**：
- 打开 Maven 工具窗口
- 找到 "Profiles" 节点
- 勾选 `use-local-starter` 即可激活

### 3. 多模块构建顺序

```bash
# 正确的构建顺序
mvn clean install -pl continew-starter -am  # 先构建 starter
mvn clean install                            # 再构建主项目

# 或者一次性构建所有模块
mvn clean install
```

---

## 最佳实践建议

### 推荐方案：保持独立版本

**理由**：
1. **语义清晰**：Starter 作为独立组件，有自己的版本意义
2. **兼容性强**：其他项目可以独立引用 Starter
3. **发布灵活**：Starter 可以单独发布到 Maven 中央仓库
4. **历史沿袭**：ContiNew Starter 本身就是从 Admin 项目中抽离出来的独立项目

**版本号对应关系示例**：
```
ContiNew Admin v4.2.0  →  使用  ContiNew Starter v2.16.0
ContiNew Admin v4.3.0  →  使用  ContiNew Starter v2.17.0
ContiNew Admin v5.0.0  →  使用  ContiNew Starter v3.0.0
```

### 何时使用统一版本？

仅在以下场景使用：
- ✅ 本地开发调试，需要同时修改 Starter 和 Admin
- ✅ 内部测试环境，不需要发布到公共仓库
- ❌ 不要用于生产环境发布
- ❌ 不要用于开源项目发布

---

## 常见问题

### Q1: 为什么我的版本号没有生效？

**A**: 检查以下几点：
1. 是否执行了 `mvn clean` 清除缓存
2. 是否正确激活了 profile（查看控制台输出）
3. IDE 是否重新导入了 Maven 项目

### Q2: 如何验证当前使用的 Starter 版本？

**A**: 
```bash
# 查看依赖树
mvn dependency:tree | grep continew-starter

# 或者查看有效 POM
mvn help:effective-pom | grep continew-starter.version -A 1
```

### Q3: 能否让 Starter 自动跟随主项目版本？

**A**: 可以，但不推荐。如果确实需要，修改主项目 pom.xml：
```xml
<properties>
    <continew-starter.version>${revision}</continew-starter.version>
</properties>
```

同时需要修改 `continew-starter-dependencies/pom.xml`，使其继承自主项目（但这会破坏 Starter 的独立性）。

---

## 总结

| 特性 | 独立版本模式 | 统一版本模式 |
|------|------------|------------|
| 默认状态 | ✅ 是 | ❌ 否 |
| 适用场景 | 生产发布 | 本地开发 |
| 版本灵活性 | 高 | 低 |
| 其他项目引用 | ✅ 支持 | ❌ 不支持 |
| 激活方式 | 无需激活 | `-Puse-local-starter` |

**推荐**：日常开发使用独立版本模式，仅在需要同时调试 Starter 和 Admin 时临时切换到统一版本模式。
