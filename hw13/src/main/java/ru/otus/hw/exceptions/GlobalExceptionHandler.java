package ru.otus.hw.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handeNotFoundException(NotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/customError");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        modelAndView.addObject("errorText", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        ModelAndView modelAndView = new ModelAndView("error/accessDeniedError");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        return modelAndView;
    }
}
