package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.TransactionRuleException;
import com.dws.challenge.helper.TransactionRequestVoBuilder;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.vo.TransactionRequestVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void init() {
        Account accountDebit = new Account("123", new BigDecimal(1000));
        this.accountsService.createAccount(accountDebit);

        Account accountTo = new Account("321", new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);
    }

    @AfterEach
    void teardown() {
        this.accountsService.getAccountsRepository().clearAccounts();
    }

    @Test
    void addAccount() {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);

        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    void addAccount_failsOnDuplicateId() {
        String uniqueId = "Id-" + System.currentTimeMillis();
        Account account = new Account(uniqueId);
        this.accountsService.createAccount(account);

        try {
            this.accountsService.createAccount(account);
            fail("Should have failed when adding duplicate account");
        } catch (DuplicateAccountIdException ex) {
            assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
        }
    }

    // New tests
    @Test
    public void testTransfer() {
        // given
        TransactionRequestVo vo = TransactionRequestVoBuilder.oneTransactionRequestVo().now();
        // when
        accountsService.transfer(vo);
        // then
        assertThat(this.accountsService.getAccount("123").getBalance()).isEqualByComparingTo("900");
    }

    @Test
    public void testTransferValueNegative() {
        // give
        TransactionRequestVo vo = TransactionRequestVoBuilder.oneTransactionRequestVo().valueNegative().now();
        try {
            // when
            accountsService.transfer(vo);
            fail("Should have failed when try transfer with negative value");
        } catch (TransactionRuleException ex) {
            // then
            assertThat(ex.getMessage()).isEqualTo("The value must be greater than 0");
        }
    }

    @Test
    public void testTransferInsufficientBalance() {
        // give
        TransactionRequestVo vo = TransactionRequestVoBuilder.oneTransactionRequestVo().highValue().now();
        try {
            //when
            accountsService.transfer(vo);
            fail("Should have failed when try transfer with insufficient balance");
        } catch (TransactionRuleException ex) {
            // then
            assertThat(ex.getMessage()).isEqualTo("Account debit with insufficient balance");
        }
    }

    @Test
    public void testTransferAccountIdInvalid() {
        // give
        TransactionRequestVo vo = TransactionRequestVoBuilder.oneTransactionRequestVo().idDebitInvalid().now();
        try {
            // when
            accountsService.transfer(vo);
            fail("Should have failed when try transfer with invalid id");
        } catch (AccountNotFoundException ex) {
            // then
            assertThat(ex.getMessage()).isEqualTo("Invalid account accountId xxx");
        }
    }

    @Test
    public void testNotifyAboutTransfer() {
        //  when
        notificationService.notifyAboutTransfer(any(), any());
        notificationService.notifyAboutTransfer(any(), any());

        // then
        verify(notificationService, times(2)).notifyAboutTransfer(any(), any());
    }
}
