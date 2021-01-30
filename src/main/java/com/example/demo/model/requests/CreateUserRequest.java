package com.example.demo.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class CreateUserRequest {

	@NotBlank
	@Email
	private String username;
	@NotBlank
	private String fullName;
	@NotBlank
	@Size(min = 8, max = 20)
	private String password;
	@NotBlank
	private String rePassword;
}
