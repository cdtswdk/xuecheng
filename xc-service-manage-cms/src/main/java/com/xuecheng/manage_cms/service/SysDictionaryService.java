package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.system.SysDictionary;

public interface SysDictionaryService {
    /**
     * 根据type查询数据字典信息
     *
     * @param type
     * @return
     */
    public SysDictionary getByType(String type);
}
