package ${packagePath};

import com.fenzhitech.framework.base.util.StringUtil;
import ${packagePath}.${tableName};
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.StatementType;

import java.util.List;
import java.util.Map;

/**
 * @Author: ${author}
 * @Date: ${dateTime}
*/
public interface ${tableName}Mapper{
    class ${tableName}MapperProvider {
        public String getColumn() {
            return "<#list columnList as fields><#if fields_index gt 0>,</#if>${fields.sqlColumnName}<#if fields.sqlColumnName ? contains('_')> ${fields.columnName}</#if></#list>" +
            " from ${sqlTableName}";
        }

        public String get${tableName}SQL(Map<?, ?> params) {
            SQL sql = new SQL();
            sql.SELECT(this.getColumn());
            <#noparse>sql.WHERE("tenant_id = #{tenantId} and id = #{id}");</#noparse>
            return sql.toString();
        }

        public String get${tableName}ListCountSQL(Map<?, ?> params) {
            SQL sql = new SQL();
            sql.SELECT("count(id)");
            sql.FROM("${sqlTableName}");
            <#noparse>sql.WHERE("tenant_id = #{tenantId}");</#noparse>
            return sql.toString();
        }

        public String get${tableName}ListSQL(Map<?, ?> params) {
            SQL sql = new SQL();
            sql.SELECT(this.getColumn());
            <#noparse>sql.WHERE("tenant_id = #{tenantId}");
            return sql + " LIMIT #{offset},#{limit}";</#noparse>
        }
    }
<#assign noInsert = ["id", "visible", "valid", "deleted"]>
<#assign timeColumn = ["createTime", "updateTime"]>
    @SelectKey(keyColumn = "id", before = false, keyProperty = "obj.id", resultType = String.class, statementType = StatementType.STATEMENT, statement = "SELECT LAST_INSERT_ID() AS id")
    @Insert("INSERT INTO ${sqlTableName} (<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if>${fields.sqlColumnName}</#if></#list>)" +
        "VALUES(<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if><#if fields.columnName == 'tenantId'><#noparse>#{tenantId}</#noparse><#elseif timeColumn?seq_contains(fields.columnName)>NOW()<#else><#noparse>#{obj.</#noparse>${fields.columnName}}</#if></#if></#list>)")
    void insert${tableName}(@Param("tenantId") String tenantId, @Param("obj") ${tableName} obj);

    @Insert("<#noparse><script></#noparse>INSERT INTO ${sqlTableName}(<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if>${fields.sqlColumnName}</#if></#list>)" +
        "<foreach collection = 'list' item='obj' separator=',' > " +
        " (<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if><#if fields.columnName == 'tenantId'><#noparse>#{tenantId}</#noparse><#elseif timeColumn?seq_contains(fields.columnName)>NOW()<#else><#noparse>#{obj.</#noparse>${fields.columnName}}</#if></#if></#list>) " +
        "</foreach> <#noparse></script></#noparse>")
    void batchInsert${tableName}(@Param("tenantId") String tenantId, @Param("list") List<${tableName}> list);

<#assign noUpdate = ["id", "visible", "valid", "deleted", "createTime", "updateTime", "tenantId", "status"]>
    @Update("UPDATE ${sqlTableName} SET <#list columnList as fields><#if !noUpdate?seq_contains(fields.columnName)>${fields.sqlColumnName}=<#noparse>#{obj.</#noparse>${fields.columnName}<#noparse>},</#noparse></#if></#list>" +
            <#noparse>",update_time=NOW() WHERE tenant_id=#{tenantId} AND id=#{obj.id}")</#noparse>
    void update${tableName}(@Param("tenantId") String tenantId, @Param("obj") ${tableName} obj);

<#list columnList as fields>
    <#if fields.columnName == 'status'>
    @Update("UPDATE ${sqlTableName} SET <#noparse>status=#{status},update_time=NOW() WHERE tenant_id=#{tenantId} AND id=#{id}")</#noparse>
    void update${tableName}Status(@Param("tenantId") String tenantId, @Param("id") Integer id, @Param("status") String status);

        <#break>
    </#if>
</#list>
    @SelectProvider(type = ${tableName}MapperProvider.class, method = "get${tableName}SQL")
    ${tableName} get${tableName}(@Param("tenantId") String tenantId, @Param("id") Integer id);

    @SelectProvider(type = ${tableName}MapperProvider.class, method = "get${tableName}ListCountSQL")
    int get${tableName}ListCount(@Param("tenantId") String tenantId);

    @SelectProvider(type = ${tableName}MapperProvider.class, method = "get${tableName}ListSQL")
    List<${tableName}> get${tableName}List(@Param("tenantId") String tenantId,@Param("offset") int offset, @Param("limit") int limit);
}

