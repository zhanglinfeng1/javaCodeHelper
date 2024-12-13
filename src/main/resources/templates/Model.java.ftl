package ${packagePath};

import ${packagePath}.${tableName}VO;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${dateTime}
 */
public class ${tableName}{
    <#list columnList as fields>
    /** ${fields.columnComment} */
    private ${fields.columnType} ${fields.columnName};
    </#list>

    public void syncField(${tableName}VO obj) {
<#assign noSyncField = ["id", "createTime"]>
<#list columnList as fields>
    <#if !noSyncField?seq_contains(fields.columnName)>
        this.${fields.columnName} = obj.${fields.columnGetMethod}();
    </#if>
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

