package com.dws.challenge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestVo {

    @NotNull
    @NotEmpty
    private String accountId;

    @NotNull
    @NotEmpty
    private String accountToId;

    @NotNull
    @Min(value = 1, message = "The value must be greater than 0")
    private BigDecimal value;
}