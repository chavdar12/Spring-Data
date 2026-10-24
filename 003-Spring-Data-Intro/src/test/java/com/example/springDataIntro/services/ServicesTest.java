package com.example.springDataIntro.services;

import com.example.springDataIntro.models.Account;
import com.example.springDataIntro.repository.AccountRepository;
import com.example.springDataIntro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({UserServiceImpl.class, AccountServiceImpl.class})
class ServicesTest {

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    private Account registerPesho() {
        userService.register("Pesho", 25, new BigDecimal("1000"));
        return accountRepository.findAll().get(0);
    }

    @Test
    void registerCreatesUserWithInitialAccount() {
        Account account = registerPesho();

        assertThat(userRepository.findAll()).singleElement().satisfies(user -> {
            assertThat(user.getUsername()).isEqualTo("Pesho");
            assertThat(user.getAge()).isEqualTo(25);
        });
        assertThat(account.getBalance()).isEqualByComparingTo("1000");
        assertThat(account.getUser().getUsername()).isEqualTo("Pesho");
    }

    @Test
    void addAccountAttachesNewAccountToUser() {
        Long userId = registerPesho().getUser().getId();

        userService.addAccount(new BigDecimal("50"), userId);

        assertThat(accountRepository.findAll()).hasSize(2).allMatch(a -> a.getUser().getId().equals(userId));
    }

    @Test
    void addAccountRejectsUnknownUser() {
        assertThatThrownBy(() -> userService.addAccount(BigDecimal.TEN, 404L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdrawAndTransferChangeBalance() {
        Long accountId = registerPesho().getId();

        accountService.withdrawMoney(new BigDecimal("150"), accountId);
        accountService.transferMoney(new BigDecimal("500"), accountId);

        assertThat(accountRepository.findById(accountId).orElseThrow().getBalance()).isEqualByComparingTo("1350");
    }

    @Test
    void withdrawRejectsInsufficientFunds() {
        Long accountId = registerPesho().getId();

        assertThatThrownBy(() -> accountService.withdrawMoney(new BigDecimal("1000.01"), accountId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(accountRepository.findById(accountId).orElseThrow().getBalance()).isEqualByComparingTo("1000");
    }

    @Test
    void operationsRejectUnknownAccount() {
        assertThatThrownBy(() -> accountService.withdrawMoney(BigDecimal.ONE, 404L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> accountService.transferMoney(BigDecimal.ONE, 404L)).isInstanceOf(IllegalArgumentException.class);
    }
}
