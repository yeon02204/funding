package com.funding.funding.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateProjectRequest(
        @NotNull Long categoryId,
        @NotBlank String title,
        String content,
        @NotNull Long goalAmount,
        @NotNull LocalDateTime deadline
) {}