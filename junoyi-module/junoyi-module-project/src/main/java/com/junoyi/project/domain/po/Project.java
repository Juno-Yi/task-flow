package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.junoyi.framework.web.domain.BaseController;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目 PO 数据对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("project")
public class Project extends BaseController {
}