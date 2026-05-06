package com.junoyi.system.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.framework.core.constant.CacheConstants;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.system.convert.SysDictDataConverter;
import com.junoyi.system.domain.po.SysDept;
import com.junoyi.system.domain.po.SysDictData;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.mapper.SysDictDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统字典 API 实现类
 * 供其他模块调用的字典服务实现
 * 使用Redis缓存提升性能
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysDictApiImpl implements SysDictApi {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictApiImpl.class);

    private final SysDictDataMapper sysDictDataMapper;
    private final SysDictDataConverter sysDictDataConverter;

    /**
     * 根据字典类型查询字典数据
     * 优先从Redis缓存获取,缓存未命中时查询数据库并缓存
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    public List<SysDictDataVO> getDictDataByType(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return List.of();
        }

        // 尝试从缓存获取
        String cacheKey = CacheConstants.DICT_DATA + dictType;
        List<SysDictDataVO> cachedData = RedisUtils.getCacheList(cacheKey);
        if (cachedData != null && !cachedData.isEmpty()) {
            log.debug("Retrieved dictionary data from cache: {}", dictType);
            return cachedData;
        }

        // 缓存未命中,查询数据库
        log.debug("Cache miss, querying dictionary data from database: {}", dictType);
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        List<SysDictDataVO> voList = sysDictDataConverter.toVoList(dictDataList);

        // 存入缓存
        if (!voList.isEmpty()) {
            RedisUtils.setCacheList(cacheKey, voList);
            log.debug("Dictionary data cached: {}", dictType);
        }

        return voList;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * 优先从Redis缓存获取,缓存未命中时查询数据库并缓存
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签,如果不存在返回 null
     */
    @Override
    public String getDictLabel(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return null;
        }

        // 尝试从缓存获取
        String cacheKey = CacheConstants.DICT_LABEL + dictType + ":" + dictValue;
        String cachedLabel = RedisUtils.getCacheObject(cacheKey);
        if (cachedLabel != null) {
            return cachedLabel;
        }

        // 缓存未命中,查询数据库
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .eq(SysDictData::getStatus, "0")
                .last("LIMIT 1");

        SysDictData dictData = sysDictDataMapper.selectOne(wrapper);
        String label = dictData != null ? dictData.getDictLabel() : null;

        // 存入缓存(包括null值,避免缓存穿透)
        if (label != null) {
            RedisUtils.setCacheObject(cacheKey, label);
        }

        return label;
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * 优先从Redis缓存获取,缓存未命中时查询数据库并缓存
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值,如果不存在返回 null
     */
    @Override
    public String getDictValue(String dictType, String dictLabel) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictLabel)) {
            return null;
        }

        // 尝试从缓存获取
        String cacheKey = CacheConstants.DICT_VALUE + dictType + ":" + dictLabel;
        String cachedValue = RedisUtils.getCacheObject(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        }

        // 缓存未命中,查询数据库
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictLabel, dictLabel)
                .eq(SysDictData::getStatus, "0")
                .last("LIMIT 1");

        SysDictData dictData = sysDictDataMapper.selectOne(wrapper);
        String value = dictData != null ? dictData.getDictValue() : null;

        // 存入缓存
        if (value != null) {
            RedisUtils.setCacheObject(cacheKey, value);
        }

        return value;
    }

    /**
     * 检查字典数据是否存在
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsDictData(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return false;
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .eq(SysDictData::getStatus, "0");

        Long count = sysDictDataMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    /**
     * 批量根据字典类型查询字典数据
     *
     * @param dictTypes 字典类型列表
     * @return 字典类型为key,字典数据列表为value的Map
     */
    @Override
    public Map<String, List<SysDictDataVO>> getDictDataByTypes(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return new HashMap<>();
        }

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysDictData::getDictType, dictTypes)
                .eq(SysDictData::getStatus, "0")
                .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = sysDictDataMapper.selectList(wrapper);
        List<SysDictDataVO> voList = sysDictDataConverter.toVoList(dictDataList);

        // 按字典类型分组
        return voList.stream()
                .collect(Collectors.groupingBy(SysDictDataVO::getDictType));
    }

    /**
     * 刷新指定字典类型的缓存
     *
     * @param dictType 字典类型
     */
    public void refreshDictCache(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return;
        }

        log.info("Refreshing dictionary cache: {}", dictType);

        // 删除字典数据列表缓存
        String dataCacheKey = CacheConstants.DICT_DATA + dictType;
        RedisUtils.deleteObject(dataCacheKey);

        // 删除该字典类型的所有标签和值缓存
        String labelPattern = CacheConstants.DICT_LABEL + dictType + ":*";
        String valuePattern = CacheConstants.DICT_VALUE + dictType + ":*";
        RedisUtils.deleteKeys(labelPattern);
        RedisUtils.deleteKeys(valuePattern);

        // 重新加载到缓存
        getDictDataByType(dictType);

        log.info("Dictionary cache refresh completed: {}", dictType);
    }

    /**
     * 刷新所有字典缓存
     */
    public void refreshAllDictCache() {
        log.info("Starting to refresh all dictionary caches");

        // 删除所有字典相关缓存
        RedisUtils.deleteKeys(CacheConstants.DICT_DATA + "*");
        RedisUtils.deleteKeys(CacheConstants.DICT_LABEL + "*");
        RedisUtils.deleteKeys(CacheConstants.DICT_VALUE + "*");

        // 查询所有字典类型
        List<SysDictData> allDictData = sysDictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getStatus, "0")
                        .orderByAsc(SysDictData::getDictSort)
        );

        // 按字典类型分组并缓存
        Map<String, List<SysDictData>> groupedData = allDictData.stream()
                .collect(Collectors.groupingBy(SysDictData::getDictType));

        for (Map.Entry<String, List<SysDictData>> entry : groupedData.entrySet()) {
            String dictType = entry.getKey();
            List<SysDictDataVO> voList = sysDictDataConverter.toVoList(entry.getValue());

            // 缓存字典数据列表
            String cacheKey = CacheConstants.DICT_DATA + dictType;
            RedisUtils.setCacheList(cacheKey, voList);

            // 缓存每个字典项的标签和值
            for (SysDictDataVO vo : voList) {
                String labelKey = CacheConstants.DICT_LABEL + dictType + ":" + vo.getDictValue();
                String valueKey = CacheConstants.DICT_VALUE + dictType + ":" + vo.getDictLabel();
                RedisUtils.setCacheObject(labelKey, vo.getDictLabel());
                RedisUtils.setCacheObject(valueKey, vo.getDictValue());
            }
        }

        log.info("All dictionary caches refreshed, total {} dictionary types cached", groupedData.size());
    }

    /**
     * 根据字典类型和字典值获取字典项（包含listClass等完整信息）
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典数据VO,如果不存在返回 null
     */
    @Override
    public SysDictDataVO getDictItem(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return null;
        }

        // 获取该字典类型的所有数据
        List<SysDictDataVO> dictDataList = getDictDataByType(dictType);
        
        // 查找匹配的字典项
        return dictDataList.stream()
                .filter(item -> dictValue.equals(item.getDictValue()))
                .findFirst()
                .orElse(null);
    }
}
