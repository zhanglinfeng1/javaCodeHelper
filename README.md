<div align="center">

# Java Code Helper
intellij idea 中搜索插件 Java Code Helper。 欢迎交流想法、反馈BUG<br />
![](https://img.shields.io/badge/IDEA-2022.3.1+-pink.svg?labelColor=blue)<br />
<a href='https://gitee.com/zlfzh/javaCodeHelper'><img src='https://gitee.com/zlfzh/javaCodeHelper/badge/star.svg?theme=dark' alt='star'></img></a>
[![](https://img.shields.io/github/stars/zhanglinfeng1/javaCodeHelper?style=flat&logo=GitHub&color=pink&labelColor=blue)](https://github.com/zhanglinfeng1/javaCodeHelper)
[![](https://img.shields.io/jetbrains/plugin/d/19944?color=pink&labelColor=blue&logo=jetbrains)](https://plugins.jetbrains.com/plugin/19944-java-code-helper)<br />
</div>

## 新代码提醒
仅对使用git的项目有效<br />
当前分支存在新代码时将在右下角和通知中提醒<br />
默认轮询时间10分钟，可配置：File > Setting > Other Settings > javaCodeHelper<br />
![image](src/main/resources/example/newCodeRemind1.png)
![image](src/main/resources/example/newCodeRemind2.png)

## 禅道新任务、新BUG提醒
有新任务、新BUG时将在右下角和通知中提醒<br />
默认轮询时间10分钟，可配置：File > Setting > Other Settings > javaCodeHelper<br />

## 彩虹括号
<>、()、[]、{} 同对颜色一致<br />
可以自定义颜色，配置路径：File > Setting > Editor > Color Scheme > 彩虹括号(javaCodeHelper)
![image](src/main/resources/example/rainbowBracket.png)

## 代码补全
构造方法内，补全字段赋值<br />
非构造方法内，根据输入的字符，补全代码<br />
根据注解补全方法参数<br />

## 快捷跳转
feign与controller的快捷跳转<br />
sql与java的快捷跳转<br />
点击![image](src/main/resources/icon/logo.svg)即可跳转<br />
点击![image](src/main/resources/icon/logoGrey.svg)即可生成简易的sql代码<br />
xml中的resultMap、refid标签跳转（ctrl+鼠标左击）

## 文字识别
目前仅支持百度ocr<br />
在idea中配置账号后，在“常用工具”可使用<br />
配置路径：File > Setting > Other Settings > javaCodeHelper

## 翻译
目前仅支持中译英，英译中<br />
目前仅支持百度翻译<br />
在idea中配置账号后，选中待翻译文本，右击选择“翻译”<br />
配置路径：File > Setting > Other Settings > javaCodeHelper

## 一键添加api注解
可根据注释一键添加Api注解。目前仅支持swagger2、swagger3<br />
选中需要添加注解的代码块，右击选择“添加接口文档注解”<br />
如果不选中代码块，会给整个类添加注解，代码过长时会有卡顿(っ °Д °;)っ

## 常用工具
1.选中文本后，右击选择 "工具" ，包含大写、小写、转驼峰、转小写下划线<br />
2.打开 View > Tool Windows > javaCodeHelper，包含以下功能 <br />

    unicode：unicode与字符串的互相转换
    urlEncode：url的encode和decode
    escape：escape转义
    ascii码：ascii码与字符串的互相转换
    二维码：二维码的生成与解析
    cron表达式：解析cron表达式，展示最近的5次执行时间
    时间戳转换：时间戳与字符串的互相转换
    加解密：一些常用的加解密方式
    进制转换：数字值转换
    文字识别：识别图片或者pdf文件中的文字
    图片转Base64：图片与Base64相互转换
    图片转pdf：图片转成pdf文件
    本机IP：获取本机内网ip、外网ip
    去除html格式：去除标签，展示纯文本

## 导出表结构
Database中右击选中 <br />
选中的是单张表，则导出单张表的结构 <br />
选中的是单个库，则导出库中所有表的结构 <br />
![image](src/main/resources/example/exportTableInfo.png)<br />

## 代码检查
具体见 File > Setting > Editor > Inspections > javaCodeHelper<br />
![image](src/main/resources/example/codeCheck.png)<br />

## 代码统计
先配置统计的文件类型（例：.java）。配置路径：File > Setting > Other Settings > javaCodeHelper > 代码统计<br />
右击项目视图任意区域，选择'统计行数'，统计选中项目的总代码行数<br />
右击项目视图任意区域，选择'统计贡献率'，统计选中模块的代码贡献率（未提交代码不参与），时间较长，耐心等待 (っ °Д °;)っ<br />
右击项目视图任意区域，选择'统计详细贡献'，统计选中模块指定日期后所有已提交代码的贡献情况，时间较长，耐心等待 (っ °Д °;)っ<br />
目前注释判断仅支持.java、.xml文件<br />
![image](src/main/resources/example/codeStatistics.png)<br />

## git提交时,展示禅道任务、bug信息
进行git提交时，点击![image](src/main/resources/icon/logo.svg)，选择BUG或者任务<br />
![image](src/main/resources/example/gitMessage.png)

## 自定义模版生成代码
主要用于学习插件开发，功能比较单一，建议使用其他更完善的插件去生成代码<br />
Database中右击选中<br />![image](src/main/resources/example/generateCode.png)<br />
模板变量如下<br />

    ${author}                       创建者
    ${dateTime}                     创建时间
    ${packagePath}                  包路径
    ${sqlTableName}                 sql表名
    ${tableName}                    java规范表名
    ${firstLowerTableName}          首字母小写表名
    ${tableComment}                 表备注

    ${columnList}                   字段信息List
        ${sqlColumnName}            sql原始字段名
        ${columnName}               java规范字段名
        ${columnSetMethod}          set方法名
        ${columnGetMethod}          get方法名
        ${firstUpperColumnName}     首字母大写的字段名
        ${underlineUpperColumnName} 下划线格式、全小写的字段名
        ${sqlColumnType}            sql字段类型
        ${columnType}               java字段类型
        ${columnComment}            字段备注

    ${queryColumnList}              用于查询的字段信息List
        ${sqlColumnName}            sql原始字段名
        ${columnName}               别名
        ${queryType}                查询方式
        ${sqlColumnType}            sql字段类型
        ${columnType}               java字段类型
