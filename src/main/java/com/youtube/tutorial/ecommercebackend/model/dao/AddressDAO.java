package com.youtube.tutorial.ecommercebackend.model.dao;

import com.youtube.tutorial.ecommercebackend.model.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Data Access Object for the Address data.
 */
public interface AddressDAO extends ListCrudRepository<Address, Long> {

  List<Address> findByUser_Id(Long id);

}
