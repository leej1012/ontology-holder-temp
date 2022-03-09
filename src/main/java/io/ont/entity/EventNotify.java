package io.ont.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

@Table(name = "eventnotify")
@Data
public class EventNotify {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String txHash;

    private Integer height;

    private Integer state;

    private BigInteger gasConsumed;

    private String notify;
}
