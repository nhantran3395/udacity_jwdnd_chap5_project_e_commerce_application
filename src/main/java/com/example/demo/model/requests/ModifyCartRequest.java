package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ModifyCartRequest {
	
	@JsonProperty
	@NotBlank
	@Email
	private String username;
	
	@JsonProperty
	@NotBlank
	private long itemId;
	
	@JsonProperty
	@NotBlank
	private int quantity;
}
