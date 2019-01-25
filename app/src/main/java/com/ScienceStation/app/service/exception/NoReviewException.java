package com.ScienceStation.app.service.exception;

public class NoReviewException extends Exception {
    public NoReviewException(){super("You can't accept this Journal without sending it to be reviewed first!");}
}
