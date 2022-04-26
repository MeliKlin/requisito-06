package br.com.mercadolivre.projetointegrador.marketplace.controller.advice;

import br.com.mercadolivre.projetointegrador.marketplace.exception.ErrorDTO;
import br.com.mercadolivre.projetointegrador.marketplace.exception.InvalidCategoryException;
import br.com.mercadolivre.projetointegrador.marketplace.exception.NotFoundException;
import br.com.mercadolivre.projetointegrador.marketplace.exception.ProductAlreadyExists;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> notFoundHandler(Exception e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(value = {InvalidCategoryException.class, ProductAlreadyExists.class})
    public ResponseEntity<ErrorDTO> badRequestParams(Exception e) {
        ErrorDTO error = new ErrorDTO();
        error.setError("Parâmetros inválidos.");
        error.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}