package com.dws.challenge;


import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private WebApplicationContext webApplicationContext;


    private static final String ACCOUNT_DEBIT_ID = "123";
    private static final String ACCOUNT_CREDIT_ID = "321";
    private static final String ACCOUNT_INVALID_ID = "XXX";
    private static final String VALUE_BALANCE = "1000";
    private static final String VALUE_TRANSFER = "100";
    private static final String VALUE_EXPECTED = "900";
    private static final String VALUE_NEGATIVE = "-1";
    private static final String VALUE_HIGH = "999999";

    @BeforeEach
    void prepareMockMvc() throws Exception {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"" + ACCOUNT_CREDIT_ID + "\",\"balance\":" + VALUE_BALANCE + "}")).andExpect(status().isCreated());

        this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":\"" + ACCOUNT_DEBIT_ID + "\",\"balance\":" + VALUE_BALANCE + "}")).andExpect(status().isCreated());
    }

    @AfterEach
    void teardown() {
        this.accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    public void testTransfer() throws Exception {
        this.mockMvc
                .perform(post("/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + ACCOUNT_DEBIT_ID + "\",\"accountToId\":\"" + ACCOUNT_CREDIT_ID + "\",\"value\":" + VALUE_TRANSFER + "}"))
                .andExpect(status().isOk());

        Account account = accountsService.getAccount(ACCOUNT_DEBIT_ID);
        assertThat(account.getBalance()).isEqualByComparingTo(VALUE_EXPECTED);
    }

    @Test
    public void testTransferValueNegative() throws Exception {
        this.mockMvc
                .perform(post("/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + ACCOUNT_DEBIT_ID + "\",\"accountToId\":\"" + ACCOUNT_CREDIT_ID + "\",\"value\":" + VALUE_NEGATIVE + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{" +
                        "\"status\":\"BAD_REQUEST\"," +
                        "\"message\":\"[The value must be greater than 0]\"" +
                        "}"));
    }

    @Test
    public void testTransferInsufficientBalance() throws Exception {
        this.mockMvc
                .perform(post("/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + ACCOUNT_DEBIT_ID + "\",\"accountToId\":\"" + ACCOUNT_CREDIT_ID + "\",\"value\":" + VALUE_HIGH + "}"))
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string("{" +
                        "\"status\":\"EXPECTATION_FAILED\"," +
                        "\"message\":\"Account debit with insufficient balance\"" +
                        "}"));
    }

    @Test
    public void testTransferAccountIdInvalid() throws Exception {
        this.mockMvc
                .perform(post("/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + ACCOUNT_INVALID_ID + "\",\"accountToId\":\"" + ACCOUNT_INVALID_ID + "\",\"value\":" + VALUE_TRANSFER + "}"))
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string("{" +
                        "\"status\":\"EXPECTATION_FAILED\"," +
                        "\"message\":\"Invalid account accountId XXX\"" +
                        "}"));
    }
}
