package ${packagePath};

import ${packagePath}.${tableName};
import ${packagePath}.${tableName}VO;

import java.util.List;

/**
 * @Author: ${author}
 * @Date: ${dateTime}
 */
public interface ${tableName}Service {

    void insert${tableName}(${tableName}VO obj);

    void update${tableName}(${tableName}VO obj);

    void delete${tableName}(Integer id);

    ${tableName} get${tableName}(Integer id);

    int get${tableName}ListCount(<#list queryColumnList as fields>String ${fields.columnName}<#if fields_has_next>, </#if></#list>);

    List<${tableName}> get${tableName}List(<#list queryColumnList as fields>String ${fields.columnName}, </#list>int offset, int limit);
}
