package com.wh.gmall.order.serviceImpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.wh.gmall.bean.OmsOrder;
import com.wh.gmall.bean.OmsOrderItem;
import com.wh.gmall.order.mapper.OmsOrderItemMapper;
import com.wh.gmall.order.mapper.OmsOrderMapper;
import com.wh.gmall.service.CartService;
import com.wh.gmall.service.OrderService;
import com.wh.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

@Service(timeout = 10000)
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OmsOrderMapper omsOrderMapper;
    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;
    @Reference
    CartService cartService;

    @Override
    //生成tradeCode
    public String getTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeKey = "user:" + memberId + ":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeKey, 60 * 15, tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    //比对交易码
    public String checkCode(String memberId, String tradeCode) {
        Jedis jedis=null;
        try {
             jedis = redisUtil.getJedis();
            String tradeKey = "user:" + memberId + ":tradeCode";
            String tradeFromCache = jedis.get(tradeKey);
            if (StringUtils.isNotBlank(tradeFromCache) && tradeFromCache.equals(tradeCode)) {
                //删除缓存中的交易码
                jedis.del(tradeKey);
                return "success";
            } else {
                return "fail";
            }
        } finally {
            jedis.close();
        }
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        //保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String id = omsOrder.getId();
        //保存订单详情
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(id);
            omsOrderItemMapper.insertSelective(omsOrderItem);
            //删除购物车数据
            cartService.deleteCart(omsOrderItem.getProductSkuId(),omsOrder.getMemberId());
            cartService.flushCartCaChe(omsOrder.getMemberId());
        }

    }

}
