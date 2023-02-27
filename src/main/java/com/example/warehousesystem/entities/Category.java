package com.example.warehousesystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author dejan.kosic
 * This is the entity class for the category entity
 *
 * Categories with their description e.g. "shoes" are stored in this entity
 */

@NoArgsConstructor
@ToString
@Data
@Table(name = "Category")
@Entity(name = "Category")
public class Category extends BaseEntity{
    @Column(
            name="Description",
            nullable = false,
            unique = true
    )
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.EAGER,targetEntity = Article.class,orphanRemoval = true)
    private List<Article> articles;

    /**
     * Custom constructor for the category entity
     * @param description descritpion of the category
     */
    public Category(String description) {
        super();
        this.description = description;
    }

}
