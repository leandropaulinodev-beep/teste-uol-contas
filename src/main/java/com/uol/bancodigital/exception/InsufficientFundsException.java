package com.uol.bancodigital.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Saldo insuficiente para realizar a transferência");
    }
}
