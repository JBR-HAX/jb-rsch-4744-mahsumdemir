package org.jetbrains.assignment.location;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

public interface LocationRepository extends CrudRepository<LocationEntity, Long> {
    @Query(value = "SELECT MAX(locationOrder) FROM LocationEntity")
    Integer findMaxLocationOrder();

    LocationEntity findFirstByLocationOrder(int locationOrder);
}
