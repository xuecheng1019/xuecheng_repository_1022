package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exeception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //查询接口
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        //拼接查询条件
        //根据课程名称模糊查询  name like '%名称%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //TODO:课程发布状态条件
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        //数据
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();

        //准备返回数据 List<T> items, long counts, long page, long pageSize
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageParams.getPageNo(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //参数合法校验 @用注解  @Validated   可以将dto上的注解@size @NotEmpty 生效
//        if (StringUtils.isBlank(dto.getName())) {
//            XueChengPlusException.cast("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(dto.getMt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            throw new RuntimeException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
//        }

        //向课程表加信息
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(dto,courseBaseNew);
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        courseBaseNew.setAuditStatus("202002");//审核状态，默认未提交
        courseBaseNew.setStatus("203001");//发布状态 未发布
        //插入到数据表中
        int insert = courseBaseMapper.insert(courseBaseNew);
        if(insert <= 0){
            throw new RuntimeException("添加课程失败");
        }

        //向销售表market表加信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseBaseNew.getId());
        //保存营销信息
        saveCourseMarket(courseMarketNew);
        //查询详细信息
        CourseBaseInfoDto courseBaseInfoDto = selectCourseBaseInfo(courseBaseNew.getId());

        return courseBaseInfoDto;
    }

    //保存营销信息
    public int saveCourseMarket(CourseMarket courseMarketNew){
        //参数校验
        String charge = courseMarketNew.getCharge();
        if(charge == null){
            throw new RuntimeException("收费规则必须选择");
        }
        if(charge.equals("201001")){
            if(courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue()<=0){
                XueChengPlusException.cast("课程为收费价格不能为空且必须大于0");
            }
        }

        //从market表中查询信息，如果存在则更新，如果不存在就添加
        CourseMarket courseMarket = courseMarketMapper.selectById(courseMarketNew.getId());
        if(courseMarket == null){
            int insert = courseMarketMapper.insert(courseMarketNew);
            return insert;
        }
        else{
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            int i = courseMarketMapper.updateById(courseMarket);
            return i;
        }
    }

    //根据Id查询详细课程信息
    public CourseBaseInfoDto selectCourseBaseInfo(Long courseId){
        //查询课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        //查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //查询课程分类名字
//        CourseCategory courseCategory = courseCategoryMapper.selectById(courseBase.getSt());
//        String categoryName = courseCategory.getName();
//
//        String parentid = courseCategory.getParentid();
//        CourseCategory courseCategoryParent = courseCategoryMapper.selectById(parentid);
//        String parentName = courseCategoryParent.getName();
        //组装
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

//        courseBaseInfoDto.setStName(categoryName);
//        courseBaseInfoDto.setMtName(parentName);
        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }

        //机构只能改本机构的课
        if(!courseBase.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //封装信息
        BeanUtils.copyProperties(dto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程信息
        int i = courseBaseMapper.updateById(courseBase);
        if(i < 0){
            XueChengPlusException.cast("修改课程失败");
        }

        //封装营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        saveCourseMarket(courseMarket);

        //查询课程信息返回

        CourseBaseInfoDto courseBaseInfo = selectCourseBaseInfo(courseId);
        return courseBaseInfo;

    }

}
