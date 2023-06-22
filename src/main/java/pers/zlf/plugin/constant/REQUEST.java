package pers.zlf.plugin.constant;

import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/12/8 15:49
 */
public class REQUEST {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final List<String> TYPE_LIST = List.of(GET, POST, PUT, DELETE);

    /** 超时时间 */
    public static final int SOCKET_TIMEOUT = 2000;

}
