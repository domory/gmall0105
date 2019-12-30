package com.wh.gmall.service;

import com.wh.gmall.bean.OmsOrder;

public interface OrderService {
    String getTradeCode(String memberId);

    String checkCode(String memberId,String tradeCode);


    void saveOrder(OmsOrder omsOrder);
}
