package com.ccs.SecretSantaApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class EntityNotCreated extends Exception{
    public EntityNotCreated(String message){
        super(message);
    }
}
