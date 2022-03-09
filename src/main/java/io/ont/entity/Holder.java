package io.ont.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

@Table(name = "holder")
@Data
public class Holder {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String address;

    @Id
    private String contract;

    private BigInteger balance;

    private Double percent;

}
