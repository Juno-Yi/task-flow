package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.api.SysDictApiImpl;
import com.junoyi.system.convert.SysDictTypeConverter;
import com.junoyi.system.domain.dto.SysDictTypeDTO;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.po.SysDictType;
import com.junoyi.system.domain.vo.SysDictTypeVO;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.system.mapper.SysDictDataMapper;
import com.junoyi.system.mapper.SysDictTypeMapper;
import com.junoyi.system.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典类型服务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements ISysDictTypeService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictTypeServiceImpl.class);
    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataMapper sysDictDataMapper;
    private final SysDictApiImpl sysDictApi;

    /**
     * 查询所有字典类型
     *
     * @return 字典类型列表
     */
    @Override
    public List<SysDictTypeVO> getAllDictTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getStatus, "0")
                .orderByAsc(SysDictType::getDictType);

        List<SysDictType> dictTypes = sysDictTypeMapper.selectList(wrapper);
        return SysDictTypeConverter.toVoList(dictTypes);
    }

    /**
     * 根据ID查询字典类型详情
     *
     * @param dictId 字典类型ID
     * @return 字典类型VO对象
     */
    @Override
    public SysDictTypeVO getDictTypeById(Long dictId) {
        SysDictType dictType = sysDictTypeMapper.selectById(dictId);
        return dictType != null ? SysDictTypeConverter.toVo(dictType) : null;
    }

    /**
     * 新增字典类型
     *
     * @param dictTypeDTO 字典类型DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictType(SysDictTypeDTO dictTypeDTO) {
        SysDictType dictType = SysDictTypeConverter.toEntity(dictTypeDTO);

        // 设置默认值
        if (dictType.getStatus() == null) {
            dictType.setStatus("0");
        }
        dictType.setCreateBy(SecurityUtils.getUserName());
        dictType.setCreateTime(DateUtils.getNowDate());

        sysDictTypeMapper.insert(dictType);

        log.info("DictType", "添加字典类型: {}", dictType.getDictType());
        
        // 刷新缓存
        sysDictApi.refreshDictCache(dictType.getDictType());

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("create", "dict_type",
                "创建了字典类型「" + dictType.getDictName() + "」",
                String.valueOf(dictType.getDictId()), dictType.getDictName(),
                JsonUtils.toJsonString(dictTypeDTO)));
    }

    /**
     * 修改字典类型
     *
     * @param dictTypeDTO 字典类型DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(SysDictTypeDTO dictTypeDTO) {
        SysDictType oldDictType = sysDictTypeMapper.selectById(dictTypeDTO.getDictId());
        if (oldDictType == null) {
            throw new RuntimeException("字典类型不存在");
        }

        SysDictType dictType = SysDictTypeConverter.toEntity(dictTypeDTO);
        dictType.setDictId(dictTypeDTO.getDictId());

        // 保留原有的字段值
        if (dictType.getStatus() == null) {
            dictType.setStatus(oldDictType.getStatus());
        }
        dictType.setUpdateBy(SecurityUtils.getUserName());
        dictType.setUpdateTime(DateUtils.getNowDate());

        sysDictTypeMapper.updateById(dictType);

        log.info("DictType", "更新字典类型: {}", dictType.getDictType());
        
        // 刷新缓存(如果字典类型改变了,需要刷新新旧两个类型的缓存)
        sysDictApi.refreshDictCache(oldDictType.getDictType());
        if (!oldDictType.getDictType().equals(dictType.getDictType())) {
            sysDictApi.refreshDictCache(dictType.getDictType());
        }

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData("update", "dict_type",
                "更新了字典类型「" + dictType.getDictName() + "」",
                String.valueOf(dictType.getDictId()), dictType.getDictName(),
                JsonUtils.toJsonString(dictTypeDTO)));
    }

    /**
     * 删除字典类型
     *
     * @param dictId 字典类型ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long dictId) {
        SysDictType dictType = sysDictTypeMapper.selectById(dictId);
        if (dictType == null) {
            throw new RuntimeException("字典类型不存在");
        }

        String dictTypeCode = dictType.getDictType();

        // 先删除该类型下的所有字典数据
        LambdaQueryWrapper<SysDictData> dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(SysDictData::getDictType, dictTypeCode);
        Long dataCount = sysDictDataMapper.selectCount(dataWrapper);
        if (dataCount > 0) {
            sysDictDataMapper.delete(dataWrapper);
            log.info("DictType", "删除字典类型 {} 关联的字典数据: {} 条", dictTypeCode, dataCount);
        }

        // 再删除字典类型
        sysDictTypeMapper.deleteById(dictId);

        log.info("DictType", "删除字典类型: {}", dictTypeCode);
        
        // 刷新缓存
        sysDictApi.refreshDictCache(dictTypeCode);

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("delete", "dict_type",
                "删除了字典类型「" + dictType.getDictName() + "」及其 " + dataCount + " 条字典数据",
                String.valueOf(dictId), dictType.getDictName()));
    }

    /**
     * 批量删除字典类型
     *
     * @param dictIds 字典类型ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypes(List<Long> dictIds) {
        // 获取所有字典类型
        List<SysDictType> dictTypes = sysDictTypeMapper.selectBatchIds(dictIds);
        
        // 先删除所有关联的字典数据
        for (SysDictType dictType : dictTypes) {
            LambdaQueryWrapper<SysDictData> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.eq(SysDictData::getDictType, dictType.getDictType());
            Long dataCount = sysDictDataMapper.selectCount(dataWrapper);
            if (dataCount > 0) {
                sysDictDataMapper.delete(dataWrapper);
                log.info("DictType", "删除字典类型 {} 关联的字典数据: {} 条", dictType.getDictType(), dataCount);
            }
        }

        // 批量删除字典类型
        for (Long dictId : dictIds) {
            sysDictTypeMapper.deleteById(dictId);
        }

        log.info("DictType", "批量删除字典类型: {} 条", dictIds.size());
        
        // 刷新所有被删除字典类型的缓存
        for (SysDictType dictType : dictTypes) {
            sysDictApi.refreshDictCache(dictType.getDictType());
        }

        // 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of("delete", "dict_type",
                "批量删除了 " + dictIds.size() + " 个字典类型",
                dictIds.toString(), null));
    }
}
