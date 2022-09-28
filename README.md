#### idea 插件简介

<ul>定制化java代码开发插件,用于学习插件开发,欢迎交流想法
    <li>根据建表SQL生成Java代码，支持自定义模板，规则见gitee</li>
    <li>一键生成类转换的构造函数</li>
    <li>一键翻译（需联网）</li>
    <li>feign到模块controller的快捷跳转</li>
</ul>

#### freemark自定义模板

    ${author}                   创建者
    ${dateTime}                 创建时间
    ${packagePath}              包路径
    ${sqlTableName}             sql表名
    ${tableName}                java规范表名
    ${firstLowerTableName}      首字母小写表名
    ${tableComment}             表备注

    ${columnList}               字段信息List
        ${sqlColumnName}        sql原始字段名
        ${columnName}           java规范字段名
        ${firstUpperColumnName} 首字母大写的字段名
        ${columnType}           字段类型
        ${columnComment}        字段备注

    ${queryColumnList}          用于查询的字段信息List
        ${sqlColumnName}        sql原始字段名
        ${columnName}           java规范字段名
        ${firstUpperColumnName} 首字母大写的字段名
        ${columnType}           字段类型
        ${queryType}            查询方式
