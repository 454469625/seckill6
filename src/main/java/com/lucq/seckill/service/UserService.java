package com.lucq.seckill.service;

import com.lucq.seckill.dao.UserDao;
import com.lucq.seckill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Transactional
    public boolean tx() {
        User u1 = new User();
        User u2 = new User();
        u1.setId(2);
        u1.setName("lcc");
        u2.setId(3);
        u2.setName("lqq");
        userDao.insert(u1);
        userDao.insert(u2);
        return true;
    }

}
