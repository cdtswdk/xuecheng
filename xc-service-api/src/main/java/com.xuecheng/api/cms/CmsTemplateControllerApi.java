package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms页面模板查询接口", description = "cms页面模板查询接口")
public interface CmsTemplateControllerApi {
    //模板查询
    @ApiOperation("查询页面模板列表")
    public QueryResponseResult findAllTemplate();
}
