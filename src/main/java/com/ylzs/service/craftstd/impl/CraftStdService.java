package com.ylzs.service.craftstd.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.util.*;
import com.ylzs.dao.craftstd.*;
import com.ylzs.dao.system.NotifyMsgDao;
import com.ylzs.dao.system.NotifyUserDao;
import com.ylzs.entity.craftstd.*;
import com.ylzs.entity.system.NotifyMsg;
import com.ylzs.entity.system.NotifyUser;
import com.ylzs.service.craftstd.ICopyFileCallback;
import com.ylzs.service.craftstd.ICraftStdService;
import com.ylzs.service.craftstd.IMaxCodeService;
import com.ylzs.service.system.IOperationLogService;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ylzs.common.util.Assert.*;
import static com.ylzs.common.util.Const.*;
import static com.ylzs.common.util.FastDFSUtil.deleteFileByUrl;
import static com.ylzs.common.util.FastDFSUtil.uploadFile;
import static com.ylzs.common.util.StringUtils.RightTrimHide;


/**
 * 说明：工艺标准服务实现
 *
 * @author lyq
 * 2019-09-30 9:27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CraftStdService implements ICraftStdService, ICopyFileCallback {
    private static final Logger logger = LoggerFactory.getLogger(CraftStdService.class);

    @Resource
    private CraftStdDao craftStdDao;
    @Resource
    private IMaxCodeService maxCodeService;
    @Resource
    private NotifyUserDao notifyUserDao;
    @Resource
    private NotifyMsgDao notifyMsgDao;
    @Resource
    private CraftStdPartDao craftStdPartDao;
    @Resource
    private CraftPartDao craftPartDao;
    @Resource
    private CraftFileDao craftFileDao;
    @Resource
    private CraftCategoryDao craftCategoryDao;

    @Resource
    private CraftStdToolDao craftStdToolDao;

    @Resource
    private IOperationLogService operationLogService;


    public Map<String, List<ShareFileInfo>> mapSharePath;

    //public static String shareBasePath = "10.7.200.135/share/";
    //public static String shareAcc = "naersi\\zoujiangxiong";
    //public static String sharePwd = "yingjiaznb";

    public CraftStdService() {
        this.mapSharePath = new ConcurrentHashMap<>();
    }

    public String getShareBasePath() {
        String shareBasePath = StaticDataCache.getInstance().getValue(Const.SHARE_BASE_PATH, "");
        return shareBasePath;
    }

    public String getShareAcc() {
        String shareAcc = StaticDataCache.getInstance().getValue(Const.SHARE_ACC, "");
        return shareAcc;
    }

    public String getSharePwd() {
        String sharePwd = StaticDataCache.getInstance().getValue(Const.SHARE_PWD, "");
        return sharePwd;
    }

    /**
     * @param basePath 基本路径
     * @param destPath 中间路径
     * @param orgFile  原始文件
     * @return 中间路径+新的文件名
     * @throws BaseException
     */
    private String saveToFile(String basePath, String destPath, MultipartFile orgFile) throws BaseException {
        if (basePath != "" && !basePath.endsWith(File.separator)) {
            basePath = basePath + File.separator;
        }
        if (!destPath.endsWith(File.separator)) {
            destPath = destPath + File.separator;
        }

        String fileName = orgFile.getOriginalFilename();
        int index = fileName.lastIndexOf("\\");
        if (index > 0) {
            fileName = fileName.substring(index + 1);
        }

        String fileExt = "";
        int k = fileName.lastIndexOf(".");
        if(k >= 0) {
            fileExt = fileName.substring(k);
        }

        long fileSize = orgFile.getSize();
        if (fileExt.equalsIgnoreCase(".jpg") ||
                fileExt.equalsIgnoreCase(".jpeg") ||
                fileExt.equalsIgnoreCase(".bmp") ||
                fileExt.equalsIgnoreCase(".png")) {
            //图片文件
            if (fileSize > 10 * 1024 * 1024) {
                throw new BaseException("上传图片文件过大，文件大小不能超过10M");
            }
        } else {
            //视频文件
            if (fileSize > 500 * 1024 * 1024) {
                throw new BaseException("上传视频文件过大，文件大小不能超过500M");
            }
        }
        fileName = UUID.randomUUID().toString().replaceAll("-", "") + fileExt;

        File dest = new File(basePath + destPath + fileName);
        if (!dest.getParentFile().exists()) {
            if (!dest.getParentFile().mkdir()) {
                throw new BaseException("创建目录失败" + dest.getParentFile().getAbsolutePath());
            }
        }
        try {

            orgFile.transferTo(dest);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(e.getMessage());
        }
        return destPath + fileName;

    }

    @Override
    public Integer addCraftStdData(CraftStd craftStd,
                                   String effectPicUrls,
                                   String handPicUrls,
                                   String videoUrl,
                                   String toolCodes,
                                   String toolNames) {
        if (craftStd.getCraftPartIds() != null && !craftStd.getCraftPartIds().isEmpty()) {
            String[] split = craftStd.getCraftPartIds().split(",");
            Integer[] ids = new Integer[split.length];
            int i = 0;
            for (String id : split) {
                ids[i++] = Integer.parseInt(id);
            }

            List<String> categoryCodes = craftCategoryDao.getCraftCategoryCodeByPartIds(ids);
            craftStd.setIsCategoryShare(categoryCodes.size() > 1);

            List<String> partCodes = craftPartDao.getCraftPartCodeByPartIds(ids);
            craftStd.setIsPartShare(partCodes.size() > 1);

            if (split.length != partCodes.size()) {
                throw new BaseException("部件id未找到:" + craftStd.getCraftPartIds());
            }
            try {
                String craftStdCode = getNewCraftStdCode("craft-std",
                        categoryCodes, partCodes, craftStd.getMakeTypeCode());
                craftStd.setCraftStdCode(craftStdCode);
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }


        List<String> fileList = new ArrayList<String>();
        List<Integer> resourceList = new ArrayList<Integer>();
        if (handPicUrls != null && !handPicUrls.isEmpty()) {
            String[] urlArr = handPicUrls.split(",");
            for(String fileUrl: urlArr) {
                fileList.add(fileUrl);
                resourceList.add(Const.RES_TYPE_HAND_IMG);
            }
            craftStd.setIsHandPic(urlArr.length > 0);
        }

        if (effectPicUrls != null && !effectPicUrls.isEmpty()) {
            String[] urlArr = effectPicUrls.split(",");
            for(String fileUrl: urlArr) {
                fileList.add(fileUrl);
                resourceList.add(Const.RES_TYPE_EFFECT_IMG);
            }
            craftStd.setIsEffectPic(urlArr.length > 0);
        }

        if (videoUrl != null && !videoUrl.isEmpty()) {
            craftStd.setIsVideo(true);
            fileList.add(videoUrl);
            resourceList.add(Const.RES_TYPE_STD_VIDEO);
        }

        Integer ret = craftStdDao.addCraftStd(craftStd);
        if (ret != null && ret >= 1) {
            boolean isPartShare = false;
            boolean isCategoryShare = false;
            if (craftStd.getCraftPartIds() != null && !craftStd.getCraftPartIds().isEmpty()) {
                int lastCategoryId = -1;
                int lastPartId = -1;

                List<CraftStdPart> craftStdParts = new ArrayList<CraftStdPart>();
                String[] split = craftStd.getCraftPartIds().split(",");
                for (String partId : split) {
                    CraftPart craftPart = craftPartDao.getCraftPartById(Integer.parseInt(partId));
                    if (craftPart == null)
                        throw new BaseException("未找到部件id:" + partId);

                    if(craftPart.getId() != null) {
                        if(lastPartId == -1) {
                            lastPartId = craftPart.getId();
                        } else {
                            if(lastPartId != craftPart.getId().intValue()) {
                                isPartShare = true;
                            }
                        }
                    }

                    if(craftPart.getCraftCategoryId() != null) {
                        if(lastCategoryId == -1) {
                            lastCategoryId = craftPart.getCraftCategoryId();
                        } else {
                            if(lastCategoryId != craftPart.getCraftCategoryId().intValue()) {
                                isCategoryShare = true;
                            }
                        }
                    }

                    CraftStdPart craftStdPart = new CraftStdPart();
                    craftStdPart.setCraftPartId(craftPart.getId());
                    craftStdPart.setCraftCategoryId(craftPart.getCraftCategoryId());
                    craftStdPart.setCraftStdId(craftStd.getId());
                    craftStdPart.setUpdateTime(new Date());
                    craftStdPart.setUpdateUser(craftStd.getCreateUser());
                    craftStdParts.add(craftStdPart);
                }
                craftStdPartDao.addCraftStdParts(craftStdParts);
            }



            for (Integer i = 0; i < fileList.size(); i++) {
                List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(null, null, resourceList.get(i), fileList.get(i));
                boolean isFinded = false;
                for (CraftFile craftFile : craftFiles) {
                    if (craftFile.getKeyId() == null) {
                        isFinded = true;
                        craftFile.setKeyId(craftStd.getId());
                        craftFile.setUpdateTime(new Date());
                        craftFile.setUpdateUser(craftStd.getCreateUser());
                        craftFileDao.updateCraftFile(craftFile);
                    }
                }
                if(!isFinded) {
                    CraftFile craftFile = new CraftFile();
                    craftFile.setKeyId(craftStd.getId());
                    craftFile.setResourceType(resourceList.get(i));
                    craftFile.setFileUrl(fileList.get(i));
                    if(craftFiles != null && craftFiles.size() > 0) {
                        craftFile.setRemark(craftFiles.get(0).getRemark());
                    }
                    craftFile.setUpdateTime(new Date());
                    craftFile.setUpdateUser(craftStd.getCreateUser());
                    craftFileDao.addCraftFile(craftFile);
                }
            }


            if(toolCodes != null && !toolCodes.isEmpty() && toolNames != null && !toolNames.isEmpty()) {
                String[] codes = toolCodes.split(",");
                String[] names = toolNames.split(",");
                for (int i = 0; i < codes.length; i++) {
                    CraftStdTool tool = new CraftStdTool();
                    long randomCode = SnowflakeIdUtil.generateId();
                    tool.setRandomCode(randomCode);
                    tool.setCraftStdId(craftStd.getId());
                    tool.setToolCode(codes[i]);
                    tool.setToolName(names[i]);
                    tool.setCreateTime(new Date());
                    tool.setCreateUser(craftStd.getCreateUser());
                    tool.setUpdateTime(new Date());
                    tool.setUpdateUser(craftStd.getCreateUser());
                    craftStdToolDao.insert(tool);
                }
            }

            CraftStd craftStdNew = new CraftStd();
            craftStdNew.setId(craftStd.getId());
            craftStdNew.setIsCategoryShare(isCategoryShare);
            craftStdNew.setIsPartShare(isPartShare);
            ret = craftStdDao.updateCraftStd(craftStdNew);
        }


        return ret;

    }


    @Override
    public Integer updateCraftStdData(CraftStd craftStd,
                                      CraftStd craftStdOld,
                                      String effectPicUrls,
                                      String handPicUrls,
                                      String videoUrl,
                                      String toolCodes,
                                      String toolNames) {

        if (craftStd.getCraftPartIds() != null) {
            boolean isPartShare = false;
            boolean isCategoryShare = false;

            String oldPartIds = craftStdOld.getCraftPartIds();
            String newPartIds = craftStd.getCraftPartIds();

            List<String> addPartIds = new ArrayList<String>();
            List<String> delPartIds = new ArrayList<String>();
            parseChange(newPartIds, oldPartIds, addPartIds, delPartIds);
            for (String delPartId : delPartIds) {
                craftStdPartDao.deleteCraftStdPart(craftStd.getId(), Integer.parseInt(delPartId));
            }

            if (addPartIds.size() > 0) {
                int lastCategoryId = -1;
                int lastPartId = -1;
                List<CraftStdPart> craftStdParts = new ArrayList<CraftStdPart>();

                for (String partId : addPartIds) {
                    CraftPart craftPart = craftPartDao.getCraftPartById(Integer.parseInt(partId));
                    if (craftPart == null)
                        throw new BaseException("未找到部件id:" + partId);

                    if(craftPart.getId() != null) {
                        if(lastPartId == -1) {
                            lastPartId = craftPart.getId();
                        } else {
                            if(lastPartId != craftPart.getId().intValue()) {
                                isPartShare = true;
                            }
                        }
                    }

                    if(craftPart.getCraftCategoryId() != null) {
                        if(lastCategoryId == -1) {
                            lastCategoryId = craftPart.getCraftCategoryId();
                        } else {
                            if(lastCategoryId != craftPart.getCraftCategoryId().intValue()) {
                                isCategoryShare = true;
                            }
                        }
                    }

                    CraftStdPart craftStdPart = new CraftStdPart();
                    craftStdPart.setCraftPartId(craftPart.getId());
                    craftStdPart.setCraftCategoryId(craftPart.getCraftCategoryId());
                    craftStdPart.setCraftStdId(craftStd.getId());
                    craftStdPart.setUpdateTime(new Date());
                    craftStdPart.setUpdateUser(craftStd.getCreateUser());
                    craftStdParts.add(craftStdPart);
                }
                craftStdPartDao.addCraftStdParts(craftStdParts);
            }

            craftStd.setIsPartShare(isPartShare);
            craftStd.setIsCategoryShare(isCategoryShare);
        }

        //视频只存一份，删除不用的
        if(videoUrl != null && !videoUrl.isEmpty()) {
            List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(craftStdOld.getId(), null, Const.RES_TYPE_STD_VIDEO, null);
            for (CraftFile craftFile : craftFiles) {
                if (!videoUrl.equalsIgnoreCase(craftFile.getFileUrl())) {
                    deleteFileByUrl(craftFile.getFileUrl());
                    craftFileDao.deleteCraftFile(craftFile.getId());
                }
            }
        }

        //更新辅助工具
        if(toolCodes != null && toolNames != null) {
            List<String> addCodes = new ArrayList<String>();
            List<String> delCodes = new ArrayList<String>();
            QueryWrapper<CraftStdTool> toolQueryWrapper = new QueryWrapper<>();
            toolQueryWrapper.lambda().eq(CraftStdTool::getCraftStdId, craftStd.getId());
            List<CraftStdTool> craftStdTools = craftStdToolDao.selectList(toolQueryWrapper);
            String oldCodes = "";
            if(craftStdTools != null && !craftStdTools.isEmpty()) {
                for(CraftStdTool tool: craftStdTools) {
                    oldCodes += "," + tool.getToolCode();
                }
                if(oldCodes.length() > 0) {
                    oldCodes = oldCodes.substring(1);
                }
            }
            parseChange(toolCodes, oldCodes, addCodes, delCodes);
            if(addCodes.size() > 0) {
                String[] codeArr = toolCodes.split(",");
                String[] nameArr = toolNames.split(",");
                for (String code : addCodes) {
                    CraftStdTool tool = new CraftStdTool();
                    long randomCode = SnowflakeIdUtil.generateId();
                    tool.setCraftStdId(craftStd.getId());
                    tool.setRandomCode(randomCode);
                    tool.setToolCode(code);
                    for(int i = 0; i < codeArr.length; i++) {
                        if(codeArr[i].equals(code)) {
                            tool.setToolName(nameArr[i]);
                            break;
                        }
                    }
                    tool.setCreateTime(new Date());
                    tool.setCreateUser(craftStd.getCreateUser());
                    tool.setUpdateTime(new Date());
                    tool.setUpdateUser(craftStd.getCreateUser());
                    craftStdToolDao.insert(tool);
                }
            }

            if(delCodes.size() > 0) {
                for(String code: delCodes) {
                    craftStdTools.stream().filter(x -> code.equals(x.getToolCode())).
                            findFirst().ifPresent(a -> {craftStdToolDao.deleteById(a.getId());});
                }
            }
        }

        Boolean isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), Const.RES_TYPE_EFFECT_IMG);
        craftStd.setIsEffectPic(isResource);
        isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), Const.RES_TYPE_HAND_IMG);
        craftStd.setIsHandPic(isResource);
        isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), Const.RES_TYPE_STD_VIDEO);
        craftStd.setIsVideo(isResource);

        Integer ret = craftStdDao.updateCraftStd(craftStd);
        return ret;
    }


    @Override
    public Integer addCraftStd(CraftStd craftStd) {
        if (craftStd.getCraftPartIds() != null && !craftStd.getCraftPartIds().isEmpty()) {
            String[] split = craftStd.getCraftPartIds().split(",");
            Integer[] ids = new Integer[split.length];
            int i = 0;
            for (String id : split) {
                ids[i++] = Integer.parseInt(id);
            }

            List<String> categoryCodes = craftCategoryDao.getCraftCategoryCodeByPartIds(ids);
            craftStd.setIsCategoryShare(categoryCodes.size() > 1);

            List<String> partCodes = craftPartDao.getCraftPartCodeByPartIds(ids);
            craftStd.setIsPartShare(partCodes.size() > 1);

            if (split.length != partCodes.size()) {
                throw new BaseException("部件id未找到:" + craftStd.getCraftPartIds());
            }
            try {
                String craftStdCode = getNewCraftStdCode("craft-std",
                        categoryCodes, partCodes, craftStd.getMakeTypeCode());
                craftStd.setCraftStdCode(craftStdCode);
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }

        Integer ret = craftStdDao.addCraftStd(craftStd);
        if(ret != null && ret >= 1) {
            boolean isPartShare = false;
            boolean isCategoryShare = false;

            //提取每个部件
            if (craftStd.getCraftPartIds() != null && !craftStd.getCraftPartIds().isEmpty()) {
                int lastCategoryId = -1;
                int lastPartId = -1;
                List<CraftStdPart> craftStdParts = new ArrayList<CraftStdPart>();
                String[] split = craftStd.getCraftPartIds().split(",");
                for (String partId : split) {
                    CraftPart craftPart = craftPartDao.getCraftPartById(Integer.parseInt(partId));
                    if (craftPart == null)
                        throw new BaseException("未找到部件id:" + partId);

                    if(craftPart.getId() != null) {
                        if(lastPartId == -1) {
                            lastPartId = craftPart.getId();
                        } else {
                            if(lastPartId != craftPart.getId().intValue()) {
                                isPartShare = true;
                            }
                        }
                    }

                    if(craftPart.getCraftCategoryId() != null) {
                        if(lastCategoryId == -1) {
                            lastCategoryId = craftPart.getCraftCategoryId();
                        } else {
                            if(lastCategoryId != craftPart.getCraftCategoryId().intValue()) {
                                isCategoryShare = true;
                            }
                        }
                    }

                    CraftStdPart craftStdPart = new CraftStdPart();
                    craftStdPart.setCraftPartId(craftPart.getId());
                    craftStdPart.setCraftCategoryId(craftPart.getCraftCategoryId());


                    craftStdPart.setCraftStdId(craftStd.getId());
                    craftStdPart.setUpdateTime(new Date());
                    craftStdPart.setUpdateUser(craftStd.getCreateUser());
                    craftStdParts.add(craftStdPart);
                }
                if (!craftStdParts.isEmpty()) {
                    craftStdPartDao.addCraftStdParts(craftStdParts);
                }
            }


            String newUrls;

            //效果图
            if (craftStd.getEffectPicUrls() != null && !craftStd.getEffectPicUrls().isEmpty()) {
                newUrls = craftStd.getEffectPicUrls();
                String[] split = newUrls.split(",");
                for (String url : split) {
                    if (url.startsWith("smb:")) {
                        addResourcePath(craftStd.getId(), Const.RES_TYPE_EFFECT_IMG, url, craftStd.getUpdateUser());
                    } else {
                        if (url.startsWith("http")) {
                            long keyId = -1;
                            List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, Const.RES_TYPE_EFFECT_IMG, url);
                            if (craftFiles != null && craftFiles.size() >= 1) {
                                for (CraftFile craftFile : craftFiles) {
                                    if (craftFile.getKeyId() == null) {
                                        craftFile.setKeyId(craftStd.getId());
                                        craftFile.setUpdateTime(new Date());
                                        craftFile.setUpdateUser(craftStd.getCreateUser());
                                        craftFileDao.updateCraftFile(craftFile);
                                    }
                                }

                            }

                        }
                    }

                }
            }
            //
            if (craftStd.getHandPicUrls() != null && !craftStd.getHandPicUrls().isEmpty()) {
                newUrls = craftStd.getHandPicUrls();
                String[] split = newUrls.split(",");
                for (String url : split) {
                    if (url.startsWith("smb:")) {
                        addResourcePath(craftStd.getId(), Const.RES_TYPE_HAND_IMG, url, craftStd.getUpdateUser());
                    } else {
                        if (url.startsWith("http")) {
                            long keyId = -1;
                            List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, Const.RES_TYPE_HAND_IMG, url);
                            if (craftFiles != null && craftFiles.size() >= 1) {
                                for (CraftFile craftFile : craftFiles) {
                                    if (craftFile.getKeyId() == null) {
                                        craftFile.setKeyId(craftStd.getId());
                                        craftFile.setUpdateTime(new Date());
                                        craftFile.setUpdateUser(craftStd.getCreateUser());
                                        craftFileDao.updateCraftFile(craftFile);
                                    }
                                }

                            }

                        }
                    }
                }
            }

            if (craftStd.getVideoUrls() != null && !craftStd.getVideoUrls().isEmpty()) {
                newUrls = craftStd.getVideoUrls();
                String[] split = newUrls.split(",");
                for (String url : split) {
                    if (url.startsWith("smb:")) {
                        addResourcePath(craftStd.getId(), Const.RES_TYPE_STD_VIDEO, url, craftStd.getUpdateUser());
                    } else {
                        if (url.startsWith("http")) {
                            long keyId = -1;
                            List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, Const.RES_TYPE_STD_VIDEO, url);
                            if (craftFiles != null && craftFiles.size() >= 1) {
                                for (CraftFile craftFile : craftFiles) {
                                    if (craftFile.getKeyId() == null) {
                                        craftFile.setKeyId(craftStd.getId());
                                        craftFile.setUpdateTime(new Date());
                                        craftFile.setUpdateUser(craftStd.getCreateUser());
                                        craftFileDao.updateCraftFile(craftFile);
                                    }
                                }

                            }

                        }
                    }
                }
            }

            CraftStd craftStdNew = new CraftStd();
            craftStdNew.setId(craftStd.getId());
            Boolean isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), 20);
            craftStdNew.setIsEffectPic(isResource);
            isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), 10);
            craftStdNew.setIsHandPic(isResource);
            isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), 30);
            craftStdNew.setIsVideo(isResource);
            craftStdNew.setIsCategoryShare(isCategoryShare);
            craftStdNew.setIsPartShare(isPartShare);
            ret = craftStdDao.updateCraftStd(craftStdNew);
        }




        return ret;
    }

    @Override
    public Integer deleteCraftStd(long[] ids, String userCode, String moduleCode) {
        int result = 0;
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        notNull(craftStds, "Error id");
        isTrue(craftStds.size() > 0, "不存在该工艺");
        isTrue(craftStds.size()==ids.length, "未找到工序");

        for(int i = 0; i < craftStds.size(); i++) {
            CraftStd craftStd = craftStds.get(i);
            isTrue(!craftStd.getStatus().equals(Const.STD_STATUS_AUDIT)
                    && !craftStd.getStatus().equals(Const.STD_STATUS_READY), "该状态不允许删除");
            String used = getCraftStdInUsed(craftStd.getCraftStdCode());
            isTrue(StringUtils.isBlank(used), "工序标准已被引用，不允许删除！" + used);

            craftStd.setIsInvalid(true);
            craftStd.setUpdateTime(new Date());
            craftStd.setUpdateUser(userCode);

            result = craftStdDao.updateCraftStd(craftStd);
            if (result > 0) {
                operationLogService.addLog(moduleCode, IOperationLogService.ActionType.DELETE,
                        userCode, "删除工艺部件" + craftStd.getCraftStdCode());
            }

        }
        return result;
    }

    private void parseChange(String newList, String oldList, List<String> addList, List<String> delList) {
        if (newList != null) {
            newList += ",";
        } else {
            newList = ",";
        }

        if (oldList != null) {
            oldList += ",";
        } else {
            oldList = ",";
        }

        String[] newSplit = newList.split(",");
        String[] oldSplit = oldList.split(",");
        for (String newItm : newSplit) {
            boolean finded = false;
            for (String oldItm : oldSplit) {
                if (newItm.equals(oldItm)) {
                    finded = true;
                }
            }
            if (!finded) {
                addList.add(newItm);
            }
        }

        for (String oldItm : oldSplit) {
            boolean finded = false;
            for (String newItm : newSplit) {
                if (oldItm.equals(newItm)) {
                    finded = true;
                }
            }
            if (!finded) {
                delList.add(oldItm);
            }
        }
    }


    @Override
    public Integer updateCraftStd(CraftStd craftStd, CraftStd craftStdOld) {

        //
        if (craftStd.getCraftPartIds() != null) {
            boolean isPartShare = false;
            boolean isCategoryShare = false;
            String oldPartIds = craftStdOld.getCraftPartIds();
            String newPartIds = craftStd.getCraftPartIds();

            List<String> addPartIds = new ArrayList<String>();
            List<String> delPartIds = new ArrayList<String>();
            parseChange(newPartIds, oldPartIds, addPartIds, delPartIds);
            for (String delPartId : delPartIds) {
                craftStdPartDao.deleteCraftStdPart(craftStd.getId(), Integer.parseInt(delPartId));
            }

            if (addPartIds.size() > 0) {
                int lastCategoryId = -1;
                int lastPartId = -1;
                List<CraftStdPart> craftStdParts = new ArrayList<CraftStdPart>();

                for (String partId : addPartIds) {
                    CraftPart craftPart = craftPartDao.getCraftPartById(Integer.parseInt(partId));
                    if (craftPart == null)
                        throw new BaseException("未找到部件id:" + partId);

                    if(craftPart.getId() != null) {
                        if(lastPartId == -1) {
                            lastPartId = craftPart.getId();
                        } else {
                            if(lastPartId != craftPart.getId().intValue()) {
                                isPartShare = true;
                            }
                        }
                    }

                    if(craftPart.getCraftCategoryId() != null) {
                        if(lastCategoryId == -1) {
                            lastCategoryId = craftPart.getCraftCategoryId();
                        } else {
                            if(lastCategoryId != craftPart.getCraftCategoryId().intValue()) {
                                isCategoryShare = true;
                            }
                        }
                    }

                    CraftStdPart craftStdPart = new CraftStdPart();
                    craftStdPart.setCraftPartId(craftPart.getId());
                    craftStdPart.setCraftCategoryId(craftPart.getCraftCategoryId());
                    craftStdPart.setCraftStdId(craftStd.getId());
                    craftStdPart.setUpdateTime(new Date());
                    craftStdPart.setUpdateUser(craftStd.getUpdateUser());
                    craftStdParts.add(craftStdPart);
                }
                craftStdPartDao.addCraftStdParts(craftStdParts);

            }
            craftStd.setIsPartShare(isPartShare);
            craftStd.setIsCategoryShare(isCategoryShare);

        }


        List<String> addUrls = new ArrayList<String>();
        List<String> delUrls = new ArrayList<String>();

        String oldUrls;
        String newUrls;

        //效果图
        if (craftStd.getEffectPicUrls() != null) {
            oldUrls = craftStdOld.getEffectPicUrls();
            newUrls = craftStd.getEffectPicUrls();
            parseChange(newUrls, oldUrls, addUrls, delUrls);
            for (String url : delUrls) {
                deleteResource(craftStd.getId(), 20, url, craftStd.getUpdateUser());
            }
            for (String url : addUrls) {
                if (url.startsWith("smb:")) {
                    addResourcePath(craftStd.getId(), 20, url, craftStd.getUpdateUser());
                } else {
                    if (url.startsWith("http")) {
                        long keyId = -1;
                        List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, 20, url);
                        if (craftFiles != null && craftFiles.size() >= 1) {
                            for (CraftFile craftFile : craftFiles) {
                                if (craftFile.getKeyId() == null) {
                                    craftFile.setKeyId(craftStd.getId());
                                    craftFile.setUpdateTime(new Date());
                                    craftFile.setUpdateUser(craftStd.getUpdateUser());
                                    craftFileDao.updateCraftFile(craftFile);
                                }
                            }

                        }

                    }
                }
            }
            Boolean isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), 20);
            craftStd.setIsEffectPic(isResource);
        }
        //
        if (craftStd.getHandPicUrls() != null) {
            addUrls.clear();
            delUrls.clear();
            oldUrls = craftStdOld.getHandPicUrls();
            newUrls = craftStd.getHandPicUrls();
            parseChange(newUrls, oldUrls, addUrls, delUrls);
            for (String url : delUrls) {
                deleteResource(craftStd.getId(), 10, url, craftStd.getUpdateUser());
            }
            for (String url : addUrls) {
                if (url.startsWith("smb:")) {
                    addResourcePath(craftStd.getId(), 10, url, craftStd.getUpdateUser());
                } else {
                    if (url.startsWith("http")) {
                        long keyId = -1;
                        List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, 10, url);
                        if (craftFiles != null && craftFiles.size() >= 1) {
                            for (CraftFile craftFile : craftFiles) {
                                if (craftFile.getKeyId() == null) {
                                    craftFile.setKeyId(craftStd.getId());
                                    craftFile.setUpdateTime(new Date());
                                    craftFile.setUpdateUser(craftStd.getUpdateUser());
                                    craftFileDao.updateCraftFile(craftFile);
                                }
                            }

                        }

                    }
                }
            }
            Boolean isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), 10);
            craftStd.setIsHandPic(isResource);
        }

        if (craftStd.getVideoUrls() != null) {
            addUrls.clear();
            delUrls.clear();
            oldUrls = craftStdOld.getVideoUrls();
            newUrls = craftStd.getVideoUrls();
            parseChange(newUrls, oldUrls, addUrls, delUrls);
            for (String url : delUrls) {
                deleteResource(craftStd.getId(), 30, url, craftStd.getUpdateUser());
            }
            for (String url : addUrls) {
                if (url.startsWith("smb:")) {
                    addResourcePath(craftStd.getId(), 30, url, craftStd.getUpdateUser());
                } else {
                    if (url.startsWith("http")) {
                        long keyId = -1;
                        List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, 30, url);
                        if (craftFiles != null && craftFiles.size() >= 1) {
                            for (CraftFile craftFile : craftFiles) {
                                if (craftFile.getKeyId() == null) {
                                    craftFile.setKeyId(craftStd.getId());
                                    craftFile.setUpdateTime(new Date());
                                    craftFile.setUpdateUser(craftStd.getUpdateUser());
                                    craftFileDao.updateCraftFile(craftFile);
                                }
                            }

                        }

                    }
                }
            }
            Boolean isResource = craftFileDao.isCraftResourceExist(craftStd.getId(), 30);
            craftStd.setIsVideo(isResource);
        }

        Integer ret = craftStdDao.updateCraftStd(craftStd);
        return ret;
    }

    @Override
    public Boolean isCraftStdExist(long id) {
        return craftStdDao.isCraftStdExist(id);
    }

    @Override
    public String getNewCraftStdCode(String moduleCode, List<String> craftCategoryCodes, List<String> craftPartCodes, String makeCode) throws Exception {
        if (craftCategoryCodes == null || craftCategoryCodes.size() < 1) {
            throw new BaseException("工艺品类代码不正确");
        }
        if (craftPartCodes == null || craftPartCodes.size() < 1) {
            throw new BaseException("工艺部件代码不正确");
        }
        if (makeCode == null || makeCode.length() < 2) {
            throw new BaseException("做工代码长度不正确");
        }

        String preStr = "J";
        if (craftCategoryCodes.size() == 1) {
            preStr = preStr + craftCategoryCodes.get(0).substring(0, 1);
        } else {
            preStr = preStr + "T";
        }

        if (craftPartCodes.size() == 1) {
            //前两位改成后两位
            //preStr = preStr + craftPartCodes.get(0).substring(0, 2);
            String partCode = craftPartCodes.get(0);
            preStr = preStr + partCode.substring(partCode.length() - 2, partCode.length());
        } else {
            preStr = preStr + "TY";
        }

        preStr = preStr + makeCode.substring(0, 2);
        return maxCodeService.getNextSerialNo(moduleCode, preStr, 5, true);
    }




    @Override
    public List<CraftStd> getCraftStdById(long[] ids) {
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        for(CraftStd std: craftStds) {
            std.setHandPicThumbUrls(getThumbnailUrls(std.getHandPicUrls()));
            std.setEffectPicThumbUrls(getThumbnailUrls(std.getEffectPicUrls()));
        }
        return craftStds;
    }

    private String getThumbnailUrls(String picUrls) {
        if(picUrls == null || picUrls.isEmpty() || "null".equals(picUrls)) {
            return null;
        }

        String result = "";
        String suffix = "_" + MAX_PIC_WIDTH + "_" + MAX_PIC_HEIGHT;
        String[] split = picUrls.split(",");
        for(String url: split) {
            int pos = url.lastIndexOf(".");
            String fileExt = "";
            if(pos >= 0) {
                fileExt = url.substring(pos);
                result += "," + url.substring(0,pos) + suffix + fileExt;
            } else {
                result += "," + url + suffix;

            }
        }

        if(result.length() > 0) {
            result = result.substring(1,result.length());
        }
        return result;
    }


    @Override
    public List<CraftStd> getCraftStdByCode(String[] craftStdCodes) {
        List<CraftStd> craftStds = craftStdDao.getCraftStdByCode(craftStdCodes);
        for(CraftStd std: craftStds) {
            std.setHandPicThumbUrls(getThumbnailUrls(std.getHandPicUrls()));
            std.setEffectPicThumbUrls(getThumbnailUrls(std.getEffectPicUrls()));
        }
        return craftStds;
    }

    @Override
    public List<CraftStd> getCraftStdByPage(String keywords, Date beginDate, Date endDate, Long id) {
        List<CraftStd> craftStds = craftStdDao.getCraftStdByPage(keywords, beginDate, endDate, id);
        return craftStds;
    }


    private String path2DestUrl(String destPath) {
        String httpString = StaticDataCache.getInstance().getValue(Const.HTTP_OUT_PREF, "");
        return httpString + destPath;

    }


    @Override
    public List<ShareFileInfo> getShareFileInfo(String parentPath, boolean isPic, boolean fetchChild) {

        String remotePhotoUrl = "smb://" + getShareBasePath(); // + File.separator;
        if (parentPath != null && parentPath != "") {
            remotePhotoUrl = parentPath;
        }

        String remotePathKey = Boolean.toString(isPic) + "|" + remotePhotoUrl;
        if (mapSharePath.get(remotePathKey) != null) {
            //刷新下次更新
            Thread thrd = new SharePathThread(remotePhotoUrl, isPic, mapSharePath, this);
            thrd.start();
            return mapSharePath.get(remotePathKey);
        }


        List<ShareFileInfo> shareFileInfos = new ArrayList<ShareFileInfo>();
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", getShareAcc(), getSharePwd());
            SmbFile remoteFile = new SmbFile(remotePhotoUrl, auth);
            getShareFileInfoSimple(remoteFile, shareFileInfos, auth, remotePhotoUrl, isPic);
            if (fetchChild) {
                for (ShareFileInfo shareFileInfo : shareFileInfos) {
                    if (!shareFileInfo.getIsFile()) {
                        //如果是文件夹
                        Thread thrd = new SharePathThread(shareFileInfo.getFullPath(), isPic, mapSharePath, this);
                        thrd.start();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return shareFileInfos;
    }


    private void getShareFileInfoSimple(SmbFile remoteFile, List<ShareFileInfo> shareFileInfos, NtlmPasswordAuthentication auth, String parentPath, boolean isPic) throws Exception {
        SmbFile files[] = remoteFile.listFiles();
        if (null != files && files.length > 0) {
            for (SmbFile file : files) {
                String fileName = file.getName();
                if (fileName.endsWith("/")) {
                    ShareFileInfo fileInfo = new ShareFileInfo();
                    fileInfo.setFullPath(file.getPath());
                    String fullPathEncode = URLEncoder.encode(file.getPath(), "utf-8").replaceAll("\\+", "%20")
                            .replaceAll("\\!", "%21")
                            .replaceAll("\\'", "%27")
                            .replaceAll("\\(", "%28")
                            .replaceAll("\\)", "%29")
                            .replaceAll("\\~", "%7E");
                    fileInfo.setFullPathEncode(fullPathEncode);
                    fileInfo.setFileName(fileName);
                    fileInfo.setIsFile(false);
                    fileInfo.setChildrens(null);
                    shareFileInfos.add(fileInfo);
                } else {
                    String fileExt = "";
                    int k = fileName.lastIndexOf(".");
                    if(k >= 0) {
                        fileExt = fileName.substring(k);
                    }

                    boolean isAdd = false;
                    if (isPic) {
                        if (fileExt.equalsIgnoreCase(".jpg") ||
                                fileExt.equalsIgnoreCase(".jpeg") ||
                                fileExt.equalsIgnoreCase(".bmp") ||
                                fileExt.equalsIgnoreCase(".png")) {
                            isAdd = true;
                        }
                    } else {
                        if (fileExt.equalsIgnoreCase(".mp4")) {
                            isAdd = true;
                        }
                    }

                    if (isAdd) {
                        ShareFileInfo fileInfo = new ShareFileInfo();
                        fileInfo.setFullPath(file.getPath());
                        String fullPathEncode = URLEncoder.encode(file.getPath(), "utf-8").replaceAll("\\+", "%20")
                                .replaceAll("\\!", "%21")
                                .replaceAll("\\'", "%27")
                                .replaceAll("\\(", "%28")
                                .replaceAll("\\)", "%29")
                                .replaceAll("\\~", "%7E");
                        fileInfo.setFullPathEncode(fullPathEncode);
                        fileInfo.setFileName(fileName);
                        fileInfo.setIsFile(true);
                        fileInfo.setChildrens(null);
                        shareFileInfos.add(fileInfo);
                    }
                }
            }
        }
    }


    private void getShareFileInfos(SmbFile remoteFile, List<ShareFileInfo> shareFileInfos, NtlmPasswordAuthentication auth, String parentPath) throws Exception {

        SmbFile files[] = remoteFile.listFiles();
        if (null != files && files.length > 0) {
            for (SmbFile file : files) {
                if (file.isFile()) {
                    ShareFileInfo fileInfo = new ShareFileInfo();
                    fileInfo.setFullPath(file.getPath());
                    String fullPathEncode = URLEncoder.encode(file.getPath(), "utf-8").replaceAll("\\+", "%20")
                            .replaceAll("\\!", "%21")
                            .replaceAll("\\'", "%27")
                            .replaceAll("\\(", "%28")
                            .replaceAll("\\)", "%29")
                            .replaceAll("\\~", "%7E");
                    fileInfo.setFullPathEncode(fullPathEncode);
                    fileInfo.setFileName(file.getName());
                    fileInfo.setIsFile(true);
                    fileInfo.setChildrens(null);
                } else if (file.isDirectory()) {
                    ShareFileInfo fileInfo = new ShareFileInfo();
                    fileInfo.setFullPath(file.getPath());
                    String fullPathEncode = URLEncoder.encode(file.getPath(), "utf-8").replaceAll("\\+", "%20")
                            .replaceAll("\\!", "%21")
                            .replaceAll("\\'", "%27")
                            .replaceAll("\\(", "%28")
                            .replaceAll("\\)", "%29")
                            .replaceAll("\\~", "%7E");
                    fileInfo.setFullPathEncode(fullPathEncode);
                    fileInfo.setFileName(file.getName());
                    fileInfo.setIsFile(false);

                    List<ShareFileInfo> shareFileInfoList = new ArrayList<ShareFileInfo>();
                    try {
                        getShareFileInfos(file, shareFileInfoList, auth, fileInfo.getFullPath());
                    } catch (Exception e) {
                    }
                    fileInfo.setChildrens(shareFileInfoList);
                }
            }
        }
    }


//    private CopyFileThread shareFileUpload(Integer resourceType, String sharePath, String basePath, String destPath) {
//
//        String oldSharePath = sharePath;
//        if (basePath != "" && !basePath.endsWith(File.separator)) {
//            basePath = basePath + File.separator;
//        }
//        if (!destPath.endsWith(File.separator)) {
//            destPath = destPath + File.separator;
//        }
//
//        //去掉win的盘符
////        if (sharePath.startsWith("\\\\")) {
////            //去掉\\
////            sharePath = sharePath.substring(2, sharePath.length());
////            sharePath = sharePath.replace("\\", "/");
////            sharePath = "smb://" + sharePath;
////        } else {
////            //去掉盘符 Y:\
////            sharePath = sharePath.substring(3, sharePath.length());
////            sharePath = sharePath.replace("\\", "/");
////            sharePath = "smb://" + shareBasePath + sharePath;
////        }
//
//        String fileExt = oldSharePath.substring(oldSharePath.lastIndexOf("."), oldSharePath.length());
//        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + fileExt;
//
//        CopyFileThread copyFile = new CopyFileThread(resourceType, basePath + destPath + fileName, sharePath, this, destPath + fileName);
//        return copyFile;
//    }

    public String path2FullPath(String path) {
        String uploadBase = StaticDataCache.getInstance().getValue(Const.UPLOAD_PATH, "");
        if (!uploadBase.endsWith(File.separator)) {
            uploadBase = uploadBase + File.separator;
        }
        return uploadBase + path;
    }


    public String addResourceFile(int resourceType, MultipartFile file) {
        String basePath = StaticDataCache.getInstance().getValue(Const.UPLOAD_PATH, "");
        String path = "";
        String destUrl = "";

        switch (resourceType) {
            case 10:
                path = StaticDataCache.getInstance().getValue(Const.PATH_HAND_PIC, "");
                break;

            case 20:
                path = StaticDataCache.getInstance().getValue(Const.PATH_EFFECT_PIC, "");
                break;

            case 30:
                path = StaticDataCache.getInstance().getValue(Const.PATH_STD_VIDEO, "");
                break;

            default:
                Assert.isTrue(true, "非法资源id");

        }
        destUrl = saveToFile(basePath, path, file);
        return path2DestUrl(destUrl);
    }

    @Override
    public String addResourceSimple(int resourceType, MultipartFile file, String userCode) {
        String destUrl = "";

        try {
            String fileName = file.getOriginalFilename();
            String fileExt = "";
            int pos = fileName.lastIndexOf(".");
            if(pos >= 0) {
                fileExt = fileName.substring(pos + 1);
            }
            destUrl = uploadFile(file.getBytes(), fileExt);
            if(StringUtils.isNotBlank(destUrl)) {
                if(destUrl.endsWith("/null")) {
                    throw new BaseException("返回错误的地址：" + destUrl);
                }
                destUrl = RightTrimHide(destUrl);
            }

            CraftFile craftFile = new CraftFile();
            craftFile.setResourceType(resourceType);
            craftFile.setRemark(fileName);
            craftFile.setFileUrl(destUrl);
            craftFile.setUpdateTime(new Date());
            craftFile.setUpdateUser(userCode);
            Integer ret = craftFileDao.addCraftFile(craftFile);
            return destUrl;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new BaseException("上传失败");
        }
    }


    @Override
    public String addResource(Long id, int resourceType, MultipartFile file, String userCode) {
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        isTrue(craftStds != null && !craftStds.isEmpty(), "非法的id");
        CraftStd craftStd = craftStds.get(0);
        isTrue(craftStd.getStatus() == null || STD_STATUS_NEW.equals(craftStd.getStatus()) || STD_STATUS_BACK.equals(craftStd.getStatus()), "该状态不允许添加资源");
        String path = "";
        String destUrl = "";

        try {
            String fileName = file.getOriginalFilename();
            String fileExt = "";
            int pos = fileName.lastIndexOf(".");
            if(pos >= 0) {
                fileExt = fileName.substring(pos + 1);
            }
            destUrl = uploadFile(file.getBytes(), fileExt);
            if(StringUtils.isNotBlank(destUrl)) {
                if(destUrl.endsWith("/null")) {
                    throw new BaseException("返回错误的地址：" + destUrl);
                }
                destUrl = RightTrimHide(destUrl);
            }



            CraftStd craftStdUpdate = new CraftStd();
            craftStdUpdate.setId(id);
            String basePath = StaticDataCache.getInstance().getValue(Const.UPLOAD_PATH, "");

            CraftFile craftFile = new CraftFile();
            craftFile.setResourceType(resourceType);
            craftFile.setKeyId(id);
            craftFile.setRemark(fileName);

            switch (resourceType) {
                case Const.RES_TYPE_HAND_IMG:
    //                path = StaticDataCache.getInstance().getValue(Const.PATH_HAND_PIC, "");
    //                destUrl = saveToFile(basePath, path, file);
                    craftStdUpdate.setIsHandPic(true);
                    break;

                case Const.RES_TYPE_EFFECT_IMG:
    //                path = StaticDataCache.getInstance().getValue(Const.PATH_EFFECT_PIC, "");
    //                destUrl = saveToFile(basePath, path, file);
                    craftStdUpdate.setIsEffectPic(true);
                    break;

                case Const.RES_TYPE_STD_VIDEO:
    //                path = StaticDataCache.getInstance().getValue(Const.PATH_STD_VIDEO, "");
    //                destUrl = saveToFile(basePath, path, file);
                    craftStdUpdate.setIsVideo(true);
                    break;
                default:
                    Assert.isTrue(true, "非法资源id");

            }

            craftStdUpdate.setUpdateUser(userCode);
            craftStdUpdate.setUpdateTime(new Date());
            Integer ret = craftStdDao.updateCraftStd(craftStdUpdate);


            //destUrl = path2DestUrl(destUrl);
            craftFile.setFileUrl(destUrl);
            craftFile.setUpdateUser(userCode);
            craftFile.setUpdateTime(craftStdUpdate.getUpdateTime());
            ret = craftFileDao.addCraftFile(craftFile);
            return destUrl;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new BaseException("上传失败");
        }
    }

    @Override
    public String addResourcePathSimple(int resourceType, String sharePath, String userCode) {
        String basePath = StaticDataCache.getInstance().getValue(Const.UPLOAD_PATH, "");
        String destUrl = "";
        CraftFile craftFile = new CraftFile();
        craftFile.setResourceType(resourceType);

        CopyFileThread thrd = null;


        String path = "";
        switch (resourceType) {
            case 10:
                path = StaticDataCache.getInstance().getValue(Const.PATH_HAND_PIC, "");
                break;

            case 20:
                path = StaticDataCache.getInstance().getValue(Const.PATH_EFFECT_PIC, "");
                break;

            case 30:
                path = StaticDataCache.getInstance().getValue(Const.PATH_STD_VIDEO, "");
                break;

            default:
                Assert.isTrue(true, "非法资源id");

        }

        thrd = CopyFileThread.shareFileUpload(this,resourceType, sharePath, basePath, path);
        destUrl = path2DestUrl(thrd.getRelativePath());
        craftFile.setFileUrl(destUrl);
        craftFile.setUpdateUser(userCode);
        craftFile.setUpdateTime(new Date());
        Integer ret = craftFileDao.addCraftFile(craftFile);
        if (thrd != null && ret != null && ret >= 1) {
            thrd.setCraftFileId(craftFile.getId());
            thrd.start();
        }

        return destUrl;
    }


    @Override
    public String addResourcePath(long id, int resourceType, String sharePath, String userCode) {
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        isTrue(craftStds != null && !craftStds.isEmpty(), "非法的id");
        CraftStd craftStd = craftStds.get(0);
        isTrue(craftStd.getStatus() == null || STD_STATUS_NEW.equals(craftStd.getStatus()) || STD_STATUS_BACK.equals(craftStd.getStatus()), "该状态不允许修改资源");

        String basePath = StaticDataCache.getInstance().getValue(Const.UPLOAD_PATH, "");
        String destUrl = "";
        CraftStd craftStdUpdate = new CraftStd();
        craftStdUpdate.setId(id);
        craftStdUpdate.setUpdateUser(userCode);
        craftStdUpdate.setUpdateTime(new Date());

        CraftFile craftFile = new CraftFile();
        craftFile.setKeyId(id);
        craftFile.setResourceType(resourceType);
        craftFile.setUpdateUser(userCode);
        craftFile.setUpdateTime(craftStdUpdate.getUpdateTime());

        CopyFileThread thrd = null;


        String path = "";
        switch (resourceType) {
            case 10:
                path = StaticDataCache.getInstance().getValue(Const.PATH_HAND_PIC, "");
                thrd = CopyFileThread.shareFileUpload(this, resourceType, sharePath, basePath, path);
                destUrl = path2DestUrl(thrd.getRelativePath());
                craftStdUpdate.setIsHandPic(true);
                break;


            case 20:
                path = StaticDataCache.getInstance().getValue(Const.PATH_EFFECT_PIC, "");
                thrd = CopyFileThread.shareFileUpload(this, resourceType, sharePath, basePath, path);
                destUrl = path2DestUrl(thrd.getRelativePath());
                craftStdUpdate.setIsEffectPic(true);
                break;


            case 30:
                path = StaticDataCache.getInstance().getValue(Const.PATH_STD_VIDEO, "");
                thrd = CopyFileThread.shareFileUpload(this, resourceType, sharePath, basePath, path);
                destUrl = path2DestUrl(thrd.getRelativePath());
                craftStdUpdate.setIsVideo(true);
                break;
            default:
                Assert.isTrue(true, "非法资源id");

        }
        craftFile.setFileUrl(destUrl);
        Integer ret = craftFileDao.addCraftFile(craftFile);
        if (thrd != null && ret != null && ret >= 1) {
            thrd.setCraftFileId(craftFile.getId());
            thrd.start();
            ret = craftStdDao.updateCraftStd(craftStdUpdate);
        }
        return destUrl;
    }

    @Override
    public Integer deleteResourceSimple(int resourceType, String fileUrl, String userCode) {
        long id = -1;
        int ret = 1;

        List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(id, null, resourceType, fileUrl);

        if(craftFiles != null && craftFiles.size() > 0) {
            for (CraftFile craftFile : craftFiles) {
                craftFileDao.deleteCraftFile(craftFile.getId());
                deleteFileByUrl(fileUrl);
            }
            ret = craftFiles.size();
        }

        //删除文件
//        if (!fileUrl.startsWith("smb:")) {
//            boolean multiRefFile = craftFileDao.isCraftFileExist(fileUrl);
//            if (!multiRefFile) {
//                String destFullPath = path2FullPath(fileUrl);
//                File file = new File(destFullPath);
//                if (file.exists()) {
//                    file.delete();
//                }
//
//            }
//        }
        return ret;
    }


    @Override
    public Integer deleteResource(long id, int resourceType, String fileUrl, String userCode) {
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        notNull(craftStds, "错误的工序id");
        isTrue(craftStds.size() > 0, "错误的工序id");
        CraftStd craftStd = craftStds.get(0);
        String destPath = fileUrl;

        CraftStd craftStdUpdate = new CraftStd();
        craftStdUpdate.setId(id);

        List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(id, null, resourceType, fileUrl);
        isTrue(craftFiles != null && craftFiles.size() >= 0, "未找到资源");

        for (CraftFile craftFile : craftFiles) {
            craftFileDao.deleteCraftFile(craftFile.getId());
            deleteFileByUrl(fileUrl);
        }
        Boolean isResource = craftFileDao.isCraftResourceExist(id, resourceType);

        switch (resourceType) {
            case Const.RES_TYPE_HAND_IMG:
                craftStdUpdate.setIsHandPic(isResource);
                break;

            case Const.RES_TYPE_EFFECT_IMG:
                craftStdUpdate.setIsEffectPic(isResource);
                break;

            case Const.RES_TYPE_STD_VIDEO:
                craftStdUpdate.setIsVideo(isResource);
                break;
        }



        //删除文件
//        if (destPath != null && destPath != "") {
//            boolean multiRefFile = craftFileDao.isCraftFileExist(destPath);
//            if (!multiRefFile) {
//                String destFullPath = path2FullPath(destPath);
//                File file = new File(destFullPath);
//                if (file.exists()) {
//                    file.delete();
//                }
//
//            }
//        }


        //更新修改时间
        craftStd.setUpdateUser(userCode);
        craftStd.setUpdateTime(new Date());
        Integer ret = craftStdDao.updateCraftStd(craftStdUpdate);
        return ret;
    }


    @Override
    public Result<Integer> commitCraftStd(long id, boolean isCommit, String user, String rejectReason) {
        CraftStd craftStd = null;
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        notNull(craftStds, "非法工序id");
        isTrue(!craftStds.isEmpty(), "非法工序id");


        craftStd = craftStds.get(0);
        if (isCommit) {
            if (!(STD_STATUS_NEW.equals(craftStd.getStatus()) || STD_STATUS_BACK.equals(craftStd.getStatus()) || craftStd.getStatus() == null)) {
                Assert.isTrue(false, "当前状态不允许提交");
            }
            //isTrue(craftStd.getIsEffectPic() != null && craftStd.getIsEffectPic(), "未设置效果图");
            isTrue(craftStd.getIsHandPic() != null && craftStd.getIsHandPic(), "未设置线稿图");
            //没有视频也可以提交
            //isTrue(craftStd.getIsVideo() != null && craftStd.getIsVideo(), "未设置视频");
            notNull(craftStd.getMakeTypeId(), "做工类型id不能为空");
            notBlank(craftStd.getMakeDesc(), "做工说明不能为空");
            notBlank(craftStd.getRequireQuality(), "品质说明不能为空");

            List<CraftStdPart> craftStdParts = craftStdPartDao.getCraftStdPartByPage(null,null,null,craftStd.getId(), null);
            notNull(craftStdParts, "工艺部件不能为空");

            craftStd.setStatus(Const.STD_STATUS_READY);
            craftStd.setCommitUser(user);
            craftStd.setCommitTime(new Date());
        } else {
            if (craftStd.getStatus() == null || !craftStd.getStatus().equals(Const.STD_STATUS_READY)) {
                Assert.isTrue(false, "当前状态不允许撤回");
            }
            if(rejectReason != null) {
                craftStd.setRejectReason(rejectReason);
                craftStd.setStatus(STD_STATUS_BACK);
            } else {
                craftStd.setStatus(STD_STATUS_NEW);
            }
        }
        craftStd.setUpdateTime(new Date());
        craftStd.setUpdateUser(user);
        Integer ret = craftStdDao.updateCraftStd(craftStd);

        //发送通知消息
        if (ret != null && ret >= 1) {

            List<NotifyMsg> notifyMsgList = new ArrayList<NotifyMsg>();
            String msgText;
            if (isCommit) {
                //提交消息
                msgText = String.format("标准工序%s已提交,请审核!", craftStd.getCraftStdCode());
                List<NotifyUser> notifyUsers = notifyUserDao.getNotifyUserByPage(null, null, null,
                        null, Const.NOTIFY_MSG_COMMIT, craftStd.getId());
                for (NotifyUser notifyUser : notifyUsers) {
                    NotifyMsg notifyMsg = new NotifyMsg();
                    notifyMsg.setReadUser(notifyUser.getUserCode());
                    notifyMsg.setIsRead(false);
                    notifyMsg.setCreateTime(new Date());
                    notifyMsg.setCreateUser(user);
                    notifyMsg.setMsgKeyId(craftStd.getId());
                    notifyMsg.setMsgType(Const.NOTIFY_MSG_COMMIT);
                    notifyMsg.setMsgTxt(msgText);
                    notifyMsgList.add(notifyMsg);
                }
            } else {
                //拒绝消息
                String commitUser = craftStd.getCommitUser();
                if (commitUser == null) commitUser = "";
                if (!commitUser.equals(user)) {
                    if(rejectReason != null) {
                        msgText = String.format("标准工序%s审核不通过,请修改!", craftStd.getCraftStdCode());
                    } else {
                        msgText = String.format("标准工序%s已撤销,请修改!", craftStd.getCraftStdCode());
                    }
                    List<NotifyUser> notifyUsers = notifyUserDao.getNotifyUserByPage(null, null, null,
                            null, Const.NOTIFY_MSG_WITHDRAW, craftStd.getId());
                    for (NotifyUser notifyUser : notifyUsers) {
                        NotifyMsg notifyMsg = new NotifyMsg();
                        notifyMsg.setReadUser(notifyUser.getUserCode());
                        notifyMsg.setIsRead(false);
                        notifyMsg.setCreateTime(new Date());
                        notifyMsg.setCreateUser(user);
                        notifyMsg.setMsgKeyId(craftStd.getId());
                        notifyMsg.setMsgType(Const.NOTIFY_MSG_WITHDRAW);
                        notifyMsg.setMsgTxt(msgText);
                        notifyMsgList.add(notifyMsg);
                    }

                }
            }

            if (!notifyMsgList.isEmpty()) {
                notifyMsgDao.addNotifyMsgs(notifyMsgList);
            }
        }
        return Result.ok(ret);
    }

    @Override
    public Result<Integer> auditCraftStd(long id, boolean isAudit, String user) {
        long[] ids = new long[]{id};
        List<CraftStd> craftStds = craftStdDao.getCraftStdById(ids);
        notNull(craftStds, "非法工序id");
        isTrue(!craftStds.isEmpty(), "非法工序id");


        CraftStd craftStd = craftStds.get(0);
        if (isAudit) {
            if (craftStd.getStatus() == null || !craftStd.getStatus().equals(Const.STD_STATUS_READY)) {
                Assert.isTrue(false, "当前状态不允许审核");
            }
            craftStd.setStatus(Const.STD_STATUS_AUDIT);
            craftStd.setOkTime(new Date());
            craftStd.setOkUser(user);

        } else {
            if (craftStd.getStatus() == null || !craftStd.getStatus().equals(Const.STD_STATUS_AUDIT)) {
                Assert.isTrue(false, "当前状态不允许反审核");
            }
            craftStd.setStatus(Const.STD_STATUS_NEW);
            craftStd.setUpdateTime(new Date());
            craftStd.setUpdateUser(user);

        }
        Integer ret = craftStdDao.updateCraftStd(craftStd);
        return Result.ok(ret);
    }

    @Override
    public List<CraftStd> getCraftStdByCondition(String craftStdCode,
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
    ) {
        return craftStdDao.getCraftStdByCondition(craftStdCode, craftStdName,
                styleCode, requireQuality, makeDesc, status,
                createTimeBeginDate, createTimeEndDate, okTimeBeginDate, okTimeEndDate,
                updateTimeBeginDate, updateTimeEndDate, remark, isVideo, isPic, commitTimeBeginDate,
                commitTimeEndDate, user, extractTimeBeginDate, extractTimeEndDate, craftCategoryId, craftPartId,
                craftCategoryCode, craftPartCode);
    }

    @Override
    public void clearResource(Long id, Integer resourceType, String error) {
        long[] ids = new long[]{id};
        List<CraftFile> craftFiles = craftFileDao.getCraftFileById(ids);
        if (craftFiles == null || craftFiles.isEmpty()) {
            return;
        }

        CraftFile craftFile = craftFiles.get(0);
        craftFile.setIsInvalid(true);
        craftFile.setRemark(error);
        craftFile.setUpdateUser("PMO");
        craftFile.setUpdateTime(new Date());
        craftFileDao.updateCraftFile(craftFile);


        if (craftFile.getKeyId() != null) {
            CraftStd craftStdUpdate = new CraftStd();
            craftStdUpdate.setId(craftFile.getKeyId());

            //值为null表示不更新
            craftStdUpdate.setIsInvalid(null);
            craftStdUpdate.setIsEffectPic(null);
            craftStdUpdate.setIsHandPic(null);
            craftStdUpdate.setIsVideo(null);

            boolean isResource = craftFileDao.isCraftResourceExist(craftFile.getKeyId(), resourceType);


            switch (resourceType) {
                case 10:
                    craftStdUpdate.setIsHandPic(isResource);
                    break;

                case 20:
                    craftStdUpdate.setIsEffectPic(isResource);
                    break;

                case 30:
                    craftStdUpdate.setIsVideo(isResource);
                    break;
            }
            craftStdDao.updateCraftStd(craftStdUpdate);
        }
    }

    @Override
    public CraftStdStatistic getCraftStdStatistic() {
        return craftStdDao.getCraftStdStatistic();
    }


    @Override
    public List<CraftStd> getNoVideoCraftStd(String craftStdCode, String craftStdName) {
        return craftStdDao.getNoVideoCraftStd(craftStdCode, craftStdName);
    }

    @Override
    public List<CraftStd> getNoHandleImageCraftStd(String craftStdCode) {
        return craftStdDao.getNoHandleImageCraftStd(craftStdCode);
    }

    @Override
    public int getNoVideoCraftStdCount() {
        return craftStdDao.getNoVideoCraftStdCount();
    }

    @Override
    public int addCraftStdHandleImage(Long craftStdId, String fileUrl, String fileName) {
        CraftStd craftStd = new CraftStd();
        craftStd.setId(craftStdId);
        craftStd.setIsHandPic(true);
        craftStdDao.updateCraftStd(craftStd);

        CraftFile craftFile = new CraftFile();
        craftFile.setRemark(fileName);
        craftFile.setFileUrl(fileUrl);
        craftFile.setKeyId(craftStdId);
        craftFile.setUpdateTime(new Date());
        craftFile.setUpdateUser("auto");
        craftFile.setResourceType(Const.RES_TYPE_HAND_IMG);
        return craftFileDao.addCraftFile(craftFile);
    }

    @Override
    public boolean isCraftStdNameExists(String craftStdName, Long craftStdId) {
        return craftCategoryDao.isCraftStdNameExists(craftStdName,craftStdId);
    }

    @Override
    public String getCraftStdInUsed(String craftStdCode) {

        return craftStdDao.getCraftStdInUsed(craftStdCode);
    }


    @Override
    public int addCraftStdVideo(Long craftStdId, String fileUrl, String fileName) {
        CraftStd craftStd = new CraftStd();
        craftStd.setId(craftStdId);
        craftStd.setIsVideo(true);
        craftStdDao.updateCraftStd(craftStd);

        CraftFile craftFile = new CraftFile();
        craftFile.setRemark(fileName);
        craftFile.setFileUrl(fileUrl);
        craftFile.setKeyId(craftStdId);
        craftFile.setUpdateTime(new Date());
        craftFile.setUpdateUser("auto");
        craftFile.setResourceType(Const.RES_TYPE_STD_VIDEO);
        return craftFileDao.addCraftFile(craftFile);
    }

//    public class CopyFileThread extends Thread {
//        private String shareFile;
//        private String destFile;
//        private CraftStdService craftStdService;
//        private Integer resourceType;
//        //关联路径
//        private String relativePath;
//        private Long craftFileId;
//
//        public String getRelativePath() {
//            return this.relativePath;
//        }
//
//        public void setCraftFileId(Long craftFileId) {
//            this.craftFileId = craftFileId;
//        }
//
//        public CopyFileThread(Integer resourceType, String destFile, String shareFile, CraftStdService craftStdService, String relativePath) {
//            super(destFile);
//            this.resourceType = resourceType;
//            this.shareFile = shareFile;
//            this.destFile = destFile;
//            this.craftStdService = craftStdService;
//            this.relativePath = relativePath;
//        }
//
//        @Override
//        public void run() {
//            try {
//                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", craftStdService.getShareAcc(), craftStdService.getSharePwd());
//                SmbFile remoteFile = new SmbFile(shareFile, auth);
//                if (!remoteFile.exists()) {
//                    throw new BaseException("文件不存在！" + shareFile);
//                }
//                if (!remoteFile.isFile()) {
//                    throw new BaseException("该路径不是文件！" + shareFile);
//                }
//
//
//                File dest = new File(destFile);
//                if (!dest.getParentFile().exists()) {
//                    if (!dest.getParentFile().mkdir()) {
//                        throw new BaseException("创建目录失败" + destFile);
//                    }
//                }
//
//                FileUtils.copyInputStreamToFile(remoteFile.getInputStream(), dest);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                craftStdService.clearResource(craftFileId, resourceType, e.getMessage());
//            }
//        }
//
//    }

    @Override
    public List<CraftStd> getCraftStdByLikeParam(Map<String,Object> param) {
        return craftStdDao.getCraftStdByLikeParam(param);
    }

    public class SharePathThread extends Thread {
        private String sharePath;
        private Map<String, List<ShareFileInfo>> mapSharePath;
        private CraftStdService craftStdService;
        private boolean isPic;

        public SharePathThread(String sharePath, boolean isPic, Map<String, List<ShareFileInfo>> mapSharePath, CraftStdService craftStdService) {
            super(sharePath);
            this.sharePath = sharePath;
            this.mapSharePath = mapSharePath;
            this.craftStdService = craftStdService;
            this.isPic = isPic;
        }

        @Override
        public void run() {
            try {
                List<ShareFileInfo> shareFileInfos = craftStdService.getShareFileInfo(sharePath, isPic, false);
                mapSharePath.put(Boolean.toString(isPic) + "|" + sharePath, shareFileInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}
