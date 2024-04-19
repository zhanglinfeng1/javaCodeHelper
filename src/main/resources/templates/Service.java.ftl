package ${packagePath};

import ${packagePath}.${tableName};
import ${packagePath}.${tableName}VO;

import java.util.List;

/**
 * @author ${author}
 * @date ${dateTime}
 */
public interface ${tableName}Service {

    void insert${tableName}(${tableName}VO obj);

    void update${tableName}(${tableName}VO obj);

    void delete${tableName}(${idColumnType} id);

    ${tableName} get${tableName}(${idColumnType} id);

<#assign inList = ["in","not in"]>
    int get${tableName}ListCount(<#list queryColumnList as fields><#if inList?seq_contains(fields.queryType)>List<${fields.columnType}><#else>${fields.columnType}</#if> ${fields.columnName}<#if fields_has_next>, </#if></#list>);

    List<${tableName}> get${tableName}List(<#list queryColumnList as fields><#if inList?seq_contains(fields.queryType)>List<${fields.columnType}><#else>${fields.columnType}</#if> ${fields.columnName}, </#list>int offset, int limit);
}
