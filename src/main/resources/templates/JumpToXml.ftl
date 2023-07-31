<#if sqlType == 'insert'>
        <insert id="${name}" useGeneratedKeys="true" keyProperty="id">
                insert into
                values()
<#elseif sqlType == 'update'>
        <update id="${name}">
                update set
                <where>
<#elseif sqlType == 'delete'>
        <delete id="${name}">
                delete from
                <where>
<#else>
        <select id="${name}" <#if (returnType?? && returnType != 'void')>resultType="${returnType}"</#if>>
                select
                from
                <where>
</#if>
<#if sqlType != 'insert'>
        <#list parameterModelList as parameter>
                <#if parameter.type == 'String'>
                <if test="${parameter.name} != null and ${parameter.name} != ''">
                <#elseif (parameter.type?contains('List') || parameter.type?contains('Set'))>
                <if test="${parameter.name} != null and ${parameter.name}.size() > 0">
                <foreach collection="${parameter.name}" item="item" open="in (" separator="," close=")"><#noparse>#{item}</#noparse></foreach>
                <#else>
                <if test="${parameter.name} != null">
                </#if>
                </if>
        </#list>
                </where>
</#if>

        </${sqlType}>