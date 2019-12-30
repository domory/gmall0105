package com.wh.gmall.service;

import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import java.util.List;

public interface UserService {
    List<UmsMember> getAllUSer();
    List<UmsMemberReceiveAddress> getMemberAddressByMemberId(String memberId);

    UmsMember login(UmsMember umsMember);

    void addToken(Long id, String token);

    void addOauthUser(UmsMember umsMember);

    UmsMember getOauthUser(Long sourceUid);

    UmsMemberReceiveAddress getMemberAddressById(String receiveAddressId);
}
