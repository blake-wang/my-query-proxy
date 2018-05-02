数据平台查询接口说明  

## 调用说明

数据发送和返回均是json格式， 请求以HTTP POST方式，请求路径： `http://kylin-query.ijunhai.com:10080/query`

## 数据格式 
```
{
  "conditions": {
    "channelId": ["1231", "1232"],
    "gameChannelId": [],
    "gameId": ["124", "342432"],
    "osType": ["IOS"],
    "start": "2017-09-02 21:03:01",
    "end": "2017-09-03 21:03:01"
  },
  "metrics": [
    {"name": "active_uv"},
    {"name": "active_nuv"},
    {"name": "active_ouv"},
    {"name": "retention_uv", "regDate": "2017-09-01", "values": [2, 3, 7]},
    {"name": "complex_retention_uv", "values": [2, 3, 7]},
    {"name": "complex_yet_pay_nuv"}
  ],
  "granularity": "day",
  "returnDemensions": ["ChannelId", "GameId"]，
  "orderByFields":["ChannelId","active_uv"]，
  "limit":"10"
}
```

### 格式说明

 参数| 说明 
-----|------
 conditions | 用于条件筛选 
 metrics | 指明需要查询的指标 
 granularity | 指明查询数据的粒度 
 returnDemensions | 指明需要返回的维度，相当于groupby字段，注意此处不需要指明时间字段，时间会根据granularity自动返回 
 orderByFields | 指明需要排序的字段
 limit | 指明需要返回的条数
 
#### conditions 

conditions用于筛选数据，目前查询接口支持四个字段的筛选：ChannelId、GameChannelId、GameId和OSType，以及两个时间筛选分别为Start和End。  
`注意`: ChannelId、GameChannelId、GameId和OSType可以由多个值，也可以为不放入conditions中，而Start和End必须不可缺少，却不能为空，格式统一为`yyyy-MM-dd HH:mm:ss`或`yyyy-MM-dd`。  

#### metrics 

metrics共有以下几种类型:   
* active_uv  活跃用户数      
* active_nuv  活跃新用户数/新增用户数      
* active_ouv  活跃老用户数  
* pay_uv  付费用户数    
* pay_nuv  付费新用户数/新用户付费人数  
* pay_ouv  付费老用户数  
* pay_amount  付费总金额  
* nu_pay_amount  新用户付费金额  
* ou_pay_amount  老用户付费金额  
* first_pay_uv  首付用户数   
* first_pay_amount  首付金额   
* retention_uv   新用户留存人数  
* first_pay_retention_uv   首付用户留存人数   
* first_pay_retention_nuv  新用户付费留存人数
* complex_retention_uv       时间段新用户留存人数
* complex_first_pay_retention_uv     时间段首付用户留存人数 
* complex_first_pay_retention_nuv    时间段新用户付费留存人数
* complex_nu_yet_pay_amount       累计新用户付费金额
* complex_yet_pay_nuv     累计新用户付费人数 

`注意`:   
`retention_uv`和`first_pay_retention_nuv`类型的指标比较特殊，需要指定regDate和values；`first_pay_retention_uv`类型需要指定firstPayDate和values。。    
* regDate为注册时间，firstPayDate为首付日期，时间格式均为**yyyy-MM-dd**
* values指明需要查询的n日留存，如示例中**"values": [2, 3, 7]**表示2日留存（次留）、三日留存和7日留存   

`complex_retention_uv`，`complex_first_pay_retention_uv`，`complex_first_pay_retention_nuv`为复杂留存指标，只需指定values，查询时间为conditions中指定的start到end的时间段。

`complex_yet_pay_nuv`,`complex_nu_yet_pay_amount`，需指定values，查询时间为conditions中指定的start到end的时间段；若不指定values，则返回conditions中指定的start到end的时间段至今的新用户付费金额或者付费人数


#### granularity

表示查询的时间粒度，目前只支持day/hour/两种种时间粒度。  

`注意`: 
* 如果metrics中包含**retention_uv**，则会自动使用`day`的时间粒度
* 传入空值即**""** 不会对时间groupby， 相当于对数据汇总


#### orderByFields 
指明需要排序的字段。

`注意`: 
* 如果不需要排序，则传空值""
* 如果granularity不为空，orderByFields字段为空，则默认按照granularity字段排序

#### limit  
* 如果不需要limit,则传空值""，默认limit为2000


## 返回的数据格式样例

```
[
  {"date": "2017-09-01", "hour": "01", "channelId": "1231", "gameid": "124", "active_uv": 324, "active_uv_revision": 324, "active_nuv": 200, "active_nuv_revision": 324}, 
  {"date": "2017-09-01", "hour": "02", "channelId": "1231", "gameid": "124", "active_uv": 324, "active_uv_revision": 324, "active_nuv": 200, "active_nuv_revision": 324}, 
  {"date": "2017-09-01", "hour": "03", "channelId": "1231", "gameid": "124", "active_uv": 324, "active_uv_revision": 324, "active_nuv": 200, "active_nuv_revision": 324}, 
  ...
]
```
`注意`: 
* 返回结果均为小写
* 留存计算的返回值与请求个数相同，若请求查询1,2,3,7日留存，则返回的指标字段为`"1retention_uv":100, "2retention_uv":53, "3retention_uv":45, "7retention_uv":12`。  
* 每个请求的metric都会返回两个结果字段，一个是总数，即"active_uv"；一个是revision字段，表示手工录入的数据，即"active_uv_revision"。revision字段默认为0。
