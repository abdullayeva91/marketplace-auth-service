package com.marketplace.marketplaceauthservice.service;

import com.marketplace.auth.UserRequest;
import com.marketplace.auth.UserResponse;
import com.marketplace.auth.UserServiceGrpc;
import com.marketplace.marketplaceauthservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;  // ← Import et
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public UserGrpcService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUserById(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı: " + request.getUserId()));

        UserResponse response = UserResponse.newBuilder()
                .setEmail(user.getEmail())
                .setUsername(user.getUsername())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}