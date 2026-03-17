package com.flexi.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flexi.app.entity.AssetApply;
import com.flexi.app.entity.AssetApplyDTO;
import com.flexi.app.entity.Asset;
import com.flexi.app.mapper.AssetApplyMapper;
import com.flexi.app.service.AssetApplyService;
import com.flexi.app.service.AssetService;
import io.github.zmxckj.flexiadmin.service.DictService;
import io.github.zmxckj.flexiadmin.service.UserService;
import io.github.zmxckj.flexiadmin.service.DeptService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AssetApplyServiceImpl extends ServiceImpl<AssetApplyMapper, AssetApply> implements AssetApplyService {

    @Autowired
    private AssetService assetService;

    @Autowired
    private DictService dictService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeptService deptService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<AssetApplyDTO> listWithDetails(Integer page, Integer size) {
        // 创建分页对象
        Page<AssetApply> pageInfo = new Page<>(page, size);
        
        // 执行分页查询
        IPage<AssetApply> applyPage = baseMapper.selectPage(pageInfo, null);
        
        // 转换为DTO并返回
        return applyPage.convert(this::convertToDTO);
    }

    @Override
    public IPage<AssetApplyDTO> listMyApplies(Integer page, Integer size, Long userId) {
        // 创建分页对象
        Page<AssetApply> pageInfo = new Page<>(page, size);
        
        // 构建查询条件，根据userId查询
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AssetApply> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        // 执行分页查询
        IPage<AssetApply> applyPage = baseMapper.selectPage(pageInfo, queryWrapper);
        
        // 转换为DTO并返回
        return applyPage.convert(this::convertToDTO);
    }

    /**
     * 将AssetApply转换为AssetApplyDTO
     */
    private AssetApplyDTO convertToDTO(AssetApply apply) {
        AssetApplyDTO dto = new AssetApplyDTO();
        BeanUtils.copyProperties(apply, dto);
        
        // 填充资产信息
        if (apply.getAssetId() != null) {
            Asset asset = assetService.getById(apply.getAssetId());
            if (asset != null) {
                dto.setAssetName(asset.getName());
                dto.setAssetCode(asset.getCode());
            }
        }
        
        // 填充用户名称
        if (apply.getUserId() != null) {
            String userName = userService.getUserNameById(apply.getUserId());
            dto.setUserName(userName);
        }
        
        // 填充部门名称
        if (apply.getDeptId() != null) {
            String deptName = deptService.getDeptNameById(apply.getDeptId());
            dto.setDeptName(deptName);
        }
        
        // 填充状态名称
        if (apply.getStatus() != null) {
            String statusName = dictService.getDictLabel("asset_apply_status", apply.getStatus());
            dto.setStatusName(statusName);
        }
        
        // 格式化时间
        if (apply.getApplyTime() != null) {
            dto.setApplyTimeStr(sdf.format(new Date(apply.getApplyTime())));
        }
        if (apply.getApprovalTime() != null) {
            dto.setApprovalTimeStr(sdf.format(new Date(apply.getApprovalTime())));
        }
        
        return dto;
    }
}