package com.xuecheng.search.service;

import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;

public interface SearchService {

    /**
     * 根据条件分页查询索引库
     *
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult list(int page, int size, CourseSearchParam
            courseSearchParam);
}
