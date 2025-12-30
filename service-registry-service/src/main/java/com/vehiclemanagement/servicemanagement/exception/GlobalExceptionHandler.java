package com.vehiclemanagement.servicemanagement.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String,Object>> handleResourceNotFoundException(ResourceNotFoundException ex){
		Map<String,Object> errorResponse=new HashMap<>();
		errorResponse.put("status", HttpStatus.NOT_FOUND.value());
		errorResponse.put("message", ex.getMessage());
		return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String,Object>> handleBadRequestException(BadRequestException ex){
		Map<String,Object> errorResponse = new HashMap<>();
		errorResponse.put("message",ex.getMessage());
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
	}
}
