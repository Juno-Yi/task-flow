package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysDictTypeDTO;
import com.junoyi.system.domain.dto.SysDictTypeQueryDTO;
import com.junoyi.system.domain.vo.SysDictTypeVO;

import java.util.List;

/**
 * 字典类型服务接口
 *
 * @author Fan
 */
public interface ISysDictTypeService {


    /**
     * 查询所有字典类型
     *
     * @return 字典类型列表
     */
    List<SysDictTypeVO> getAllDictTypes();

    /**
     * 根据ID查询字典类型详情
     *
     * @param dictId 字典类型ID
     * @return 字典类型VO对象
     */
    SysDictTypeVO getDictTypeById(Long dictId);

    /**
     * 新增字典类型
     *
     * @param dictTypeDTO 字典类型DTO
     */
    void addDictType(SysDictTypeDTO dictTypeDTO);

    /**
     * 修改字典类型
     *
     * @param dictTypeDTO 字典类型DTO
     */
    void updateDictType(SysDictTypeDTO dictTypeDTO);

    /**
     * 删除字典类型
     *
     * @param dictId 字典类型ID
     */
    void deleteDictType(Long dictId);

    /**
     * 批量删除字典类型
     *
     * @param dictIds 字典类型ID列表
     */
    void deleteDictTypes(List<Long> dictIds);
}
