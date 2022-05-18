package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Users;
import com.example.mapper.UsersMapper;
import com.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * (Users)表服务实现类
 *
 * @author makejava
 * @since 2022-03-24 13:55:55
 */
@Service("usersService")
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService, UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据账号查询用户信息
        List<Users> usersList = usersMapper.selectList(new QueryWrapper<Users>().eq("username", username).eq("is_delete", false));
        if (usersList != null && usersList.size() > 0) {
            Users users = usersList.get(0);
            if (users != null) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + users.getRole());
                List<SimpleGrantedAuthority> authorities = new LinkedList<>();
                authorities.add(simpleGrantedAuthority);
                return new User(users.getUsername(), users.getPassword(), authorities);
            }
        }
        return null;
    }
}
