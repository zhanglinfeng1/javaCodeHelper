public String ${name}(Map<String,?> params){
<#list parameterModelList as parameter>
        ${parameter.type} ${parameter.name} = (${parameter.type}) params.get("${parameter.name}");
</#list>
        SQL sql  = new SQL();
<#if sqlType == 'insert'>
        sql.INSERT_INTO("");
        sql.VALUES("", "");
<#elseif sqlType == 'update'>
        sql.UPDATE("");
        sql.SET("");
<#elseif sqlType == 'delete'>
        sql.DELETE_FROM("");
<#else>
        sql.SELECT("");
        sql.FROM("");
</#if>
<#list parameterModelList as parameter>
        <#if parameter.type == 'String'>
                if (!StringUtils.isEmpty(${parameter.name})) {
        <#elseif (parameter.type?contains('List') || parameter.type?contains('Set'))>
                if (null != ${parameter.name} && !${parameter.name}.isEmpty()) {
        <#else>
                if (null != ${parameter.name}) {
        </#if>
        <#if sqlType != 'insert'>
                sql.WHERE(" ");
        </#if>
       }
</#list>
        return sql.toString();
}
