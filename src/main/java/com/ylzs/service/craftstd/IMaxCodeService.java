package com.ylzs.service.craftstd;

public interface IMaxCodeService {
    Integer updateMaxId(String moduleCode, String preStr);

    /**
     * @param moduleCode 模块代码
     * @param preStr 流水号前缀
     * @param len 流水号长度
     * @param hasHistory 是否要取取消（除了建标要取）
     * @return 前缀+流水号
     */
    String getNextSerialNo(String moduleCode, String preStr, int len, boolean hasHistory);
}
