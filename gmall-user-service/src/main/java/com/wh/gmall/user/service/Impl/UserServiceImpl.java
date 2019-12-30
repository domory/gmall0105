package com.wh.gmall.user.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.bean.UmsMember;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import com.wh.gmall.service.UserService;
import com.wh.gmall.user.mapper.UmsMemberMapper;
import com.wh.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.wh.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author DOMORY
 */
@Service(timeout = 10000)
public class UserServiceImpl implements UserService {

    @Autowired
     UmsMemberMapper umsMemberMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getAllUSer() {
        List<UmsMember> umsMemberList=umsMemberMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getMemberAddressByMemberId(String memberId) {
        Example example=new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId",memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);
        return umsMemberReceiveAddresses;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis=null;
        try{
            jedis = redisUtil.getJedis();
            String UmsMemberstr = jedis.get("user:" + umsMember.getUsername() + umsMember.getPassword() + "info");
            if(StringUtils.isNotBlank(UmsMemberstr)){
                //用户 密码正确
                UmsMember umsMemberfromcache = JSON.parseObject(UmsMemberstr, UmsMember.class);
                return umsMemberfromcache;
            }else {
                //用户密码错误 或者 缓存没有 需要从数据库查
                Example example=new Example(UmsMember.class);
                example.createCriteria().andEqualTo("username",umsMember.getUsername()).andEqualTo("password",umsMember.getPassword());
                UmsMember umsMemberfromDb = umsMemberMapper.selectOneByExample(example);
                //加入缓存
                if(umsMemberfromDb!=null) {                                                           //数据在缓存中时间为1小时
                    jedis.setex("user:" + umsMember.getUsername() + umsMember.getPassword() + "info", 60*60,JSON.toJSONString(umsMemberfromDb));
                }
                return umsMemberfromDb;
            }
        }
        finally {
            jedis.close();
        }
    }

    @Override
    public void addToken(Long id, String token) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:"+id.toString()+"token",60,JSON.toJSONString(token));
            jedis.close();
    }

    @Override
    public void  addOauthUser(UmsMember umsMember) {
        Example example=new Example(UmsMember.class);
        example.createCriteria().andEqualTo("sourceUid",umsMember.getSourceUid());
        UmsMember umsMember1 = umsMemberMapper.selectOneByExample(example);
        if(umsMember1!=null){
            Example example1=new Example(UmsMember.class);
            example1.createCriteria().andEqualTo("id",umsMember1.getId());
            umsMemberMapper.updateByExample(umsMember,example1);
        }
        else {
            umsMemberMapper.insert(umsMember);
        }
    }

    @Override
    public UmsMember getOauthUser(Long sourceUid) {
        Example example=new Example(UmsMember.class);
        example.createCriteria().andEqualTo("sourceUid",sourceUid);
        UmsMember umsMember = umsMemberMapper.selectOneByExample(example);
        return umsMember;
    }

    @Override
    public UmsMemberReceiveAddress getMemberAddressById(String receiveAddressId) {
        Example example=new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id",receiveAddressId);
       UmsMemberReceiveAddress umsMemberReceiveAddresses = umsMemberReceiveAddressMapper.selectOneByExample(example);
        return umsMemberReceiveAddresses;
    }
}
