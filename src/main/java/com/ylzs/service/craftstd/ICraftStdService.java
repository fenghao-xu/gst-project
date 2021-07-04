package com.ylzs.service.craftstd;

import com.ylzs.common.util.Result;
import com.ylzs.entity.craftstd.CraftStd;
import com.ylzs.entity.craftstd.CraftStdStatistic;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 说明：工艺标准服务接口
 *
 * @author lyq
 * 2019-09-30 9:24
 */
public interface ICraftStdService {
    Integer addCraftStd(CraftStd craftStd);

    Integer deleteCraftStd(long[] ids, String userCode, String moduleCode);

    Integer updateCraftStd(CraftStd craftStd, CraftStd craftStdOld);

    Boolean isCraftStdExist(long id);


    Integer updateCraftStdData(CraftStd craftStd, CraftStd craftStdOld,
                               String effectPicUrls,
                               String handPicUrls,
                               String video,
                               String toolCodes,
                               String toolNames);

    Integer addCraftStdData(CraftStd craftStd,
                            String effectPicUrls,
                            String handPicUrls,
                            String videoUrl,
                            String toolCodes,
                            String toolNames);

    String getNewCraftStdCode(String moduleCode, List<String> craftCategoryCodes, List<String> craftPartCodes, String makeCode) throws Exception;


    List<CraftStd> getCraftStdById(long[] ids);

    List<CraftStd> getCraftStdByCode(String[] craftStdCodes);

    List<CraftStd> getCraftStdByPage(String keywords, Date beginDate, Date endDate, Long id);

    String addResource(Long id, int resourceType, MultipartFile file, String userCode);

    String addResourceFile(int resourceType, MultipartFile file);

    String addResourceSimple(int resourceType, MultipartFile file, String userCode);

    Integer deleteResource(long id, int resourceType, String fileUrl, String userCode);
    Integer deleteResourceSimple(int resourceType, String fileUrl, String userCode);

    String addResourcePath(long id, int resourceType, String sharePath, String userCode);

    String addResourcePathSimple(int resourceType, String sharePath, String userCode);

    List<ShareFileInfo> getShareFileInfo(String parentPath, boolean isPic, boolean fetchChild);


    Result<Integer> commitCraftStd(long id, boolean isCommit, String user, String rejectReason);

    Result<Integer> auditCraftStd(long id, boolean isAudit, String user);

    public  List<CraftStd> getCraftStdByLikeParam(Map<String,Object> param);

    List<CraftStd> getCraftStdByCondition(String craftStdCode,
                                          String craftStdName,
                                          String styleCode,
                                          String requireQuality,
                                          String makeDesc,
                                          String status,
                                          Date createTimeBeginDate,
                                          Date createTimeEndDate,
                                          Date okTimeBeginDate,
                                          Date okTimeEndDate,
                                          Date updateTimeBeginDate,
                                          Date updateTimeEndDate,
                                          String remark,
                                          Boolean isVideo,
                                          Boolean isPic,
                                          Date commitTimeBeginDate,
                                          Date commitTimeEndDate,
                                          String user,
                                          Date extractTimeBeginDate,
                                          Date extractTimeEndDate,
                                          Integer craftCategoryId,
                                          Integer craftPartId,
                                          String craftCategoryCode,
                                          String craftPartCode
    );

    CraftStdStatistic getCraftStdStatistic();

    List<CraftStd> getNoVideoCraftStd(String craftStdCode, String craftStdName);
    List<CraftStd> getNoHandleImageCraftStd(String craftStdCode);


    int addCraftStdVideo(Long craftStdId, String fileUrl, String fileName);
    int getNoVideoCraftStdCount();
    int addCraftStdHandleImage(Long craftStdId, String fileUrl, String fileName);
    boolean isCraftStdNameExists(String craftStdName, Long craftStdId);
    String getCraftStdInUsed(String craftStdCode);

    /**
     * 共享文件路径
     */
    @Data
    static class ShareFileInfo {
        private String fullPath;
        private String fileName;
        private String fullPathEncode;
        private Boolean isFile;
        private List<ShareFileInfo> childrens;
    }


}