package com.ylzs.service.bigstylerecord;

import com.ylzs.entity.aps.CappPiPreScheduleResult;
import com.ylzs.entity.bigstylerecord.BigStyleNodeRecord;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import com.ylzs.service.IOriginService;
import com.ylzs.vo.BigStyleNodeRecordExportVo;

import java.util.Date;
import java.util.List;


public interface IBigStyleNodeRecordService extends IOriginService<BigStyleNodeRecord> {
    void updateAll(List<BigStyleNodeRecord> styles);
    //接收到主数据后更新记录
    void addOrUpdateBigStyleNodeRecord(BigStyleMasterData bigStyleMasterData, List<BigStyleMasterDataSKC> skcList);
    //根据APS预排产结果更新工作组
    void updateStyleNodeRecord(CappPiPreScheduleResult preResult);

    void calcStyleNodeRecord(List<BigStyleNodeRecord> styles);

    /**
     * @param styleCode 大货款号
     * @param branchBeginTime 分科开始时间
     * @param branchEndTime 分科结束时间
     */
    void updateStyleNodeBranchInfo(String styleCode, Date branchBeginTime, Date branchEndTime, String createUserCode);

    List<BigStyleNodeRecordExportVo> getBigStyleNodeRecordVos(List<BigStyleNodeRecord> styles);
}



