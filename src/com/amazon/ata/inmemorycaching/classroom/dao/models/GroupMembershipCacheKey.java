package com.amazon.ata.inmemorycaching.classroom.dao.models;

import java.util.Objects;

// This is the key for accessing the cache

// Since it requires multiple values, we need a class to hold and manage values

// This class is immutable - final class, final instance data, no setters, only uses immutable data (Strings)
// Note: there was no need for defensive copying or defensive returns
// Being immutable means the class can be used successfully in a concurrent processing environment (threads)

public final class GroupMembershipCacheKey {

    private final String userId;
    private final String groupId;

    // Adding final to a parameter means it cannot change
    public GroupMembershipCacheKey(final String userId, final String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        GroupMembershipCacheKey request = (GroupMembershipCacheKey) obj;

        return userId.equals(request.userId) && groupId.equals(request.groupId);
    }
}
