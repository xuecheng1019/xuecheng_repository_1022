package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 课程信息管理接口
 */
public interface CourseBaseInfoService {

    //课程查询
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程
     * @param companyId 机构id
     * @param addCourseDto 课程信息
     * @return 课程详细信息
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据ID查询课程详细信息
     * @param courseId
     * @return
     */
    public CourseBaseInfoDto selectCourseBaseInfo(Long courseId);

    /**
     * 修改课程
     * @param companyId
     * @param dto
     * @return
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);
}
