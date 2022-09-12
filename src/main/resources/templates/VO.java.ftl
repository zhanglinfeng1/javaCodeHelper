package ${packagePath};

import ${packagePath}.${tableName};

<#list columnList as fields>
    <#if fields.columnType == 'Timestamp'>
import java.sql.Timestamp;
        <#break>
    </#if>
</#list>

/**
 * ${tableComment}
 * @Author: ${author}
 * @Date: ${dateTime}
*/
public class ${tableName}VO{
<#assign useless = ["visible", "valid", "deleted", "tenantId"]>
    <#list columnList as fields>
    <#if !useless?seq_contains(fields.columnName)>
    /** ${fields.columnComment} */
    private ${fields.columnType} ${fields.columnName};
<#else></#if>
    </#list>

    public ${tableName}VO() {
    }

    public ${tableName}VO(${tableName} obj) {
<#list columnList as fields>
<#if !useless?seq_contains(fields.columnName)>
        this.${fields.columnName} = obj.get${fields.firstUpperColumnName}();
<#else></#if></#list>
    }

<#list columnList as fields>
<#if !useless?seq_contains(fields.columnName)>
    public void set${fields.firstUpperColumnName}(${fields.columnType} ${fields.columnName}){
        this.${fields.columnName} = ${fields.columnName};
    }

    public ${fields.columnType} get${fields.firstUpperColumnName}(){
        return ${fields.columnName};
    }
<#else></#if>
</#list>
}

