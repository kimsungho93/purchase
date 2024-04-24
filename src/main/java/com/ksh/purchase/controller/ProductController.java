package com.ksh.purchase.controller;

import com.ksh.purchase.controller.reqeust.CreateProductRequest;
import com.ksh.purchase.controller.response.ProductResponse;
import com.ksh.purchase.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @PostMapping("/api/v1/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest productRequest,
            @AuthenticationPrincipal User user) {
        long userId = Long.parseLong(user.getUsername());
        productService.createProduct(productRequest, userId);
        return ResponseEntity.ok().body(productService.createProduct(productRequest, userId));
    }

    // 상품 전체 조회
    @GetMapping("/api/v1/products")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }

    // 상품 단 건 조회
    @GetMapping("/api/v1//{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }
}
