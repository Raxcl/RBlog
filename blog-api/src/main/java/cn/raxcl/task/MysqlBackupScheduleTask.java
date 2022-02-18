package cn.raxcl.task;

import cn.raxcl.exception.NotFoundException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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

//    @Value("${spring.data}")
    private String host = "81.71.87.241";

    private Integer port = 3407;

    private String username = "root";

    private String password = "062627";

    /**
     * 提取mysql数据并同步到百度网盘
     */
    public void syncMysqlToCloud() throws IOException, InterruptedException {
        //1. 提取mysql数据
        log.info("备份数据库");
        String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        dataBaseDump(backName);
        //2. 备份数据至百度网盘

    }



    /**
     * 提取mysql数据操作
     * @param backName sql备份名
     */
    private void dataBaseDump(String backName) throws IOException, InterruptedException {
        File file;
        //判断目录是否存在
        if (isOsLinux()){
            file = new File("/mnt/docker/mysql/mysql_backup");
        }else{
            file = new File("D:\\mysql_backup");
        }

        if (!file.exists()){
            boolean mkdir = file.mkdirs();
            if (!mkdir){
                log.error("创建文件夹失败");
                throw new NotFoundException("创建文件夹失败");
            }
        }
        File datafile = new File(file + File.separator + backName + ".sql");
        if (datafile.exists()){
            log.warn("文件名已存在，请更换:{}",backName);
            return;
        }
//        /bin/sh -c mysqldump -h81.71.87.241 -P3407 -uroot -p062627 r_blog > /mnt/docker/mysql/mysql_backup/2022-02-17-19-43-10.sql
//        docker exec -it mysql mysqldump -uroot -p062627 r_blog > /mnt/docker/mysql/mysql_backup/2022-03-17-19-11-11.sql

        //拼接cmd命令
        String command;
        if (isOsLinux()){
//            s = "/bin/sh -c mysqldump -h" + host + " -P" + port + " -u" + username + " -p" + password + " r_blog > " + datafile;
            command = "docker exec -it mysql mysqldump -u" + username + " -p" + password + " r_blog > " + datafile;
        }else{
            command = "cmd /c mysqldump -h" + host + " -P" + port + " -u" + username + " -p" + password + " r_blog > " + datafile;
        }

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

    public static void main(String[] args) throws IOException, InterruptedException {
        MysqlBackupScheduleTask mysqlBackupScheduleTask = new MysqlBackupScheduleTask();
        String backName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        mysqlBackupScheduleTask.dataBaseDump(backName);
    }
}
