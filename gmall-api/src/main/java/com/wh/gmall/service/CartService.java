package com.wh.gmall.service;

import com.wh.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem ifCartExitByUserId(String memberId, String skuId);

    void saveCart(OmsCartItem omsCartItem);

    void updateCartBy(OmsCartItem omsCartItemFromDB);

    void flushCartCaChe(String memberId);

    List<OmsCartItem> cartList(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
