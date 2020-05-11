package io.github.makbn.atraaf.core.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "tbl_core_environment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnvironmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "fk_application_id")
    private ApplicationEntity application;

    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "fk_accessky_id")
    private AccessKeyEntity key;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "environment", cascade = {CascadeType.ALL})
    private Set<CollaboratorEntity> collaborators;
}
