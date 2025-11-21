package de.sample.aiarchitecture.cart.domain.model;

import de.sample.aiarchitecture.sharedkernel.domain.marker.Value;
import org.jmolecules.ddd.annotation.ValueObject;

/**
 * Value Object representing the status of a shopping cart.
 */
@ValueObject
public enum CartStatus implements Value {
  /**
   * Cart is active and can be modified.
   */
  ACTIVE,

  /**
   * Cart has been checked out and cannot be modified.
   */
  CHECKED_OUT,

  /**
   * Cart has been abandoned by the customer.
   */
  ABANDONED
}
