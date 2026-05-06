package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysDictDataDTO;
import com.junoyi.system.domain.dto.SysDictDataQueryDTO;
import com.junoyi.system.domain.vo.SysDictDataVO;

import java.util.List;

/**
 * 字典数据业务接口类
 *
 * @author Fan
 */
public interface ISysDictDataService {

    /**
     * 分页查询字典数据列表
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果对象
     */
    PageResult<SysDictDataVO> getDictDataList(SysDictDataQueryDTO queryDTO);

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SysDictDataVO> getDictDataByType(String dictType);

    /**
     * 根据ID查询字典数据详情
     *
     * @param dictCode 字典数据编码
     * @return 字典数据VO对象
     */
    SysDictDataVO getDictDataById(Long dictCode);

    /**
     * 新增字典数据
     *
     * @param dictDataDTO 字典数据DTO
     */
    void addDictData(SysDictDataDTO dictDataDTO);

    /**
     * 修改字典数据
     *
     * @param dictDataDTO 字典数据DTO
     */
    void updateDictData(SysDictDataDTO dictDataDTO);

    /**
     * 删除字典数据
     *
     * @param dictCode 字典数据编码
     */
    void deleteDictData(Long dictCode);

    /**
     * 批量删除字典数据
     *
     * @param dictCodes 字典数据编码列表
     */
    void deleteDictDataList(List<Long> dictCodes);
}