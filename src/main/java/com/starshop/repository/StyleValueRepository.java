package com.starshop.repository;

import com.starshop.domain.StyleValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** @owner: TV3 dùng. */
public interface StyleValueRepository extends JpaRepository<StyleValue, Long> {
    List<StyleValue> findByStyleId(Long styleId);
}
