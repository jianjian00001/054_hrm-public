package com.hrm.service;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hrm.entity.Role;
import com.hrm.mapper.RoleMapper;
import com.hrm.dto.Response;
import com.hrm.dto.ResponseDTO;
import com.hrm.util.HutoolExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author qiujie
 * @since 2022-02-28
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {


    public ResponseDTO add(Role role) {
        if (save(role)) {
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


    public ResponseDTO edit(Role role) {
        if (updateById(role)) {
            return Response.success();
        }
        return Response.error();
    }


    public ResponseDTO findById(Integer id) {
        Role role = getById(id);
        if (role != null) {
            return Response.success(role);
        }
        return Response.error();
    }

    public ResponseDTO findAll() {
        List<Role> list = list();
        return Response.success(list);
    }


    public ResponseDTO list(Integer current, Integer size, String name) {
        IPage<Role> config = new Page<>(current, size);
        QueryWrapper<Role> wrapper = null;
        if (name != "" && name != null) {
            wrapper = new QueryWrapper<>();
            wrapper.like("name", name);
        }
        IPage<Role> page = page(config, wrapper);
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
        List<Role> list = list();
        HutoolExcelUtil.writeExcel(response,list,"角色信息表",Role.class);
        return Response.success();
    }

    /**
     * 数据导入
     *
     * @param file
     * @return
     */
    @Transactional
    public ResponseDTO imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Role> list = reader.readAll(Role.class);
        // IService接口中的方法.批量插入数据
        if (saveBatch(list)) {
            return Response.success();
        }
        return Response.error();
    }


}




