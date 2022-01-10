package cn.raxcl.common;

import cn.raxcl.model.temp.LogDTO;

/**
 * @author c-long.chan
 */
public interface LogService {
    /**
     * 抽取出的，保存日志公共方法
     * @param ip ip
     * @param userAgent userAgent
     * @return LogDTO
     */
    LogDTO saveLog(String ip, String userAgent);
}
