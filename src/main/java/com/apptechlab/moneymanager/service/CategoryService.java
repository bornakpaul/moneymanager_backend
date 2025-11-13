package com.apptechlab.moneymanager.service;

import com.apptechlab.moneymanager.dto.CategoryDto;
import com.apptechlab.moneymanager.entity.CategoryEntity;
import com.apptechlab.moneymanager.entity.ProfileEntity;
import com.apptechlab.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final  ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public CategoryDto saveCategory(CategoryDto categoryDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDto.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with this name already exists");
        }

        CategoryEntity newCategory = toEntity(categoryDto,profile);
        newCategory = categoryRepository.save(newCategory);
        return toDto(newCategory);
    }


    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){
        return  CategoryEntity.builder()
                .name(categoryDto.getName())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .type(categoryDto.getType())
                .build();
    }

    private CategoryDto toDto(CategoryEntity entity){
        return CategoryDto.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile() .getId() : null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .type(entity.getType())
                .build();
    }
}
