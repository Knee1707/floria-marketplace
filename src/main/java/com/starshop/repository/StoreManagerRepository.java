package com.starshop.repository;

import com.starshop.domain.StoreManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** @owner: TV3 dùng. Bảng nối nhiều–nhiều: 1 Manager phụ trách nhiều shop, 1 shop có thể có nhiều Manager. */
public interface StoreManagerRepository extends JpaRepository<StoreManager, Long> {
    List<StoreManager> findByManagerId(Long managerId);

    boolean existsByStoreIdAndManagerId(Long storeId, Long managerId);

    Optional<StoreManager> findByStoreIdAndManagerId(Long storeId, Long managerId);

    /** JOIN FETCH manager vì open-in-view=false (JSP đọc tên/email Manager). */
    @Query("select sm from StoreManager sm join fetch sm.manager m where sm.store.id in :storeIds order by sm.id")
    List<StoreManager> findWithManagerByStoreIdIn(@Param("storeIds") Collection<Long> storeIds);
}
