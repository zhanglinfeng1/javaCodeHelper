# 功能简介
idea 中搜索插件 Java Code Helper。用于学习插件开发。欢迎交流想法、反馈BUG

## 代码补全
构造方法内，补全字段赋值<br />
非构造方法内，根据输入的字符，补全代码<br />

## 快捷跳转
feign与controller的快捷跳转<br />
sql与java的快捷跳转<br />

## 翻译
目前仅支持百度翻译，需申请百度翻译平台账号，每月免费翻译100万字符<br />
在idea中配置账号后，选中待翻译文本，右击选择JavaCodeHelp > Translate<br />
配置路径：File > Setting > Other Settings > JavaCodeHelp

## 一键添加api注解
可根据注释一键添加Api注解。目前仅支持swagger<br />
右击选择JavaCodeHelp > AddApiAnnotation

## 代码检查
feign中方法未被调用，方法名置灰<br />
陆续新增中...

## 代码统计
项目视图的模块后会备注上该模块的总代码行数<br />
右击项目，选择Statistical Contribution Rate，会根据git记录统计自己对整个项目的贡献率<br />
目前注释判断仅支持.java、.xml文件

## 根据建表SQL生成代码
主要用于学习插件开发，功能比较单一，建议使用其他更完善的插件去生成代码<br />
需要完整的建表语句加commont<br />
建议使用自定义模板，生成的代码更符合自己的项目。可在配置中下载默认模板，在此基础上修改<br />
模板变量如下<br />

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
        ${sqlColumnType}        sql字段类型
        ${columnType}           java字段类型
        ${columnComment}        字段备注

    ${queryColumnList}          用于查询的字段信息List
        ${sqlColumnName}        sql原始字段名
        ${columnName}           别名
        ${queryType}            查询方式
        ${sqlColumnType}        sql字段类型
        ${columnType}           java字段类型

# 版本改动
<ul>1.9.0
    <li>[NEW]一键统计项目贡献率</li>
    <li>[优化]代码统计逻辑</li>
</ul>
<ul>1.8.0
    <li>[NEW]代码统计</li>
    <li>[优化]代码补全提示，去除递归</li>
</ul>
<ul>1.7.1
    <li>[优化]跳转优化</li>
    <li>[优化]不跳转模块列表按字母排序</li>
</ul>
<ul>1.7.0
    <li>[BUG]请求百度翻译失败时，未关闭连接</li>
    <li>[BUG]添加注解时的一些报错</li>
    <li>[NEW]代码检查</li>
</ul>
<ul>1.6.0
    <li>[BUG]修复打开注解类时报错</li>
    <li>[DELETE]去除代码检查功能</li>
    <li>[NEW]根据注释一键添加api注解，仅支持添加swagger注解</li>
</ul>
<ul>1.5.4
    <li>[优化]更改跳转图标的展示位置</li>
    <li>[BUG]修复代码补全新行判断问题</li>
</ul>
<ul>1.5.3
    <li>[BUG]补全提示不展示</li>
</ul>
<ul>1.5.2
    <li>[NEW]可配置feign不跳转的模块</li>
</ul>
<ul>1.5.1
    <li>[BUG]代码补全不提示</li>
    <li>[优化]翻译相关报错以弹窗形式展示</li>
</ul>
<ul>1.5.0
    <li>[优化]代码补全中的变量命名</li>
    <li>[优化]跳转图标加载速度</li>
    <li>[NEW]sql与java的快捷跳转</li>
</ul>
<ul>1.4.4
    <li>[优化]配置界面美化</li>
</ul>
<ul>1.4.3
    <li>优化提示</li>
    <li>[BUG]Mapper模板批量插入缺关键字VALUES</li>
</ul>
<ul>1.4.2
    <li>[BUG]修复翻译卡顿</li>
    <li>[BUG]修复模板选择逻辑颠倒</li>
    <li>[NEW]可选择Date, Timestamp, LocalDateTime来处理数据库中的时间类型字段</li>
</ul>
<ul>1.4.1
    <li>BUG修复</li>
</ul>
<ul>1.4.0
    <li>代码补全逻辑升级</li>
</ul>
<ul>1.3.7
    <li>优化代码补全</li>
</ul>
<ul>1.3.6
    <li>优化代码补全</li>
</ul>
<ul>1.3.5
    <li>自定义模板路径移入配置</li>
    <li>优化代码提示</li>
</ul>
<ul>1.3.4
    <li>支持postgresql建表SQL</li>
</ul>
<ul>1.3.3
    <li>去除：跳转可配置是否判断返回类型一致</li>
    <li>优化自定义查询字段</li>
</ul>
<ul>1.3.2
    <li>新增：跳转可配置是否判断返回类型一致</li>
</ul>
<ul>1.3.1
    <li>处理BUG</li>
</ul>
<ul>1.3.0
    <li>新增：代码智能补全</li>
    <li>去除：一键生成类转换的构造函数</li>
</ul>
<ul>1.2.6
    <li>部分展示改为英文，鼠标悬停有中文</li>
    <li>增加配置，在包含指定字符的文件夹中查找跳转路径，加速跳转图标的展示</li>
</ul>
<ul>1.2.5
    <li>处理BUG</li>
    <li>去除无用依赖</li>
</ul>
<ul>1.2.4
    <li>处理BUG</li>
    <li>可配置feign快捷跳转</li>
</ul>
<ul>1.2.3
    <li>翻译功能回归，目前只支持百度翻译，需要自己注册账号，申请每月免费翻译额度</li>
</ul>
<ul>1.2.2
    <li>加速跳转图标的展示</li>
    <li>支持的最低版本 182.5107.41 (2018.2.7)</li>
</ul>
<ul>1.2.1
    <li>没有稳定的翻译API, 去除翻译功能</li>
</ul>
<ul>1.2.0
    <li>feign、服务模块间的快捷跳转</li>
</ul>
<ul>1.1.3
    <li>支持 mysql, oracle</li>
    <li>支持自定义模板</li>
</ul>
<ul>1.1.2
    <li>处理BUG</li>
</ul>
<ul>1.1.1
    <li>弃用GSON, 改用fastjson2</li>
    <li>支持自选查询字段</li>
</ul>
<ul>1.1.0
    <li>一键翻译 (需联网)</li>
</ul>
<ul>1.0.1
    <li>支持idea版本至 2022.2.2</li>
</ul>
<ul>1.0.0
    <li>根据建表SQL生成Java代码</li>
    <li>一键生成类转换的构造函数</li>
</ul>