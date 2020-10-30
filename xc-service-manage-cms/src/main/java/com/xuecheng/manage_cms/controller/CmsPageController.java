package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private PageService pageService;

    @Override
    @RequestMapping(value = "/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable(value = "page") int page, @PathVariable(value = "size") int size, QueryPageRequest queryPageRequest) {

        return this.pageService.findList(page, size, queryPageRequest);
    }

    @Override
    @PostMapping(value = "/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return this.pageService.add(cmsPage);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsPage getById(@PathVariable("id") String id) {
        return this.pageService.findById(id);
    }

    @Override
    @PutMapping("/edit/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsPageResult updatePage(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return this.pageService.update(id, cmsPage);
    }

    @Override
    @DeleteMapping(value = "/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return this.pageService.delete(id);
    }

    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return this.pageService.postPage(pageId);
    }

    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return this.pageService.save(cmsPage);
    }

    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return this.pageService.postPageQuick(cmsPage);
    }
}
