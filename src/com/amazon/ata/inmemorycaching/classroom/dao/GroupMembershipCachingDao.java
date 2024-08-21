package com.amazon.ata.inmemorycaching.classroom.dao;

import com.amazon.ata.inmemorycaching.classroom.dao.models.GroupMembershipCacheKey;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

// This will manage the calls to a cache manager
// If there is a miss in cache, it will use the original DAO to get the data from the data store
// If there is a hit in cache, it will return the data from cache

// A cache needs a cache key to identify entries in the cache
// in this example the cache key is the userId and groupId

// Since we have multiple values we want to treat as a single unit
// we need a class to hold and manage the values (OOP)

// We will be using the Google Guava Cache Manager
// We will be inserting calls to this caching DAO between the application and the data base
// so we need to be sure we mimic the behavior of the original DAO (same parameters, same return)

public class GroupMembershipCachingDao {

    // Define a reference to the cache for the cache manager to use
    // Guava uses a LoadingCache object for its caches
    // LoadingCache<cache-key, value to be cached>
    LoadingCache<GroupMembershipCacheKey, Boolean> theCache;

    // Constructor must instantiate the cache and assign it to the reference
    // The delegateDao being injected and received as a parameter by the constructor
    // is the DAO to use if the cache manager can't find an entry in the cache
    @Inject
    public GroupMembershipCachingDao(final GroupMembershipDao delegateDao) {
        this.theCache = CacheBuilder.newBuilder()
                .maximumSize(20000) // Max number of entries in cache
                .expireAfterWrite(3, TimeUnit.HOURS) // Evict cache entry 3 hours after it's written
                .build(CacheLoader.from(delegateDao::isUserInGroup)); // go build cache with the delegateDao method specified
                // The delegateDao must have a method named whatever comes after :: that receives a cache key
    }

    // The method the application will call with the same parameters and same return value type expected
    public boolean isUserInGroup(String userId, String groupId) {
        // Call the cache manager to see if the entry we want is in the cache
        // or get it from the database if it's not

        // Using the getUnchecked() method to call the cache manager to avoid dealing with a possible exception
        // using .get() instead of .getUnchecked() requires some exception handling (they both do the same thing)

        // We don't know or care if there is a cache hit or miss. The cache manager dealt with it.
        return theCache.getUnchecked(new GroupMembershipCacheKey(userId, groupId));
    }
}
