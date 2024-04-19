package ${packagePath};

import ${packagePath}.${tableName};
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author ${author}
 * @date ${dateTime}
 */
public interface ${tableName}Mapper {

    void insert(@Param("obj") ${tableName} obj);

    void batchInsert(@Param("list") List<${tableName}> list);

    void update(@Param("obj") ${tableName} obj);

    void delete(@Param("id") ${idColumnType} id);

    ${tableName} get(@Param("id") ${idColumnType} id);
<#assign inList = ["in","not in"]>

    int getListCount(<#list queryColumnList as fields>@Param("${fields.columnName}") <#if inList?seq_contains(fields.queryType)>List<${fields.columnType}><#else>${fields.columnType}</#if> ${fields.columnName}<#if fields_has_next>, </#if></#list>);

    List<${tableName}> getList(<#list queryColumnList as fields>@Param("${fields.columnName}") <#if inList?seq_contains(fields.queryType)>List<${fields.columnType}><#else>${fields.columnType}</#if> ${fields.columnName}, </#list>@Param("offset") int offset, @Param("limit") int limit);
}

