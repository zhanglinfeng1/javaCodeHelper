package ${packagePath};

import com.fenzhitech.framework.base.exception.CodeException;
import ${packagePath}.${tableName}Mapper;
import ${packagePath}.${tableName};
import ${packagePath}.${tableName}Service;
import ${packagePath}.${tableName}VO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

/**
 * @Author: ${author}
 * @Date: ${dateTime}
*/
@Service
public class ${tableName}ServiceImpl implements ${tableName}Service{
    @Resource
    private ${tableName}Mapper ${firstLowerTableName}Mapper;

    @Override
    @Transactional(rollbackFor=Exception.class, propagation= Propagation.REQUIRED)
    public void insert${tableName}(String tenantId, ${tableName}VO obj) {
        //新增${tableComment}
        ${tableName} ${firstLowerTableName} = new ${tableName}(obj);
        ${firstLowerTableName}Mapper.insert${tableName}(tenantId,${firstLowerTableName});
    }

    @Override
    @Transactional(rollbackFor=Exception.class, propagation= Propagation.REQUIRED)
    public void update${tableName}(String tenantId, ${tableName}VO obj) {
        //编辑${tableComment}
        ${tableName} ${firstLowerTableName} = new ${tableName}(obj);
        ${firstLowerTableName}Mapper.update${tableName}(tenantId,${firstLowerTableName});
    }

<#list columnList as fields>
    <#if fields.columnName == 'status'>
    @Override
    @Transactional(rollbackFor=Exception.class, propagation= Propagation.REQUIRED)
    public void update${tableName}Status(String tenantId, Integer id, String status){
        ${firstLowerTableName}Mapper.update${tableName}Status(tenantId,id,status);
    }

        <#break>
    </#if>
</#list>
    @Override
    public ${tableName} get${tableName}(String tenantId, Integer id) {
        if(null == id){
            throw new CodeException("ID_NOT_NULL","${tableComment}id不能为空");
        }
        ${tableName} ${firstLowerTableName} = ${firstLowerTableName}Mapper.get${tableName}(tenantId, id);
        if(null == ${firstLowerTableName}){
            throw new CodeException("NOT_EXIST","${tableComment}不存在");
        }
        return ${firstLowerTableName};
    }

    @Override
    public int get${tableName}ListCount(String tenantId) {
        return ${firstLowerTableName}Mapper.get${tableName}ListCount(tenantId);
    }

    @Override
    public List<${tableName}> get${tableName}List(String tenantId, int offset, int limit) {
        List<${tableName}> list = ${firstLowerTableName}Mapper.get${tableName}List(tenantId, offset, limit);
        return null == list ? new ArrayList<>() : list;
    }
}
