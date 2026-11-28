package com.marques.onibus_horarios_api.presentation.handler;

import com.marques.onibus_horarios_api.application.exception.BuscaDeHorarioInvalidaException;
import com.marques.onibus_horarios_api.application.exception.CredenciaisInvalidasException;
import com.marques.onibus_horarios_api.application.exception.ErroDeImportacaoException;
import com.marques.onibus_horarios_api.application.exception.ImportacaoSemRotasException;
import com.marques.onibus_horarios_api.domain.exception.CodigoDeLinhaDuplicadoException;
import com.marques.onibus_horarios_api.domain.exception.HorarioDuplicadoException;
import com.marques.onibus_horarios_api.domain.exception.LinhaNaoEncontradaException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ApiExceptionHandler.class.getName());

    @ExceptionHandler(LinhaNaoEncontradaException.class)
    public ProblemDetail handleNotFound(LinhaNaoEncontradaException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({
            CodigoDeLinhaDuplicadoException.class,
            HorarioDuplicadoException.class,
            DataIntegrityViolationException.class
    })
    public ProblemDetail handleConflict(Exception exception) {
        String detail = exception instanceof CodigoDeLinhaDuplicadoException
                || exception instanceof HorarioDuplicadoException
                ? exception.getMessage()
                : "O registro conflita com dados já existentes.";
        return problem(HttpStatus.CONFLICT, detail);
    }

    @ExceptionHandler({
            BuscaDeHorarioInvalidaException.class,
            ErroDeImportacaoException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ProblemDetail handleBadRequest(Exception exception) {
        String detail = switch (exception) {
            case HttpMessageNotReadableException ignored -> "O corpo da requisição é inválido.";
            case MethodArgumentTypeMismatchException mismatch ->
                    "Parâmetro inválido: " + mismatch.getName() + ".";
            case MissingServletRequestParameterException missing ->
                    "Parâmetro obrigatório ausente: " + missing.getParameterName() + ".";
            default -> exception.getMessage();
        };
        return problem(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolations(ConstraintViolationException exception) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "A requisição contém campos inválidos.");
        Map<String, String> errors = new TreeMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errors.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage())
        );
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "A requisição contém campos inválidos.");
        Map<String, String> errors = new TreeMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ImportacaoSemRotasException.class)
    public ProblemDetail handleUnprocessableImport(ImportacaoSemRotasException exception) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail handleInvalidCredentials(CredenciaisInvalidasException exception) {
        return problem(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException exception) {
        String detail = exception.getReason() == null
                ? exception.getStatusCode().toString()
                : exception.getReason();
        return ProblemDetail.forStatusAndDetail(exception.getStatusCode(), detail);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        LOGGER.log(Level.SEVERE, "Erro inesperado ao processar requisição da API.", exception);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno. Tente novamente.");
    }

    private ProblemDetail problem(HttpStatus status, String detail) {
        return ProblemDetail.forStatusAndDetail(status, detail);
    }
}
