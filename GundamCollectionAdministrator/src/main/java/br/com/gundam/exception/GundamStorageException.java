package br.com.gundam.exception;

public class GundamStorageException extends RuntimeException {
    public GundamStorageException(String message) {
        super(message);
    }

    public GundamStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
