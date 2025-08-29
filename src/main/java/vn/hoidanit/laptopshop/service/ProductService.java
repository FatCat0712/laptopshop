package vn.hoidanit.laptopshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repo;

    @Autowired
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> listAll() {
        return repo.findAll();
    }

    public Product get(Long productId) throws ProductNotFoundException {
        Optional<Product> product = repo.findById(productId);
        if(product.isEmpty()) {
            throw new ProductNotFoundException("Could not find any products with ID " + productId);
        }
        return product.get();
    }

    public void save(Product productInForm) throws ProductNotFoundException {
        Long productId = productInForm.getId();
        Product product;
        if(productId != null) {
            product = get(productId);
        }
        else {
            product = new Product();
        }
        product.setName(productInForm.getName());
        product.setPrice(productInForm.getPrice());
        product.setDetailDesc(productInForm.getDetailDesc());
        product.setShortDesc(productInForm.getShortDesc());
        product.setQuantity(productInForm.getQuantity());
        product.setFactory(productInForm.getFactory());
        product.setTarget(productInForm.getTarget());

        if(productInForm.getImage() != null && !productInForm.getImage().isEmpty()) {
            product.setImage(productInForm.getImage());
        }

        repo.save(product);
    }

    public void deleteProduct(Long productId) {
        repo.deleteById(productId);
    }
}
