package com.ase.attendanceservice.model.dto.incoming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class MessageRequestDTO {
    @NotNull
    private Long organizerId;

    @NotBlank
    private String message;

    @NotEmpty
    private List<Long> recipientIds;

    private Instant messageTimestamp = Instant.now();

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(List<Long> recipientIds) {
        this.recipientIds = recipientIds;
    }

    public Instant getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(Instant messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "organizerId=" + organizerId +
                ", message='" + message + '\'' +
                ", recipientIds=" + recipientIds +
                ", messageTimestamp=" + messageTimestamp +
                '}';
    }
}
