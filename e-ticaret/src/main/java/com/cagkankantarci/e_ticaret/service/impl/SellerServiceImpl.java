package com.cagkankantarci.e_ticaret.service.impl;



import com.cagkankantarci.e_ticaret.entity.Seller;
import com.cagkankantarci.e_ticaret.repository.SellerRepository;
import com.cagkankantarci.e_ticaret.service.SellerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public Optional<Seller> findByUserId(Long userId) {
        return sellerRepository.findByUserId(userId);
    }

    @Override
    public Seller save(Seller seller) {
        return sellerRepository.save(seller);
    }
}
