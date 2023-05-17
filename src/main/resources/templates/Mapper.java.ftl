package ${packagePath};

import ${packagePath}.${tableName};
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: ${author}
 * @Date: ${dateTime}
 */
public interface ${tableName}Mapper {
    class ${tableName}MapperProvider {
        public String getColumn() {
            return "<#list columnList as fields><#if fields_index gt 0>,</#if>${fields.sqlColumnName}<#if fields.sqlColumnName ? contains('_')> ${fields.columnName}</#if></#list>" +
                    " from ${sqlTableName}";
        }

        public void addWhere(SQL sql, Map<?, ?> params) {
<#assign inList = ["in","not in"]>
<#assign eqList = ["=",">", ">=", "<", "<="]>
<#list queryColumnList as fields>
<#if eqList?seq_contains(fields.queryType)>
            Optional.ofNullable(params.get("${fields.columnName}")).map(String::valueOf).ifPresent(t -> sql.WHERE(" ${fields.sqlColumnName} ${fields.queryType} <#noparse>#{</#noparse>${fields.columnName}}"));
<#elseif inList?seq_contains(fields.queryType)>
            <#noparse>List<String></#noparse> asList = Arrays.asList(${fields.columnName}.split(","));
            String str = StringUtils.collectionToDelimitedString(asList, ",", "'", "'");
            Optional.ofNullable(params.get("${fields.columnName}")).map(String::valueOf).ifPresent(t -> sql.WHERE(" ${fields.sqlColumnName} ${fields.queryType} (" + str + ") "));
    <#else>
            Optional.ofNullable(params.get("${fields.columnName}")).map(String::valueOf).ifPresent(t -> sql.WHERE(" ${fields.sqlColumnName} ${fields.queryType} concat('%',<#noparse>#{</#noparse>${fields.columnName}},'%')"));
    </#if>
</#list>
        }

        public String get${tableName}SQL(Map<?, ?> params) {
            SQL sql = new SQL();
            sql.SELECT(this.getColumn());
            <#noparse>sql.WHERE("id = #{id}");</#noparse>
            return sql.toString();
        }

        public String get${tableName}ListCountSQL(Map<?, ?> params) {
            SQL sql = new SQL();
            sql.SELECT("count(id)");
            sql.FROM("${sqlTableName}");
            addWhere(sql, params);
            return sql.toString();
        }

        public String get${tableName}ListSQL(Map<?, ?> params) {
            SQL sql = new SQL();
            sql.SELECT(this.getColumn());
            addWhere(sql, params);
            <#noparse>return sql + " LIMIT #{offset},#{limit}";</#noparse>
        }
    }

<#assign noInsert = ["id"]>
    @SelectKey(keyColumn = "id", before = false, keyProperty = "obj.id", resultType = Integer.class, statementType = StatementType.STATEMENT, statement = "SELECT LAST_INSERT_ID() AS id")
    @Insert("INSERT INTO ${sqlTableName} (<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if>${fields.sqlColumnName}</#if></#list>)" +
            "VALUES(<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if><#noparse>#{obj.</#noparse>${fields.columnName}}</#if></#list>)")
    void insert${tableName}(@Param("obj") ${tableName} obj);

    @Insert("<#noparse><script></#noparse>INSERT INTO ${sqlTableName}(<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#if fields_index gt 1>,</#if>${fields.sqlColumnName}</#if></#list>)VALUES" +
            "<foreach collection = 'list' item='obj' separator=',' > " +
            " (<#list columnList as fields><#if !noInsert?seq_contains(fields.columnName)><#noparse>#{obj.</#noparse>${fields.columnName}}<#if fields_has_next>,</#if></#if></#list>) " +
            "</foreach> <#noparse></script></#noparse>")
    void batchInsert${tableName}(@Param("list") List<${tableName}> list);

<#assign noUpdate = ["id"]>
    @Update("UPDATE ${sqlTableName} SET <#list columnList as fields><#if !noUpdate?seq_contains(fields.columnName)>${fields.sqlColumnName}=<#noparse>#{obj.</#noparse>${fields.columnName}}<#if fields_has_next>,</#if></#if></#list>" +
            <#noparse>" WHERE id=#{obj.id}")</#noparse>
    void update${tableName}(@Param("obj") ${tableName} obj);

    @Delete("delete from ${sqlTableName} WHERE id=<#noparse>#{id}</#noparse>")
    void delete${tableName}(@Param("id") Integer id);

    @SelectProvider(type = ${tableName}MapperProvider.class, method = "get${tableName}SQL")
    ${tableName} get${tableName}(@Param("id") Integer id);

    @SelectProvider(type = ${tableName}MapperProvider.class, method = "get${tableName}ListCountSQL")
    int get${tableName}ListCount(<#list queryColumnList as fields>@Param("${fields.columnName}") String ${fields.columnName}<#if fields_has_next>, </#if></#list>);

    @SelectProvider(type = ${tableName}MapperProvider.class, method = "get${tableName}ListSQL")
    List<${tableName}> get${tableName}List(<#list queryColumnList as fields>@Param("${fields.columnName}") String ${fields.columnName}, </#list>@Param("offset") int offset, @Param("limit") int limit);
}

