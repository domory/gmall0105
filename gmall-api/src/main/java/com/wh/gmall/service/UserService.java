package com.wh.gmall.service;

import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import java.util.List;

public interface UserService {
    List<UmsMember> getAllUSer();
    List<UmsMemberReceiveAddress> getMemberAddressById(Integer Id);
}
