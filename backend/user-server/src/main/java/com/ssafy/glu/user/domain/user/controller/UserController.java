package com.ssafy.glu.user.domain.user.controller;

import static com.ssafy.glu.user.global.util.HeaderUtil.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.glu.user.domain.user.dto.request.AttendanceRequest;
import com.ssafy.glu.user.domain.user.dto.request.ExpUpdateRequest;
import com.ssafy.glu.user.domain.user.dto.request.UserRegisterRequest;
import com.ssafy.glu.user.domain.user.dto.request.UserUpdateRequest;
import com.ssafy.glu.user.domain.user.dto.response.AttendanceResponse;
import com.ssafy.glu.user.domain.user.dto.response.ExpUpdateResponse;
import com.ssafy.glu.user.domain.user.dto.response.UserResponse;
import com.ssafy.glu.user.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody UserRegisterRequest userRegisterRequest) {
		userService.register(userRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<UserResponse> getUser(@RequestHeader(USER_ID) Long userId) {
		UserResponse user = userService.getUser(userId);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@PutMapping
	public ResponseEntity<Void> updateUser(@RequestHeader(USER_ID) Long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
		userService.updateUser(userId, userUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteUser(@RequestHeader(USER_ID) Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/check")
	public ResponseEntity<Boolean> checkUser(String id) {
		Boolean result = userService.checkUser(id);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/attendance")
	public ResponseEntity<List<AttendanceResponse>> getAttendance(@RequestHeader(USER_ID) Long userId, AttendanceRequest request) {
		List<AttendanceResponse> result = userService.getAttendance(userId, request);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}


	@PutMapping("/exp")
	public ResponseEntity<ExpUpdateResponse> updateExp(@RequestHeader(USER_ID) Long userId, @RequestBody ExpUpdateRequest expUpdateRequest) {
		ExpUpdateResponse result = userService.updateExp(userId, expUpdateRequest);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}


	@GetMapping("/all")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		List<UserResponse> result = userService.getAll();
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
