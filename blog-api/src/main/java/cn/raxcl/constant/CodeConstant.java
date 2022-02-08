package cn.raxcl.constant;

/**
 * @author c-long.chan
 * @date 2022-01-05 15:53:21
 */
public class CodeConstant {
    private CodeConstant(){}

    public static final Integer SUCCESS = 200;

    /**
     * 1000 * 60 * 60 * 24 * 3 三天
     */
    public static long EXPIRE_TIME = 259200000;
    /**
     * 部署上线务必修改此配置，否则无法保证token安全性(加密盐）
     */
    public static String SECRET_KEY = "3015b8ea-9ceb-4fa9-89ae-8c713ef1130e";
}
