package org.khr.stockserviceimpl.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMapper {
    int reduceStock(@Param("product") String product, @Param("stock") int stock);
}
