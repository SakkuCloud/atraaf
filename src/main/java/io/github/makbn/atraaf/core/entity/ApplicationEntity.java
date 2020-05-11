package io.github.makbn.atraaf.core.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "tbl_core_application")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String description;

    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "application", cascade = {CascadeType.ALL})
    private Set<EnvironmentEntity> environments;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "application", cascade = {CascadeType.ALL})
    private Set<ParameterEntity> parameters;

    @ManyToOne
    @JoinColumn(name = "fk_owner_id")
    private UserEntity owner;


}
