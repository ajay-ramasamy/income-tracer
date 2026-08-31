package com.dev.money.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.money.dto.AuthDTO;
import com.dev.money.dto.ProfileDTO;
import com.dev.money.entity.ProfileEntity;
import com.dev.money.repository.ProfileRepository;
import com.dev.money.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {

        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        String activationLink =activationURL+"/api/v1.0/activate?token="+ newProfile.getActivationToken();
        String subject = "Activate your account";
        String body ="Click the following link to activate your account: "+ activationLink;
        emailService.sendEmail(newProfile.getEmail(),subject,body);
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .name(profileDTO.getName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .name(profileEntity.getName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    //validate the token
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile->{
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                    .orElseThrow(()->new UsernameNotFoundException("Profile not found with email : "+authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser=null;
        if(email == null){
            currentUser = getCurrentProfile();
        }
        else{
            currentUser = profileRepository.findByEmail(email)
                            .orElseThrow(()->new UsernameNotFoundException("user not found with this email : "+email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String,Object> authenticateAndGenerateToken(AuthDTO authDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                "token",token,
                "user",getPublicProfile(authDTO.getEmail())
            );
        }
        catch(Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }

}
