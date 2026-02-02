package br.com.gundam.handler;

import br.com.gundam.exception.GundamResourceNotFoundException;
import br.com.gundam.exception.GundamStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GundamResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(GundamResourceNotFoundException ex, Model model) {
        logger.error("Resource Not Found: {}", ex.getMessage());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(GundamStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleStorageException(GundamStorageException ex, Model model) {
        logger.error("Storage Error: {}", ex.getMessage(), ex);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Erro de Armazenamento: " + ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleDatabaseException(org.springframework.dao.DataAccessException ex, Model model) {
        logger.error("Database Connection Failure: {}", ex.getMessage(), ex);
        model.addAttribute("errorCode", "503");
        model.addAttribute("errorMessage",
                "Serviço indisponível temporariamente. Verifique a conexão com o banco de dados.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        logger.error("Unexpected Error: {}", ex.getMessage(), ex);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "Ocorreu um erro inesperado no sistema Gundam.");
        return "error/error";
    }
}
