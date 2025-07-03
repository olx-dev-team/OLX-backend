package uz.pdp.backend.olxapp.service;

public interface CaptchaService {
    boolean verify(String token);
}
