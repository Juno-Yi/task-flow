package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.domain.dto.SysDictDataDTO;
import com.junoyi.system.domain.dto.SysDictDataQueryDTO;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.service.ISysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统字典数据控制器
 *
 * @author Fan
 */
@Tag(name = "系统字典数据")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictDataController.class);
    private final ISysDictDataService dictDataService;

    /**
     * 分页查询字典数据列表
     */
    @Operation(summary = "获取字典数据列表", description = "分页查询字典数据列表")
    @GetMapping("/list")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.get.list"})
    public R<PageResult<SysDictDataVO>> getDictDataList(SysDictDataQueryDTO queryDTO) {
        return R.ok(dictDataService.getDictDataList(queryDTO));
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Operation(summary = "根据字典类型查询字典数据", description = "根据字典类型查询所有启用状态的字典数据")
    @GetMapping("/type/{dictType}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.get.all"})
    public R<List<SysDictDataVO>> getDictDataByType(@Parameter(description = "字典类型") @PathVariable("dictType") String dictType) {
        return R.ok(dictDataService.getDictDataByType(dictType));
    }

    /**
     * 根据ID查询字典数据详情
     */
    @Operation(summary = "获取字典数据详情", description = "根据ID获取字典数据详情")
    @GetMapping("/{dictCode}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.get.id"})
    public R<SysDictDataVO> getDictDataById(@Parameter(description = "字典数据编码") @PathVariable("dictCode") Long dictCode) {
        return R.ok(dictDataService.getDictDataById(dictCode));
    }

    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据", description = "新增字典数据")
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.add.data"})
    public R<Void> addDictData(@RequestBody @Valid SysDictDataDTO dictDataDTO) {
        dictDataService.addDictData(dictDataDTO);
        return R.ok();
    }

    /**
     * 修改字典数据
     */
    @Operation(summary = "修改字典数据", description = "修改字典数据")
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.update.data"})
    public R<Void> updateDictData(@RequestBody @Valid SysDictDataDTO dictDataDTO) {
        dictDataService.updateDictData(dictDataDTO);
        return R.ok();
    }

    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据", description = "根据ID删除字典数据")
    @DeleteMapping("/{dictCode}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.delete.data.code"})
    public R<Void> deleteDictData(@Parameter(description = "字典数据编码") @PathVariable("dictCode") Long dictCode) {
        dictDataService.deleteDictData(dictCode);
        return R.ok();
    }

    /**
     * 批量删除字典数据
     */
    @Operation(summary = "批量删除字典数据", description = "批量删除字典数据")
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.delete.code.batch"})
    public R<Void> deleteDictDataList(@RequestBody List<Long> dictCodes) {
        dictDataService.deleteDictDataList(dictCodes);
        return R.ok();
    }
}