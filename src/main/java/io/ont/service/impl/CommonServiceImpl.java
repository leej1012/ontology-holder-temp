package io.ont.service.impl;

import io.ont.entity.Holder;
import io.ont.mapper.HolderMapper;
import io.ont.service.CommonService;
import io.ont.utils.SDKUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@NoArgsConstructor
public class CommonServiceImpl implements CommonService {
    @Autowired
    private HolderMapper holderMapper;
    @Autowired
    private SDKUtil sdkUtil;
    private Integer maxCount = 50;


    @Override
    public List<Holder> getAssetHolder(String contract, Integer from, Integer count) {
        BigDecimal totalSupply = sdkUtil.getOntOngTotalSupply(contract);
        count = Math.min(maxCount, count);
        List<Holder> result = holderMapper.selectByPage(contract, from, count);
        result.forEach(one -> {
            BigDecimal balance = new BigDecimal(one.getBalance());
            double percent = balance.divide(totalSupply, 18, RoundingMode.DOWN).doubleValue();
            one.setPercent(percent);
        });
        return result;
    }

    @Override
    public Integer getAssetHolderCount(String contract) {
        Example example = new Example(Holder.class);
        example.and().andEqualTo("contract", contract);
        int count = holderMapper.selectCountByExample(example);
        return count;
    }
}
