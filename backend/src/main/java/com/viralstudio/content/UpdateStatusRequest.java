package com.viralstudio.content;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStatusRequest(@NotNull ContentStatus status, @Size(max = 1000) String editorNotes) {}
