package com.hrm.controller;

import com.hrm.entity.Leave;
import com.hrm.dto.ResponseDTO;
import com.hrm.service.LeaveService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 请假表 前端控制器
 * </p>
 *
 * @author qiujie
 * @since 2022-03-27
 */
@RestController
@RequestMapping("/leave")
public class LeaveController {
    @Resource
    private LeaveService leaveService;

    @ApiOperation("新增")
    @PostMapping
    public ResponseDTO add(@RequestBody Leave leave) {
        return this.leaveService.add(leave);
    }

    @ApiOperation("逻辑删除")
    @DeleteMapping("/{id}")
    public ResponseDTO delete(@PathVariable Integer id) {
        return this.leaveService.deleteById(id);
    }

    @ApiOperation("批量逻辑删除")
    @DeleteMapping("/batch/{ids}")
    public ResponseDTO deleteBatch(@PathVariable List<Integer> ids) {
        return this.leaveService.deleteBatch(ids);
    }

    @ApiOperation("编辑更新")
    @PutMapping
    public ResponseDTO edit(@RequestBody Leave leave) {
        return this.leaveService.edit(leave);
    }

    @ApiOperation("查询")
    @GetMapping("/{id}")
    public ResponseDTO findById(@PathVariable Integer id) {
        return this.leaveService.findById(id);
    }


    @ApiOperation("获取")
    @GetMapping("/{deptId}/{typeId}")
    public ResponseDTO find(@PathVariable Integer deptId, @PathVariable Integer typeId) {
        return this.leaveService.find(deptId, typeId);
    }

    @ApiOperation("设置假期")
    @PostMapping("/set")
    public ResponseDTO setLeave(@RequestBody Leave leave) {
        return this.leaveService.setLeave(leave);
    }


    @ApiOperation("查询")
    @GetMapping("/dept/{id}")
    public ResponseDTO findByDeptId(@PathVariable Integer id) {
        return this.leaveService.findByDeptId(id);
    }

}

