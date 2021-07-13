package com.energizeglobal.egsinterviewtest.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Product implements Serializable {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "productIdGenerator"
    )
    @SequenceGenerator(
            name = "productIdGenerator",
            sequenceName = "product_sequence",
            initialValue = 1,
            allocationSize = 50
    )
    private Long id;
    private String title;
    @Column(
            columnDefinition = "TEXT",
            nullable = false
    )
    private String description;
    private Double price;
    private Integer count;

    @ToString.Exclude
    @ManyToMany(
            cascade = {CascadeType.MERGE, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "product_category",
            joinColumns = { @JoinColumn(name = "product_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "category_title", referencedColumnName = "title") }
    )
    private Set<Category> categories = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;

        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
