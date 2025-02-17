package com.bonny.springbootmall.dao;

import com.bonny.springbootmall.dto.ProductQueryParams;
import com.bonny.springbootmall.dto.ProductRequest;
import com.bonny.springbootmall.model.Product;

import java.util.List;

public interface ProductDao {

    // 計算商品總筆數
    Integer countProduct (ProductQueryParams productQueryParams);

    // 查詢商品列表
    // 依category條件去查詢
    // 依關鍵字條件去查詢
    // 一併將參數值傳出去
    List<Product> getProducts(ProductQueryParams productQueryParams);

    // 依id查詢
    Product getProductById(Integer productId);

    // 新增商品
    Integer createProduct(ProductRequest productRequest);

    // 修改商品
    void updateProduct(Integer productId, ProductRequest productRequest);

    // 扣除商品庫存
    void updateStock(Integer productId, Integer stock);

    // 刪除商品
    void deleteProductById(Integer productId);
}
