package com.alexlin7.demo.controller;

import com.alexlin7.demo.auth.AuthRequest;
import com.alexlin7.demo.auth.JWTServer;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping(value = "/auth",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final JWTServer jwtServer;

    public AuthController(JWTServer jwtServer) {
        this.jwtServer = jwtServer;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> issueToken(@Valid @RequestBody AuthRequest request) {
        String token = jwtServer.generateToken(request);
        Map<String, String> response = Collections.singletonMap("token", token);

        return ResponseEntity.ok(response);
    }

    @Hidden
    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parseToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Map<String, Object> response = jwtServer.parseToken(token);

        return ResponseEntity.ok(response);
    }
}
