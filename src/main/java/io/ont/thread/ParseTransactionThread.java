package io.ont.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.io.BinaryReader;
import io.ont.entity.EventNotify;
import io.ont.entity.Holder;
import io.ont.exception.OntologyHolderException;
import io.ont.mapper.EventNotifyMapper;
import io.ont.utils.Constant;
import io.ont.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;


/**
 * @author Lee
 */
@Slf4j
@Service
@EnableAutoConfiguration
public class ParseTransactionThread {
    @Autowired
    private SDKUtil ontSdkUtil;
    @Autowired
    private EventNotifyMapper eventNotifyMapper;

    @Async("asyncTaskExecutor")
    public Future<List<Holder>> parseOntTransaction(JSONObject event, int blockHeight, Set<String> involvedAddress) throws IOException {
        List<Holder> holders = new ArrayList<>();
        String txHash = event.getString("TxHash");
        Integer state = event.getInteger("State");
        BigInteger gasConsumed = event.getBigInteger("GasConsumed");
        JSONArray notifyArray = event.getJSONArray("Notify");
        JSONArray statesArray;
        Boolean needLogEvent = false;
        for (Object notifyObj : notifyArray) {
            JSONObject notify = (JSONObject) notifyObj;
            String contractAddress = notify.getString("ContractAddress");
            if (Constant.ONT_CONTRACT_ADDRESS.equals(contractAddress) || Constant.ONG_CONTRACT_ADDRESS.equals(contractAddress)) {
                // ONT,ONG交易
                needLogEvent = true;
                Object statesObj = notify.get("States");
                if (statesObj instanceof JSONArray) {
                    statesArray = (JSONArray) statesObj;
                    String method = statesArray.getString(0);
                    if (Constant.NATIVE_TRANSFER_EVENT.equals(method)) {
                        // ONT,ONG转账
                        String fromAddress = statesArray.getString(1);
                        String toAddress = statesArray.getString(2);
                        String fromAddrContract = fromAddress + contractAddress;
                        if (involvedAddress.add(fromAddrContract)) {
                            Holder holder = ontSdkUtil.getOntOngBalance(contractAddress, fromAddress);
                            if (holder == null) {
                                throw new OntologyHolderException("get holder error");
                            }
                            holders.add(holder);
                        }
                        String toAddrContract = toAddress + contractAddress;
                        if (involvedAddress.add(toAddrContract)) {
                            Holder holder = ontSdkUtil.getOntOngBalance(contractAddress, toAddress);
                            if (holder == null) {
                                throw new OntologyHolderException("get holder error");
                            }
                            holders.add(holder);
                        }
                    }
                } else if (statesObj instanceof String && Constant.ONG_CONTRACT_ADDRESS.equals(contractAddress)) {
                    // evm的ONG转账
                    String statesStr = (String) statesObj;
                    String states = statesStr.substring(2);
                    byte[] bys = Helper.hexToBytes(states);
                    ByteArrayInputStream bais = new ByteArrayInputStream(bys);
                    BinaryReader br = new BinaryReader(bais);
                    br.readBytes(20);
                    br.readInt();
                    byte[] eventHashBytes = br.readBytes(32);
                    String eventHash = Helper.toHexString(eventHashBytes);
                    if (Constant.TRANSFER_EVENT_HASH.equalsIgnoreCase(eventHash)) {
                        byte[] fromBytes = br.readBytes(32);
                        String fromAddress = Helper.toHexString(fromBytes).substring(24);
                        String fromBase58Address = Address.parse(fromAddress).toBase58();
                        byte[] toBytes = br.readBytes(32);
                        String toAddress = Helper.toHexString(toBytes).substring(24);
                        String toBase58Address = Address.parse(toAddress).toBase58();

                        String fromAddrContract = fromBase58Address + contractAddress;
                        if (involvedAddress.add(fromAddrContract)) {
                            Holder holder = ontSdkUtil.getOntOngBalance(contractAddress, fromBase58Address);
                            if (holder == null) {
                                throw new OntologyHolderException("get holder error");
                            }
                            holders.add(holder);
                        }
                        String toAddrContract = toBase58Address + contractAddress;
                        if (involvedAddress.add(toAddrContract)) {
                            Holder holder = ontSdkUtil.getOntOngBalance(contractAddress, toBase58Address);
                            if (holder == null) {
                                throw new OntologyHolderException("get holder error");
                            }
                            holders.add(holder);
                        }
                    }
                }
            }
        }
        if (needLogEvent) {
            EventNotify eventNotify = new EventNotify();
            eventNotify.setTxHash(txHash);
            eventNotify.setHeight(blockHeight);
            eventNotify.setState(state);
            eventNotify.setGasConsumed(gasConsumed);
            eventNotify.setNotify(JSON.toJSONString(notifyArray));
            try {
                eventNotifyMapper.insertSelective(eventNotify);
            } catch (DuplicateKeyException e) {
                log.error("eventNotify already exist");
            }
        }
        return new AsyncResult<>(holders);
    }
}
