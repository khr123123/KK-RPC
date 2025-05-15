package org.khr.orderserviceimpl.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders (productId, quantity) VALUES ( #{productId}, #{quantity})")
    int insertOrder(@Param("productId") String productId, @Param("quantity") int quantity);
}
