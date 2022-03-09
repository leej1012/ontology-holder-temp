package io.ont.mapper;

import io.ont.entity.EventNotify;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;


@Component
public interface EventNotifyMapper extends Mapper<EventNotify> {
    Integer selectMaxHeight();
}
