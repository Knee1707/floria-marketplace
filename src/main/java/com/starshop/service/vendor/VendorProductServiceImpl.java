package com.starshop.service.vendor;

import com.starshop.domain.Category;
import com.starshop.domain.Product;
import com.starshop.domain.Store;
import com.starshop.domain.enums.CommonEnums.ProductStatus;
import com.starshop.repository.CategoryRepository;
import com.starshop.repository.ProductRepository;
import com.starshop.repository.VendorShipperQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Triển khai nghiệp vụ CRUD sản phẩm của cửa hàng (F4.3).
 * Luôn kiểm tra quyền sở hữu (ownership) chống lỗ hổng IDOR.
 * @owner: TV2 | @leaf: F4.3 CRUD sản phẩm | @status: DONE
 */
@Service
@RequiredArgsConstructor
public class VendorProductServiceImpl implements VendorProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VendorShipperQueryRepository queryRepository;
    private final StoreService storeService;

    @Override
    @Transactional(readOnly = true)
    public Page<Product> myProducts(Pageable pageable) {
        Store myStore = requireMyStore();
        return queryRepository.findProductsByStoreId(myStore.getId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Product get(Long productId) {
        Store myStore = requireMyStore();
        Product product = queryRepository.findProductWithStoreAndCategory(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        if (!product.getStore().getId().equals(myStore.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền truy cập sản phẩm của cửa hàng khác");
        }
        return product;
    }

    @Override
    @Transactional
    public void save(Long id, String name, String description, BigDecimal price, Integer stock,
                     Long categoryId, String thumbnailUrl) {
        Store myStore = requireMyStore();

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải lớn hơn 0");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không hợp lệ");
        }

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục sản phẩm không tồn tại"));
        }

        Product product;
        if (id == null) {
            product = Product.builder()
                    .store(myStore)
                    .soldCount(0L)
                    .ratingAvg(BigDecimal.ZERO)
                    .status(ProductStatus.ACTIVE)
                    .build();
        } else {
            product = queryRepository.findProductWithStoreAndCategory(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm cần cập nhật"));
            if (!product.getStore().getId().equals(myStore.getId())) {
                throw new IllegalArgumentException("Bạn không có quyền chỉnh sửa sản phẩm của cửa hàng khác");
            }
            if (product.getStatus() == ProductStatus.BANNED) {
                throw new IllegalStateException("Sản phẩm đã bị Quản trị viên khoá, không thể chỉnh sửa");
            }
        }

        product.setName(name.trim());
        product.setDescription(description != null ? description.trim() : "");
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
            product.setThumbnailUrl(thumbnailUrl.trim());
        }

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void hide(Long productId) {
        Store myStore = requireMyStore();
        Product product = queryRepository.findProductWithStoreAndCategory(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        if (!product.getStore().getId().equals(myStore.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền thao tác sản phẩm của cửa hàng khác");
        }
        if (product.getStatus() == ProductStatus.BANNED) {
            throw new IllegalStateException("Sản phẩm đang bị khoá bởi Quản trị viên");
        }

        // Chuyển đổi trạng thái ACTIVE <-> HIDDEN thuận tiện cho chủ shop
        if (product.getStatus() == ProductStatus.HIDDEN) {
            product.setStatus(ProductStatus.ACTIVE);
        } else {
            product.setStatus(ProductStatus.HIDDEN);
        }
        productRepository.save(product);
    }

    private Store requireMyStore() {
        Store store = storeService.myStore();
        if (store == null) {
            throw new IllegalStateException("Bạn chưa đăng ký cửa hàng");
        }
        return store;
    }
}
