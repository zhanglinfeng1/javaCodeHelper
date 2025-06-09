package pers.zlf.plugin.constant;

/**
 * @author zhanglinfeng
 * @date create in 2022/9/26 16:27
 */
public class Annotation {
    /** feign注解 */
    public static final String OPEN_FEIGN_CLIENT = "org.springframework.cloud.openfeign.FeignClient";
    public static final String NETFLIX_FEIGN_CLIENT = "org.springframework.cloud.netflix.feign.FeignClient";

    /** mapping注解 */
    public static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    public static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    public static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    public static final String PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    public static final String DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    public static final String PATCH_MAPPING = "org.springframework.web.bind.annotation.PatchMapping";

    /** controller注解 */
    public static final String CONTROLLER_1 = "org.springframework.web.bind.annotation.Controller";
    public static final String CONTROLLER_2 = "org.springframework.stereotype.Controller";
    public static final String REST_CONTROLLER = "org.springframework.web.bind.annotation.RestController";

    /** ibatis注解 */
    public static final String IBATIS_SELECT_PROVIDER = "org.apache.ibatis.annotations.SelectProvider";
    public static final String IBATIS_INSERT_PROVIDER = "org.apache.ibatis.annotations.InsertProvider";
    public static final String IBATIS_UPDATE_PROVIDER = "org.apache.ibatis.annotations.UpdateProvider";
    public static final String IBATIS_DELETE_PROVIDER = "org.apache.ibatis.annotations.DeleteProvider";
    public static final String IBATIS_SELECT = "org.apache.ibatis.annotations.Select";
    public static final String IBATIS_INSERT = "org.apache.ibatis.annotations.Insert";
    public static final String IBATIS_UPDATE = "org.apache.ibatis.annotations.Update";
    public static final String IBATIS_DELETE = "org.apache.ibatis.annotations.Delete";
    public static final String IBATIS_PARAM = "org.apache.ibatis.annotations.Param";

    /** 注解常用属性 */
    public static final String VALUE = "value";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    public static final String TAGS = "tags";
    public static final String REQUIRED = "required";
    public static final String NAME = "name";
    public static final String SUMMARY = "summary";
    public static final String HIDDEN = "hidden";

    /** api注解 */
    public static final String SPRINGFOX_API_IGNORE = "springfox.documentation.annotations.ApiIgnore";
    public static final String SWAGGER2_API = "io.swagger.annotations.Api";
    public static final String SWAGGER2_API_OPERATION = "io.swagger.annotations.ApiOperation";
    public static final String SWAGGER2_API_PARAM = "io.swagger.annotations.ApiParam";
    public static final String SWAGGER2_API_MODEL = "io.swagger.annotations.ApiModel";
    public static final String SWAGGER2_API_MODEL_PROPERTY = "io.swagger.annotations.ApiModelProperty";
    public static final String SWAGGER3_TAG = "io.swagger.v3.oas.annotations.tags.Tag";
    public static final String SWAGGER3_OPERATION = "io.swagger.v3.oas.annotations.Operation";
    public static final String SWAGGER3_SCHEMA = "io.swagger.v3.oas.annotations.media.Schema";
    public static final String SWAGGER3_PARAMETER = "io.swagger.v3.oas.annotations.Parameter";

    /** 参数注解 */
    public static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
    public static final String REQUEST_PART = "org.springframework.web.bind.annotation.RequestPart";
    public static final String REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
    public static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    public static final String REQUEST_ATTRIBUTE = "org.springframework.web.bind.annotation.RequestAttribute";
    public static final String REQUEST_HEADER = "org.springframework.web.bind.annotation.RequestHeader";

}
