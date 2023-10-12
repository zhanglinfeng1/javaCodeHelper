package ${packagePath};

<#list columnList as fields>
    <#if fields.columnType == 'Timestamp'>
import java.sql.Timestamp;
        <#break>
    <#elseif fields.columnType == 'Date'>
import java.util.Date;
        <#break>
    <#elseif fields.columnType == 'LocalDateTime'>
import java.time.LocalDateTime;
        <#break>
    <#else>
    </#if>
</#list>

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

    public ${tableName}() {
    }

    public ${tableName}(${tableName}VO obj) {
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

