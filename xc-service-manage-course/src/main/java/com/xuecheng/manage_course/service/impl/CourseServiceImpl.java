package com.xuecheng.manage_course.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachPlanMapper teachPlanMapper;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private TeachPlanRepository teachPlanRepository;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;


    @Value("${course‐publish.siteId}")
    private String siteId;
    @Value("${course‐publish.templateId}")
    private String templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;
    @Value("${course‐publish.pageWebPath}")
    private String pageWebPath;
    @Value("${course‐publish.pagePhysicalPath}")
    private String pagePhysicalPath;
    @Value("${course‐publish.dataUrlPre}")
    private String dataUrlPre;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CoursePubRepository coursePubRepository;

    @Override
    public TeachplanNode findTeachPlanList(String courseId) {
        return this.teachPlanMapper.selectList(courseId);
    }

    @Override
    @Transactional
    public ResponseResult addTeachPlan(Teachplan teachplan) {
        //校验课程id和课程计划名称
        if (teachplan == null || StringUtils.isBlank(teachplan.getCourseid()) || StringUtils.isBlank(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程id
        String courseid = teachplan.getCourseid();
        //父节点id
        String parentid = teachplan.getParentid();
        if (StringUtils.isBlank(parentid)) {
            parentid = this.getTeachPlanRoot(courseid);
        }
        //取出父节点信息
        Optional<Teachplan> optionalTeachplan = this.teachPlanRepository.findById(parentid);
        if (!optionalTeachplan.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        Teachplan teachplanParent = optionalTeachplan.get();
        //设置父节点
        teachplan.setParentid(parentid);
        //未发布
        teachplan.setStatus("0");
        //父节点级别
        String parentGrade = teachplanParent.getGrade();
        if (parentGrade.equals("1")) {
            teachplan.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplan.setGrade("3");
        }

        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        this.teachPlanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest) {

        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 20;
        }

        //调用mapper查询
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseListPage = this.courseMapper.findCourseListPage(courseListRequest);
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setTotal(courseListPage.getTotal());
        queryResult.setList(courseListPage.getResult());

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    @Override
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> optional = this.courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase one = this.getCourseBaseById(id);
        if (one == null) {
            ExceptionCast.cast(CourseCode.COURSE_FIND_COURSENOTEXIST);
        }
        //修改课程信息
        one.setName(courseBase.getName());
        one.setUsers(courseBase.getUsers());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setDescription(courseBase.getDescription());

        this.courseBaseRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = this.courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(id);
        if (one != null) {
            //修改信息
            one.setCharge(courseMarket.getCharge());
            one.setPrice(courseMarket.getPrice());
            one.setValid(courseMarket.getValid());
            one.setStartTime(courseMarket.getStartTime());
            one.setEndTime(courseMarket.getEndTime());
            one.setQq(courseMarket.getQq());
        } else {
            //添加课程营销信息
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            one.setId(id);
        }
        this.courseMarketRepository.save(one);
        return one;
    }

    @Override
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {

        CoursePic coursePic = null;
        //先查询
        Optional<CoursePic> optional = this.coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        //没有课程图片则创建新对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        //设置对象信息
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        this.coursePicRepository.save(coursePic);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = this.coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long l = this.coursePicRepository.deleteByCourseid(courseId);
        if (l > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    @Override
    public CourseView courseView(String id) {

        CourseView courseView = new CourseView();
        //课程基本信息
        Optional<CourseBase> courseBaseOptional = this.courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            courseView.setCourseBase(courseBaseOptional.get());
        }
        //课程营销信息
        CourseMarket courseMarket = this.getCourseMarketById(id);
        if (courseMarket != null) {
            courseView.setCourseMarket(courseMarket);
        }
        //课程图片信息
        CoursePic coursePic = this.findCoursePic(id);
        if (coursePic != null) {
            courseView.setCoursePic(coursePic);
        }
        //课程节点信息
        TeachplanNode teachplanNode = this.teachPlanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    @Override
    public CoursePublishResult preview(String id) {

        CourseBase courseBase = this.findCourseBaseById(id);

        CmsPage cmsPage = new CmsPage();

        //页面信息
        //站点
        cmsPage.setSiteId(siteId);
        //页面名称
        cmsPage.setPageName(id + ".html");
        //数据url
        cmsPage.setDataUrl(dataUrlPre + id);
        //模板
        cmsPage.setTemplateId(templateId);
        //页面访问路径
        cmsPage.setPageWebPath(pageWebPath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(pagePhysicalPath);
        //页面别名
        cmsPage.setPageAliase(courseBase.getName());

        //远程请求cms服务保存页面
        CmsPageResult cmsPageResult = this.cmsPageClient.saveCmsPage(cmsPage);

        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //课程预览路径
        String pageUrl = previewUrl + pageId;

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    @Override
    @Transactional
    public CoursePublishResult publish(String id) {

        //发布课程
        CmsPage cmsPage = new CmsPage();
        //查询课程信息
        CourseBase courseBaseById = this.findCourseBaseById(id);
        //页面信息
        //站点
        cmsPage.setSiteId(siteId);
        //页面名称
        cmsPage.setPageName(id + ".html");
        //数据url
        cmsPage.setDataUrl(dataUrlPre + id);
        //模板
        cmsPage.setTemplateId(templateId);
        //页面访问路径
        cmsPage.setPageWebPath(pageWebPath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(pagePhysicalPath);
        //页面别名
        cmsPage.setPageAliase(courseBaseById.getName());

        CmsPostPageResult cmsPostPageResult = this.cmsPageClient.postPageQuick(cmsPage);

        if (!cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //更新课程状态
        CourseBase courseBase = this.saveCoursePubState(id);

        //将课程保存到数据库
        //创建课程索引信息
        CoursePub coursePub = this.createCoursePub(id);


        //将课程索引信息保存到数据库
        CoursePub newCoursePub = this.saveCoursePub(id, coursePub);

        if (newCoursePub == null) {
            //创建课程索引信息失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }

        //页面url
        String pageUrl = cmsPostPageResult.getPageUrl();

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    //保存coursePub对象
    private CoursePub saveCoursePub(String id, CoursePub coursePub) {

        //课程id为空，抛出异常
        if (StringUtils.isBlank(id)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //创建coursePubNew
        CoursePub coursePubNew = null;

        Optional<CoursePub> coursePubOptional = this.coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        } else {
            coursePubNew = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub, coursePubNew);
        //主键
        coursePubNew.setId(id);

        //时间戳
        coursePubNew.setTimestamp(new Date());

        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);

        //保存到数据库
        this.coursePubRepository.save(coursePubNew);

        return coursePubNew;
    }

    //创建coursePub对象
    private CoursePub createCoursePub(String id) {

        CoursePub coursePub = new CoursePub();
        //设置主键
        coursePub.setId(id);

        //课程基本信息
        Optional<CourseBase> courseBaseOptional = this.courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> courseMarketOptional = this.courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程图片信息
        Optional<CoursePic> coursePicOptional = this.coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()) {
            CoursePic coursePic = coursePicOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //教学计划
        TeachplanNode teachplanNode = this.teachPlanMapper.selectList(id);
        //将课程计划转成json
        String teachPlanString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachPlanString);

        return coursePub;
    }

    //更新课程状态
    private CourseBase saveCoursePubState(String id) {
        CourseBase courseBaseById = this.findCourseBaseById(id);
        if (courseBaseById != null) {
            courseBaseById.setStatus("202002");
            return this.courseBaseRepository.save(courseBaseById);
        }
        return null;
    }

    //根据id查找课程基本信息
    private CourseBase findCourseBaseById(String id) {
        Optional<CourseBase> courseBaseOptional = this.courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            return courseBaseOptional.get();
        }
        ExceptionCast.cast(CourseCode.COURSE_FIND_COURSENOTEXIST);
        return null;
    }

    @Override
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        //课程状态默认为未发布
        courseBase.setStatus("202001");
        this.courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
    }

    //获取课程根结点，如果没有则添加根结点
    public String getTeachPlanRoot(String courseId) {
        //找不到该课程
        Optional<CourseBase> optional = this.courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        //取出课程计划根结点
        List<Teachplan> teachplanList = this.teachPlanRepository.findByCourseidAndParentid(courseId, "0");

        if (teachplanList == null || teachplanList.size() == 0) {
            //新增一个根结点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");//1级
            teachplanRoot.setStatus("0");//未发布
            this.teachPlanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);
        return teachplan.getId();
    }
}
