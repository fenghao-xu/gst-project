package com.ylzs.entity.materialCraft.enums;

/**
 * @author weikang
 * @Description 状态1000.草稿 1010.已审核 1020.已发布(生效) 1030.失效 1040.未生效
 * @Date 2020/3/9
 */
public enum StatusEnum {

    DRAFT_STATUS(1000, "草稿"),

    AUDITED_STATUS(1010, "已审核"),

    PUBLISHED_STATUS(1020, "已发布"),

    INVALID_STATUS(1030, "失效"),

    NOT_ACTIVE_STATUS(1040, "即将生效");

    private int dataBaseVal;

    private String descr;

    StatusEnum(int dataBaseVal, String descr) {

        this.dataBaseVal = dataBaseVal;
        this.descr = descr;
    }

    /**
     * 解析字符串.
     * 通过传入的 type 获取对应的中文
     * @return
     */
    public static final String parse(int value)
    {
        try
        {
            StatusEnum[] templateTypes = StatusEnum.values();

            for (StatusEnum type : templateTypes)
            {
                if (type.dataBaseVal==value)
                {
                    return type.descr;
                }
            }

            return "";
        }
        catch (Throwable t)
        {
            return "";
        }
    }

    public int getDataBaseVal() {
        return dataBaseVal;
    }

    public void setDataBaseVal(int dataBaseVal) {
        this.dataBaseVal = dataBaseVal;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
