package com.ksh.purchase.service;

import com.ksh.purchase.controller.reqeust.CreateProductRequest;
import com.ksh.purchase.controller.reqeust.OrderProductDTO;
import com.ksh.purchase.controller.response.ProductResponse;
import com.ksh.purchase.entity.Product;
import com.ksh.purchase.entity.User;
import com.ksh.purchase.entity.enums.ProductStatus;
import com.ksh.purchase.exception.CustomException;
import com.ksh.purchase.exception.ErrorCode;
import com.ksh.purchase.repository.ProductRepository;
import com.ksh.purchase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 상품 등록
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Product product = request.toEntity(request, user);
        product.setUser(user);
        return ProductResponse.from(productRepository.save(product));
    }

    // 상품 목록 조회
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findAllByStatus(pageRequest, ProductStatus.SALE).stream()
                .map(ProductResponse::from)
                .toList();
    }

    // 상품 단 건 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    public List<Product> findByIdIn(List<OrderProductDTO> ids) {
        return productRepository.findByIdIn(ids.stream()
                .map(OrderProductDTO::productId)
                .toList());
    }

    public Product findBtyId(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
