package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.AuthDto;
import com.apptechlab.moneymanager.dto.ProfileDto;
import com.apptechlab.moneymanager.entity.ProfileEntity;
import com.apptechlab.moneymanager.event.ProfileActivatedEvent;
import com.apptechlab.moneymanager.repository.ProfileRepository;
import com.apptechlab.moneymanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    @Value("${app.activation.url}")
    private String activationUrl;

    private final ProfileRepository profileRepository;
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
            String token = jwtUtil.generateToken(authDto.getEmail());
            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDto.getEmail()
                    ));
        }catch (Exception e){
            throw new RuntimeException("Invalid email or password");
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
