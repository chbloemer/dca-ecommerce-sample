package de.sample.aiarchitecture.sharedkernel.domain.marker;

import org.jmolecules.stereotype.Stereotype;

@Stereotype(groups = "dca", value = "Entity")
public interface Entity<T extends Entity<T, ID>, ID extends Id> {
  ID id();

    default boolean sameIdentityAs(final T other) {
        return other != null && id().equals(other.id());
    }
}
