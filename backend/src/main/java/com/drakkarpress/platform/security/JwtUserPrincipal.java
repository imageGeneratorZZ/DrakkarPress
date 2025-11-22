package com.drakkarpress.platform.security;

import java.util.UUID;

public record JwtUserPrincipal(UUID userId, String username, String role, String subscription) { }
