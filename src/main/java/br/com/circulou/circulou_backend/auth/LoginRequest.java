package br.com.circulou.circulou_backend.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String email;
    private String senha;
}
