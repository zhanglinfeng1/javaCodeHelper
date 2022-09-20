package ${packagePath};

import com.fenzhitech.framework.base.util.PageUtil;
import com.fenzhitech.wbuild.common.validate.ReplacePlaceHolder;
import com.fenzhitech.wbuild.common.vo.PageVO;
import ${packagePath}.${tableName};
import ${packagePath}.${tableName}Service;
import ${packagePath}.${tableName}VO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
* @Author: ${author}
* @Date: ${dateTime}
*/
@RestController
public class ${tableName}Controller {
    @Resource
    private ${tableName}Service ${firstLowerTableName}Service;

    @ReplacePlaceHolder
    @RequestMapping(path = {"/v1/${projectName}${sqlTableName}"}, method = {RequestMethod.GET}, produces = {"application/json"})
    public ${tableName}VO get${tableName}(@RequestHeader("tenant_id") String tenantId, @RequestParam("id") Integer id) {
        ${tableName} ${firstLowerTableName} = ${firstLowerTableName}Service.get${tableName}(tenantId, id);
        return new ${tableName}VO(${firstLowerTableName});
    }

    @RequestMapping(path = {"/v1/${projectName}${sqlTableName}"}, method = {RequestMethod.POST}, produces = {"application/json"})
    public Map<String,String> insert${tableName}(@RequestHeader("tenant_id") String tenantId, @RequestBody ${tableName}VO ${firstLowerTableName}VO) {
        ${firstLowerTableName}Service.insert${tableName}(tenantId, ${firstLowerTableName}VO);
        return new HashMap<>(2);
    }

    @RequestMapping(path = {"/v1/${projectName}${sqlTableName}"}, method = {RequestMethod.PUT}, produces = {"application/json"})
    public Map<String,String> update${tableName}(@RequestHeader("tenant_id") String tenantId, @RequestBody ${tableName}VO ${firstLowerTableName}VO) {
        ${firstLowerTableName}Service.update${tableName}(tenantId, ${firstLowerTableName}VO);
        return new HashMap<>(2);
    }

<#list columnList as fields>
    <#if fields.columnName == 'status'>
    @ReplacePlaceHolder
    @RequestMapping(path = {"/v1/${projectName}${sqlTableName}/status"}, method = {RequestMethod.PUT}, produces = {"application/json"})
    public Map<String,String> update${tableName}Status(@RequestHeader("tenant_id") String tenantId, @RequestParam("id") Integer id, @RequestParam("status") String status) {
        ${firstLowerTableName}Service.update${tableName}Status(tenantId, id, status);
        return new HashMap<>(2);
    }

    @ReplacePlaceHolder
    @RequestMapping(path = {"/v1/${projectName}${sqlTableName}/status_count"}, method = {RequestMethod.GET}, produces = {"application/json"})
    public StatusCountVO get${tableName}StatusCount(@RequestHeader("tenant_id") String tenantId<#list queryColumnList as fields>, @RequestParam("${fields.sqlColumnName}") String ${fields.columnName}</#list>) {
        int totalCount = ${firstLowerTableName}Service.get${tableName}ListCount(tenantId<#list queryColumnList as fields>, ${fields.columnName}</#list>);
        StatusCountVO statusCountVO = new StatusCountVO();
        return statusCountVO;
    }

        <#break>
    </#if>
</#list>
    @ReplacePlaceHolder
    @RequestMapping(path = {"/v1/${projectName}${sqlTableName}/list"}, method = {RequestMethod.GET}, produces = {"application/json"})
    public PageVO<${tableName}VO> get${tableName}List(@RequestHeader("tenant_id") String tenantId<#list queryColumnList as fields>, @RequestParam("${fields.sqlColumnName}") String ${fields.columnName}</#list>, @RequestParam("page") int page, @RequestParam("limit") int limit) {
        PageVO<${tableName}VO> pageVO = new PageVO<>();
        pageVO.setDatas(new ArrayList<>());
        int totalCount = ${firstLowerTableName}Service.get${tableName}ListCount(tenantId<#list queryColumnList as fields>, ${fields.columnName}</#list>);
        pageVO.setPage(page);
        pageVO.setTotalCount(totalCount);
        if(totalCount == 0){
        return pageVO;
        }
        int totalPage = PageUtil.getTotalPage(totalCount, limit);
        pageVO.setTotalPage(totalPage);
        if(page>totalPage){
        return pageVO;
        }
        int offset = PageUtil.getOffset(limit,page);
        List<${tableName}> dataList = ${firstLowerTableName}Service.get${tableName}List(tenantId<#list queryColumnList as fields>, ${fields.columnName}</#list>, offset, limit);
        pageVO.setDatas(dataList.stream().map(${tableName}VO::new).collect(Collectors.toList()));
        return pageVO;
    }
}
