package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.TransactionRuleException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.vo.TransactionRequestVo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountsService {


    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        Account account = this.accountsRepository.getAccount(accountId);
        if (account == null) {
            throw new AccountNotFoundException("Invalid account accountId " + accountId);
        }
        return account;
    }

    /**
     * Method responsible for transferring money
     *
     * @param vo Information transaction
     */
    public void transfer(TransactionRequestVo vo) {
        log.info("transfer - start [" + Thread.currentThread().getId() + "]");
        EntityLockWrapper<Account> accountDebitEntityLockWrapper = null;
        EntityLockWrapper<Account> accountCreditEntityLockWrapper = null;

        try {
            // Check negative value
            isValueNegative(vo);

            // Get debit account and try lock it
            Account accountDebit = getAccount(vo.getAccountId());
            accountDebitEntityLockWrapper = new EntityLockWrapper<>(accountDebit);
            accountDebitEntityLockWrapper.tryLock();
            log.info("Account debit locked [" + +Thread.currentThread().getId() + "]");

            // check debit account balance
            isBalanceNotSufficient(accountDebit, vo.getValue());

            // get credit account and try lock it
            Account accountCredit = getAccount(vo.getAccountToId());
            accountCreditEntityLockWrapper = new EntityLockWrapper<>(accountCredit);
            accountCreditEntityLockWrapper.tryLock();
            log.info("Account credit locked [" + Thread.currentThread().getId() + "]");

            // debit and credit accounts are locked, then is secure change them
            accountsRepository.executeTransfer(accountCredit, accountDebit, vo.getValue());

            sendNotification(accountCredit, accountDebit, vo.getValue());
        } finally {
            // unlock account objects
            unlockAccount(accountDebitEntityLockWrapper, "Account debit unlocked");
            unlockAccount(accountCreditEntityLockWrapper, "Account credit unlocked");
            log.info("transfer - end [" + Thread.currentThread().getId() + "]");
        }
    }

    private void sendNotification(Account accountCredit, Account accountDebit, BigDecimal value) {
        log.info("Sending notification");
        this.notificationService.notifyAboutTransfer(accountCredit, "Credited value " + value + " from " + accountDebit.getAccountId());
        this.notificationService.notifyAboutTransfer(accountDebit, "Debited value " + value + " to " + accountCredit.getAccountId());
    }

    private void isValueNegative(TransactionRequestVo vo) {
        if (vo.getValue().intValue() <= 0) {
            throw new TransactionRuleException("The value must be greater than 0");
        }
    }

    private void isBalanceNotSufficient(Account accountDebit, BigDecimal value) {
        if (accountDebit.getBalance().compareTo(value) < 0) {
            throw new TransactionRuleException("Account debit with insufficient balance");
        }
    }

    private void unlockAccount(EntityLockWrapper<Account> accountDebitEntityLockWrapper, String logMessage) {
        if (accountDebitEntityLockWrapper != null) {
            accountDebitEntityLockWrapper.unlock();
            log.info(logMessage + "[" + Thread.currentThread().getId() + "]");
        }
    }
}
