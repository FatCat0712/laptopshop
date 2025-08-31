package vn.hoidanit.laptopshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    Optional<CartDetail> findByUserAndProduct(User user, Product product);

    List<CartDetail> findByUser(User user);

    @Query("SELECT SUM(cd.quantity) FROM CartDetail cd WHERE cd.user.id = ?1")
    Integer sumQuantityByUser(Long userId);

}
