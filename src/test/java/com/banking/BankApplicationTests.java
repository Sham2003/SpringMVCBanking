package com.banking;


import com.banking.model.User;
import com.banking.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureMockMvc
class BankApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

//	@MockBean
//	private UserService userService;

	@Test
	@Order(1)
	void testIndexPage() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/register")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/login")));
		System.out.println("Index html valid test succeeded");
	}

	@Test
	@Order(2)
	void testLoginPage() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Login")));
		System.out.println("Login html valid test succeeded");
	}

	@Test
	@Order(3)
	void testRegisterPage() throws Exception {
		mockMvc.perform(get("/register"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Create Account")));
		System.out.println("Register html valid test succeeded");
	}

	@Test
	@Order(4)
	void testRegisterUser() throws Exception {
		User existing = userRepository.findByEmail("sham@example.com");
		if(existing != null){
			userRepository.delete(existing);
		}

		//Mockito.doNothing().when(userService).sendOtpEmail(Mockito.anyString(), Mockito.anyString());


		mockMvc.perform(post("/register")
						.param("name", "Sham")
						.param("email", "sham@example.com")
						.param("password", "Secret@123")
						.param("confirmPassword", "Secret@123"))
				.andExpect(status().isOk())
				.andExpect(view().name("verify-registration-otp"));
		System.out.println("Stage 1 user creation done");
		User found = userRepository.findByEmail("sham@example.com");

		assertThat(found).isNotNull();
		assertThat(found.getName()).isEqualTo("Sham");
		System.out.println("User found in DB ");

		mockMvc.perform(post("/verify-registration-otp")
				.param("email", found.getEmail())
				.param("otp", found.getOtp()))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))  // Redirects to login page after successful registration
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Registration successful. Please login.")));
		System.out.println("Register User Test Done Successfully");
	}


	@Test
	@Order(5)
	void testDashboardPage() throws Exception {
		mockMvc.perform(post("/login")
					.param("name", "Sham")
					.param("email", "sham@example.com")
					.param("password", "Secret@123"))
				.andExpect(status().is3xxRedirection())  // ✅ because it's a redirect
				.andExpect(redirectedUrl("/dashboard?email=sham@example.com"));
		System.out.println("Login valid test succeeded");
	}

}
