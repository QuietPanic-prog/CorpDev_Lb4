package ru.kafpin.lb3.model;

import java.time.LocalDate;
import jakarta.persistence.*;
@Entity
@Table(name = "servicemen")
public class Serviceman {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String patronymic;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String position;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rank_id", nullable = false)
    private MilitaryRank rank;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public MilitaryRank getRank() { return rank; }
    public void setRank(MilitaryRank rank) { this.rank = rank; }
}

