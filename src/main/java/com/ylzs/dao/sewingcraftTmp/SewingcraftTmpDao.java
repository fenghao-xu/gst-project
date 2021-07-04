package com.ylzs.dao.sewingcraftTmp;

import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.vo.SewingCraftResource;
import com.ylzs.vo.craftstd.CraftStdVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-10-15 15:23
 */
public interface SewingcraftTmpDao {
    public List<SewingCraftWarehouse> getDataByPager(Map<String, Object> param);

    public CraftStdVo checkStdData(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public List<String> getWorkplaceBySewingRandomAndCraft(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);

    public void updateSysnStatus(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode, @Param("sysnStatus") Integer sysnStatus);

    public List<SewingCraftResource> getSewingStdPicAndVedio(@Param("randomCode") Long randomCode, @Param("craftCode") String craftCode);
}
