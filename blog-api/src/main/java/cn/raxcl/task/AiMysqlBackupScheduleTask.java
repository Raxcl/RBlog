package cn.raxcl.task;

import cn.raxcl.exception.NotFoundException;
import com.alibaba.fastjson.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * mysql相关任务  -- 其他业务 ，借用java进行同步
 * @author c-long.chan
 * @date 2023-10-07 14:34:35
 */

@Component
@Slf4j
public class AiMysqlBackupScheduleTask {
    @Value("${ai-backup.qi-niu-yun.path}")
    private String filePath;
    @Value("${mysql-backup.qi-niu-yun.accessKey}")
    private String accessKey;
    @Value("${mysql-backup.qi-niu-yun.secretKey}")
    private String secretKey;
    @Value("${ai-backup.qi-niu-yun.bucket}")
    private String bucket;

    /**
     * 提取mysql数据并同步到七牛云
     */
    public void syncMysqlToCloud() throws IOException, InterruptedException {
        //1. 提取mysql数据
        log.info("开始备份数据库");
        String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".sql";
        dataBaseDump(backName);
        //2. 备份数据至七牛云
        String localFilePath = filePath + File.separator + backName;
        boolean upload = upload(localFilePath, backName);
        if (!upload){
            throw new NotFoundException("数据上传失败,请联系管理员");
        }
    }

    /**
     * 提取mysql数据操作
     * @param backName sql备份名
     */
    private void dataBaseDump(String backName) throws IOException, InterruptedException {
        //非Linux下判断目录是否存在
        log.warn("如果项目部署在docker等容器中，请将目录与宿主机进行映射！！！");
        if(!isOsLinux()){
            Path path = Paths.get(filePath);
            //判断目录是否存在
            //File存在创建文件夹的缺陷，改用Files
            log.info("判断目录是否存在:{}",path);
            if (Files.notExists(path)){
                log.info("目录不存在，创建它~");
                Path directories = Files.createDirectories(path);
                log.info("创建目录成功:{}",directories);
            }else{
                log.info("目录已存在");
            }
        }
        File datafile = new File(filePath + File.separator + backName);
        if (datafile.exists()){
            log.error("文件名已存在，请更换:{}",backName);
            throw new NotFoundException("文件名已存在，请更换");
        }
        //拼接cmd命令
        String command = "sh /mnt/docker/mysql_backup/shell/aiMysqlDump.sh " + backName;
        log.info("备份命令为:{}",command);
        List<String> commandList = new ArrayList<>();
        Collections.addAll(commandList,command.split(" "));
        log.info("commandList为:{}",commandList);
        log.info("开始执行备份操作");
        //执行备份命令
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        log.info("执行完成:{}",processBuilder);
        //读取命令的输出信息
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        process.waitFor();
        if (process.exitValue() != 0) {
            log.error("命令执行失败");
            throw new NotFoundException("命令执行失败");
        }
        log.info("打印进程输出信息====================================================");
        String info;
        while ((info = bufferedReader.readLine()) != null){
            log.info(info);
        }
        log.info("打印进程输出信息结束=====================================================");
        if (process.waitFor() == 0){
            log.info("数据库备份成功，备份路径为:{}",datafile);
        }
    }

    /**
     * 判断项目运行环境是否为Linux
     * @return boolean
     */
    private boolean isOsLinux() {
        Properties properties = System.getProperties();
        String os = properties.getProperty("os.name");
        return os != null && os.toLowerCase().contains("linux");
    }

    /**
     * 将sql备份上传至七牛云
     * @param localFilePath 文件路径（包含文件名）
     * @param fileName 文件名
     * @return boolean
     */
    public boolean upload(String localFilePath,String fileName){
        //1. 构建一个带指定Region对象的配置类（指定七牛云的机房区域）
        Configuration configuration = new Configuration(Region.huanan());
        //其他参数参考类注释
        UploadManager uploadManager = new UploadManager(configuration);
        //准备上传
        try {
            Auth auth = Auth.create(accessKey,secretKey);
            String upToken = auth.uploadToken(bucket);
            //上传文件
            Response response = uploadManager.put(localFilePath, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet defaultPutRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            log.info("上传文件到七牛云成功:{}",defaultPutRet);
            return true;
        } catch (IOException e) {
            log.info("上传文件至七牛云失败:{}",e.toString());
            return false;
        }
    }
}
