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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * mysql相关任务
 * @author c-long.chan
 * @date 2022/2/17 12:37
 */

@Component
@Slf4j
public class MysqlBackupScheduleTask {

    @Value("${mysql-backup.host}")
    private String host;
    @Value("${mysql-backup.port}")
    private Integer port;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${mysql-backup.qi-niu-yun.linuxPath}")
    private String linuxPath;
    @Value("${mysql-backup.qi-niu-yun.winPath}")
    private String winPath;
    @Value("${mysql-backup.qi-niu-yun.accessKey}")
    private String accessKey;
    @Value("${mysql-backup.qi-niu-yun.secretKey}")
    private String secretKey;
    @Value("${mysql-backup.qi-niu-yun.bucket}")
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
        String localFilePath = (isOsLinux() ? linuxPath + "/" : winPath + "\\") + backName;
        boolean upload = upload(localFilePath, backName);
        if (!upload){
            throw new NotFoundException("备份数据失败,请联系管理员");
        }
    }

    /**
     * 提取mysql数据操作
     * @param backName sql备份名
     */
    private void dataBaseDump(String backName) throws IOException, InterruptedException {
        Path path = Paths.get(isOsLinux() ? linuxPath : winPath);
//        File file = new File(isOsLinux() ? linuxPath : winPath);
        //判断目录是否存在
        //File存在创建文件夹的缺陷，改用Files
        log.info("判断目录是否存在:{}",path);
        if (Files.notExists(path)){
            log.info("目录不存在，创建它~");
//            boolean mkdir = file.mkdir();
                Path directories = Files.createDirectory(path);
                log.info("创建目录成功:{}",directories);

//            if (mkdir){
//                log.info("创建文件夹成功");
//            }else{
//                log.error("创建文件夹失败");
//                throw new NotFoundException("创建文件夹失败");
//            }
        }
        File datafile = new File(path + File.separator + backName);
        if (datafile.exists()){
            log.warn("文件名已存在，请更换:{}",backName);
            return;
        }
        //拼接cmd命令
        String command = (isOsLinux() ?
                "docker exec -it mysql mysqldump -u" + username + " -p" + password + " r_blog > " :
                "cmd /c mysqldump -h" + host + " -P" + port + " -u" + username + " -p" + password + " r_blog > "
                ) + datafile;
        log.info("备份命令为:{}",command);
        Process exec = Runtime.getRuntime().exec(command);
        if (exec.waitFor() == 0){
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
