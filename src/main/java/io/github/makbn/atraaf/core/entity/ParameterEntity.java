package io.github.makbn.atraaf.core.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_core_parameter")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String alias;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ParamType type;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = ParameterEntity.class, mappedBy = "parent", cascade = {CascadeType.ALL})
    private Set<ParameterEntity> children;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = ValueEntity.class, mappedBy = "parameter", cascade = {CascadeType.ALL})
    private Set<ValueEntity> values;

    @ManyToOne(targetEntity = ParameterEntity.class)
    @JoinColumn(name = "fk_parent_id")
    private ParameterEntity parent;

    @ManyToOne
    @JoinColumn(name = "fk_application_id")
    private ApplicationEntity application;

    public enum ParamType {
        TEXT, NUMBER, COMPLEX
    }
}
