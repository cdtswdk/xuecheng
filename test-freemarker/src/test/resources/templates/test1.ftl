<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
学生个数：${stus?size}
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
        <td>日期</td>
    </tr>
    <#if stus??>
        <#list stus as stu>
            <tr>
                <td>${stu_index+1}</td>
                <td <#if stu.name == '小明'>style="background: aqua"</#if>>${stu.name}</td>
                <td>${stu.age}</td>
                <td <#if stu.money gte 200>style="background: crimson" </#if>>${stu.money}</td>
                <td>${stu.birthday?string('yyyy年MM月dd日hh:mm:ss')}</td>
            </tr>
        </#list>
    </#if>
</table>
<#--遍历map数据-->
姓名：${(stuMap['stu1'].name)!''}<br/>
年龄：${(stuMap['stu1'].age)!''}<br/>
姓名：${(stuMap.stu1.name)!''}<br/>
年龄：${(stuMap.stu1.age)!''}<br/>
遍历输出两个学生信息：<br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stuMap?keys as k>
        <tr>
            <td>${k_index + 1}</td>
            <td>${stuMap[k].name}</td>
            <td>${stuMap[k].age}</td>
            <td>${stuMap[k].money}</td>
        </tr>
    </#list>
</table>
<br/>
${point?c}
<br/>
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank} 账号：${data.account}
</body>
</html>