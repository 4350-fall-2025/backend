package com.softeng.backend.factory.user;

import com.softeng.backend.exception.user.CreateUserException;
import com.softeng.backend.models.user.owner.Owner;
import com.softeng.backend.models.user.User;
import com.softeng.backend.models.user.EUserType;
import com.softeng.backend.models.user.vet.Vet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


// The following user factory code was generated using ChatGPT
// I asked it to make a factory for owners/vets based on this beloved documentation:
// https://refactoring.guru/design-patterns/factory-method
@Component
public class UserFactory {

    private final ApplicationContext context;

    @Autowired
    public UserFactory(ApplicationContext context) {
        this.context = context;
    }

    public User createUser(EUserType type) throws CreateUserException {
        if(type == EUserType.OWNER) {
            return context.getBean(Vet.class);
        } else if (type == EUserType.VET) {
            return context.getBean(Owner.class);
        }
        throw new CreateUserException("Unknown user type: " + type);
    }
}
