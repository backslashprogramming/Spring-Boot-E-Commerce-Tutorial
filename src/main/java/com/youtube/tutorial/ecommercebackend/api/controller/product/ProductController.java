package com.youtube.tutorial.ecommercebackend.api.controller.product;

import com.youtube.tutorial.ecommercebackend.model.Product;
import com.youtube.tutorial.ecommercebackend.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to handle the creation, updating & viewing of products.
 */
@RestController
@RequestMapping("/product")
public class ProductController {

  /** The Product Service. */
  private ProductService productService;

  /**
   * Constructor for spring injection.
   * @param productService
   */
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * Gets the list of products available.
   * @return The list of products.
   */
  @GetMapping
  public List<Product> getProducts() {
    return productService.getProducts();
  }

}
