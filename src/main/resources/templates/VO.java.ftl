package ${packagePath};

import ${packagePath}.${tableName};
import java.sql.Timestamp;
import java.util.Date;
import java.time.LocalDateTime;

/**
 * ${tableComment}VO
 *
 * @author ${author}
 * @date ${dateTime}
 */
public class ${tableName}VO{
    <#list columnList as fields>
    /** ${fields.columnComment} */
    private ${fields.columnType} ${fields.columnName};
    </#list>

    public ${tableName}VO() {
    }

    public ${tableName}VO(${tableName} obj) {
<#list columnList as fields>
        this.${fields.columnName} = obj.get${fields.firstUpperColumnName}();
</#list>
    }

<#list columnList as fields>
    public void set${fields.firstUpperColumnName}(${fields.columnType} ${fields.columnName}) {
        this.${fields.columnName} = ${fields.columnName};
    }

    public ${fields.columnType} get${fields.firstUpperColumnName}() {
        return ${fields.columnName};
    }

</#list>
}

