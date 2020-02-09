package com.crud.error;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long id) {
        super("Account Number not found : " + id);
    }

}
