package com.cagkankantarci.e_ticaret.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cagkankantarci.e_ticaret.entity.Seller;
import com.cagkankantarci.e_ticaret.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Date;



@Entity
@Table(name="product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    
    @ManyToOne
@JoinColumn(name = "seller_id")
private Seller seller;

public Seller getSeller() {
    return seller;
}

public void setSeller(Seller seller) {
    this.seller = seller;
}

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "sku")
    private String sku;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "active")
    private boolean active;
    @Column(name = "units_in_stock")
    private int unitsInStock;
    @Column(name = "date_created")
    @CreationTimestamp
    private Date dateCreated;
    @Column(name = "last_updated")
    @UpdateTimestamp
    private Date lastUpdated;
    public Object getPrice() {
        return this.unitPrice;
    }
    
    public Object getStock() {
        return this.unitsInStock;
    }
    
    public void setStock(Object stock) {
        if (stock instanceof Integer) {
            this.unitsInStock = (Integer) stock;
        } else if (stock instanceof String) {
            try {
                this.unitsInStock = Integer.parseInt((String) stock);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Stok değeri geçerli bir sayı olmalıdır");
            }
        } else {
            throw new IllegalArgumentException("Stok değeri Integer veya String tipinde olmalıdır");
        }
    }
    
    // Product.java dosyasında alan adlarını Angular ile uyumlu hale getirin
public void setPrice(Object price) {
    if (price instanceof BigDecimal) {
        this.unitPrice = (BigDecimal) price;
    } else if (price instanceof Double) {
        this.unitPrice = BigDecimal.valueOf((Double) price);
    } else if (price instanceof String) {
        try {
            this.unitPrice = new BigDecimal((String) price);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Geçersiz fiyat formatı");
        }
    } else if (price instanceof Integer) {
        this.unitPrice = new BigDecimal((Integer) price);
    } else if (price == null) {
        this.unitPrice = BigDecimal.ZERO;
    } else {
        throw new IllegalArgumentException("Desteklenmeyen fiyat tipi: " + price.getClass());
    }
}



    
}
