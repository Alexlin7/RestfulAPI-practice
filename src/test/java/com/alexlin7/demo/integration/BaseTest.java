package com.alexlin7.demo.integration;

import com.alexlin7.demo.auth.AuthRequest;
import com.alexlin7.demo.entity.appUser.AppUser;
import com.alexlin7.demo.entity.appUser.UserAuthority;
import com.alexlin7.demo.repository.AppUserRepository;
import com.alexlin7.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

    protected final ObjectMapper mapper = new ObjectMapper();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String USER_PASSWORD = "12345678";
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected AppUserRepository appUserRepository;
    protected HttpHeaders httpHeaders;

    @Before
    public void initHttpHeader() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @After
    public void clearDB() {
        productRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    protected AppUser createUser(String name, List<UserAuthority> authorities) {
        AppUser appUser = new AppUser();
        appUser.setEmailAddress(name + "@test.com");
        appUser.setPassword(passwordEncoder.encode(USER_PASSWORD));
        appUser.setName(name);
        appUser.setAuthorities(authorities);

        return appUserRepository.insert(appUser);
    }

    protected void login(String emailAddress) throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(emailAddress);
        authRequest.setPassword(USER_PASSWORD);
        MvcResult result = mockMvc.perform(post("/auth")
                        .headers(httpHeaders)
                        .content(mapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JSONObject tokenRes = new JSONObject(result.getResponse().getContentAsString());
        String accessToken = tokenRes.getString("token");
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer" + accessToken);
    }

    protected void logout() {
        httpHeaders.remove(HttpHeaders.AUTHORIZATION);
    }
}
