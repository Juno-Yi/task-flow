package com.junoyi.system.controller;

import com.junoyi.framework.core.domain.module.R;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.security.annotation.PlatformScope;
import com.junoyi.framework.security.enums.PlatformType;
import com.junoyi.framework.web.domain.BaseController;
import com.junoyi.system.api.SysDictApiImpl;
import com.junoyi.system.domain.dto.SysDictTypeDTO;
import com.junoyi.system.domain.dto.SysDictTypeQueryDTO;
import com.junoyi.system.domain.vo.SysDictTypeVO;
import com.junoyi.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 系统字典类型控制器
 *
 * @author Fan
 */
@Tag(name = "系统字典类型")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController extends BaseController {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysDictTypeController.class);
    private final ISysDictTypeService dictTypeService;
    private final SysDictApiImpl sysDictApi;

    /**
     * 查询所有字典类型
     */
    @Operation(summary = "查询所有字典类型", description = "查询所有启用状态的字典类型")
    @GetMapping("/all")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.get.all"})
    public R<List<SysDictTypeVO>> getAllDictTypes() {
        return R.ok(dictTypeService.getAllDictTypes());
    }

    /**
     * 根据ID查询字典类型详情
     */
    @Operation(summary = "获取字典类型详情", description = "根据ID获取字典类型详情")
    @GetMapping("/{dictId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.get.id"})
    public R<SysDictTypeVO> getDictTypeById(@Parameter(description = "字典类型ID") @PathVariable("dictId") Long dictId) {
        return R.ok(dictTypeService.getDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @Operation(summary = "新增字典类型", description = "新增字典类型")
    @PostMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.add.type"})
    public R<Void> addDictType(@RequestBody @Valid SysDictTypeDTO dictTypeDTO) {
        dictTypeService.addDictType(dictTypeDTO);
        return R.ok();
    }

    /**
     * 修改字典类型
     */
    @Operation(summary = "修改字典类型", description = "修改字典类型")
    @PutMapping
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.update.type"})
    public R<Void> updateDictType(@RequestBody @Valid SysDictTypeDTO dictTypeDTO) {
        dictTypeService.updateDictType(dictTypeDTO);
        return R.ok();
    }

    /**
     * 删除字典类型
     */
    @Operation(summary = "删除字典类型", description = "根据ID删除字典类型")
    @DeleteMapping("/{dictId}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.delete.type.id"})
    public R<Void> deleteDictType(@Parameter(description = "字典类型ID") @PathVariable("dictId") Long dictId) {
        dictTypeService.deleteDictType(dictId);
        return R.ok();
    }

    /**
     * 批量删除字典类型
     */
    @Operation(summary = "批量删除字典类型", description = "批量删除字典类型")
    @DeleteMapping("/batch")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.delete.type.batch"})
    public R<Void> deleteDictTypes(@RequestBody List<Long> dictIds) {
        dictTypeService.deleteDictTypes(dictIds);
        return R.ok();
    }

    /**
     * 刷新指定字典类型的缓存
     */
    @Operation(summary = "刷新字典缓存", description = "刷新指定字典类型的Redis缓存")
    @PostMapping("/refresh/{dictType}")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.refresh.type"})
    public R<Void> refreshDictCache(@Parameter(description = "字典类型") @PathVariable("dictType") String dictType) {
        sysDictApi.refreshDictCache(dictType);
        log.info("DictCache", "Manually refreshed dictionary cache: {}", dictType);
        return R.ok("Dictionary cache refreshed successfully");
    }

    /**
     * 刷新所有字典缓存
     */
    @Operation(summary = "刷新所有字典缓存", description = "刷新所有字典类型的Redis缓存")
    @PostMapping("/refresh/all")
    @PlatformScope(PlatformType.ADMIN_WEB)
    @Permission(value = {"system.ui.dict.view", "system.api.dict.refresh.all"})
    public R<Void> refreshAllDictCache() {
        sysDictApi.refreshAllDictCache();
        log.info("DictCache", "Manually refreshed all dictionary caches");
        return R.ok("All dictionary caches refreshed successfully");
    }
}