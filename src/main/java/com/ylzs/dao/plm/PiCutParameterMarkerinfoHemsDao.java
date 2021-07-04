package com.ylzs.dao.plm;

import com.ylzs.dao.BaseDAO;
import com.ylzs.entity.plm.PiCutParameterMarkerinfoHems;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @className PiCutParameterMarkerinfoHemsDao
 * @Description
 * @Author sky
 * @create 2020-03-20 10:06:01
 */
@Repository
public interface PiCutParameterMarkerinfoHemsDao extends BaseDAO<PiCutParameterMarkerinfoHems> {

    @Select("SELECT * FROM capp_pi_cut_parameter_markerinfo_hems WHERE order_id = #{orderId} AND order_line_id = #{orderLineId}")
    List<PiCutParameterMarkerinfoHems> getOrderMarkerInfoHemosAll(String orderId,String orderLineId);
}
