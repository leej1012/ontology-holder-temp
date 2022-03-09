package io.ont.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import io.ont.entity.Holder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class SDKUtil {
    @Autowired
    private ConfigParam configParam;

    private OntSdk wm;

    private OntSdk getOntSdk() {
        if (wm == null) {
            wm = OntSdk.getInstance();
            wm.setRestful(configParam.ONTOLOGY_RESTFUL_URL);
            wm.openWalletFile("wallet.json");
        }
        if (wm.getWalletMgr() == null) {
            wm.openWalletFile("wallet.json");
        }
        return wm;
    }

    public int getBlockHeight() throws Exception {
        OntSdk ontSdk = getOntSdk();
        int blockHeight = ontSdk.getConnect().getBlockHeight();
        return blockHeight;
    }

    public Object getEventByBlockHeight(int blockHeight) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Object event = ontSdk.getConnect().getSmartCodeEvent(blockHeight);
        return event;
    }

    public BigDecimal getOntBalance(String address) {
        try {
            OntSdk ontSdk = getOntSdk();
            long ontBalance = ontSdk.nativevm().ont().queryBalanceOf(address);
            return BigDecimal.valueOf(ontBalance);
        } catch (Exception e) {
            log.error("getOntBalance error,address:{}", address, e);
        }
        return null;
    }

    public BigDecimal getOntBalanceV2(String address) {
        try {
            OntSdk ontSdk = getOntSdk();
            long ontBalance = ontSdk.nativevm().ontV2().queryBalanceOf(address);
            return BigDecimal.valueOf(ontBalance).divide(Constant.ONT_DECIMAL, 9, RoundingMode.DOWN);
        } catch (Exception e) {
            log.error("getOntBalance error,address:{}", address, e);
        }
        return null;
    }

    public BigDecimal getOngBalance(String address) {
        try {
            OntSdk ontSdk = getOntSdk();
            long ongBalance = ontSdk.nativevm().ong().queryBalanceOf(address);
            return BigDecimal.valueOf(ongBalance).divide(Constant.DECIMAL_NINE, 9, RoundingMode.DOWN);
        } catch (Exception e) {
            log.error("getOngBalance error,address:{}", address, e);
        }
        return null;
    }

    public BigDecimal getOngBalanceV2(String address) {
        try {
            OntSdk ontSdk = getOntSdk();
            BigInteger ongBalance = ontSdk.nativevm().ongV2().queryBalanceOf(address);
            return new BigDecimal(ongBalance).divide(Constant.ONG_DECIMAL, 18, RoundingMode.DOWN);
        } catch (Exception e) {
            log.error("getOngBalance error,address:{}", address, e);
        }
        return null;
    }

    public BigDecimal queryBalanceOfOngV2(String address) {
        try {
            OntSdk ontSdk = getOntSdk();
            if (!StringUtils.isEmpty(address)) {
                List<Object> list = new ArrayList<>();
                list.add(Address.decodeBase58(address));
                byte[] arg = NativeBuildParams.createCodeParamsScript(list);
                Transaction tx = ontSdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ontSdk.nativevm().ong().getContractAddress())), Constant.BALANCE_OF_V2, arg, null, 0L, 0L);
                Object obj = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
                String res = ((JSONObject) obj).getString("Result");
                return res != null && !res.equals("") ? new BigDecimal(new BigInteger(Helper.reverse(res), 16)).divide(Constant.ONG_DECIMAL, 18, RoundingMode.DOWN) : BigDecimal.ZERO;
            }
        } catch (Exception e) {
            log.error("getOngBalance error,address:{}", address, e);
        }
        return null;
    }

    public BigDecimal queryBalanceOfNeoOep4(String address, String contractAddress) {
        try {
            OntSdk ontSdk = getOntSdk();
            List<Object> paramList = new ArrayList<>();
            List<Object> argsList = new ArrayList<>();
            argsList.add(Address.decodeBase58(address));
            paramList.add(Constant.BALANCE_OF.getBytes());
            paramList.add(argsList);
            byte[] params = BuildParams.createCodeParamsScript(paramList);
            Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress), null, params, address, Constant.GAS_LIMIT, Constant.GAS_PRICE);
            JSONObject result = (JSONObject) ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            BigDecimal balance = new BigDecimal(Helper.BigIntFromNeoBytes(Helper.hexToBytes(result.getString("Result"))));
            return balance;
        } catch (Exception e) {
            log.error("queryBalanceOfNeoOep4 error,address:{},contractAddress:{}", address, contractAddress, e);
        }
        return null;
    }

    public Holder getOntOngBalance(String contract, String address) {
        BigInteger balance = null;
        try {
            OntSdk ontSdk = getOntSdk();
            if (Constant.ONT_CONTRACT_ADDRESS.equals(contract)) {
                long ontBalance = ontSdk.nativevm().ont().queryBalanceOf(address);
                balance = BigInteger.valueOf(ontBalance);
            } else if (Constant.ONG_CONTRACT_ADDRESS.equals(contract)) {
                long ongBalance = ontSdk.nativevm().ong().queryBalanceOf(address);
                balance = BigInteger.valueOf(ongBalance);
            }
            if (Constant.GOVERNANCE_ADDRESS.equals(address)) {
                address = Constant.GOVERNANCE_HEX_ADDRESS;
            }
            if (Constant.ONT_BASE_ADDRESS.equals(address)) {
                address = Constant.ONT_HEX_ADDRESS;
            }
            Holder holder = new Holder();
            holder.setAddress(address);
            holder.setContract(contract);
            holder.setBalance(balance);
            return holder;
        } catch (Exception e) {
            log.error("getOntOngBalance error,address:{},contract:{}", address, contract, e);
        }
        return null;
    }

    public BigDecimal getOntOngTotalSupply(String contract) {
        BigDecimal totalSupply = null;
        try {
            OntSdk ontSdk = getOntSdk();
            if (Constant.ONT_CONTRACT_ADDRESS.equals(contract)) {
                long ontTotalSupply = ontSdk.nativevm().ont().queryTotalSupply();
                totalSupply = BigDecimal.valueOf(ontTotalSupply);
            } else if (Constant.ONG_CONTRACT_ADDRESS.equals(contract)) {
                long ongTotalSupply = ontSdk.nativevm().ong().queryTotalSupply();
                totalSupply = BigDecimal.valueOf(ongTotalSupply);
            }
            return totalSupply;
        } catch (Exception e) {
            log.error("getOntOngTotalSupply error,contract:{}", contract, e);
        }
        return null;
    }
}
