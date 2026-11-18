package br.com.ecommerce.customer.customer_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addAddress(Address address) {
        if (address == null) throw new IllegalArgumentException("Address must not be null");
        if (!addresses.contains(address)) addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        if (address != null && addresses.remove(address)) {
            address.setCustomer(null);
        }
    }
}
