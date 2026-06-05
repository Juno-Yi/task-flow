package com.junoyi.system.convert;

import com.junoyi.system.domain.dto.SysMenuDTO;
import com.junoyi.system.domain.po.SysMenu;
import com.junoyi.system.domain.vo.SysMenuVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单转换器（静态工具类）
 * <p>
 * 提供系统菜单在不同层级对象之间的转换功能：
 * - PO（持久层对象）与 VO（视图层对象）之间的转换
 * - DTO（数据传输对象）与 PO、VO 之间的转换
 * - 批量转换支持
 * - 部分字段更新支持
 * </p>
 * <p>
 * 菜单系统包含丰富的配置项，如路由信息、组件路径、显示控制、缓存策略等，
 * 该转换器负责在这些复杂字段之间进行准确的数据传递。
 * </p>
 *
 * @author Fan
 */
public final class SysMenuConverter {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private SysMenuConverter() {
    }

    /**
     * 将菜单实体对象（PO）转换为视图对象（VO）
     * <p>
     * 主要用于从数据库查询菜单信息后返回给前端展示。该方法会复制所有菜单配置字段，
     * 包括路由信息、组件配置、显示控制、缓存策略、权限控制等完整字段。
     * 时间字段保持原有的 Date 类型不变。
     * </p>
     *
     * @param entity 菜单实体对象，包含数据库中的完整菜单配置信息
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysMenuVO toVo(SysMenu entity) {
        if (entity == null) {
            return null;
        }
        SysMenuVO vo = new SysMenuVO();
        vo.setId(entity.getId());
        vo.setParentId(entity.getParentId());
        vo.setName(entity.getName());
        vo.setPath(entity.getPath());
        vo.setComponent(entity.getComponent());
        vo.setTitle(entity.getTitle());
        vo.setIcon(entity.getIcon());
        vo.setMenuType(entity.getMenuType());
        vo.setSort(entity.getSort());
        vo.setIsHide(entity.getIsHide());
        vo.setIsHideTab(entity.getIsHideTab());
        vo.setKeepAlive(entity.getKeepAlive());
        vo.setIsIframe(entity.getIsIframe());
        vo.setLink(entity.getLink());
        vo.setIsFullPage(entity.getIsFullPage());
        vo.setFixedTab(entity.getFixedTab());
        vo.setActivePath(entity.getActivePath());
        vo.setShowBadge(entity.getShowBadge());
        vo.setShowTextBadge(entity.getShowTextBadge());
        vo.setPermission(entity.getPermission());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 将菜单传输对象（DTO）转换为实体对象（PO）
     * <p>
     * 主要用于接收前端传入的菜单配置数据，准备保存到数据库时使用。
     * 该方法会复制所有菜单配置字段，包括：
     * - 基础信息：id、parentId、name、title、icon
     * - 路由配置：path、component、link、activePath
     * - 显示控制：menuType、sort、isHide、isHideTab、isFullPage、fixedTab
     * - 功能配置：keepAlive、isIframe、showBadge、showTextBadge
     * - 权限控制：permission、status
     * - 其他：remark
     * </p>
     * <p>
     * 注意：此方法不会处理创建时间和更新时间，这些字段通常由框架自动填充。
     * </p>
     *
     * @param dto 菜单传输对象，包含前端传入的完整菜单配置信息
     * @return 转换后的实体对象，如果输入为 null 则返回 null
     */
    public static SysMenu toEntity(SysMenuDTO dto) {
        if (dto == null) {
            return null;
        }
        SysMenu entity = new SysMenu();
        entity.setId(dto.getId());
        entity.setParentId(dto.getParentId());
        entity.setName(dto.getName());
        entity.setPath(dto.getPath());
        entity.setComponent(dto.getComponent());
        entity.setTitle(dto.getTitle());
        entity.setIcon(dto.getIcon());
        entity.setMenuType(dto.getMenuType());
        entity.setSort(dto.getSort());
        entity.setIsHide(dto.getIsHide());
        entity.setIsHideTab(dto.getIsHideTab());
        entity.setKeepAlive(dto.getKeepAlive());
        entity.setIsIframe(dto.getIsIframe());
        entity.setLink(dto.getLink());
        entity.setIsFullPage(dto.getIsFullPage());
        entity.setFixedTab(dto.getFixedTab());
        entity.setActivePath(dto.getActivePath());
        entity.setShowBadge(dto.getShowBadge());
        entity.setShowTextBadge(dto.getShowTextBadge());
        entity.setPermission(dto.getPermission());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
        return entity;
    }

