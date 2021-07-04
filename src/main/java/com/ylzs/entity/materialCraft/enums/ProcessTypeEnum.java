package com.ylzs.entity.materialCraft.enums;

/**
 * @author weikang
 * @Description 工序处理方式 -1减少 0替换 1增加
 * @Date 2020/3/9
 */
public enum ProcessTypeEnum {

    REDUCE(-1, "减少"),

    EXCHANGE(0, "替换"),

    ADD(1, "增加");

    private int dataBaseVal;

    private String descr;

    ProcessTypeEnum(int dataBaseVal, String descr) {

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
            ProcessTypeEnum[] templateTypes = ProcessTypeEnum.values();

            for (ProcessTypeEnum type : templateTypes)
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
