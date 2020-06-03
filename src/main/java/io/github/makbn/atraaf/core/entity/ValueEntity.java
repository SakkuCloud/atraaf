package io.github.makbn.atraaf.core.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbl_core_value")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private boolean isDefault;

    @Column
    private boolean excluded;

    @Column(columnDefinition = "TEXT", length = 3700)
    private String raw;

    @ManyToOne
    @JoinColumn(name = "fk_environment_id")
    private EnvironmentEntity environment;

    @ManyToOne
    @JoinColumn(name = "fk_paramter_id")
    private ParameterEntity parameter;
}
