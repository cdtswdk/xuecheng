package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    private CourseService courseService;

    //查询课程计划
    @Override
    @RequestMapping(value = "/teachPlan/list/{courseId}")
    public TeachplanNode findTeachPlanList(@PathVariable("courseId") String courseId) {
        return this.courseService.findTeachPlanList(courseId);
    }

    @Override
    @PostMapping("/teachPlan/add")
    public ResponseResult addTeachPlan(@RequestBody Teachplan teachplan) {
        return this.courseService.addTeachPlan(teachplan);
    }

    @Override
    @RequestMapping(value = "/courseBase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return this.courseService.findCourseList(page, size, courseListRequest);
    }

    @Override
    @RequestMapping(value = "/courseBase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) throws RuntimeException {
        return this.courseService.getCourseBaseById(courseId);
    }

    @Override
    @PutMapping(value = "/courseBase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return this.courseService.updateCourseBase(id, courseBase);
    }

    @Override
    @RequestMapping(value = "/courseMarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseMarketById(courseId);
    }

    @Override
    @PostMapping(value = "/courseMarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        CourseMarket one = this.courseService.updateCourseMarket(id, courseMarket);
        if (one != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    @Override
    @PostMapping("/coursePic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        return this.courseService.saveCoursePic(courseId, pic);
    }

    @Override
    @GetMapping("/coursePic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return this.courseService.findCoursePic(courseId);
    }

    @Override
    @DeleteMapping("/coursePic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return this.courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseView/{id}")
    public CourseView courseView(@PathVariable("id") String id) {
        return this.courseService.courseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return this.courseService.preview(id);
    }

    @Override
    @RequestMapping(value = "/courseBase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return this.courseService.addCourseBase(courseBase);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable String id) {
        return courseService.publish(id);
    }
}
