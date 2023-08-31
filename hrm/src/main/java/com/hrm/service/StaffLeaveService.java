package com.hrm.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrm.dto.Response;
import com.hrm.dto.ResponseDTO;
import com.hrm.entity.Attendance;
import com.hrm.entity.StaffLeave;
import com.hrm.enums.AttendanceStatusEnum;
import com.hrm.enums.AuditStatusEnum;
import com.hrm.mapper.StaffLeaveMapper;
import com.hrm.util.HutoolExcelUtil;
import com.hrm.vo.StaffLeaveVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiujie
 * @since 2022-04-05
 */
@Service
public class StaffLeaveService extends ServiceImpl<StaffLeaveMapper, StaffLeave> {

    @Resource
    private StaffLeaveMapper staffLeaveMapper;

    @Resource
    private AttendanceService attendanceService;


    public ResponseDTO add(StaffLeave staffLeave) {
        if (save(staffLeave)) {
            return Response.success();
        }
        return Response.error();
    }

    public ResponseDTO deleteById(Integer id) {
        if (removeById(id)) {
            return Response.success();
        }
        return Response.error();
    }

    public ResponseDTO deleteBatch(List<Integer> ids) {
        if (removeBatchByIds(ids)) {
            return Response.success();
        }
        return Response.error();
    }

    /**
     * 设置请假，当请假通过之后，就将休假的考勤状态设为休假
     *
     * @param staffLeave
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO edit(StaffLeave staffLeave) {
        // 如果同意放假，就将考勤日的状态设置为放假
        if (staffLeave.getStatus() == AuditStatusEnum.APPROVE) {
            for (int i = 0; i < staffLeave.getDays(); i++) {
                Date attendanceDate = DateUtil.offsetDay(staffLeave.getStartDate(), i).toSqlDate();
                // 因为周末本就要休息，所以只需记录在休假期间包括的工作日的考勤状态到数据库
                if (!DateUtil.isWeekend(attendanceDate)) {
                    Attendance attendance = new Attendance().setAttendanceDate(attendanceDate).setStaffId(staffLeave.getStaffId()).setStatus(AttendanceStatusEnum.LEAVE);
                    QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("staff_id", attendance.getStaffId()).eq("attendance_date", attendance.getAttendanceDate());
                    if (!this.attendanceService.saveOrUpdate(attendance, queryWrapper)) {
                        return Response.error();
                    }
                }
            }
        }
        if (updateById(staffLeave)) {
            return Response.success();
        }
        return Response.error();
    }


    public ResponseDTO findById(Integer id) {
        StaffLeave staffLeave = getById(id);
        if (staffLeave != null) {
            return Response.success(staffLeave);
        }
        return Response.error();
    }


    public ResponseDTO list(Integer current, Integer size, String name) {
        IPage<StaffLeaveVO> config = new Page<>(current, size);
        if (name == null) {
            name = "";
        }
        IPage<StaffLeaveVO> page = this.staffLeaveMapper.listStaffLeaveVO(config, name);
        // 将响应数据填充到map中
        Map map = new HashMap();
        map.put("pages", page.getPages());
        map.put("total", page.getTotal());
        map.put("list", page.getRecords());
        return Response.success(map);
    }

    /**
     * 数据导出
     *
     * @param response
     * @return
     */
    public ResponseDTO export(HttpServletResponse response) throws IOException {
        List<StaffLeave> list = list();
        HutoolExcelUtil.writeExcel(response, list, "员工请假记录表", StaffLeave.class);
        return Response.success();
    }

    /**
     * 数据导入
     *
     * @param file
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<StaffLeave> list = HutoolExcelUtil.readExcel(inputStream, 1, StaffLeave.class);
        // IService接口中的方法.批量插入数据
        if (saveBatch(list)) {
            return Response.success();
        }
        return Response.error();
    }


    public ResponseDTO findByStaffId(Integer current, Integer size, Integer id) {
        IPage<StaffLeaveVO> config = new Page<>(current, size);
        IPage<StaffLeaveVO> page = this.staffLeaveMapper.listStaffLeaveVOByStaffId(config, id);
        // 将响应数据填充到map中
        Map map = new HashMap();
        map.put("pages", page.getPages());
        map.put("total", page.getTotal());
        map.put("list", page.getRecords());
        return Response.success(map);
    }


    /**
     * 查找未被审批的申请
     *
     * @param id
     * @return
     */
    public ResponseDTO findUnauditedByStaffId(Integer id) {
        QueryWrapper<StaffLeave> query = new QueryWrapper<>();
        query.eq("staff_id", id).eq("status", AuditStatusEnum.UNAUDITED.getCode());
        List<StaffLeave> list = list(query);
        if (list.size() > 0) {
            return Response.success();
        }
        return Response.error();
    }

}




