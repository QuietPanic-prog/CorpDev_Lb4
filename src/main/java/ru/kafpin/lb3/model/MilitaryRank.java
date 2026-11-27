package ru.kafpin.lb3.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
@Entity
@Table(name = "military_ranks")
public class MilitaryRank {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @OneToMany(mappedBy = "rank")
    private List<Serviceman> servicemen = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Serviceman> getServicemen() { return servicemen; }
    public void setServicemen(List<Serviceman> servicemen) { this.servicemen = servicemen; }
}