    /**
     * 将菜单传输对象（DTO）直接转换为视图对象（VO）
     * <p>
     * 主要用于在不涉及数据库操作的场景下，进行对象格式的转换，
     * 例如在业务逻辑层处理菜单数据后直接返回给前端。
     * 此方法直接复制所有字段，不进行任何格式转换。
     * </p>
     *
     * @param dto 菜单传输对象
     * @return 转换后的视图对象，如果输入为 null 则返回 null
     */
    public static SysMenuVO dtoToVo(SysMenuDTO dto) {
        if (dto == null) {
            return null;
        }
        SysMenuVO vo = new SysMenuVO();
        vo.setId(dto.getId());
        vo.setParentId(dto.getParentId());
        vo.setName(dto.getName());
        vo.setPath(dto.getPath());
        vo.setComponent(dto.getComponent());
        vo.setTitle(dto.getTitle());
        vo.setIcon(dto.getIcon());
        vo.setMenuType(dto.getMenuType());
        vo.setSort(dto.getSort());
        vo.setIsHide(dto.getIsHide());
        vo.setIsHideTab(dto.getIsHideTab());
        vo.setKeepAlive(dto.getKeepAlive());
        vo.setIsIframe(dto.getIsIframe());
        vo.setLink(dto.getLink());
        vo.setIsFullPage(dto.getIsFullPage());
        vo.setFixedTab(dto.getFixedTab());
        vo.setActivePath(dto.getActivePath());
        vo.setShowBadge(dto.getShowBadge());
        vo.setShowTextBadge(dto.getShowTextBadge());
        vo.setPermission(dto.getPermission());
        vo.setStatus(dto.getStatus());
        vo.setRemark(dto.getRemark());
        return vo;
    }

    /**
     * 批量将菜单实体对象列表（PO）转换为视图对象列表（VO）
     * <p>
     * 使用 Stream API 进行流式转换，适用于查询菜单树或菜单列表的场景。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null，避免调用方出现空指针异常。
     * </p>
     *
     * @param entityList 菜单实体对象列表
     * @return 转换后的视图对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysMenuVO> toVoList(List<SysMenu> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return Collections.emptyList();
        }
        return entityList.stream().map(SysMenuConverter::toVo).collect(Collectors.toList());
    }

    /**
     * 批量将菜单传输对象列表（DTO）转换为实体对象列表（PO）
     * <p>
     * 适用于批量导入或批量新增菜单的场景，使用 Stream API 进行高效转换。
     * 如果输入列表为 null 或空集合，则返回空集合而非 null。
     * </p>
     *
     * @param dtoList 菜单传输对象列表
     * @return 转换后的实体对象列表，如果输入为 null 或空则返回空集合
     */
    public static List<SysMenu> toEntityList(List<SysMenuDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(SysMenuConverter::toEntity).collect(Collectors.toList());
    }

    /**
     * 使用 DTO 中的数据更新现有实体对象
     * <p>
     * 采用选择性更新策略：只有当 DTO 中的字段不为 null 时才会更新实体对象的对应字段。
     * 这种方式适用于部分更新的场景（如 PATCH 请求），避免将未提供的字段覆盖为 null。
     * 对于菜单这种具有大量配置项的对象，选择性更新尤为重要，可以只修改需要变更的配置。
     * </p>
     *
     * @param dto    菜单传输对象，包含需要更新的字段值（非 null 字段才会被更新）
     * @param entity 需要更新的实体对象，更新操作会直接修改该对象的状态
     */
    public static void updateEntity(SysMenuDTO dto, SysMenu entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getId() != null) entity.setId(dto.getId());
        if (dto.getParentId() != null) entity.setParentId(dto.getParentId());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getPath() != null) entity.setPath(dto.getPath());
        if (dto.getComponent() != null) entity.setComponent(dto.getComponent());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getIcon() != null) entity.setIcon(dto.getIcon());
        if (dto.getMenuType() != null) entity.setMenuType(dto.getMenuType());
        if (dto.getSort() != null) entity.setSort(dto.getSort());
        if (dto.getIsHide() != null) entity.setIsHide(dto.getIsHide());
        if (dto.getIsHideTab() != null) entity.setIsHideTab(dto.getIsHideTab());
        if (dto.getKeepAlive() != null) entity.setKeepAlive(dto.getKeepAlive());
        if (dto.getIsIframe() != null) entity.setIsIframe(dto.getIsIframe());
        if (dto.getLink() != null) entity.setLink(dto.getLink());
        if (dto.getIsFullPage() != null) entity.setIsFullPage(dto.getIsFullPage());
        if (dto.getFixedTab() != null) entity.setFixedTab(dto.getFixedTab());
        if (dto.getActivePath() != null) entity.setActivePath(dto.getActivePath());
        if (dto.getShowBadge() != null) entity.setShowBadge(dto.getShowBadge());
        if (dto.getShowTextBadge() != null) entity.setShowTextBadge(dto.getShowTextBadge());
        if (dto.getPermission() != null) entity.setPermission(dto.getPermission());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getRemark() != null) entity.setRemark(dto.getRemark());
    }
}
