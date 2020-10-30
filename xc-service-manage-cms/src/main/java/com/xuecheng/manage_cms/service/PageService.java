package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface PageService {

    /**
     * 页面列表分页查询
     *
     * @param page             当前页码
     * @param size             页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     * 增加页面
     *
     * @param cmsPage
     * @return
     */
    CmsPageResult add(CmsPage cmsPage);

    /**
     * 根据id查询页面
     *
     * @param id
     * @return
     */
    CmsPage findById(String id);

    /**
     * 修改页面
     *
     * @param id
     * @param cmsPage
     * @return
     */
    CmsPageResult update(String id, CmsPage cmsPage);

    /**
     * 根据id删除页面
     *
     * @param id
     * @return
     */
    ResponseResult delete(String id);

    /**
     * 页面静态化
     *
     * @param pageId
     * @return
     */
    String getPageHtml(String pageId);

    /**
     * 页面发布
     *
     * @param pageId
     * @return
     */
    ResponseResult postPage(String pageId);

    /**
     * 保存页面
     *
     * @param cmsPage
     * @return
     */
    CmsPageResult save(CmsPage cmsPage);

    /**
     * 一键发布页面
     *
     * @param cmsPage
     * @return
     */
    CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
