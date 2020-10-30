package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface CourseService {

    /**
     * 查询课程计划
     *
     * @param courseId
     * @return
     */
    TeachplanNode findTeachPlanList(String courseId);

    /**
     * 添加课程计划
     *
     * @param teachplan
     * @return
     */
    ResponseResult addTeachPlan(Teachplan teachplan);

    /**
     * 查询课程列表
     *
     * @param size
     * @param page
     * @param courseListRequest
     * @return
     */
    QueryResponseResult findCourseList(int page, int size, CourseListRequest courseListRequest);

    /**
     * 添加课程
     *
     * @param courseBase
     * @return
     */
    AddCourseResult addCourseBase(CourseBase courseBase);

    /**
     * 根据课程id查询课程基本信息
     *
     * @param courseId
     * @return
     */
    CourseBase getCourseBaseById(String courseId);

    /**
     * 修改课程基本信息
     *
     * @param id
     * @param courseBase
     * @return
     */
    ResponseResult updateCourseBase(String id, CourseBase courseBase);

    /**
     * 根据课程id获取课程营销信息
     *
     * @param courseId
     * @return
     */
    CourseMarket getCourseMarketById(String courseId);

    /**
     * 跟新课程营销计划
     *
     * @param id
     * @param courseMarket
     * @return
     */
    CourseMarket updateCourseMarket(String id, CourseMarket courseMarket);

    /**
     * 保存课程图片
     *
     * @param courseId
     * @param pic
     * @return
     */
    ResponseResult saveCoursePic(String courseId, String pic);

    /**
     * 查询课程图片
     *
     * @param courseId
     * @return
     */
    CoursePic findCoursePic(String courseId);

    /**
     * 删除课程图片
     *
     * @param courseId
     * @return
     */
    ResponseResult deleteCoursePic(String courseId);

    /**
     * 查询课程预览详细信息
     *
     * @param id
     * @return
     */
    CourseView courseView(String id);

    /**
     * 根据课程id预览课程
     *
     * @param id
     * @return
     */
    CoursePublishResult preview(String id);

    /**
     * 发布课程
     *
     * @param id
     * @return
     */
    CoursePublishResult publish(String id);
}
