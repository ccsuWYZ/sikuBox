package com.siku.service.impl;

import com.siku.constant.CacheConstants;
import com.siku.mapper.TikuDetailMapper;
import com.siku.mapper.TikuMapper;
import com.siku.pojo.TikuDetail;
import com.siku.pojo.TikuDetailFormate;
import com.siku.result.AjaxResult;
import com.siku.service.TikuDetailService;
import com.siku.utils.FormateTikuDetail;
import com.siku.utils.RedisCache;
import javafx.scene.effect.Light;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class TikuDetailServiceImpl implements TikuDetailService {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private TikuMapper tikuMapper;
    @Autowired
    private TikuDetailMapper tikuDetailMapper;
    @Override
    public void loadingConfigCache() {
        for (String  bankid : tikuMapper.selectBankid()) {

            //从数据库得到数据
            List<TikuDetail> tikuDetailList = tikuDetailMapper.selectAllByBankId(bankid);


            //把从数据库得到的数据格式化成前端需要的
            List<TikuDetailFormate> tikuDetailFormateList = FormateTikuDetail.formateTikudetail(tikuDetailList);

            //存进缓存，后续可以加个条件，只缓存公共题库
            redisCache.setCacheList(getCacheKey(bankid), tikuDetailFormateList);
        }
    }

    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(CacheConstants.TIKUDETAIL_INFO_KEY + "*");
        redisCache.deleteObject(keys);
    }

    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.TIKUDETAIL_INFO_KEY + configKey;
    }

  /**
   * 项目启动时，把信息表存到缓存
   */
    @PostConstruct
    public void init() {
        resetConfigCache();
    }




    @Override
    public AjaxResult selectAll(String bankId) {
        System.out.println(bankId);

        List<TikuDetailFormate> tikuDetailFormateList = redisCache.getCacheList(CacheConstants.TIKUDETAIL_INFO_KEY + bankId);

//                List<TikuDetailFormate> tikuDetailFormateList ;

//        System.out.println(tikuDetailFormateList.size());


        if (tikuDetailFormateList.isEmpty()) {
            System.out.println("题目列表缓存为空");
            List<TikuDetail> tikuDetailList = tikuDetailMapper.selectAllByBankId(bankId);
            tikuDetailFormateList = FormateTikuDetail.formateTikudetail(tikuDetailList);
        }

        return new AjaxResult(200, "获取题目列表成功",tikuDetailFormateList );
    }
}
