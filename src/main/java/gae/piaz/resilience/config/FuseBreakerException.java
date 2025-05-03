package gae.piaz.resilience.config;

public class FuseBreakerException extends RuntimeException {

    public FuseBreakerException(String message) {
        super(message);
    }
}
