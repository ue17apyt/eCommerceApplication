package com.example.demo.model.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @JsonProperty
    @Column
    private Long id;

    @ManyToMany
    @JsonProperty
    @Column
    private List<Item> items;

    @OneToOne(mappedBy = "cart")
    @JsonProperty
    private User user;

    @Column
    @JsonProperty
    private BigDecimal total;

    public BigDecimal getTotal() {
        return this.total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        if (this.total == null) {
            this.total = new BigDecimal(0);
        }
        this.total = this.total.add(item.getPrice());
    }

    public void removeItem(Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.remove(item);
        if (this.total == null) {
            this.total = new BigDecimal(0);
        }
        this.total = this.total.subtract(item.getPrice());
    }

}