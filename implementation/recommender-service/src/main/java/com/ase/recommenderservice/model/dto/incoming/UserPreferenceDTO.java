package com.ase.recommenderservice.model.dto.incoming;

import jakarta.validation.constraints.NotNull;

public class UserPreferenceDTO {
    @NotNull
    private Boolean receivePromotionalEmails;

    public UserPreferenceDTO() {
    }

    public UserPreferenceDTO(boolean promotionalEmailOptIn) {
        this.receivePromotionalEmails = promotionalEmailOptIn;
    }

    public boolean getReceivePromotionalEmails() {
        return receivePromotionalEmails;
    }

    public void setReceivePromotionalEmails(boolean receivePromotionalEmails) {
        this.receivePromotionalEmails = receivePromotionalEmails;
    }
}
