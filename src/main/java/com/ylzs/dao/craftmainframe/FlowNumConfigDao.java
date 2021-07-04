package com.ylzs.dao.craftmainframe;

import com.ylzs.entity.craftmainframe.FlowNumConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface FlowNumConfigDao {
    List<FlowNumConfig> getFlowNumConfigAll();

}
