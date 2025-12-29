package com.vehiclemanagement.userservice.service;

import org.springframework.stereotype.Service;

import com.vehiclemanagement.userservice.repository.UserRepository;

@Service
public class UserServiceImple {
	private final  UserRepository userRepository;
	public UserServiceImple(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
}
