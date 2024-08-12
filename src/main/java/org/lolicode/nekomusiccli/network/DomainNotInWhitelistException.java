package org.lolicode.nekomusiccli.network;

public class DomainNotInWhitelistException extends SecurityException {
    public DomainNotInWhitelistException(String message) {
        super(message);
    }
}
