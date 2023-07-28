package com.project.requisition.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

    public static UUID getUUIDFromString(String uuidString) {
        return uuidString == null ? null : UUID.fromString(uuidString);
    }
}
