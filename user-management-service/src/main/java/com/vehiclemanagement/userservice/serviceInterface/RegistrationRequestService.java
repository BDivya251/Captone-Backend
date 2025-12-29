package com.vehiclemanagement.userservice.serviceInterface;

public interface RegistrationRequestService {
	 void approveRequest(Long requestId);
	    void rejectRequest(Long requestId);
}
