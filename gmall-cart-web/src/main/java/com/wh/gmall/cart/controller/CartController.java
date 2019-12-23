package com.wh.gmall.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.annotations.LoginRequired;
import com.wh.gmall.bean.OmsCartItem;
import com.wh.gmall.bean.PmsSkuInfo;
import com.wh.gmall.service.CartService;
import com.wh.gmall.service.SkuService;
import com.wh.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author DOMORY
 */
@Controller
@CrossOrigin
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    @RequestMapping("toTrade")
    @LoginRequired(loginsuccess = true)
    public String toTrade(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        return "toTrade";
    }

    @RequestMapping("addToCart")
    @LoginRequired(loginsuccess = false)
    public String addToCart(String skuId, BigDecimal quantity, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuBySkuId(skuId);
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //将商品信息封装成购物车信息,添加到购物车
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductSkuCode("11111");
        omsCartItem.setQuantity(quantity);
        //判断用户是否登录
        String memberId = "1";
        //未登录 使用cookie保存数据
        if (StringUtils.isBlank(memberId)) {
            //用户需要登录 先将商品添加到购物车(cookie里可能存在多个商品)
            //先判断cookie里之前是否有数据，从cookie里拿出数据,然后和添加的商品进行比较
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                //将cookie内容和刚刚添加的相比较
                Boolean exit = if_cart_exit(omsCartItems, omsCartItem);
                if (exit) {
                    //如果有，表明之前添加过该商品，数量相加
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            /*更新数量*/
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }
                } else {
                    //没有，新增到购物车
                    omsCartItems.add(omsCartItem);
                }
            } else {
                //cookie内没有值
                omsCartItems.add(omsCartItem);
            }
            //更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        } else {
            //用户已登录 根据数据库信息更新购物车信息
            //从db查询用户是否买过该商品
            OmsCartItem omsCartItemFromDB = cartService.ifCartExitByUserId(memberId, skuId);
            if (omsCartItemFromDB == null) {
                //用户没买过
                omsCartItem.setMemberId(memberId);
                cartService.saveCart(omsCartItem);
            } else {
                //买过
                omsCartItemFromDB.setQuantity(omsCartItemFromDB.getQuantity().add(quantity));
                cartService.updateCartBy(omsCartItemFromDB);
            }
            //同步购物车缓存 根据用户id更新缓存
            cartService.flushCartCaChe(memberId);
        }
        return "redirect:/success.html";//重定向到suucess请求
    }

    private Boolean if_cart_exit(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        // boolean b=false;
        for (OmsCartItem cartItem : omsCartItems) {
            if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("cartList")
    @LoginRequired(loginsuccess = false)
    public String cartList(ModelMap modelMap, HttpServletRequest request) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = "1";
        if (StringUtils.isNotBlank(memberId)) {
            //如果用户登录 查询db(缓存查询)
            omsCartItems = cartService.cartList(memberId);
        } else {
            //没有登录 查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isNotBlank(cartListCookie)) {
                //如果有缓存
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
            }
        }
        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
        }
        modelMap.put("cartList", omsCartItems);
        //结算价格
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount", totalAmount);
        return "cartList";
    }

    //获得结算价格
    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                totalAmount = totalAmount.add(omsCartItem.getTotalPrice());
            }
        }
        return totalAmount;
    }

    @RequestMapping("checkCart")
    @LoginRequired(loginsuccess = false)
    public String checkCart(String isChecked, String skuId, ModelMap modelMap) {
        String memberId = "1";
        //更新选中状态
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        cartService.checkCart(omsCartItem);
        //从缓存中拿
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
        //算价格
        for (OmsCartItem omsCartItem1 : omsCartItems) {
            omsCartItem1.setTotalPrice(omsCartItem1.getPrice().multiply(omsCartItem1.getQuantity()));
        }
        modelMap.put("cartList", omsCartItems);
        //结算价格
        BigDecimal totalAmount = getTotalAmount(omsCartItems);
        modelMap.put("totalAmount", totalAmount);
        return "cartListInner";
    }

    // @RequestMapping("success")
    public String success() {
        return "success";
    }
}
