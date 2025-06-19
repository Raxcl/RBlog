#!/bin/bash

echo "=== PDF模块测试脚本 ==="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请确保已安装Java 8或更高版本"
    exit 1
fi

echo "检测到Java版本:"
java -version

# 设置项目路径
PROJECT_PATH="/Users/raxcl/code/ideaProjects/self/RBlog/blog-api"
cd "$PROJECT_PATH" || exit 1

echo "当前工作目录: $(pwd)"

# 创建临时编译目录
mkdir -p temp/classes
mkdir -p temp/lib

echo "=== 1. 下载必要的依赖JAR包 ==="

# 创建依赖下载脚本
cat > temp/download_deps.sh << 'EOF'
#!/bin/bash

# 创建lib目录
mkdir -p lib

# 下载核心依赖
wget -O lib/itextpdf-5.5.13.3.jar "https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar" 2>/dev/null || echo "iText PDF下载失败，请手动下载"
wget -O lib/itext-asian-5.2.0.jar "https://repo1.maven.org/maven2/com/itextpdf/itext-asian/5.2.0/itext-asian-5.2.0.jar" 2>/dev/null || echo "iText Asian下载失败，请手动下载"
wget -O lib/jfreechart-1.5.3.jar "https://repo1.maven.org/maven2/org/jfree/jfreechart/1.5.3/jfreechart-1.5.3.jar" 2>/dev/null || echo "JFreeChart下载失败，请手动下载"
wget -O lib/jcommon-1.0.24.jar "https://repo1.maven.org/maven2/org/jfree/jcommon/1.0.24/jcommon-1.0.24.jar" 2>/dev/null || echo "JCommon下载失败，请手动下载"

echo "依赖下载完成（部分可能失败，需要手动处理）"
EOF

cd temp
chmod +x download_deps.sh
# ./download_deps.sh

echo "=== 2. 编译PDF模块 ==="

# 设置classpath
CLASSPATH="lib/*:classes"

# 编译DTO类
echo "编译DTO类..."
javac -cp "$CLASSPATH" -d classes "../src/main/java/cn/raxcl/dto/ChartDataPoint.java" 2>/dev/null
javac -cp "$CLASSPATH" -d classes "../src/main/java/cn/raxcl/dto/ChartData.java" 2>/dev/null
javac -cp "$CLASSPATH" -d classes "../src/main/java/cn/raxcl/dto/PdfGenerateRequest.java" 2>/dev/null

# 编译服务类
echo "编译服务类..."
javac -cp "$CLASSPATH" -d classes "../src/main/java/cn/raxcl/service/ChartService.java" 2>/dev/null
javac -cp "$CLASSPATH" -d classes "../src/main/java/cn/raxcl/service/PdfReportService.java" 2>/dev/null

# 编译测试类
echo "编译测试类..."
javac -cp "$CLASSPATH" -d classes "../src/main/java/cn/raxcl/test/PdfModuleTest.java" 2>/dev/null

echo "=== 3. 运行测试 ==="

if [ -f "classes/cn/raxcl/test/PdfModuleTest.class" ]; then
    echo "运行PDF模块测试..."
    java -cp "$CLASSPATH" cn.raxcl.test.PdfModuleTest
    
    if [ -f "test-report.pdf" ]; then
        echo "✅ PDF生成成功！文件保存为: temp/test-report.pdf"
        echo "   文件大小: $(ls -lh test-report.pdf | awk '{print $5}')"
    else
        echo "❌ PDF生成失败"
    fi
else
    echo "❌ 编译失败，无法运行测试"
fi

echo "=== 4. 生成部署指南 ==="

cat > ../DEPLOYMENT_GUIDE.md << 'EOF'
# PDF模块部署指南

## 环境要求

- Java 8 或更高版本
- Spring Boot 2.x 或更高版本
- Maven 3.6 或更高版本

## 依赖配置

确保pom.xml中包含以下依赖：

```xml
<!-- PDF生成 -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>

<!-- 中文字体支持 -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-asian</artifactId>
    <version>5.2.0</version>
</dependency>

<!-- 图表生成 -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.3</version>
</dependency>
```

## 部署步骤

1. **复制PDF模块文件到项目中**
   ```bash
   cp -r src/main/java/cn/raxcl/dto/* your-project/src/main/java/cn/raxcl/dto/
   cp -r src/main/java/cn/raxcl/service/ChartService.java your-project/src/main/java/cn/raxcl/service/
   cp -r src/main/java/cn/raxcl/service/PdfReportService.java your-project/src/main/java/cn/raxcl/service/
   cp -r src/main/java/cn/raxcl/controller/PdfReportController.java your-project/src/main/java/cn/raxcl/controller/
   ```

2. **添加Spring注解（可选）**
   如果需要Spring管理，在控制器上添加注解：
   ```java
   @RestController
   @RequestMapping("/api/pdf-report")
   public class PdfReportController { ... }
   ```

3. **配置字体（重要）**
   确保系统中有STSong-Light字体，或修改PdfReportService中的字体设置。

4. **测试部署**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

5. **验证API**
   ```bash
   curl -X GET http://localhost:8080/api/pdf-report/sample -o test.pdf
   ```

## 故障排除

### 编译错误
- 检查依赖是否正确添加
- 确保Java版本兼容

### 字体问题
- 使用系统默认字体替代STSong-Light
- 或下载并安装对应字体文件

### 内存问题
- 调整JVM堆内存大小：-Xmx512m

## 生产环境建议

1. 配置日志记录
2. 添加监控和健康检查
3. 设置合理的超时时间
4. 配置错误处理和重试机制
5. 考虑使用对象池优化性能

EOF

echo "✅ 部署指南已生成: DEPLOYMENT_GUIDE.md"

cd ..
echo "=== 测试完成 ==="
echo ""
echo "📝 接下来的步骤："
echo "1. 查看生成的文档: PDF_MODULE_GUIDE.md"
echo "2. 查看部署指南: DEPLOYMENT_GUIDE.md"
echo "3. 启动Spring Boot应用测试API接口"
echo "4. 使用curl命令测试API功能"
echo ""
echo "🔧 如果遇到编译问题："
echo "1. 确保Maven依赖已正确配置"
echo "2. 检查Java环境和版本"
echo "3. 手动下载依赖JAR包到lib目录"
echo ""
echo "�� 如需帮助，请参考文档或联系开发团队" 