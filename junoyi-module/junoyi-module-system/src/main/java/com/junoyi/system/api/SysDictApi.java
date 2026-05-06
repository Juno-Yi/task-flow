package com.junoyi.system.api;

import com.junoyi.system.domain.vo.SysDictDataVO;

import java.util.List;

/**
 * 系统字典 API 接口
 * 供其他模块调用的字典服务接口
 *
 * @author Fan
 */
public interface SysDictApi {

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SysDictDataVO> getDictDataByType(String dictType);

    /**
     * 根据字典类型和字典值获取字典标签
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签，如果不存在返回 null
     */
    String getDictLabel(String dictType, String dictValue);

    /**
     * 根据字典类型和字典标签获取字典值
     * 
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值，如果不存在返回 null
     */
    String getDictValue(String dictType, String dictLabel);

    /**
     * 检查字典数据是否存在
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return true-存在，false-不存在
     */
    boolean existsDictData(String dictType, String dictValue);

    /**
     * 批量根据字典类型查询字典数据
     * 
     * @param dictTypes 字典类型列表
     * @return 字典类型为key，字典数据列表为value的Map
     */
    java.util.Map<String, List<SysDictDataVO>> getDictDataByTypes(List<String> dictTypes);

    /**
     * 根据字典类型和字典值获取字典项（包含listClass等完整信息）
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典数据VO,如果不存在返回 null
     */
    SysDictDataVO getDictItem(String dictType, String dictValue);
}
