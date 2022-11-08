package com.youtube.tutorial.ecommercebackend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * A product available for purchasing.
 */
@Entity
@Table(name = "product")
public class Product {

  /** Unique id for the product. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  /** The name of the product. */
  @Column(name = "name", nullable = false, unique = true)
  private String name;
  /** The short description of the product. */
  @Column(name = "short_description", nullable = false)
  private String shortDescription;
  /** The long description of the product. */
  @Column(name = "long_description")
  private String longDescription;
  /** The price of the product. */
  @Column(name = "price", nullable = false)
  private Double price;
  /** The inventory of the product. */
  @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE, optional = false, orphanRemoval = true)
  private Inventory inventory;

  /**
   * Gets the inventory of the product.
   * @return The inventory.
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Sets the inventory of the product.
   * @param inventory The inventory.
   */
  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  /**
   * Gets the price of the product.
   * @return The price.
   */
  public Double getPrice() {
    return price;
  }

  /**
   * Sets the price of the product.
   * @param price The price.
   */
  public void setPrice(Double price) {
    this.price = price;
  }

  /**
   * Gets the long description of the product.
   * @return The long description.
   */
  public String getLongDescription() {
    return longDescription;
  }

  /**
   * Sets the long description of the product.
   * @param longDescription The long description.
   */
  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  /**
   * Gets the short description of the product.
   * @return The short description.
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Sets the short description of the product.
   * @param shortDescription The short description.
   */
  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  /**
   * Gets the name of the product.
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the product.
   * @param name The name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the id of the product.
   * @return The id.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the id of the product.
   * @param id The id.
   */
  public void setId(Long id) {
    this.id = id;
  }

}