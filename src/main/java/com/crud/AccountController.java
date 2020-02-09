package com.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.crud.Account;
import com.crud.AccountRepository;
import com.crud.error.AccountNotFoundException;
@RestController
public class AccountController {
	@Autowired
    private AccountRepository repository;

    // Find
    @GetMapping("/accounts")
    List<Account> findAll() {
        return repository.findAll();
    }

    // Save
    @PostMapping("/accounts")
    //return 201 instead of 200
    @ResponseStatus(HttpStatus.CREATED)
    Account newAccount(@RequestBody Account newAccount) {
        return repository.save(newAccount);
    }

    // Find
    @GetMapping("/accounts/{accountNumber}")
    Account findOne(@PathVariable Long accountNumber) {
        return repository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    // Save or update
    @PutMapping("/accounts/{accountNumber}")
    Account saveOrUpdate(@RequestBody Account newAccount, @PathVariable Long accountNumber) {

         return repository.findById(accountNumber)
                .map(x -> {
                    x.setAccountNumber(newAccount.getAccountNumber());
                    x.setAccountBalance(newAccount.getAccountBalance());
                    return repository.save(x);
                })
                .orElseGet(() -> {
                    newAccount.setAccountNumber(accountNumber);
                    return repository.save(newAccount);
                });
    }

    @DeleteMapping("/accounts/{accountNumber}")
    void deleteAccount(@PathVariable Long accountNumber) {
        repository.deleteById(accountNumber);
    }

}
