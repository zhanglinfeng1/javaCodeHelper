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
 * @author ${author}
 * @date ${dateTime}
 */
@Service
public class ${tableName}ServiceImpl implements ${tableName}Service {
    @Resource
    private ${tableName}Mapper ${firstLowerTableName}Mapper;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insert${tableName}(${tableName}VO obj) {
        //新增${tableComment}
        ${tableName} ${firstLowerTableName} = new ${tableName}();
        ${firstLowerTableName}.syncField(obj);
        ${firstLowerTableName}Mapper.insert(${firstLowerTableName});
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update${tableName}(${tableName}VO obj) {
        //编辑${tableComment}
        ${tableName} ${firstLowerTableName} = this.get${tableName}(obj.getId());
        ${firstLowerTableName}.syncField(obj);
        ${firstLowerTableName}Mapper.update(${firstLowerTableName});
    }

    @Override
    public void delete${tableName}(${idColumnType} id) {
        ${firstLowerTableName}Mapper.delete(id);
    }

    @Override
    public ${tableName} get${tableName}(${idColumnType} id) {
        //return Optional.ofNullable(id).map(t -> ${firstLowerTableName}Mapper.getById(id)).orElseThrow(() -> new Exception("", ""));
        return Optional.ofNullable(id).map(t -> ${firstLowerTableName}Mapper.getById(id)).orElse(new ${tableName}());
    }

<#assign inList = ["in","not in"]>
    @Override
    public int get${tableName}ListCount(<#list queryColumnList as fields><#if inList?seq_contains(fields.queryType)>List<${fields.columnType}><#else>${fields.columnType}</#if> ${fields.columnName}<#if fields_has_next>, </#if></#list>) {
        return ${firstLowerTableName}Mapper.getListCount(<#list queryColumnList as fields>${fields.columnName}<#if fields_has_next>, </#if></#list>);
    }

    @Override
    public List<${tableName}> get${tableName}List(<#list queryColumnList as fields><#if inList?seq_contains(fields.queryType)>List<${fields.columnType}><#else>${fields.columnType}</#if> ${fields.columnName}, </#list>int offset, int limit) {
        return Optional.ofNullable(${firstLowerTableName}Mapper.getList(<#list queryColumnList as fields>${fields.columnName}, </#list>offset, limit)).orElse(new ArrayList<>());
    }
}
