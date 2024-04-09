package ${packagePath};

import ${packagePath}.${tableName}VO;
import java.sql.Timestamp;
import java.util.Date;
import java.time.LocalDateTime;

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
        this.${fields.columnName} = obj.get${fields.firstUpperColumnName}();
    </#if>
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

