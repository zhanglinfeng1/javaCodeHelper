#### idea 插件简介

<ul>定制化java代码开发插件,用于学习插件开发,欢迎交流想法
    <li>根据建表SQL一键生成mapper、model、VO、service、controller代码</li>
    <ul>
        <li>支持基础增删查改、列表查询、批量新增</li>
        <li>支持mysql、oracle，需带上COMMENT</li>
        <li>支持生成ibatis版代码</li>
        <li>支持自定义模板</li>
    </ul>
    <li>一键生成类转换的构造函数</li>
    <li>一键翻译（需联网）</li>
</ul>

#### freemark自定义模板

<p>创建者   ${author}</p>
<p>创建时间 ${dateTime}</p>
<p>项目名 ${projectName}</p>
<p>包路径 ${packagePath}</p>
<p>sql表名 ${sqlTableName}</p>
<p>java规范表名 ${tableName}</p>
<p>首字母小写表名 ${firstLowerTableName}</p>
<p>表备注 ${tableComment}</p>
<p>字段信息 ${columnList}</p>
    <li>sql原始字段名 ${sqlColumnName}</li>
    <li>java规范字段名 ${columnName}</li>
    <li>首字母大写的字段名 ${firstUpperColumnName}</li>
    <li>字段类型 ${columnType}</li>
    <li>字段备注 ${columnComment}</li>
<p>用于查询的字段信息 ${queryColumnList}</p>
    <li>sql原始字段名 ${sqlColumnName}</li>
    <li>java规范字段名 ${columnName}</li>
    <li>首字母大写的字段名 ${firstUpperColumnName}</li>
    <li>字段类型 ${columnType}</li>
    <li>查询方式 ${queryType}</li>
