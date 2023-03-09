package ${packagePath};

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
import java.util.Optional;

/**
 * @Author: ${author}
 * @Date: ${dateTime}
 */
@Service
public class ${tableName}ServiceImpl implements ${tableName}Service {
    @Resource
    private ${tableName}Mapper ${firstLowerTableName}Mapper;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insert${tableName}(${tableName}VO obj) {
        //新增${tableComment}
        ${tableName} ${firstLowerTableName} = new ${tableName}(obj);
        ${firstLowerTableName}Mapper.insert${tableName}(${firstLowerTableName});
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update${tableName}(${tableName}VO obj) {
        //编辑${tableComment}
        ${tableName} ${firstLowerTableName} = this.get${tableName}(obj.getId());
<#assign noUpdate = ["id", "createTime"]>
<#list columnList as fields>
    <#if !noUpdate?seq_contains(fields.columnName)>
        ${firstLowerTableName}.set${fields.firstUpperColumnName}(obj.get${fields.firstUpperColumnName}());
    </#if>
</#list>
        ${firstLowerTableName}Mapper.update${tableName}(${firstLowerTableName});
    }

    @Override
    public void delete${tableName}(Integer id) {
        ${firstLowerTableName}Mapper.delete${tableName}(id);
    }

    @Override
    public ${tableName} get${tableName}(Integer id) {
        //return Optional.ofNullable(id).map(t -> ${firstLowerTableName}Mapper.get${tableName}(id)).orElseThrow(() -> new Exception("", ""));
        return Optional.ofNullable(id).map(t -> ${firstLowerTableName}Mapper.get${tableName}(id)).orElse(new ${tableName}());
    }

    @Override
    public int get${tableName}ListCount(<#list queryColumnList as fields>String ${fields.columnName}<#if fields_has_next>, </#if></#list>) {
        return ${firstLowerTableName}Mapper.get${tableName}ListCount(<#list queryColumnList as fields>${fields.columnName}<#if fields_has_next>, </#if></#list>);
    }

    @Override
    public List<${tableName}> get${tableName}List(<#list queryColumnList as fields>String ${fields.columnName}, </#list>int offset, int limit) {
        return Optional.ofNullable(${firstLowerTableName}Mapper.get${tableName}List(<#list queryColumnList as fields>${fields.columnName}, </#list>offset, limit)).orElse(new ArrayList<>());
    }
}
