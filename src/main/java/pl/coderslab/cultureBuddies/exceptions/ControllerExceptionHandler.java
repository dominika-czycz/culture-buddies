package pl.coderslab.cultureBuddies.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {Exception.class})
    public ModelAndView handleConflict(HttpServletRequest request,
                                       Exception ex) throws Exception {
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }
        log.debug("Handling exception {}. Request address {}", ex.toString(), request.getRequestURI());
        log.debug("{}", ex.getMessage());
        ex.printStackTrace();
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", ex.getLocalizedMessage());
        mav.setViewName("/errors/error");
        return mav;
    }
}