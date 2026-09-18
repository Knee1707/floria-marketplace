package com.starshop.service.customer;

import com.starshop.domain.ProductViewHistory;
import com.starshop.repository.ProductRepository;
import com.starshop.repository.ProductViewHistoryRepository;
import com.starshop.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/** @owner: L. */
@Service
@RequiredArgsConstructor
public class ViewHistoryServiceImpl implements ViewHistoryService {

    private final ProductViewHistoryRepository historyRepository;
    private final ProductRepository productRepository;
    private final CurrentUser currentUser;

    @Override
    @Transactional
    public void record(Long productId) {
        if (!isLoggedIn()) return;
        Long uid = currentUser.id();
        historyRepository.findByUserIdAndProductId(uid, productId).ifPresentOrElse(
                h -> { h.setViewedAt(Instant.now()); historyRepository.save(h); },
                () -> productRepository.findById(productId).ifPresent(p ->
                        historyRepository.save(ProductViewHistory.builder()
                                .user(currentUser.get()).product(p).viewedAt(Instant.now()).build())));
    }

    @Override
    public List<ProductViewHistory> myRecent() {
        return historyRepository.findTop20ByUserIdOrderByViewedAtDesc(currentUser.id());
    }

    private boolean isLoggedIn() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());
    }
}
