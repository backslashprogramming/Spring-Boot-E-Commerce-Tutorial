package com.youtube.tutorial.ecommercebackend.api.controller.user;

import com.youtube.tutorial.ecommercebackend.api.model.DataChange;
import com.youtube.tutorial.ecommercebackend.model.Address;
import com.youtube.tutorial.ecommercebackend.model.LocalUser;
import com.youtube.tutorial.ecommercebackend.model.dao.AddressDAO;
import com.youtube.tutorial.ecommercebackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Rest Controller for user data interactions.
 */
@RestController
@RequestMapping("/user")
public class UserController {

  /** The Address DAO. */
  private AddressDAO addressDAO;
  private SimpMessagingTemplate simpMessagingTemplate;
  private UserService userService;

  /**
   * Constructor for spring injection.
   * @param addressDAO
   * @param simpMessagingTemplate
   * @param userService
   */
  public UserController(AddressDAO addressDAO,
                        SimpMessagingTemplate simpMessagingTemplate,
                        UserService userService) {
    this.addressDAO = addressDAO;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.userService = userService;
  }

  /**
   * Gets all addresses for the given user and presents them.
   * @param user The authenticated user account.
   * @param userId The user ID to get the addresses of.
   * @return The list of addresses.
   */
  @GetMapping("/{userId}/address")
  public ResponseEntity<List<Address>> getAddress(
      @AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {
    if (!userService.userHasPermissionToUser(user, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ResponseEntity.ok(addressDAO.findByUser_Id(userId));
  }

  /**
   * Allows the user to add a new address.
   * @param user The authenticated user.
   * @param userId The user id for the new address.
   * @param address The Address to be added.
   * @return The saved address.
   */
  @PutMapping("/{userId}/address")
  public ResponseEntity<Address> putAddress(
      @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
      @RequestBody Address address) {
    if (!userService.userHasPermissionToUser(user, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    address.setId(null);
    LocalUser refUser = new LocalUser();
    refUser.setId(userId);
    address.setUser(refUser);
    Address savedAddress = addressDAO.save(address);
    simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
        new DataChange<>(DataChange.ChangeType.INSERT, address));
    return ResponseEntity.ok(savedAddress);
  }

  /**
   * Updates the given address.
   * @param user The authenticated user.
   * @param userId The user ID the address belongs to.
   * @param addressId The address ID to alter.
   * @param address The updated address object.
   * @return The saved address object.
   */
  @PatchMapping("/{userId}/address/{addressId}")
  public ResponseEntity<Address> patchAddress(
      @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
      @PathVariable Long addressId, @RequestBody Address address) {
    if (!userService.userHasPermissionToUser(user, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    if (address.getId() == addressId) {
      Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
      if (opOriginalAddress.isPresent()) {
        LocalUser originalUser = opOriginalAddress.get().getUser();
        if (originalUser.getId() == userId) {
          address.setUser(originalUser);
          Address savedAddress = addressDAO.save(address);
          simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
              new DataChange<>(DataChange.ChangeType.UPDATE, address));
          return ResponseEntity.ok(savedAddress);
        }
      }
    }
    return ResponseEntity.badRequest().build();
  }

}
