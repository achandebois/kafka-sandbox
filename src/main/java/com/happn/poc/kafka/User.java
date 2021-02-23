package com.happn.poc.kafka;

import com.happn.poc.kafka.events.CharmEvent;
import com.happn.poc.kafka.events.UserCreated;
import com.happn.poc.kafka.events.UserEvent;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class User {

    static final User TOMBSTONE = null;

    String userId;
    String userName;

    Long charms;

    public User with(Long charm) {
        return toBuilder()
                .charms(Optional.ofNullable(charm).orElse(CharmEvent.initializer()))
                .build();
    }

    public User after(UserEvent event) {
        if (event instanceof UserCreated) {
            return createUser((UserCreated) event);
        }
        return TOMBSTONE;
    }

    private User createUser(UserCreated userCreated) {
        return new User(userCreated.getUserId(), userCreated.getUserName(), CharmEvent.initializer());
    }

    public static User empty() {
        return new User(null, null, CharmEvent.initializer());
    }

}
