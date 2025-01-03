package com.desafio.account.payable.domain.model;

public enum AccountStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    PENDING("PENDING"),
    PAID("PAID"),
    CANCELLED("CANCELLED");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static boolean isValidStatus(String status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
