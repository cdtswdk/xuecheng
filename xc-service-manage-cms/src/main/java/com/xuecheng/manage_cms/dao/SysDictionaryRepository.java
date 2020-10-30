package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SysDictionaryRepository extends MongoRepository<SysDictionary, String> {

    /**
     * 根据type查找数据字典
     *
     * @param type
     * @return
     */
    public SysDictionary findByDType(String type);
}
