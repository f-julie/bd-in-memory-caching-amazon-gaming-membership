package com.amazon.ata.inmemorycaching.classroom.dao;

import com.amazon.ata.inmemorycaching.classroom.dao.models.*;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.collect.ImmutableMap;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Manages access to GroupMembership items.
 */
public class GroupMembershipDao {

    private final DynamoDBMapper mapper;

    /**
     * Creates an GroupMembershipDao with the given DDB mapper.
     * @param mapper DynamoDBMapper
     */
    @Inject
    public GroupMembershipDao(final DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Creates a GroupMembership for the provided userId in the provided group. A GroupMembershipAudit is also
     * saved to mark the addition of the userId to the provided group.
     * @param userId - the userId to add the membership for
     * @param group - the group to grant membership to
     * @return the new GroupMembership created for the userId
     */
    public GroupMembership addUserToGroup(final String userId, final Group group) {
        GroupMembership membership = new GroupMembership();
        membership.setGroupId(group.getId());
        membership.setUserId(userId);
        membership.setGroupType(group.getType());

        GroupMembershipAudit auditRecord = new GroupMembershipAudit(userId, group.getId(), ZonedDateTime.now(),
            GroupMembershipAuditAction.ADD);

        mapper.save(membership);
        mapper.save(auditRecord);

        return membership;
    }

    /**
     * Checks if the user has a membership in the group with id: groupId.
     * @param userId the user to check for
     * @param groupId the id of the group
     * @return true if the userId has a membership in the group, false otherwise
     */
    public boolean isUserInGroup(final String userId, final String groupId) {
        GroupMembership membership = new GroupMembership();
        membership.setUserId(userId);
        membership.setGroupId(groupId);

        membership = mapper.load(membership);

        return membership != null;
    }

    // This method will be called by the cache manager if there is a miss in the cache
    // It needs to receive a cache-key because that's all the cache manager has, deconstruct it, and call the original DAO method
    public boolean isUserInGroup(GroupMembershipCacheKey theCacheKey) {
        return this.isUserInGroup(theCacheKey.getUserId(), theCacheKey.getGroupId()); // this. is optional because we're in the same class
    }

    /**
     * Returns a list of userIds that have memberships in the group with id: groupId.
     * @param groupId the id of the group to get userIds for
     * @return the userIds that have a membership in the group
     */
    public List<String> getUserIdsInGroup(final String groupId) {
        GroupMembership membership = new GroupMembership();
        membership.setGroupId(groupId);

        DynamoDBQueryExpression<GroupMembership> queryExpression = new DynamoDBQueryExpression();
        queryExpression.withIndexName(GroupMembership.GROUP_ID_USER_ID__GSI);
        queryExpression.withHashKeyValues(membership);

        List<GroupMembership> memberships = mapper.query(GroupMembership.class, queryExpression);

        return memberships.stream().map(groupMembership -> groupMembership.getUserId()).collect(Collectors.toList());
    }

    /**
     * Deletes the membership for the provided userId in the group with id: groupId. A GroupMembershipAudit is also
     * saved to mark the removal of the userId from the provided group.
     * @param userId the userId with the membership to be removed
     * @param groupId the group to remove the user from
     */
    public void removeUserFromGroup(final String userId, final String groupId) {
        GroupMembership membership = new GroupMembership();
        membership.setGroupId(groupId);
        membership.setUserId(userId);

        GroupMembershipAudit auditRecord = new GroupMembershipAudit(userId, groupId, ZonedDateTime.now(),
            GroupMembershipAuditAction.REMOVE);

        mapper.delete(membership);
        mapper.save(auditRecord);
    }

    /**
     * Returns a list of groupIds that the provided userId has a membership in.
     * @param userId the userId to get the groups for
     * @return the groupIds of the groups the user has a membership in
     */
    public List<String> getGroupIdsForUser(final String userId) {
        return getGroupIdsForUser(userId, null);
    }

    /**
     * Returns a list of groupIds with the matching group type that the provided userId has a membership in.
     * @param userId the userId to get the groups for
     * @param groupType provides the filer for groups to return
     * @return a list of groupIds of the provided type that the user has a membership in
     */
    public List<String> getGroupIdsForUser(final String userId, final GroupType groupType) {
        GroupMembership membership = new GroupMembership();
        membership.setUserId(userId);

        DynamoDBQueryExpression<GroupMembership> queryExpression = new DynamoDBQueryExpression();
        queryExpression.withHashKeyValues(membership);

        if (groupType != null) {
            queryExpression.withFilterExpression("groupType = :groupType");
            queryExpression.withExpressionAttributeValues(
                ImmutableMap.of(":groupType", new AttributeValue(groupType.name())));
        }

        List<GroupMembership> memberships = mapper.query(GroupMembership.class, queryExpression);

        return memberships.stream().map(groupMembership -> groupMembership.getGroupId()).collect(Collectors.toList());
    }
}
