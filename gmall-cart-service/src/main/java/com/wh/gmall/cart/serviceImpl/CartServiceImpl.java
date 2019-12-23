package com.wh.gmall.cart.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.bean.OmsCartItem;
import com.wh.gmall.cart.Mapper.OmsCartItemMapper;
import com.wh.gmall.service.CartService;
import com.wh.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DOMORY
 */
@Service(timeout = 10000)
public class CartServiceImpl implements CartService {

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public OmsCartItem ifCartExitByUserId(String memberId, String skuId) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId).andEqualTo("productSkuId", skuId);
        OmsCartItem omsCartItem = omsCartItemMapper.selectOneByExample(example);
        return omsCartItem;
    }

    @Override
    public void saveCart(OmsCartItem omsCartItem) {
        omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void updateCartBy(OmsCartItem omsCartItemFromDB) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", omsCartItemFromDB.getId());
        omsCartItemMapper.updateByExample(omsCartItemFromDB, example);
    }

    @Override
    public void flushCartCaChe(String memberId) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);
        //同步到缓存中
        Map<String,String> map=new HashMap<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            map.put(omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem));
        }
        Jedis jedis = redisUtil.getJedis();
        //先删除缓存 再添加缓存
        jedis.del("user:"+memberId+":cart");
        jedis.hmset("user:"+memberId+":cart",map);

        jedis.close();
    }

    @Override
    public List<OmsCartItem> cartList(String memberId) {
        //查询缓存
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
        List<OmsCartItem> omsCartItems=new ArrayList<>();
        if(hvals!=null){
            //如果缓存有 从缓存拿
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
            }
        }
//        else {
//            //没有 从db拿
//            Example example=new Example(OmsCartItem.class);
//            example.createCriteria().andEqualTo("memberId",memberId);
//            omsCartItems = omsCartItemMapper.selectByExample(example);
//        }
        jedis.close();
        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example example=new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("productSkuId",omsCartItem.getProductSkuId()).andEqualTo("memberId",omsCartItem.getMemberId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem,example);
        //修改完数据库后进行缓存同步
        flushCartCaChe(omsCartItem.getMemberId());
    }
}
