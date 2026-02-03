package com.apptechlab.moneymanager.event;

import com.apptechlab.moneymanager.entity.ProfileEntity;
import org.springframework.context.ApplicationEvent;

public class ProfileActivatedEvent extends ApplicationEvent {

    private final ProfileEntity profile;

    public ProfileActivatedEvent(Object source, ProfileEntity profile) {
        super(source);
        this.profile = profile;
    }

    public ProfileEntity getProfile() {
        return profile;
    }
}
