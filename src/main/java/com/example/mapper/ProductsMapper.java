package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Products;
import com.example.entity.vo.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProductsMapper extends BaseMapper<Products> {

    @Update("update products set product_is_delete = true where product_id = #{productId}")
    Boolean deleteProductBy(Integer productId);

    @Select("select product_name,product_price,order_amount from products  natural join order_product_user natural join orders " +
            " where product_category = #{productCategory} and order_create_time between #{startDate} and #{endDate}")
    List<Orders> getAboutOrderInformation(Date startDate, Date endDate, String productCategory);
}
