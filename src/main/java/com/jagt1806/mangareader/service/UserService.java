package com.jagt1806.mangareader.service;

import com.jagt1806.mangareader.http.request.user.ChangePasswordRequest;
import com.jagt1806.mangareader.http.request.user.UserUpdateRequest;
import com.jagt1806.mangareader.http.response.user.UserListResponse;
import com.jagt1806.mangareader.http.response.user.UserResponse;

public interface UserService {
  UserListResponse getUsers(String username, String email, String role, int offset, int limit, Boolean enabled);

  UserResponse getUserById(Long id);

  void updateUser(Long id, UserUpdateRequest request);

  void changePassword(Long id, ChangePasswordRequest request);

  void deleteUser(Long id);
}
