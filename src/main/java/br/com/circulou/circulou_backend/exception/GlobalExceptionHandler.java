package br.com.circulou.circulou_backend.exception;

import br.com.circulou.circulou_backend.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorResponseDTO.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponseDTO.ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Dados inválidos")
                .mensagem("Um ou mais campos apresentam erros de validação")
                .erros(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Recurso não encontrado")
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Erro de negócio")
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro("Erro inesperado")
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro("Erro interno do servidor")
                .mensagem(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
