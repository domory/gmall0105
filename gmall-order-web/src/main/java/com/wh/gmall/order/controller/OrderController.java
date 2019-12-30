package com.wh.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.annotations.LoginRequired;
import com.wh.gmall.bean.OmsCartItem;
import com.wh.gmall.bean.OmsOrder;
import com.wh.gmall.bean.OmsOrderItem;
import com.wh.gmall.bean.UmsMemberReceiveAddress;
import com.wh.gmall.service.CartService;
import com.wh.gmall.service.OrderService;
import com.wh.gmall.service.SkuService;
import com.wh.gmall.service.UserService;
import com.wh.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@CrossOrigin
public class OrderController {
    @Reference
    CartService cartService;
    @Reference
    UserService userService;
    @Reference
    OrderService orderService;
    @Reference
    SkuService skuService;

    @RequestMapping("submitOrder")
    @LoginRequired(loginsuccess = true)
    public String submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        //进行比对交易码
        String success = orderService.checkCode(memberId, tradeCode);
        if (success.equals("success")) {
            List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
            OmsOrder omsOrder = new OmsOrder();
            omsOrder.setAutoConfirmDay(7);
            omsOrder.setMemberId(memberId);
            omsOrder.setMemberUsername(nickname);
            String tradeNum = "gmall";
            tradeNum = tradeNum + System.currentTimeMillis();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhss");
            tradeNum = tradeNum + simpleDateFormat.format(new Date());
            omsOrder.setOrderSn(tradeNum);
            omsOrder.setTotalAmount(totalAmount);
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getMemberAddressById(receiveAddressId);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setSourceType(0);
            omsOrder.setStatus(0);
            omsOrder.setPayAmount(totalAmount);
            omsOrder.setNote("尽快发货");
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    //校验价格
                    boolean b = skuService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                    if (b == false) {
                        return "tradeFail";
                    }
                    //校验库存
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setOrderSn(tradeNum);
                    omsOrderItem.setRealAmount(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                    omsOrderItem.setProductSkuCode("11111");
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItems.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            //将订单和订单详情写入数据库
            //删除购物车对应的商品
            orderService.saveOrder(omsOrder);
            //重定向到支付页面
            return null;
        } else {
            return "tradeFail";
        }

    }


    @RequestMapping("toTrade")
    @LoginRequired(loginsuccess = true)
    public String toTrade(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //登录后查询缓存
        omsCartItems = cartService.cartList(memberId);
        //     if(omsCartItems.isEmpty()){
        String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
        if (StringUtils.isNotBlank(cartListCookie)) {
            //如果有缓存有cookie,证明之前登录过   没有缓存有cookie证明刚刚在网址买东西
            omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            for (OmsCartItem omsCartItem : omsCartItems) {
                // omsCartItem.setMemberId(memberId);
                //  cartService.saveCart(omsCartItem);
                OmsCartItem omsCartItemFromDB = cartService.ifCartExitByUserId(memberId, omsCartItem.getProductSkuId());
                if (omsCartItemFromDB == null) {
                    //用户没买过
                    omsCartItem.setMemberId(memberId);
                    cartService.saveCart(omsCartItem);
                } else {
                    //买过
                    omsCartItemFromDB.setQuantity(omsCartItemFromDB.getQuantity().add(omsCartItem.getQuantity()));
                    cartService.updateCartBy(omsCartItemFromDB);
                }
            }
            cartService.flushCartCaChe(memberId);
            String token = request.getParameter("token");
            CookieUtil.deleteCookie(request, response, "cartListCookie");
            return "redirect:http://localhost:8084/cartList?token=" + token;
        } else {
            List<UmsMemberReceiveAddress> memberAddressList = userService.getMemberAddressByMemberId(memberId);
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItems.add(omsOrderItem);
                }
            }
            modelMap.put("omsOrderItems", omsOrderItems);
            modelMap.put("memberAddressList", memberAddressList);
            modelMap.put("totalAmount", getTotalAmount(omsCartItems));
            //每次提交订单都要生成一个交易码
            String tradeCode = orderService.getTradeCode(memberId);
            modelMap.put("tradeCode", tradeCode);
            return "trade";
        }
    }


    //获得结算价格
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }
}
