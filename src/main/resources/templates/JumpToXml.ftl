<#if sqlType == 'insert'>
        <insert id="${name}" useGeneratedKeys="true" keyProperty="id">
                INSERT INTO xx ()
                VALUES ()
<#elseif sqlType == 'update'>
        <update id="${name}">
                UPDATE xx SET
                <where>
<#elseif sqlType == 'delete'>
        <delete id="${name}">
                DELETE FROM xx
                <where>
<#else>
        <select id="${name}" <#if (returnType?? && returnType != 'void')>resultType="${returnType}"</#if>>
                SELECT xx
                FROM xx
                <where>
</#if>
<#if sqlType != 'insert'>
        <#list parameterModelList as parameter>
                <#if parameter.type == 'String'>
                <if test="${parameter.name} != null and ${parameter.name} != ''">
                        AND xx = <#noparse>#{</#noparse>${parameter.name}}
                <#elseif (parameter.type?contains('List') || parameter.type?contains('Set'))>
                <if test="${parameter.name} != null and ${parameter.name}.size() > 0">
                        AND xx IN
                <foreach collection="${parameter.name}" item="item" open="(" separator="," close=")">
                        <#noparse>#{item}</#noparse>
                </foreach>
                <#else>
                <if test="${parameter.name} != null">
                        AND xx = <#noparse>#{</#noparse>${parameter.name}}
                </#if>
                </if>
        </#list>
                </where>
</#if>
        </${sqlType}>