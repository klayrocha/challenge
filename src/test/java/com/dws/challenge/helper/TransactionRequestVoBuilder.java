package com.dws.challenge.helper;

import com.dws.challenge.vo.TransactionRequestVo;
import java.math.BigDecimal;

/**
 * Class builder for TransactionRequestVo class
 *
 * @author klayrocha
 */
public class TransactionRequestVoBuilder {

    private TransactionRequestVo vo;

    private TransactionRequestVoBuilder() {
    }

    public static TransactionRequestVoBuilder oneTransactionRequestVo() {
        TransactionRequestVoBuilder builder = new TransactionRequestVoBuilder();
        builder.vo = new TransactionRequestVo();
        builder.vo.setAccountId("123");
        builder.vo.setAccountToId("321");
        builder.vo.setValue(new BigDecimal(100));
        return builder;
    }

    public TransactionRequestVoBuilder valueNegative() {
        vo.setValue(new BigDecimal(-1));
        return this;
    }

    public TransactionRequestVoBuilder highValue() {
        vo.setValue(new BigDecimal(9000000));
        return this;
    }

    public TransactionRequestVoBuilder idDebitInvalid() {
        vo.setAccountId("xxx");
        return this;
    }

    public TransactionRequestVo now() {
        return vo;
    }
}