package cn.raxcl.common.impl;

import cn.raxcl.common.LogService;
import cn.raxcl.model.temp.LogDTO;
import cn.raxcl.util.IpAddressUtils;
import cn.raxcl.util.UserAgentUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author c-long.chan
 * 2022-01-10 08:52:24
 */
@Service
public class LogServiceImpl implements LogService {
    private final UserAgentUtils userAgentUtils;

    public LogServiceImpl(UserAgentUtils userAgentUtils) {
        this.userAgentUtils = userAgentUtils;
    }


    @Override
    public LogDTO saveLog(String ip, String userAgent){
        LogDTO logDTO = new LogDTO(ip, userAgent);
        String ipSource = IpAddressUtils.getCityInfo(logDTO.getIp());
        Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(logDTO.getUserAgent());
        String os = userAgentMap.get("os");
        String browser = userAgentMap.get("browser");
        logDTO.setIpSource(ipSource).setOs(os).setBrowser(browser);
        return logDTO;
    }
}
