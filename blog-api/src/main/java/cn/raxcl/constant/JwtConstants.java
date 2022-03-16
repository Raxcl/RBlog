package cn.raxcl.constant;

/**
 * jwt常量
 *
 * @author Raxcl
 * @date 2022/3/15 16:11
 */
public class JwtConstants {
    private JwtConstants(){}
    /**
     * 博主token前缀
     */
    public static final String ADMIN_PREFIX = "admin:";

    public static final Integer SUCCESS = 200;

    /**
     * 1000 * 60 * 60 * 24 * 3 三天
     */
    public static final long EXPIRE_TIME = 259200000;
    /**
     * 部署上线务必修改此配置，否则无法保证token安全性(加密盐）
     */
    public static final String SECRET_KEY = "3015b8ea-9ceb-4fa9-89ae-8c713ef1130e";
}
