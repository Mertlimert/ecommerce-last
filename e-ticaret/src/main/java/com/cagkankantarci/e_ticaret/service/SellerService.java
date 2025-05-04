package com.cagkankantarci.e_ticaret.service;

import com.cagkankantarci.e_ticaret.entity.Seller;
import java.util.Optional;

public interface SellerService {
    Optional<Seller> findByUserId(Long userId);
    Seller save(Seller seller);
}
