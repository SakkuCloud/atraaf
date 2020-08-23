package io.github.makbn.atraaf.core.crud;

import io.github.makbn.atraaf.core.entity.ApplicationEntity;
import io.github.makbn.atraaf.core.entity.EnvironmentEntity;
import io.github.makbn.atraaf.core.entity.ParameterEntity;
import io.github.makbn.atraaf.core.entity.UserEntity;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import org.apache.commons.lang.Validate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationCRUD {
    private final SessionFactory hibernate;

    @Autowired
    public ApplicationCRUD(SessionFactory hibernate) {
        this.hibernate = hibernate;
    }

    @Transactional
    public Optional<Long> createApplication(ApplicationEntity applicationEntity) throws InternalServerException {
        try {
            Session session = hibernate.getCurrentSession();
            Optional<Serializable> id = Optional.of(session.save(applicationEntity));
            return id.map(ser -> (Long) ser);
        } catch (Exception e) {
            throw InternalServerException.builder()
                    .code(100)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public void updateApplication(ApplicationEntity applicationEntity) {
        if (applicationEntity != null) {
            Session session = hibernate.getCurrentSession();
            session.update(applicationEntity);
        }
    }

    @Transactional
    public void removeApplication(ApplicationEntity applicationEntity) {
        if (applicationEntity != null) {
            Session session = hibernate.getCurrentSession();
            session.delete(applicationEntity);
        }
    }

    @Transactional
    public void removeEnvironment(EnvironmentEntity environmentEntity) {
        if (environmentEntity != null) {
            Session session = hibernate.getCurrentSession();

            session.createQuery("delete from ValueEntity where environment = :env")
                    .setParameter("env", environmentEntity)
                    .executeUpdate();

            session.delete(environmentEntity);
        }
    }

    @Transactional
    public void removeParameter(ParameterEntity parameterEntity) {
        if (parameterEntity != null) {
            Session session = hibernate.getCurrentSession();

//            session.createQuery("delete from ValueEntity where parameter = :param")
//                    .setParameter("param", parameterEntity)
//                    .executeUpdate();

            session.delete(parameterEntity);
        }
    }

    @Transactional
    public Optional<ApplicationEntity> findApplicationById(Long id){
        Validate.notNull(id, "id parameter is required");
        Session session = hibernate.getCurrentSession();
        return session.createQuery("from ApplicationEntity  app where app.id = :id ", ApplicationEntity.class)
                .setParameter("id", id).uniqueResultOptional();
    }

    @Transactional
    public List<ApplicationEntity> getAllApplications(UserEntity user) {
        Session session = hibernate.getCurrentSession();
        return session.createQuery("SELECT app FROM ApplicationEntity app " +
                "where app.owner.id = :userId " +
                "or :userId in ( select collab.user.id from EnvironmentEntity env " +
                "left join CollaboratorEntity collab on collab.environment = env where  env.application = app )", ApplicationEntity.class)
                .setParameter("userId", user.getId())
                .list();
    }

    @Transactional
    public Optional<Long> createEnvironment(EnvironmentEntity environmentEntity) throws InternalServerException {
        try {
            Session session = hibernate.getCurrentSession();
            Optional<Serializable> id = Optional.of(session.save(environmentEntity));
            return id.map(ser -> (Long) ser);
        } catch (Exception e) {
            throw InternalServerException.builder()
                    .code(101)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Optional<EnvironmentEntity> findEnvironmentById(Long envId) {
        Session session = hibernate.getCurrentSession();
        return session.createQuery("FROM EnvironmentEntity  ee where ee.id = :id", EnvironmentEntity.class)
                .setParameter("id", envId)
                .uniqueResultOptional();
    }

    @Transactional
    public Optional<ParameterEntity> findParameterByName(String name, ApplicationEntity app) {
        Session session = hibernate.getCurrentSession();
        return session.createQuery("FROM ParameterEntity  pe where pe.name = :name and pe.application=:app", ParameterEntity.class)
                .setParameter("name", name)
                .setParameter("app", app)
                .uniqueResultOptional();
    }

    @Transactional
    public Optional<Long> createParameter(ParameterEntity parameterEntity) throws InternalServerException {
        try {
            Session session = hibernate.getCurrentSession();
            Optional<Serializable> id = Optional.of(session.save(parameterEntity));
            return id.map(ser -> (Long) ser);
        } catch (Exception e) {
            e.printStackTrace();
            throw InternalServerException.builder()
                    .code(102)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Transactional
    public void updateParameter(ParameterEntity parameterEntity) throws InternalServerException {
        try {
            Session session = hibernate.getCurrentSession();
            session.update(parameterEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw InternalServerException.builder()
                    .code(102)
                    .message(e.getMessage())
                    .build();
        }
    }
}
