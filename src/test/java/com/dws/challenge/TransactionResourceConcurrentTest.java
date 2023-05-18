package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.helper.TransactionRequestVoBuilder;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.vo.TransactionRequestVo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionResourceConcurrentTest {


    @Autowired
    private AccountsService accountsService;

    @Mock
    private NotificationService notificationService;

    @BeforeAll
    public void init() {
        Account accountDebit = new Account("123", new BigDecimal(99000));
        this.accountsService.createAccount(accountDebit);

        Account accountTo = new Account("321", new BigDecimal(99000));
        this.accountsService.createAccount(accountTo);
    }

    @AfterAll
    void teardown() {
        this.accountsService.getAccountsRepository().clearAccounts();
    }

    @Execution(ExecutionMode.CONCURRENT)
    @RepeatedTest(50)
    public void testTransfer() throws InterruptedException {
        System.out.println("transfer - start [" + Thread.currentThread().getId() + "]");
        // given
        TransactionRequestVo vo = TransactionRequestVoBuilder.oneTransactionRequestVo().now();
        // when
        accountsService.transfer(vo);
        // then
        assertThat(this.accountsService.getAccount("123").getBalance()).isLessThan(new BigDecimal(99000));
        System.out.println("transfer - end [" + Thread.currentThread().getId() + "]");
    }
}
