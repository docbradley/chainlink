package com.adamdbradley.chainlink.exceptions;

public class CommunicationFailureException extends Exception {

    private static final long serialVersionUID = -1095997280808006673L;

    public CommunicationFailureException(String message) {
        super(message);
    }

}
