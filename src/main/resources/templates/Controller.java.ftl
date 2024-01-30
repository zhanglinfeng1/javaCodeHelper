package ${packagePath};

import ${packagePath}.${tableName};
import ${packagePath}.${tableName}Service;
import ${packagePath}.${tableName}VO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ${author}
 * @date ${dateTime}
 */
@RestController
public class ${tableName}Controller {
    @Resource
    private ${tableName}Service ${firstLowerTableName}Service;

    @GetMapping("/v1/${sqlTableName}")
    public ${tableName}VO get${tableName}(@RequestParam("id") Integer id) {
        ${tableName} ${firstLowerTableName} = ${firstLowerTableName}Service.get${tableName}(id);
        return new ${tableName}VO(${firstLowerTableName});
    }

    @PostMapping("/v1/${sqlTableName}")
    public Map<String, String> insert${tableName}(@RequestBody ${tableName}VO ${firstLowerTableName}VO) {
        ${firstLowerTableName}Service.insert${tableName}(${firstLowerTableName}VO);
        return new HashMap<>(2);
    }

    @PutMapping("/v1/${sqlTableName}")
    public Map<String, String> update${tableName}(@RequestBody ${tableName}VO ${firstLowerTableName}VO) {
        ${firstLowerTableName}Service.update${tableName}(${firstLowerTableName}VO);
        return new HashMap<>(2);
    }

    @DeleteMapping("/v1/${sqlTableName}")
    public Map<String, String> delete${tableName}(@RequestParam Integer id) {
        ${firstLowerTableName}Service.delete${tableName}(id);
        return new HashMap<>(2);
    }

    @GetMapping("/v1/${sqlTableName}/list")
    public PageVO<${tableName}VO> get${tableName}List(<#list queryColumnList as fields>@RequestParam("${fields.underlineUpperColumnName}") String ${fields.columnName}, </#list>@RequestParam("page") int page, @RequestParam("limit") int limit) {
        PageVO<${tableName}VO> pageVO = new PageVO<>();
        pageVO.setDatas(new ArrayList<>());
        int totalCount = ${firstLowerTableName}Service.get${tableName}ListCount(<#list queryColumnList as fields>${fields.columnName}<#if fields_has_next>, </#if></#list>);
        pageVO.setPage(page);
        pageVO.setTotalCount(totalCount);
        if (totalCount == 0) {
            return pageVO;
        }
        int totalPage = PageUtil.getTotalPage(totalCount, limit);
        pageVO.setTotalPage(totalPage);
        if (page > totalPage) {
            return pageVO;
        }
        int offset = PageUtil.getOffset(limit, page);
        List<${tableName}> dataList = ${firstLowerTableName}Service.get${tableName}List(<#list queryColumnList as fields>, ${fields.columnName}, </#list>offset, limit);
        pageVO.setDatas(dataList.stream().map(${tableName}VO::new).collect(Collectors.toList()));
        return pageVO;
    }
}
