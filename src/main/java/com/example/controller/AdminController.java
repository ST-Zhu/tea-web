package com.example.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Orders;
import com.example.entity.Products;
import com.example.entity.Users;
import com.example.result.Result;
import com.example.service.OrdersService;
import com.example.service.ProductsService;
import com.example.service.UsersService;
import com.example.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.plaf.IconUIResource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private OrdersService ordersService;

    //TODO 管理端首页
    @RequestMapping("index")
    public String index(Model model, String searchYear, String searchMonth) {
        Map<String, Object> map = productsService.getEchartsData(searchYear, searchMonth);
        model.addAttribute("result", map);
        model.addAttribute("searchYear", searchYear);
        model.addAttribute("searchMonth", searchMonth);
        return "index_admin";
    }

    //TODO 转换信息类型
    public static String switchCategory2String(String category) {
        switch (category) {
            case "1":
                category = "茶园土地";
                break;
            case "2":
                category = "茶叶品种";
                break;
        }
        return category;
    }

    //TODO 转换信息类型
    public static String switchCategory2Number(String category) {
        switch (category) {
            case "茶园土地":
                category = "1";
                break;
            case "茶叶品种":
                category = "2";
                break;
        }
        return category;
    }

    //TODO 业务管理-查询
    @RequestMapping("queryInformation")
    public String queryInformation(Model model, String category, String searchName) {
        if (null == searchName) {
            searchName = "";
        }
        List<Products> result = productsService.list(new QueryWrapper<Products>().like("product_name", searchName).eq("product_category", category).eq("product_is_delete", false));
        category = switchCategory2String(category);
        String categoryNum = switchCategory2Number(category);
        model.addAttribute("result", result);
        model.addAttribute("category", category);
        model.addAttribute("categoryNum", categoryNum);
        model.addAttribute("searchName", searchName);
        return "product_admin";
    }

    //TODO 插入和修改信息
    @PostMapping("insertInformation")
    @ResponseBody
    public Result insertInformation(@Validated Products products, String category) {
        if (null == products.getProductId()) {
            long count = productsService.count(new QueryWrapper<Products>().eq("product_name", products.getProductName()));
            if (count >= 1) {
                return Result.buildFail("该名称已存在");
            }
            products.setProductCategory(category);
            products.setProductNumber(IdUtil.simpleUUID());
            products.setProductCreateTime(new Date());
            products.setProductIsDelete(false);
        } else {
            products.setProductUpdateTime(new Date());
        }
        productsService.saveOrUpdate(products);
        return Result.buildSuccess();
    }

    //TODO 删除信息
    @RequestMapping("deleteInformation")
    public String deleteInformation(String productId, String category) {
        productsService.deleteProductById(productId);
        category = switchCategory2Number(category);
        return "redirect:/admin/queryInformation?category=" + category;
    }

    //TODO 查询用户
    @RequestMapping("queryUser")
    public String queryUser(Model model, String searchName) {
        if (null == searchName) {
            searchName = "";
        }
        List<Users> result = usersService.list(new QueryWrapper<Users>().like("username", searchName).eq("role", "1").eq("is_delete", false));
        for (Users users : result) {
            users.setPassword("");
        }
        model.addAttribute("result", result);
        model.addAttribute("searchName", searchName);
        return "user";
    }


    //TODO 添加或者修改用户
    @RequestMapping("insertUser")
    @ResponseBody
    public Result insertUser(@Validated Users users, String password_2) {
        System.out.println(users);
        users.setRole("1");
        if (users.getPassword().equals(password_2)) {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            if (null == users.getUserId()) {
                List<Users> list = usersService.list(new QueryWrapper<Users>().eq("username", users.getUsername()));
                if (list.size() > 0) {
                    return Result.buildFail("该用户名已存在");
                }
                users.setCart("");
                users.setCollection("");
                users.setIsDelete(false);
                users.setUserCreateTime(new Date());
            } else {
                users.setUserUpdateTime(new Date());
            }
            usersService.saveOrUpdate(users);
            return Result.buildSuccess("账号编辑成功");
        }
        return Result.buildFail("密码和重复密码输入不一致");
    }

    //TODO 删除用户
    @RequestMapping("deleteUser")
    public String deleteUser(Users users) {
        users.setIsDelete(true);
        usersService.updateById(users);
        return "redirect:/admin/queryUser";
    }

    //TODO 查询管理员
    @RequestMapping("queryAdmin")
    public String queryAdmin(Model model, String searchName) {
        if (null == searchName) {
            searchName = "";
        }
        List<Users> result = usersService.list(new QueryWrapper<Users>().like("username", searchName).eq("role", "2").eq("is_delete", false));
        for (Users users : result) {
            users.setPassword("");
        }
        model.addAttribute("result", result);
        model.addAttribute("searchName", searchName);
        return "admin";
    }

    //TODO 添加或者修改管理员
    @RequestMapping("insertAdmin")
    @ResponseBody
    public Result insertAdmin(@Validated Users users, String password_2) {
        users.setRole("2");
        if (users.getPassword().equals(password_2)) {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            if (null == users.getUserId()) {
                List<Users> list = usersService.list(new QueryWrapper<Users>().eq("username", users.getUsername()));
                if (list.size() > 0) {
                    return Result.buildFail("该用户名已存在");
                }
                users.setCart("");
                users.setCollection("");
                users.setIsDelete(false);
                users.setUserCreateTime(new Date());
            } else {
                users.setUserUpdateTime(new Date());
            }
            usersService.saveOrUpdate(users);
            return Result.buildSuccess("账号编辑成功");
        }
        return Result.buildFail("密码和重复密码输入不一致");
    }

    //TODO 删除管理员
    @RequestMapping("deleteAdmin")
    public String deleteAdmin(Users users) {
        users.setIsDelete(true);
        usersService.updateById(users);
        return "redirect:/admin/queryAdmin";
    }

    //TODO 查看订单
    @GetMapping("queryOrder")
    public String queryOrder(String searchName, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (null == searchName) {
            searchName = "";
        }
        Users users = usersService.getOne(new QueryWrapper<Users>().eq("username", userDetails.getUsername()));
        List<com.example.entity.vo.Orders> list = ordersService.getAllOrder("%" + searchName + "%");
        model.addAttribute("result", list);
        model.addAttribute("searchName", searchName);
        return "order_admin";
    }

    //TODO 删除订单
    @RequestMapping("deleteOrders")
    public String deleteOrders(String orderNumber){
        ordersService.deleteOrdersByOrderNumber(orderNumber);
        return "redirect:/admin/queryOrder";
    }

    //TODO 发货
    @RequestMapping("sendProduct")
    @ResponseBody
    public String sendProduct(String orderNumber) {
        ordersService.sendProductByOrderNumber(orderNumber);
        return JSON.toJSONString(Result.buildSuccess("发货成功"));
    }

    //TODO 个人中心
    @RequestMapping("mine")
    public String mine(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Users one = usersService.getOne(new QueryWrapper<Users>().eq("username", userDetails.getUsername()));
        Users users = new Users();
        users.setUserId(one.getUserId());
        users.setUsername(one.getUsername());
        users.setAge(one.getAge());
        users.setPhone(one.getPhone());
        users.setEmail(one.getEmail());
        model.addAttribute("result", users);
        return "mine_admin";
    }

    //TODO 个人中心修改密码
    @RequestMapping("updateAccount")
    @ResponseBody
    public Result updateAccount(@Validated Users users, String password_2) {
        if (users.getPassword().equals(password_2)) {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setUserUpdateTime(new Date());
            usersService.updateById(users);
            return Result.buildSuccess("密码修改成功");
        }
        return Result.buildFail("密码和重复密码不一致");
    }

}
