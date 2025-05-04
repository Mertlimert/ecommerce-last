package com.cagkankantarci.e_ticaret.repository;

import com.cagkankantarci.e_ticaret.entity.Address;
import com.cagkankantarci.e_ticaret.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // "order" alanını kullanarak
    List<Address> findByOrder(User user);
}