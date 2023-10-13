package com.project.requisition.exception;

import com.project.requisition.model.response.GenericResponse;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;

import static com.project.requisition.constants.ErrorMessage.INVALIDATE_ERROR;
import static com.project.requisition.constants.ErrorMessage.UNEXPECTED_ERROR;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse handleDefaultException(Exception e) {
        return new GenericResponse(UNEXPECTED_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = {
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            ServletRequestBindingException.class,
            MethodArgumentNotValidException.class,
            NoHandlerFoundException.class,
            AsyncRequestTimeoutException.class,
            ErrorResponseException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            BindException.class
    })
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Object> handleServletException(Exception e) {
        var response = new GenericResponse("Servlet Error", e.getMessage());
        return ResponseEntity.of(Optional.of(response));
    }

    @ExceptionHandler(value = { NotFoundException.class, InvalidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GenericResponse handleSpecificException(Exception e) {
        return new GenericResponse(INVALIDATE_ERROR, e.getMessage());
    }
}
