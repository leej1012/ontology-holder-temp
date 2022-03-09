package io.ont.schedulers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.ont.entity.Holder;
import io.ont.mapper.EventNotifyMapper;
import io.ont.mapper.HolderMapper;
import io.ont.thread.ParseTransactionThread;
import io.ont.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

@Component
@Slf4j
@EnableScheduling
public class NodeSyncScheduler {
    @Autowired
    private SDKUtil ontSdkUtil;
    @Autowired
    private HolderMapper holderMapper;
    @Autowired
    private EventNotifyMapper eventNotifyMapper;
    @Autowired
    private ParseTransactionThread parseTransaction;

    private static Integer currentHeight;


    /**
     * 同步ontology区块信息
     *
     * @return
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 3 * 1000)
    public void syncOntNodeInfo() {
        try {
            int latestBlockNumber = ontSdkUtil.getBlockHeight();
            if (currentHeight == null) {
                currentHeight = eventNotifyMapper.selectMaxHeight();
            }

            List<Future<List<Holder>>> futureList = new ArrayList<>();
            Set<String> involvedAddress = new HashSet<>();
            while (currentHeight < latestBlockNumber) {
                try {
                    List<Holder> holders = new ArrayList<>();
                    currentHeight++;
                    JSONArray smartCodeEvent = new JSONArray();
                    Object eventByBlockHeight = ontSdkUtil.getEventByBlockHeight(currentHeight);
                    if (eventByBlockHeight instanceof JSONArray) {
                        smartCodeEvent = (JSONArray) eventByBlockHeight;
                    }
                    for (Object obj : smartCodeEvent) {
                        JSONObject event = (JSONObject) obj;
                        // 多线程解析交易并同步等待结果
                        Future<List<Holder>> future = parseTransaction.parseOntTransaction(event, currentHeight, involvedAddress);
                        futureList.add(future);
                    }
                    long start = System.currentTimeMillis();
                    for (Future<List<Holder>> future : futureList) {
                        List<Holder> partHolders = future.get();
                        holders.addAll(partHolders);
                        partHolders.clear();
                    }
                    long end = System.currentTimeMillis();
                    log.info("#####parse block:{} finished, spend:{}", currentHeight, end - start);
                    holderMapper.batchSave(holders);
                    holders.clear();
                } catch (Exception e) {
                    log.error("parse block error.height:{}", currentHeight, e);
                    currentHeight--;
                } finally {
                    futureList.clear();
                    involvedAddress.clear();
                }
            }
        } catch (Exception e) {
            log.error("syncOntNodeInfo error:", e);
        }
    }
}
