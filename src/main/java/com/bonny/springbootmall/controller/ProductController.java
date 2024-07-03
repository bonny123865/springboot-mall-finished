package com.bonny.springbootmall.controller;

import com.bonny.springbootmall.constant.ProductCategory;
import com.bonny.springbootmall.dto.ProductQueryParams;
import com.bonny.springbootmall.dto.ProductRequest;
import com.bonny.springbootmall.model.Product;
import com.bonny.springbootmall.service.ProductService;
import com.bonny.springbootmall.util.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 因使用了@Max、@Min，所以須加「@Validated」
@Validated
@RestController
public class ProductController {

    // 注入ProductService
    @Autowired
    private ProductService productService;

    // 查詢商品列表
    // 即使商品數據不存在，但「/products」這個API是存在的，所以還是會回200給前端
    // 可以使用 enum 去做，已經會被轉換完成(Spring Boot會自動將前端傳過來的字串去轉換成「ProductCategory」)，需要將category從Service層 拉到Dao層裡面去
    // "(required = false)" 代表前端不一定要帶上這一個參數，特好用，讚
    @GetMapping("/products")
    // 改用Page類型的Product數據
    public ResponseEntity<Page<Product>> getProducts(

            // 查詢條件 Filtering
            // @RequestParam 表示從url中取得請求參數
            // 加入「required = false」，category變為「可選」參數
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String search,

            // 排序 Sorting
            // "orderBy" : 商品類型或是什麼類別來做排序，預設使用 "defaultValue"
            // "sort" : 升冪是降冪排列，預設使用 "desc"，這種排序只限定於單排序，只能用一個條件進行排序
            // "http://localhost:8080/products" : 會使用預設的 "創建時間(created_date)" 來做排序
            @RequestParam(defaultValue = "created_date") String orderBy,
            @RequestParam(defaultValue = "desc") String sort,

            // 分頁 Pagination
            // limit : 限制需要取出幾個參數，目前預設 5 筆
            // offset : 會跳過前X筆數據，預設都不跳過
            // 如果加上 @Max、@Min : 要加上 "@Validated"
            // @Max(1000):前端傳過來的參數值最大不能超過1000 ，@Min(0):不能傳負數
            @RequestParam(defaultValue = "5") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {

        // 將前端的參數值存在productQueryParams
        ProductQueryParams productQueryParams = new ProductQueryParams();
        productQueryParams.setCategory(category);
        productQueryParams.setSearch(search);
        productQueryParams.setOrderBy(orderBy);
        productQueryParams.setSort(sort);
        productQueryParams.setLimit(limit);
        productQueryParams.setOffset(offset);

        // 取得 product list : 參數傳遞
        // 依category條件去查詢 、 依關鍵字條件去查詢 、 一併將參數值傳出去
        List<Product> productList = productService.getProducts(productQueryParams);

        // 取得 product 總數 : 因為總比數會因為查詢條件不同而改變，因此要傳回 "productQueryParams"
        Integer total = productService.countProduct(productQueryParams);

        // 分頁 Paging
        // 將前端的值存到page內，再將由page回傳給前端
        Page<Product> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);

        // 將查詢的商品數據放到results內，然後回傳給前端
        page.setResults(productList);

        // 就算資源不存在，但是 URL 存在，所以都需要固定回傳 200 OK 給前端
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    // 取得商品數據 : 依 id 條件查詢
    @GetMapping("/products/{productId}")
    // PathVariable 將路徑「{productId}」傳進參數「productId」
    public ResponseEntity<Product> getProduct(@PathVariable Integer productId) {

        // 取得商品數據，需使用ProductService內的方法
        Product product = productService.getProductById(productId);

        if (product != null) {
            // 資料不為空，回傳狀態碼200，body為查詢值
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } else {
            // 如果查詢出來是NULL，會回傳 "NOT_FOUND"，資料為空，回傳狀態碼404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 新增商品
    // 新創建class專門負責去接住前端所傳過來的json參數，避免讓原本的class(Product)太過複雜
    // 如果有加上 "Not Null"，記得參數要加「@Valid」，這樣新的class「ProductRequest」的「@NotNull」才會生效
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {

        // 會去資料庫創建商品
        // 要去返回資料庫所生成的productId回來
        Integer productId = productService.createProduct(productRequest);

        // 查詢商品數據回來
        Product product = productService.getProductById(productId);

        // 回給前端201
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // 修改商品
    // 記得參數要加「@Valid」，這樣新的class「ProductRequest」的「@NotNull」才會生效
    //可以有效確定、界定前端只能去修改 "productRequest" 這個變數值，而不是去修改整個 "Product" 物件
    @PutMapping("products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer productId,
                                                 @RequestBody @Valid ProductRequest productRequest) {

        // 1.先查詢要更新的商品有沒有在資料庫內
        Product product = productService.getProductById(productId);

        if (product == null) {
            // 不存在回傳404給前端
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 2.修改商品數據(要更新的是哪一個商品, 商品修改過後的值是什麼)
        // updateProduct不會返回值
        productService.updateProduct(productId, productRequest);

        // 查詢更新商品數據
        Product updatedProduct = productService.getProductById(productId);

        // 回傳ResponseEntity給前端，updatedProduct放在 body 傳給前端
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);

    }

    // 刪除商品 (商品存在，成功刪除。商品本來就不存在)
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {

        // 不需要先查詢資料存不存在，前端只要該商品不在就好了
        productService.deleteProductById(productId);

        // 回傳204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
