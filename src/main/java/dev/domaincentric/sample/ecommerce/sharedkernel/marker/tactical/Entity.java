package dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical;

public interface Entity<T extends Entity<T, ID>, ID extends Id> {
  ID id();

  default boolean sameIdentityAs(final T other) {
    return other != null && id().equals(other.id());
  }
}
