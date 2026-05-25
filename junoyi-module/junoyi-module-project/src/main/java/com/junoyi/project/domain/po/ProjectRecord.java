package com.junoyi.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 项目动态记录 PO 数据实体对象
 *
 * @author Fan
 */
@Data
@TableName("project_record")
public class ProjectRecord {

    @TableId
    private Long id;


}