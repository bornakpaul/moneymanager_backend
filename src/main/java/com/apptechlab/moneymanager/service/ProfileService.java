package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.AuthDto;
import com.apptechlab.moneymanager.dto.ProfileDto;
import com.apptechlab.moneymanager.dto.RefreshTokenDto;
import com.apptechlab.moneymanager.dto.ResetPasswordDto;
import com.apptechlab.moneymanager.entity.ProfileEntity;
import com.apptechlab.moneymanager.entity.RefreshTokenEntity;
import com.apptechlab.moneymanager.event.DefaultCategoryListener;
import com.apptechlab.moneymanager.event.ProfileActivatedEvent;
import com.apptechlab.moneymanager.repository.*;
import com.apptechlab.moneymanager.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    @Value("${app.activation.url}")
    private String activationUrl;

    private final ProfileRepository profileRepository;
    private final CategoryRepository categoryRepository;
    private final DefaultCategoryListener defaultCategoryListener;
    private final ExpenseRepository expenseRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final IncomeRepository incomeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileDto registerProfile(ProfileDto profileDto){
        ProfileEntity newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        // send activation email
        String activationLink =  activationUrl+"/api/v1.0/activate?token="+ newProfile.getActivationToken();
        String subject = "Activate your Money Manager account";
        String body = "Click on the following link to activate your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject,body);
        return toDto(newProfile);
    }

    public boolean activateProfile(String activationToken){
        return  profileRepository.findByActivationToken(activationToken).map(profile ->
                {
                    profile.setIsActive(true);
                    profileRepository.save(profile);

                    eventPublisher.publishEvent(new ProfileActivatedEvent(this, profile));
                    defaultCategoryListener.handleProfileActivation(new ProfileActivatedEvent(this, profile));
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: "+ email));
    }

    @Transactional
    public ProfileDto updateProfile(ProfileDto profileData){
       ProfileEntity currentUser = this.getCurrentProfile();
       if(!Objects.equals(currentUser.getId(), profileData.getId())){
           throw new RuntimeException("You don't have the access to update this profile");
       }
        ProfileEntity updatedProfile;
       if(Objects.equals(currentUser.getEmail(), profileData.getEmail())){
           currentUser.setFullName(profileData.getFullName());
           currentUser.setProfileImageUrl(profileData.getProfileImageUrl());
           updatedProfile = profileRepository.save(currentUser);
       }else{
           updatedProfile = toEntity(profileData);
           updatedProfile.setPassword(currentUser.getPassword());
           updatedProfile.setActivationToken(UUID.randomUUID().toString());
           updatedProfile = profileRepository.save(updatedProfile);

           // send activation email
           String activationLink =  activationUrl+"/api/v1.0/activate?token="+ updatedProfile.getActivationToken();
           String subject = "Activate your Money Manager account";
           String body = "Click on the following link to activate your account: " + activationLink;
           emailService.sendEmail(updatedProfile.getEmail(), subject,body);
       }

       return toDto(updatedProfile);
    }

    @Transactional
    public ProfileDto updatePassword(Long profileId, String password){
        ProfileEntity currentUser = this.getCurrentProfile();
        if(!Objects.equals(currentUser.getId(), profileId)){
            throw new RuntimeException("You don't have the access to update this profile");
        }
        currentUser.setPassword(passwordEncoder.encode(password));
        ProfileEntity updatedProfile = profileRepository.save(currentUser);
        return toDto(updatedProfile);
    }

    public void initiatePasswordReset(String email){
        ProfileEntity profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        String token = UUID.randomUUID().toString();
        profile.setResetPasswordToken(token);
        profile.setResetPasswordExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));

        profileRepository.save(profile);

        String subject = "Reset your Money Manager password";
        String body = "Your password reset token is: "+ token
                + "\nIt will expire in 15 minutes.";
        emailService.sendEmail(email,subject,body);
    }

    @Transactional
    public ProfileDto resetPassword(ResetPasswordDto resetPasswordDto){
        return profileRepository.findByResetPasswordToken(resetPasswordDto.getToken())
                .filter(profileEntity -> profileEntity.getResetPasswordExpiresAt().isAfter(Instant.now()))
                .map(profileEntity -> {
                    profileEntity.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
                    profileEntity.setResetPasswordToken(null);
                    profileEntity.setResetPasswordExpiresAt(null);
                    profileRepository.save(profileEntity);
                    return toDto(profileEntity);
                }).orElseThrow(() -> new RuntimeException("Reset Password Failed!!"));
    }

    public RefreshTokenDto refreshAccessToken(String requestToken){
        return refreshTokenRepository.findByToken(requestToken)
                .map(this::verifyExpiration)
                .map(tokenEntity -> {
                    ProfileEntity profile = tokenEntity.getProfile();

                    String newAccessToken = jwtUtil.generateToken(profile.getEmail());

                    refreshTokenRepository.delete(tokenEntity);
                    String newRefreshToken = createRefreshToken(profile).getToken();

                    RefreshTokenDto refreshTokenDto = new RefreshTokenDto();

                    refreshTokenDto.setAccessToken(newAccessToken);
                    refreshTokenDto.setRefreshToken(newRefreshToken);

                    return refreshTokenDto;
                }).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }


    @Transactional
    public RefreshTokenEntity createRefreshToken(ProfileEntity profile){
        refreshTokenRepository.deleteByProfile(profile);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .profile(profile)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(604800000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token){
        if(token.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please log in again.");
        }
        return token;
    }

    @Transactional
    public void deleteCurrentProfile() {
        ProfileEntity currentUser = this.getCurrentProfile();
        Long profileId = currentUser.getId();

        expenseRepository.deleteByProfileId(profileId);
        incomeRepository.deleteByProfileId(profileId);
        categoryRepository.deleteByProfileId(profileId);
        refreshTokenRepository.deleteByProfileId(profileId);
        profileRepository.delete(currentUser);

        SecurityContextHolder.clearContext();
    }

    public ProfileDto getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if(email == null){
            currentUser = getCurrentProfile();
        }else{
            currentUser = profileRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: "+ email));
        }
        return toDto(currentUser);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));

            ProfileEntity profile = profileRepository.findByEmail(authDto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: "+ authDto.getEmail()));
            String token = jwtUtil.generateToken(authDto.getEmail());
            RefreshTokenEntity refreshToken = createRefreshToken(profile);
            return Map.of(
                    "token",token,
                    "refreshToken",refreshToken,
                    "user",getPublicProfile(authDto.getEmail()
                    ));
        }catch (Exception e){
            throw new RuntimeException("Invalid email or password" + "\n" + e);
        }
    }

    public ProfileEntity toEntity(ProfileDto profileDto){
        return  ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public ProfileDto toDto(ProfileEntity profileEntity){
        return  ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();

    }
}
