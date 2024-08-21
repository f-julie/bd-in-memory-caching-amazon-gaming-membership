package com.amazon.ata.inmemorycaching.classroom.dependency;

import com.amazon.ata.inmemorycaching.classroom.activity.AddUserToGroupActivity;
import com.amazon.ata.inmemorycaching.classroom.activity.CheckUserInGroupActivity;
import com.amazon.ata.inmemorycaching.classroom.activity.CreateGroupActivity;
import com.amazon.ata.inmemorycaching.classroom.activity.GetGroupActivity;
import com.amazon.ata.inmemorycaching.classroom.activity.GetGroupsForUserActivity;
import com.amazon.ata.inmemorycaching.classroom.activity.GetUsersInGroupActivity;
import com.amazon.ata.inmemorycaching.classroom.activity.RemoveUserFromGroupActivity;
import com.amazon.ata.inmemorycaching.classroom.dao.GroupDao;
import com.amazon.ata.inmemorycaching.classroom.dao.GroupMembershipCachingDao;
import com.amazon.ata.inmemorycaching.classroom.dao.GroupMembershipDao;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerServiceComponent implements ServiceComponent {
  private Provider<DynamoDBMapper> provideDynamoDBMapperProvider;

  private DaggerServiceComponent(Builder builder) {
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static ServiceComponent create() {
    return new Builder().build();
  }

  private GroupMembershipDao getGroupMembershipDao() {
    return new GroupMembershipDao(provideDynamoDBMapperProvider.get());
  }

  private GroupDao getGroupDao() {
    return new GroupDao(provideDynamoDBMapperProvider.get());
  }

  private GroupMembershipCachingDao getGroupMembershipCachingDao() {
    return new GroupMembershipCachingDao(getGroupMembershipDao());
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {
    this.provideDynamoDBMapperProvider =
        DoubleCheck.provider(DaoModule_ProvideDynamoDBMapperFactory.create(builder.daoModule));
  }

  @Override
  public AddUserToGroupActivity provideAddUserToGroupActivity() {
    return new AddUserToGroupActivity(getGroupMembershipDao(), getGroupDao());
  }

  @Override
  public CheckUserInGroupActivity provideCheckUserInGroupActivity() {
    return new CheckUserInGroupActivity(getGroupMembershipCachingDao());
  }

  @Override
  public CreateGroupActivity provideCreateGroupActivity() {
    return new CreateGroupActivity(getGroupDao());
  }

  @Override
  public GetGroupActivity provideGetGroupActivity() {
    return new GetGroupActivity(getGroupDao());
  }

  @Override
  public GetGroupsForUserActivity provideGetGroupsForUserActivity() {
    return new GetGroupsForUserActivity(getGroupMembershipDao());
  }

  @Override
  public GetUsersInGroupActivity provideGetUsersInGroupActivity() {
    return new GetUsersInGroupActivity(getGroupMembershipDao());
  }

  @Override
  public RemoveUserFromGroupActivity provideRemoveUserFromGroupActivity() {
    return new RemoveUserFromGroupActivity(getGroupMembershipDao());
  }

  public static final class Builder {
    private DaoModule daoModule;

    private Builder() {}

    public ServiceComponent build() {
      if (daoModule == null) {
        this.daoModule = new DaoModule();
      }
      return new DaggerServiceComponent(this);
    }

    public Builder daoModule(DaoModule daoModule) {
      this.daoModule = Preconditions.checkNotNull(daoModule);
      return this;
    }
  }
}
