package io.github.makbn.atraaf.core.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_core_accesskey")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String accessKey;

    @Column
    private boolean removed;

    @CreationTimestamp
    @Column
    private Date created;

    @UpdateTimestamp
    @Column
    private Date updated;
}
