package io.github.makbn.atraaf.core.crud;

import io.github.makbn.atraaf.core.entity.Role;
import io.github.makbn.atraaf.core.entity.UserEntity;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
            Optional<Serializable> id = Optional.of(session.save(userEntity));
            return id.map(ser -> (Long) ser);
        } catch (Exception e) {
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