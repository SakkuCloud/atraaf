package io.github.makbn.atraaf.core.crud;

import io.github.makbn.atraaf.api.request.LoginReq;
import io.github.makbn.atraaf.core.entity.Role;
import io.github.makbn.atraaf.core.entity.UserEntity;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import io.github.makbn.atraaf.core.exception.InvalidRequestException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;

@Component
public class UserCRUD {
    private final SessionFactory hibernate;

    @Autowired
    public UserCRUD(SessionFactory hibernate) {
        this.hibernate = hibernate;
    }


    @Transactional
    public Optional<Long> createUser(UserEntity userEntity) throws InternalServerException {
        try {
            Session session = hibernate.getCurrentSession();

            //check duplicate username email
            if (!CollectionUtils.isEmpty(
                    session.createQuery("from UserEntity where username = :username or email = :email")
                            .setParameter("username", userEntity.getUsername())
                            .setParameter("email", userEntity.getEmail()).list()))
                throw new InvalidRequestException("Duplicate username or email!", 409);

            Optional<Serializable> id = Optional.of(session.save(userEntity));
            return id.map(ser -> (Long) ser);
        } catch (Exception e) {
            if(e instanceof InvalidRequestException)
                throw e;
            throw InternalServerException.builder()
                    .code(100)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Optional<UserEntity> findUserById(long id){
        Session session = hibernate.getCurrentSession();
        return session.createQuery("from UserEntity  u where u.id = :id", UserEntity.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Transactional
    public Optional<UserEntity> findUserByLoginInfo(LoginReq loginReq) {
        Session session = hibernate.getCurrentSession();
        return session.createQuery("from UserEntity  u where u.username = :username and " +
                "u.password = :password", UserEntity.class)
                .setParameter("username", loginReq.getUsername())
                .setParameter("password", loginReq.getPassword())
                .uniqueResultOptional();
    }

    @Transactional
    public Optional<UserEntity> test(){
        Session session = hibernate.getCurrentSession();
        UserEntity userEntity = UserEntity.builder()
                .email("m@m.com")
                .roles(Collections.singleton(Role.ROLE_USER))
                .username("makbn")
                .build();
        Long id = (Long) session.save(userEntity);
        userEntity.setId(id);
        return Optional.of(userEntity);
    }

}
