package com.starshop.service.admin;

import com.starshop.domain.Delivery;
import com.starshop.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * F6.7 Nhà vận chuyển (tên + phí ship). Checkout của Leader đọc danh sách này để tính phí.
 * @owner: TV3 | @leaf: F6.7 | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private static final BigDecimal MAX_FEE = new BigDecimal("10000000");

    private final DeliveryRepository deliveryRepository;
    private final AdminScope adminScope;

    @Override
    @Transactional(readOnly = true)
    public List<Delivery> all() {
        return deliveryRepository.findAll(Sort.by("shippingFee", "id"));
    }

    @Override
    @Transactional
    public void save(Long id, String name, BigDecimal shippingFee) {
        adminScope.requireAdmin();
        String clean = name == null ? "" : name.trim();
        if (clean.length() < 2 || clean.length() > 120) {
            throw new IllegalArgumentException("Tên nhà vận chuyển phải từ 2 đến 120 ký tự");
        }
        if (shippingFee == null || shippingFee.signum() < 0 || shippingFee.compareTo(MAX_FEE) > 0) {
            throw new IllegalArgumentException("Phí ship phải từ 0 đến 10.000.000 đ");
        }
        Delivery d = (id == null) ? new Delivery()
                : deliveryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà vận chuyển #" + id));
        d.setName(clean);
        d.setShippingFee(shippingFee);
        deliveryRepository.save(d);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        adminScope.requireAdmin();
        if (!deliveryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy nhà vận chuyển #" + id);
        }
        deliveryRepository.deleteById(id);
    }
}
