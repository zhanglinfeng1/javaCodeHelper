package constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author zhanglinfeng
 * @Date create in 2022/9/26 16:27
 */
public class ANNOTATION_CONSTANT {
    /** feign注解 */
    public static final String OPEN_FEIGN_CLIENT = "org.springframework.cloud.openfeign.FeignClient";
    public static final String NETFLIX_FEIGN_CLIENT = "org.springframework.cloud.netflix.feign.FeignClient";

    /** mapping注解 */
    public static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    public static final String DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    public static final List<String> MAPPING_LIST = Arrays.asList(REQUEST_MAPPING, POST_MAPPING, GET_MAPPING, PUT_MAPPING, DELETE_MAPPING);

    /** controller注解 */
    public static final String CONTROLLER = "org.springframework.web.bind.annotation.Controller";
    public static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    public static final List<String> CONTROLLER_LIST = Arrays.asList(CONTROLLER, REST_CONTROLLER);

    /** 注解常用属性 */
    public static final String VALUE = "value";
    public static final String PATH = "path";
    public static final String METHOD = "method";


}
