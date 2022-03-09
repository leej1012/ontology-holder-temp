package io.ont.utils;


import java.math.BigDecimal;
import java.util.*;

/**
 * 常量
 */
public class Constant {
    public static final BigDecimal DECIMAL_EIGHTEEN = new BigDecimal("1000000000000000000");
    public static final BigDecimal DECIMAL_NINE = new BigDecimal("1000000000");
    public static final BigDecimal ONG_DECIMAL = DECIMAL_EIGHTEEN;
    public static final BigDecimal ONT_DECIMAL = DECIMAL_NINE;

    public static final Integer GAS_PRICE = 2500;
    public static final Integer GAS_LIMIT = 20000;

    public static final Map<String, Object> BLANK_PARAM = new HashMap<>();

    public static final String BALANCE_OF = "balanceOf";
    public static final String BALANCE_OF_V2 = "balanceOfV2";
    public static final String TRANSFER_EVENT_HASH = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
    public static final String NATIVE_TRANSFER_EVENT = "transfer";
    public static final String ONT_CONTRACT_ADDRESS = "0100000000000000000000000000000000000000";
    public static final String ONG_CONTRACT_ADDRESS = "0200000000000000000000000000000000000000";
    public static final String GOVERNANCE_ADDRESS = "AFmseVrdL9f9oyCzZefL9tG6UbviEH9ugK";
    public static final String ONT_BASE_ADDRESS = "AFmseVrdL9f9oyCzZefL9tG6UbvhUMqNMV";
    public static final String GOVERNANCE_HEX_ADDRESS = "0700000000000000000000000000000000000000";
    public static final String ONT_HEX_ADDRESS = "0100000000000000000000000000000000000000";
}
