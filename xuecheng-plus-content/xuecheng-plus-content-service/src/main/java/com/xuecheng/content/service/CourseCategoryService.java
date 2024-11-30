package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

import java.util.List;

/**
 * 课程信息管理接口
 */
public interface CourseCategoryService {
    //课程查询
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
