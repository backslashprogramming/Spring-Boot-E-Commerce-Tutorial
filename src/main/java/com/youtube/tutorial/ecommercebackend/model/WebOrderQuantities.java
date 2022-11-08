package com.youtube.tutorial.ecommercebackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * The quantity ordered of a product.
 */
@Entity
@Table(name = "web_order_quantities")
public class WebOrderQuantities {

  /** The unqiue id of the order quantity. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  /** The product being ordered. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
  /** The quantity being ordered. */
  @Column(name = "quantity", nullable = false)
  private Integer quantity;
  /** The order itself. */
  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private WebOrder order;

  /**
   * Gets the order.
   * @return The order.
   */
  public WebOrder getOrder() {
    return order;
  }

  /**
   * Sets the order.
   * @param order The order.
   */
  public void setOrder(WebOrder order) {
    this.order = order;
  }

  /**
   * Gets the quantity ordered.
   * @return The quantity.
   */
  public Integer getQuantity() {
    return quantity;
  }

  /**
   * Sets the quantity ordered.
   * @param quantity The quantity.
   */
  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  /**
   * Gets the product ordered.
   * @return The product.
   */
  public Product getProduct() {
    return product;
  }

  /**
   * Sets the product.
   * @param product The product.
   */
  public void setProduct(Product product) {
    this.product = product;
  }

  /**
   * Gets the id.
   * @return The id.
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the id.
   * @param id The id.
   */
  public void setId(Long id) {
    this.id = id;
  }

}