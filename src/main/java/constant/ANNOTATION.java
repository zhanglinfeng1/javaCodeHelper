package constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 16:27
 */
public class ANNOTATION {
    /** feign注解 */
    public static final String OPEN_FEIGN_CLIENT = "org.springframework.cloud.openfeign.FeignClient";
    public static final String NETFLIX_FEIGN_CLIENT = "org.springframework.cloud.netflix.feign.FeignClient";

    /** mapping注解 */
    public static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    public static final String DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";

    /** controller注解 */
    public static final String CONTROLLER_1 = "org.springframework.web.bind.annotation.Controller";
    public static final String CONTROLLER_2 = "org.springframework.stereotype.Controller";
    public static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    public static final List<String> CONTROLLER_LIST = Arrays.asList(CONTROLLER_1, CONTROLLER_2, REST_CONTROLLER);

    /** ibatis注解 */
    public static final String IBATIS_SELECT_PROVIDER = "org.apache.ibatis.annotations.SelectProvider";
    public static final String IBATIS_INSERT_PROVIDER = "org.apache.ibatis.annotations.InsertProvider";
    public static final String IBATIS_UPDATE_PROVIDER = "org.apache.ibatis.annotations.UpdateProvider";
    public static final String IBATIS_DELETE_PROVIDER = "org.apache.ibatis.annotations.DeleteProvider";
    public static final List<String> IBATIS_PROVIDER_LIST = Arrays.asList(IBATIS_SELECT_PROVIDER, IBATIS_INSERT_PROVIDER, IBATIS_UPDATE_PROVIDER, IBATIS_DELETE_PROVIDER);

    /** 注解常用属性 */
    public static final String VALUE = "value";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    public static final String TYPE = "type";

}
