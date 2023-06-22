package pers.zlf.plugin.constant;

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
    public static final List<String> MAPPING_LIST = List.of(REQUEST_MAPPING, POST_MAPPING, GET_MAPPING, PUT_MAPPING, DELETE_MAPPING);

    /** controller注解 */
    public static final String CONTROLLER_1 = "org.springframework.web.bind.annotation.Controller";
    public static final String CONTROLLER_2 = "org.springframework.stereotype.Controller";
    public static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";
    public static final List<String> CONTROLLER_LIST = List.of(CONTROLLER_1, CONTROLLER_2, REST_CONTROLLER);

    /** ibatis注解 */
    public static final String IBATIS_SELECT_PROVIDER = "org.apache.ibatis.annotations.SelectProvider";
    public static final String IBATIS_INSERT_PROVIDER = "org.apache.ibatis.annotations.InsertProvider";
    public static final String IBATIS_UPDATE_PROVIDER = "org.apache.ibatis.annotations.UpdateProvider";
    public static final String IBATIS_DELETE_PROVIDER = "org.apache.ibatis.annotations.DeleteProvider";
    public static final List<String> IBATIS_PROVIDER_LIST = List.of(IBATIS_SELECT_PROVIDER, IBATIS_INSERT_PROVIDER, IBATIS_UPDATE_PROVIDER, IBATIS_DELETE_PROVIDER);

    /** 注解常用属性 */
    public static final String VALUE = "value";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    public static final String TYPE = "type";
    public static final String TAGS = "tags";
    public static final String REQUIRED = "required";

    /** api注解 */
    public static final String SPRINGFOX_API_IGNORE = "springfox.documentation.annotations.ApiIgnore";
    public static final String SWAGGER_API = "io.swagger.annotations.Api";
    public static final String SWAGGER_API_OPERATION = "io.swagger.annotations.ApiOperation";
    public static final String SWAGGER_API_PARAM = "io.swagger.annotations.ApiParam";
    public static final String SWAGGER_API_MODEL = "io.swagger.annotations.ApiModel";
    public static final String SWAGGER_API_MODEL_PROPERTY = "io.swagger.annotations.ApiModelProperty";

    /** 参数注解 */
    public static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    public static final String REQUEST_PART = "org.springframework.web.bind.annotation.RequestPart";
    public static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    public static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    public static final String REQUEST_ATTRIBUTE = "org.springframework.web.bind.annotation.RequestAttribute";
    public static final String REQUEST_HEADER = "org.springframework.web.bind.annotation.RequestHeader";

}
