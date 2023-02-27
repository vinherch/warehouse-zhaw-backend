package com.example.warehousesystem.entities;

import com.mysql.cj.conf.PropertyDefinitions;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author dejan.kosic
 * Base entity class for common fields in all entities
 *
 * Stores the id, version, created and modified timestamp for all entities
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY

    )
    @Column (
            name="Id",
            updatable = false,
            nullable = false,
            unique = true,
            insertable = false
    )
    protected long id;
    @Version
    @Column(name = "Version", columnDefinition = "integer DEFAULT 1", nullable = false)
    protected long version=1;

    @CreatedDate
    @Column(name = "Created_Timestamp",nullable = false)
    private String  createdTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


    @LastModifiedDate
    @Column(name = "Modified_Timestamp",nullable = false)
    private String modifiedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

}
