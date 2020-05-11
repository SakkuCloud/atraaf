package io.github.makbn.atraaf.core.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_core_collaborator")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollaboratorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private boolean removed;

    @CreationTimestamp
    private Date created;

    @ManyToOne
    @JoinColumn(name = "fk_environment_id")
    private EnvironmentEntity environment;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private UserEntity user;

}
