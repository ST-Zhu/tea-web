package com.example.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Products;
import com.example.entity.vo.Orders;
import com.example.mapper.ProductsMapper;
import com.example.service.ProductsService;
import com.example.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * (Products)表服务实现类
 *
 * @author makejava
 * @since 2022-03-24 14:11:47
 */
@Service("productsService")
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements ProductsService {

    @Autowired
    private ProductsMapper productsMapper;

    @Override
    public Boolean deleteProductById(String productId) {
        Integer productId_m = StringUtil.changeString(productId);
        return productsMapper.deleteProductBy(productId_m);
    }

    @Override
    public Map<String, Object> getEchartsData(String searchYear, String searchMonth) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String startTime = "";
        String endTime = "";
        if (!"".equals(searchYear) && searchYear != null && !"".equals(searchMonth) && searchMonth != null) {
            Integer searchMonth_m = StringUtil.changeString(searchMonth);
            if (searchMonth_m >= 10) {
                startTime = searchYear + "-" + searchMonth + "-" + "01";
            } else {
                startTime = searchYear + "-" + "0" + searchMonth + "-" + "01";
            }
            int mon = Integer.parseInt(searchMonth) + 1;
            if (13 == mon) {
                endTime = (Integer.parseInt(searchYear) + 1) + "-" + "01-01";
            } else if (mon >= 10) {
                endTime = searchYear + "-" + mon + "-" + "01";
            } else {
                endTime = searchYear + "-" + "0" + mon + "-" + "01";
            }
        } else {
            if (month >= 10) {
                startTime = year + "-" + month + "-" + "01";
            } else {
                startTime = year + "-" + "0" + month + "-" + "01";
            }
            int mon = month + 1;
            if (13 == mon) {
                endTime = (year + 1) + "-" + "01-01";
            } else if (mon >= 10) {
                endTime = year + "-" + mon + "-" + "01";
            } else {
                endTime = year + "-" + "0" + mon + "-" + "01";
            }
            searchYear = String.valueOf(year);
            searchMonth = String.valueOf(month);
        }
        Date startDate = DateUtil.parse(startTime);
        Date endDate = DateUtil.parse(endTime);
        List<com.example.entity.vo.Orders> list1 = productsMapper.getAboutOrderInformation(startDate, endDate, "1");
        Integer totalAmount1 = 0;
        Double totalMoney1 = 0.0;
        for (Orders orders : list1) {
            totalAmount1 += orders.getOrderAmount();
            totalMoney1 += orders.getProductPrice() * orders.getOrderAmount();
        }
        List<com.example.entity.vo.Orders> list2 = productsMapper.getAboutOrderInformation(startDate, endDate, "2");
        Integer totalAmount2 = 0;
        Double totalMoney2 = 0.0;
        for (Orders orders : list2) {
            totalAmount2 += orders.getOrderAmount();
            totalMoney2 += orders.getProductPrice() * orders.getOrderAmount();
        }
        Double totalAmount = totalAmount1 + totalMoney2;
        Double totalMoney = totalMoney1 + totalMoney2;
        Map<String, Object> map = new HashMap<>();
        map.put("totalAmount", totalAmount);
        map.put("totalAmount1", totalAmount1);
        map.put("totalAmount2", totalAmount2);
        map.put("totalMoney", totalMoney);
        map.put("totalMoney1", totalMoney1);
        map.put("totalMoney2", totalMoney2);
        map.put("searchYear", searchYear);
        map.put("searchMonth", searchMonth);
        return map;
    }
}
