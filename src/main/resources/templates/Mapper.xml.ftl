<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packagePath}.${tableName}Mapper">

    <resultMap id="BaseResultMap" type="${packagePath}.${tableName}">
        <id column="id" property="id"/>
<#list columnList as fields>
<#if fields.columnName != 'id'>
        <result column="${fields.sqlColumnName}" property="${fields.columnName}" />
</#if>
</#list>
    </resultMap>

    <sql id="BaseColumn">
        <#list columnList as fields><#if fields_index gt 0>,</#if>${fields.sqlColumnName}<#if fields.sqlColumnName ? contains('_')> ${fields.columnName}</#if></#list>
    </sql>

    <insert id="insert" parameterType="${packagePath}.${tableName}" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO ${sqlTableName} (<#list columnList as fields><#if fields.columnName != 'id'><#if fields_index gt 1>,</#if>${fields.sqlColumnName}</#if></#list>)
        VALUES(<#list columnList as fields><#if fields.columnName != 'id'><#if fields_index gt 1>,</#if><#noparse>#{obj.</#noparse>${fields.columnName}}</#if></#list>)
    </insert>

    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO ${sqlTableName} (<#list columnList as fields><#if fields.columnName != 'id'><#if fields_index gt 1>,</#if>${fields.sqlColumnName}</#if></#list>)
        VALUES
        <foreach collection = 'list' item='obj' separator=',' >
            (<#list columnList as fields><#if fields.columnName != 'id'><#noparse>#{obj.</#noparse>${fields.columnName}}<#if fields_has_next>,</#if></#if></#list>)
        </foreach>
    </insert>

    <update id="update">
        UPDATE ${sqlTableName} SET
<#list columnList as fields>
<#if fields.columnName != 'id'>
        <if test="<#noparse>#{obj.</#noparse>${fields.columnName}} != null and <#noparse>#{obj.</#noparse>${fields.columnName}} != ''">
            ${fields.sqlColumnName} = <#noparse>#{obj.</#noparse>${fields.columnName}},
        </if>
</#if>
</#list>
        WHERE id = <#noparse>#{</#noparse>obj.id}
    </update>

    <delete id="delete">
        DELETE FROM ${sqlTableName} WHERE id = <#noparse>#{</#noparse>id}
    </delete>

    <select id="getById" resultMap="BaseResultMap">
        SELECT <include refid="BaseColumn"/> FROM ${sqlTableName} WHERE id = <#noparse>#{</#noparse>id}
    </select>

    <select id="getListCount" resultType="int">
        SELECT count(id) FROM ${sqlTableName}
        <where>
<#assign inList = ["in","not in"]>
<#assign eqList = ["=",">", ">=", "<", "<="]>
<#list queryColumnList as fields>
            <if test="<#noparse>#{obj.</#noparse>${fields.columnName}} != null and <#noparse>#{obj.</#noparse>${fields.columnName}} != ''">
<#if eqList?seq_contains(fields.queryType)>
                AMD ${fields.sqlColumnName} ${fields.queryType} <#noparse>#{</#noparse>${fields.columnName}}
<#elseif inList?seq_contains(fields.queryType)>
                AND ${fields.sqlColumnName} ${fields.queryType}(
                <foreach collection="fields.columnName" item="item" separator=","><#noparse>#{</#noparse>item}</foreach>
                )
<#else>
                AMD ${fields.sqlColumnName} ${fields.queryType} concat('%',<#noparse>#{</#noparse>${fields.columnName}},'%')
            </if>
</#if>
</#list>
        </where>
    </select>

    <select id="getList" resultMap="BaseResultMap">
        SELECT <include refid="BaseColumn"/> FROM ${sqlTableName}
        <where>
<#list queryColumnList as fields>
            <if test="<#noparse>#{obj.</#noparse>${fields.columnName}} != null and <#noparse>#{obj.</#noparse>${fields.columnName}} != ''">
<#if eqList?seq_contains(fields.queryType)>
                    AMD ${fields.sqlColumnName} ${fields.queryType} <#noparse>#{</#noparse>${fields.columnName}}
<#elseif inList?seq_contains(fields.queryType)>
                    AND ${fields.sqlColumnName} ${fields.queryType}(
                    <foreach collection="fields.columnName" item="item" separator=","><#noparse>#{</#noparse>item}</foreach>
                    )
<#else>
                    AMD ${fields.sqlColumnName} ${fields.queryType} concat('%',<#noparse>#{</#noparse>${fields.columnName}},'%')
</#if>
</#list>
        </where>
        LIMIT <#noparse>#{</#noparse>offset},<#noparse>#{</#noparse>limit}
    </select>
</mapper>
