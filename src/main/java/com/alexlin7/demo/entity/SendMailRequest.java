package com.alexlin7.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SendMailRequest {

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @NotEmpty
    private String[] receivers;

}
