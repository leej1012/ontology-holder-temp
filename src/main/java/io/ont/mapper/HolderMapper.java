package io.ont.mapper;

import io.ont.entity.Holder;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Component
public interface HolderMapper extends Mapper<Holder> {
    int batchSave(List<Holder> holders);

    List<Holder> selectByPage(String contract, Integer from, Integer count);
}
