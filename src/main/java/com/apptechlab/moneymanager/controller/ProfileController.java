package com.apptechlab.moneymanager.controller;

import com.apptechlab.moneymanager.dto.AuthDto;
import com.apptechlab.moneymanager.dto.ProfileDto;
import com.apptechlab.moneymanager.dto.UpdatePasswordDto;
import com.apptechlab.moneymanager.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Profile Controller",description = "API's to register, login and activate user profiles.")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registeredProfile = profileService.registerProfile(profileDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody ProfileDto profileDto){
        ProfileDto updateProfile = profileService.updateProfile(profileDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(updateProfile);
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<ProfileDto> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto){
        ProfileDto updateProfile = profileService.updatePassword(updatePasswordDto.getId(), updatePasswordDto.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(updateProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.ok("Profile activated successfully");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDto authDto){
        try{
            if(!profileService.isAccountActive(authDto.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account is not active. Please activate your account first"));
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteProfile(){
        profileService.deleteCurrentProfile();
        return ResponseEntity.ok(Map.of("message","Profile and all associated data deleted successfully."));
    }
}
