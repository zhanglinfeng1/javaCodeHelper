package ${packagePath};

import ${packagePath}.${tableName};
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

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
        this.${fields.columnName} = obj.${fields.columnGetMethod}();
</#list>
    }

<#list columnList as fields>
    public void ${fields.columnSetMethod}(${fields.columnType} ${fields.columnName}) {
        this.${fields.columnName} = ${fields.columnName};
    }

    public ${fields.columnType} ${fields.columnGetMethod}() {
        return ${fields.columnName};
    }

</#list>
}

