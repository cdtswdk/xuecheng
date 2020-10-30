package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.service.impl.PageServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private PageServiceImpl pageService;

    @Test
    public void testFindAll() {
        List<CmsPage> list = this.cmsPageRepository.findAll();
        for (CmsPage cmsPage : list) {
            System.out.println(cmsPage);
        }
    }

    @Test
    public void testFindPage() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<CmsPage> pages = this.cmsPageRepository.findAll(pageable);
        List<CmsPage> list = pages.getContent();
        for (CmsPage cmsPage : list) {
            System.out.println(cmsPage);
        }
    }

    @Test
    public void testInsert() {
        //定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    @Test
    public void testUpdate() {
        Optional<CmsPage> optional = this.cmsPageRepository.findById("5f6194c106488134ec72d404");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setSiteId("s02");
            CmsPage save = this.cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }
    }

    @Test
    public void testGenerateHtml() {
        this.pageService.getPageHtml("5f69a9b51f71cb2b18e355a9");
    }
}
