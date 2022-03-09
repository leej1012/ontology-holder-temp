package io.ont.service;

import io.ont.entity.Holder;

import java.util.List;

public interface CommonService {
    List<Holder> getAssetHolder(String contract, Integer from, Integer count);

    Integer getAssetHolderCount(String contract);

}
