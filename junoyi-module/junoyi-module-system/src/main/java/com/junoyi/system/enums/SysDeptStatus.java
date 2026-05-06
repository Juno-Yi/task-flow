package com.junoyi.system.enums;

/**
 * 部门状态枚举类
 *
 * @author Fam
 */
public enum SysDeptStatus {

    ENABLE(1,"启用"),

    DISABLE(0,"禁用");

    private final int code;

    private final String label;

    private SysDeptStatus(int code, String label){
        this.code = code;
        this.label = label;
    }

    public int getCode(){
        return code;
    }

    public String getLabel(){
        return label;
    }

    public static SysDeptStatus fromCode(int code) {
        for (SysDeptStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return ENABLE;
    }
}
