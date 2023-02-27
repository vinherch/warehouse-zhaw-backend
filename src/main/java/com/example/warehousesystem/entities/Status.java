package com.example.warehousesystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author dejan.kosic
 * This is the entity class for the status entity
 *
 * Statuses and their description are stored in this entity e.g. status "active"
 */

@NoArgsConstructor
@Data
@ToString
@Table(name="Status")
@Entity(name = "Status")
public class Status extends BaseEntity{

    @Column(
            name="Description",
            nullable = false,
            unique = true
    )
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status",cascade = CascadeType.ALL,fetch = FetchType.EAGER,targetEntity = Article.class,orphanRemoval = true)
    private List<Article> articles;

    /**
     * Custom constructor for the status entity
     * @param description description for the entity
     */
    public Status(String description) {
        super();
        this.description = description;
    }
}
