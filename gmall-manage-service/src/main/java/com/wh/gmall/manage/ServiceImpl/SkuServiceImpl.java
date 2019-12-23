package com.wh.gmall.manage.ServiceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.wh.gmall.bean.PmsSkuAttrValue;
import com.wh.gmall.bean.PmsSkuImage;
import com.wh.gmall.bean.PmsSkuInfo;
import com.wh.gmall.bean.PmsSkuSaleAttrValue;
import com.wh.gmall.manage.Mapper.PmsSkuAttrValueMapper;
import com.wh.gmall.manage.Mapper.PmsSkuImageMapper;
import com.wh.gmall.manage.Mapper.PmsSkuInfoMapper;
import com.wh.gmall.manage.Mapper.PmsSkuSaleAttrValueMapper;
import com.wh.gmall.service.SkuService;
import com.wh.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.UUID;

/**
 * @author DOMORY
 */
@Service(timeout = 10000)//设置调用方法的超时时间
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //插入skuInfo
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        //插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
        //插入销售属性关联
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
        //插入图片信息
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {

            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
    }

    //从数据库中查询数据
    public PmsSkuInfo getSkuBySkuIdFromDb(String skuId) {
        //sku商品对象
        Example example=new Example(PmsSkuInfo.class);
        example.createCriteria().andEqualTo("id",skuId);
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectOneByExample(example);
        //sku图片集合
        Example example1=new Example(PmsSkuImage.class);
        example1.createCriteria().andEqualTo("skuId",skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.selectByExample(example1);
        pmsSkuInfo.setSkuImageList(pmsSkuImages);
        return pmsSkuInfo;
    }

    @Override
    public PmsSkuInfo getSkuBySkuId(String skuId) {
        PmsSkuInfo  pmsSkuInfo=new PmsSkuInfo();
        //连接缓存
        Jedis jedis = redisUtil.getJedis();
        //查询缓存
        String skukey="sku:"+skuId+":info";
        String skuJson = jedis.get(skukey);
        if(StringUtils.isNotBlank(skuJson)){
            //如果缓存有，从缓存中拿
         pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
           //System.out.println(Thread.currentThread().getName()+"从缓存中拿到数据");
        }else{
            //如果缓存没有，查数据库，设置分布式锁(解决缓存击穿问题)
            String token = UUID.randomUUID().toString();
            //拿到锁的进程只有十秒的时间
            String Ok = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10*1000);
         //  System.out.println(Thread.currentThread().getName()+"拿到锁");
            if(StringUtils.isNotBlank(Ok) && Ok.equals("OK")){
                pmsSkuInfo=getSkuBySkuIdFromDb(skuId);
                if(pmsSkuInfo!=null){
                    //睡眠5秒再把数据放入缓存
                    try {
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //数据库查询结果放入缓存
                    jedis.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                 //   System.out.println(Thread.currentThread().getName()+"放入缓存");
                }else {
                    //如果数据库没有，为防止缓存穿透，将空值设置给redis
                    jedis.setex("sku:"+skuId+":info",60*3,JSON.toJSONString(""));
                }
                //在访问完数据库之后，释放锁
                String lockToken = jedis.get("sku:" + skuId + ":lock");
                //根据value值删除自己的锁
                if(StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)){
                jedis.del("sku:" + skuId + ":lock");
                }

            }else {
                try {
                    Thread.sleep(3000);
                 //   System.out.println(Thread.currentThread().getName()+"自旋");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuBySkuId(skuId);
            }
        }
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuAttrValueListBySpuId(String productId) {
        List<PmsSkuInfo> pmsSkuInfos= pmsSkuInfoMapper.selectSkuAttrValueListBySpuId(productId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku(String catalog3Id) {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
            return pmsSkuInfos;

    }
}
