package com.amazon.ata.inmemorycaching.classroom.activity;

import com.amazon.ata.inmemorycaching.classroom.dao.GroupMembershipCachingDao;
import dagger.internal.Factory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class CheckUserInGroupActivity_Factory implements Factory<CheckUserInGroupActivity> {
  private final Provider<GroupMembershipCachingDao> groupMembershipDaoProvider;

  public CheckUserInGroupActivity_Factory(
      Provider<GroupMembershipCachingDao> groupMembershipDaoProvider) {
    this.groupMembershipDaoProvider = groupMembershipDaoProvider;
  }

  @Override
  public CheckUserInGroupActivity get() {
    return new CheckUserInGroupActivity(groupMembershipDaoProvider.get());
  }

  public static CheckUserInGroupActivity_Factory create(
      Provider<GroupMembershipCachingDao> groupMembershipDaoProvider) {
    return new CheckUserInGroupActivity_Factory(groupMembershipDaoProvider);
  }
}
