package com.proyecto.mangareader.app.service;

import com.proyecto.mangareader.app.request.auth.RegisterRequest;

import com.proyecto.mangareader.app.request.auth.ResendValidatedRequest;
import com.proyecto.mangareader.app.request.users.ChangePasswordRequest;
import com.proyecto.mangareader.app.request.users.ForgotPasswordRequest;
import com.proyecto.mangareader.app.request.users.ResetPasswordRequest;
import com.proyecto.mangareader.app.request.auth.VerificationRequest;
import com.proyecto.mangareader.app.request.users.UpdatedUserRequest;
import com.proyecto.mangareader.app.responses.ok.OkResponse;
import com.proyecto.mangareader.app.responses.user.UserListResponse;
import com.proyecto.mangareader.app.responses.user.UserResponse;
import jakarta.mail.MessagingException;


public interface IUsersService {
     UserListResponse getAllUsers(String username, String email, String role, int offset, int limit, Boolean enabled);

     UserResponse getUserById(Long idUser);

     UserResponse registerUser(RegisterRequest request) throws MessagingException;

     void verifyEmail(VerificationRequest request);

     void forgotPassword(ForgotPasswordRequest request) throws MessagingException;

     void resetPassword(ResetPasswordRequest request);

     OkResponse deleteUser(Long id);

     UserResponse updateUser(Long id, UpdatedUserRequest request);

     void resendValidatedEmail(ResendValidatedRequest request) throws MessagingException;

     void changePassword(Long id, ChangePasswordRequest request);
}
