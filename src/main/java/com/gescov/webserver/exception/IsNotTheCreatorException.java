package com.gescov.webserver.exception;

import com.gescov.webserver.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class IsNotTheCreatorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IsNotTheCreatorException(Class<User> c, String creatorID, String schoolID) {
        super(c.getSimpleName() + " 'id' " + creatorID + " is not the creator of the school " + schoolID);
    }

}
