package pl.coderslab.cultureBuddies.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.net.BindException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {InvalidDataFromExternalRestApiException.class,
            EmptyKeysException.class, NotExistingRecordException.class, RelationshipAlreadyCreatedException.class})
    public ModelAndView handleConflict(HttpServletRequest request,
                                       Exception ex)  {
        log.debug("Handling exception {}. Request address {}", ex.toString(), request.getRequestURI());
        log.debug("{}", ex.getMessage());
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", ex.getLocalizedMessage());
        mav.setViewName("/errors/error");
        return mav;
    }
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestPartException.class,
            BindException.class,
            NoHandlerFoundException.class,
            AsyncRequestTimeoutException.class})
    public ModelAndView handleAnotherConflict(HttpServletRequest request,
                                       Exception ex)  {
        log.debug("Handling exception {}. Request address {}", ex.toString(), request.getRequestURI());
        log.debug("{}", ex.getMessage());
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", "Something went wrong");
        mav.setViewName("/errors/error");
        return mav;
    }
}