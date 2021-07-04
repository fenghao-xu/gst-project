package com.ylzs.service.craftstd.impl;

import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.util.BaseException;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.PinyinUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.craftstd.CraftFileDao;
import com.ylzs.dao.craftstd.MachineDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.entity.craftstd.CraftFile;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.craftstd.Machine;
import com.ylzs.service.craftstd.ICopyFileCallback;
import com.ylzs.service.craftstd.IMachineService;
import com.ylzs.service.interfaceOutput.IInterfaceOutputService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Const.RES_TYPE_MACHINE_IMG;
import static com.ylzs.common.util.FastDFSUtil.deleteFileByUrl;
import static com.ylzs.common.util.FastDFSUtil.uploadFile;
import static com.ylzs.common.util.StringUtils.RightTrimHide;


/**
 * 说明：机器服务实现
 *
 * @author lyq
 * 2019-09-30 10:37
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MachineService implements IMachineService, ICopyFileCallback {
    private static final Logger logger = LoggerFactory.getLogger(MachineService.class);

    @Resource
    private MachineDao machineDao;
    @Resource
    private CraftFileDao craftFileDao;
    @Resource
    private DictionaryDao dictionaryDao;

    @Resource
    private IInterfaceOutputService interfaceOutputService;

    @Resource
    ThreadPoolTaskExecutor taskExecutor;


    @Override
    public Integer addMachine(Machine machine) {
        machine.setPyHeadChar(PinyinUtil.getAllFirstLetter(machine.getMachineNameCn()));
        if(StringUtils.isEmpty(machine.getWorkTypeCode()) && !ObjectUtils.isNull(machine.getWorkTypeId())) {
            List<Dictionary> dics = dictionaryDao.getDictionaryById(machine.getWorkTypeId());
            if (ObjectUtils.isNotEmptyList(dics)) {
                machine.setWorkTypeCode(dics.get(0).getDicValue());
                machine.setWorkTypeName(dics.get(0).getValueDesc());
            }
        }
        return machineDao.addMachine(machine);
    }

    @Override
    public Machine getByCodeAndMakeTypeCode(String code,String makeTypeCode) {
        return machineDao.getByCodeAndMakeTypeCode(code,makeTypeCode);
    }

    @Override
    public List<Machine> getAllMachine() {
        return machineDao.getAllMachine();
    }

    @Override
    public Integer deleteMachine(Integer machineId, String userCode) {
        Integer ret = 0;
        Machine machine = new Machine();
        machine.setId(machineId);
        machine.setUpdateUser(userCode);
        machine.setUpdateTime(new Date());
        machine.setIsInvalid(true);
        ret = machineDao.updateMachine(machine);
        return ret;
    }

    private String path2DestUrl(String destPath) {
        String httpString = StaticDataCache.getInstance().getValue(Const.HTTP_OUT_PREF, "");
        return httpString + destPath;

    }

    @Override
    public void clearResource(Long id, Integer resourceType, String error) {

    }

    @Override
    public Integer updateMachine(Machine machine) {
        if(machine.getMachineNameCn() != null && machine.getMachineNameCn() != "") {
            machine.setPyHeadChar(PinyinUtil.getAllFirstLetter(machine.getMachineNameCn()));
        }
        if(machine.getPicUrl() != null && !machine.getPicUrl().isEmpty()) {
            Machine machineOld = machineDao.getByCodeAndWorkTypeId(machine.getMachineCode(),machine.getWorkTypeId());
            if(machineOld != null) {
                Long keyId = Long.valueOf(machineOld.getId());
                List<CraftFile> craftFiles = craftFileDao.getCraftFileByPage(keyId, null, RES_TYPE_MACHINE_IMG, null);
                for (CraftFile craftFile : craftFiles) {
                    if (!machine.getPicUrl().equalsIgnoreCase(craftFile.getFileUrl())) {
                        deleteFileByUrl(craftFile.getFileUrl());
                        craftFileDao.deleteCraftFile(craftFile.getId());
                    }
                }
                machine.setIsPic(true);
            }
        }

        if(StringUtils.isEmpty(machine.getWorkTypeCode()) && !ObjectUtils.isNull(machine.getWorkTypeId())) {
            List<Dictionary> dics = dictionaryDao.getDictionaryById(machine.getWorkTypeId());
            if (ObjectUtils.isNotEmptyList(dics)) {
                machine.setWorkTypeCode(dics.get(0).getDicValue());
                machine.setWorkTypeName(dics.get(0).getValueDesc());
            }
        }

        int ret = machineDao.updateMachine(machine);
        if(ret > 0) {
            //发送机器信息
            taskExecutor.execute(() -> {
                interfaceOutputService.sendMachines(machine.getMachineCode(), true);
            });
        }

        return ret;
    }

    @Override
    public List<Machine> getMachineByCode(String[] machineCodes) {
        return machineDao.getMachineByCode(machineCodes);
    }

    @Override
    public List<Machine> getMachineById(Integer[] machineIds) {
        return machineDao.getMachineById(machineIds);
    }

    @Override
    public List<Machine> getMachineByPage(String keywords, Date beginDate, Date endDate, Integer workTypeId, Integer deviceId) {
        return machineDao.getMachineByPage(keywords, beginDate, endDate, workTypeId, deviceId);
    }

    @Override
    public Long getStdCountByMachineCode(String machineCode) {
        return machineDao.getStdCountByMachineCode(machineCode);
    }

    @Override
    public String addResource(Machine machine,  MultipartFile file, String userCode) {
        try {
            String fileName = file.getOriginalFilename();
            String fileExt = "";
            int pos = fileName.lastIndexOf(".");
            if(pos >= 0) {
                fileExt = fileName.substring(pos + 1);
            }
            String destUrl = uploadFile(file.getBytes(), fileExt);
            if(StringUtils.isNotBlank(destUrl)) {
                if(destUrl.endsWith("/null")) {
                    throw new BaseException("返回错误的地址：" + destUrl);
                }
                destUrl = RightTrimHide(destUrl);
            }
            CraftFile craftFile = new CraftFile();
            craftFile.setResourceType(RES_TYPE_MACHINE_IMG);
            craftFile.setRemark(fileName);
            craftFile.setFileUrl(destUrl);
            Long keyId = null;
            if(machine != null) {
                keyId = Long.valueOf(machine.getId());
            }
            craftFile.setKeyId(keyId);
            craftFile.setUpdateUser(userCode);
            craftFile.setUpdateTime(new Date());
            craftFileDao.addCraftFile(craftFile);

            if(machine != null) {
                Machine machineNew = new Machine();
                machineNew.setMachineCode(machine.getMachineCode());
                machineNew.setIsPic(true);
                machineNew.setUpdateUser(userCode);
                machineNew.setUpdateTime(new Date());
                machineDao.updateMachine(machineNew);
            }

            return destUrl;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new BaseException("上传失败");
        }

    }

    @Override
    public Integer deleteResource(Machine machine, String fileUrl, String userCode) {
        deleteFileByUrl(fileUrl);
        Long keyId = null;
        Integer ret = null;

        ret = craftFileDao.deleteByKeyAndFileUrl(keyId,null, RES_TYPE_MACHINE_IMG, fileUrl);
        if(machine != null) {
            keyId = Long.valueOf(machine.getId());

            Machine machineNew = new Machine();
            machineNew.setId(machine.getId());
            machineNew.setIsPic(craftFileDao.isCraftResourceExist(keyId, RES_TYPE_MACHINE_IMG));
            machineNew.setUpdateUser(userCode);
            machineNew.setUpdateTime(new Date());
            machineDao.updateMachine(machineNew);
        }

        return ret;
    }

    @Override
    public Machine getMachindCodeData(String code,String workTypeCode) {
        try {
            return machineDao.getMachindCodeData(code,workTypeCode);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
