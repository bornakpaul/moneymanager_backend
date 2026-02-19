package com.apptechlab.moneymanager.event;

import com.apptechlab.moneymanager.entity.CategoryEntity;
import com.apptechlab.moneymanager.entity.ProfileEntity;
import com.apptechlab.moneymanager.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultCategoryListener {
    private final CategoryRepository categoryRepository;

    private static final List<String> DEFAULT_EXPENSE_CATEGORIES = List.of(
            "Shopping", "Food", "Rent","Bills", "Transport"
    );

    private static final List<String> DEFAULT_INCOME_CATEGORIES = List.of(
            "Salary", "Freelance", "Investment","Gift"
    );

    public DefaultCategoryListener(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @EventListener
    @Transactional
    public void handleProfileActivation(ProfileActivatedEvent event) {
        ProfileEntity profile = event.getProfile();
        Long profileId = profile.getId();

        // Process Expense Categories
        List<CategoryEntity> expenseCategories = DEFAULT_EXPENSE_CATEGORIES.stream()
                .filter(name -> !categoryRepository.existsByNameAndProfileId(name, profileId))
                .map(name -> {
                    CategoryEntity category = new CategoryEntity();
                    category.setProfile(profile);
                    category.setType("Expense");
                    category.setName(name);
                    return category;
                })
                .toList();

        if (!expenseCategories.isEmpty()) {
            categoryRepository.saveAll(expenseCategories);
            System.out.println("Default expense categories created for user: " + profile.getEmail());
        }

        // Process Income Categories
        List<CategoryEntity> incomeCategories = DEFAULT_INCOME_CATEGORIES.stream()
                .filter(name -> !categoryRepository.existsByNameAndProfileId(name, profileId))
                .map(name -> {
                    CategoryEntity category = new CategoryEntity();
                    category.setProfile(profile);
                    category.setType("Income");
                    category.setName(name);
                    return category;
                })
                .toList();

        if (!incomeCategories.isEmpty()) {
            categoryRepository.saveAll(incomeCategories);
            System.out.println("Default income categories created for user: " + profile.getEmail());
        }
    }
}
