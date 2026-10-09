package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.AppUser;
import com.aptitudearena.backend.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Optional<AppUser> findById(UUID id) {
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }

    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }
}