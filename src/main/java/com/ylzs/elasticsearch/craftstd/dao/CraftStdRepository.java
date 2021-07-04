package com.ylzs.elasticsearch.craftstd.dao;

import com.ylzs.elasticsearch.craftstd.esbo.CraftStdEsBO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author xuwei
 * @create 2020-03-03 10:05
 */
public interface CraftStdRepository extends ElasticsearchRepository<CraftStdEsBO,Long>{
}