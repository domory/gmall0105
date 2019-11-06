package com.wh.gmall.user.service.Impl;

import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import com.wh.gmall.service.UserService;
import com.wh.gmall.user.mapper.UmsMemberMapper;
import com.wh.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UmsMemberMapper umsMemberMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUSer() {
        List<UmsMember> umsMemberList=umsMemberMapper.selectAll(); /*umsMemberMapper.selectAllUser();*/
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getMemberAddressById(Integer Id) {
        Example example=new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id",Id);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
        return umsMemberReceiveAddresses;
    }
}
