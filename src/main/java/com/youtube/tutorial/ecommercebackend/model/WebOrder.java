package com.youtube.tutorial.ecommercebackend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Order generated from the website.
 */
@Entity
@Table(name = "web_order")
public class WebOrder {

  /** Unique id for the order. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  /** The user of the order. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private LocalUser user;
  /** The shipping address of the order. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "address_id", nullable = false)
  private Address address;
  /** The quantities ordered. */
  @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<WebOrderQuantities> quantities = new ArrayList<>();

  /**
   * Gets the quantities ordered.
   * @return The quantities.
   */
  public List<WebOrderQuantities> getQuantities() {
    return quantities;
  }

  /**
   * Sets the quantities ordered.
   * @param quantities The quantities.
   */
  public void setQuantities(List<WebOrderQuantities> quantities) {
    this.quantities = quantities;
  }

  /**
   * Gets the address of the order.
   * @return The address.
   */
  public Address getAddress() {
    return address;
  }

  /**
   * Sets the address of the order.
   * @param address The address.
   */
  public void setAddress(Address address) {
    this.address = address;
  }

  /**
   * Gets the user of the order.
   * @return The user.
   */
  public LocalUser getUser() {
    return user;
  }

  /**
   * Sets the user of the order.
   * @param user The user.
   */
  public void setUser(LocalUser user) {
    this.user = user;
  }

  /**
   * Gets the id of the order.
   * @return The id.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the id of the order.
   * @param id The id.
   */
  public void setId(Long id) {
    this.id = id;
  }

}