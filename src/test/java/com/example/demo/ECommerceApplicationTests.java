package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ECommerceApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void checkRegisterUserSuccess () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("bernard.harvey@gmail.com"))
				.andExpect(jsonPath("$.fullName").value("Bernard Harvey"))
				.andExpect(jsonPath("$.createdAt").isNotEmpty());
	}

	@Test
	public void checkRegisterUserFailed_UsernameExist () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691")));

		//another register request with the same username. this will cause Validation Exception
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Johnson Harvey","bharv10691","bharv10691"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.apierror.message").value("Username exists!"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
		;
	}

	@Test
	public void checkRegisterUserFailed_rePasswordNotMatch () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv106911"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.apierror.message").value("Passwords don't match!"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
		;
	}

	@Test
	public void checkRegisterUserFailed_usernameFailedEmailConstraint () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey","Bernard Harvey","bharv10691","bharv106911"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.apierror.message").value("Validation error"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.apierror.subErrors.[0].field").value("username"))
				.andExpect(jsonPath("$.apierror.subErrors.[0].message").value("must be a well-formed email address"));
		;
	}

	@Test
	public void checkRegisterUserFailed_passwordFailedLengthConstraint () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv","bharv"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.apierror.message").value("Validation error"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.apierror.subErrors.[0].field").value("password"))
				.andExpect(jsonPath("$.apierror.subErrors.[0].message").value("size must be between 8 and 20"));
		;
	}

	@Test
	public void checkLoginUserFailed_UserDoesNotExist () throws Exception {
		mvc.perform(post("/api/user/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getAuthRequestBody("bernard.harvey1@gmail.com","bharv10691"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(header().doesNotExist("Authorization"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("NOT_FOUND"))
				.andExpect(jsonPath("$.apierror.message").value("User was not found for parameters {username=bernard.harvey1@gmail.com}"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty());
	}

	@Test
	public void checkLoginUserFailed_PasswordIncorrect () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691")));

		mvc.perform(post("/api/user/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getAuthRequestBody("bernard.harvey@gmail.com","bharv106911"))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andExpect(header().doesNotExist("Authorization"));
	}

	@Test
	public void checkFindUserByIdSuccess () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691")));

		mvc.perform(get("/api/user/id/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("bernard.harvey@gmail.com"))
				.andExpect(jsonPath("$.fullName").value("Bernard Harvey"))
				.andExpect(jsonPath("$.createdAt").isNotEmpty());
		;
	}

	@Test
	public void checkFindUserByUsernameSuccess () throws Exception {
		mvc.perform(post("/api/user/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691")));

		mvc.perform(get("/api/user/bernard.harvey@gmail.com"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.username").value("bernard.harvey@gmail.com"))
				.andExpect(jsonPath("$.fullName").value("Bernard Harvey"))
				.andExpect(jsonPath("$.createdAt").isNotEmpty());
		;
	}

	@Test
	public void checkFindUserByIdFailed_UserNotFound () throws Exception {
		mvc.perform(get("/api/user/id/2"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("NOT_FOUND"))
				.andExpect(jsonPath("$.apierror.message").value("User was not found for parameters {id=2}"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty());
		;
	}

	@Test
	public void checkFindUserByUsername_UserNotFound () throws Exception {
		mvc.perform(get("/api/user/bernard.harvey1@gmail.com"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("NOT_FOUND"))
				.andExpect(jsonPath("$.apierror.message").value("User was not found for parameters {username=bernard.harvey1@gmail.com}"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty());
		;
	}

	@Test
	public void checkFindAllItemsFailed_Unauthenticated () throws Exception {
		mvc.perform(get("/api/item"))
				.andExpect(status().isUnauthorized())
		;
	}

	@Test
	public void checkFindItemByIdFailed_Unauthenticated () throws Exception {
		mvc.perform(get("/api/item/1"))
				.andExpect(status().isUnauthorized())
		;
	}

	@Test
	public void checkFindItemByNameFailed_Unauthenticated () throws Exception {
		mvc.perform(get("/api/item/watch"))
				.andExpect(status().isUnauthorized())
		;
	}

	@Test
	public void checkFindAllItemsSuccess () throws Exception {
		mvc.perform(get("/api/item").header("Authorization",getAuthToken()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect((jsonPath("$",hasSize(3))))
				.andExpect((jsonPath("$.[0].id").value(1)))
				.andExpect((jsonPath("$.[0].name").value("TISSOT SEASTAR 1000 CHRONOGRAPH")))
				.andExpect((jsonPath("$.[1].id").value(2)))
				.andExpect((jsonPath("$.[1].name").value("TISSOT GENTLEMAN")))
				.andExpect((jsonPath("$.[2].id").value(3)))
				.andExpect((jsonPath("$.[2].name").value("TISSOT LE LOCLE POWERMATIC 80")))
		;
	}

	@Test
	public void checkFindItemByIdSuccess () throws Exception {
		mvc.perform(get("/api/item/3").header("Authorization",getAuthToken()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect((jsonPath("$.id").value(3)))
				.andExpect((jsonPath("$.name").value("TISSOT LE LOCLE POWERMATIC 80")))
				.andExpect((jsonPath("$.price").value(890)))
				.andExpect((jsonPath("$.description").value("The name Le Locle seems to be a reliable ingredient of success.")))
		;
	}

	@Test
	public void checkFindItemByNameSuccess () throws Exception {
		mvc.perform(get("/api/item/name/TISSOT LE LOCLE POWERMATIC 80").header("Authorization",getAuthToken()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect((jsonPath("$.[0].id").value(3)))
				.andExpect((jsonPath("$.[0].name").value("TISSOT LE LOCLE POWERMATIC 80")))
				.andExpect((jsonPath("$.[0].price").value(890)))
				.andExpect((jsonPath("$.[0].description").value("The name Le Locle seems to be a reliable ingredient of success.")))
		;
	}

	@Test
	public void checkFindItemByIdFailed_ItemNotFound () throws Exception {
		mvc.perform(get("/api/item/4").header("Authorization",getAuthToken()))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("NOT_FOUND"))
				.andExpect(jsonPath("$.apierror.message").value("Item was not found for parameters {id=4}"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty());
		;
	}

	@Test
	public void checkFindItemByNameFailed_ItemNotFound () throws Exception {
		mvc.perform(get("/api/item/name/TISSOT").header("Authorization",getAuthToken()))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.apierror.status").value("NOT_FOUND"))
				.andExpect(jsonPath("$.apierror.message").value("Item was not found for parameters {name=TISSOT}"))
				.andExpect(jsonPath("$.apierror.timestamp").isNotEmpty());
		;
	}

	private static String getCreateUserRequestBody (String username, String fullName, String password, String rePassword){
		return "{\n" +
				"    \"username\":" + "\"" + username + "\"" + ",\n" +
				"    \"fullName\":" + "\"" + fullName + "\"" + ",\n" +
				"    \"password\":" + "\"" + password + "\"" + ",\n" +
				"    \"rePassword\":" + "\"" + rePassword + "\"" + "\n" +
				"}";
	}

	private static String getAuthRequestBody (String username, String password){
		return "{\n" +
				"    \"username\":" + "\"" + username + "\"" + ",\n" +
				"    \"password\":" + "\"" + password + "\"" + "\n" +
				"}";
	}

	private static String getAuthToken () {
		return "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxLG5oYW50aGFuaHRAZ21haWwuY29tIiwiaXNzIjoiZXhhbXBsZS5pbyIsImlhdCI6MTYxMjA3NzAwMiwiZXhwIjoxNjEyNjgxODAyfQ.S7lPuiWXD4wvwQ0y3QyrPrpNXf5ih_0ECqjF7upf_2AnANZLBFrQnycpmCGXNI2dK6GPI4MAH48VazAQJdILww";
	}
}
