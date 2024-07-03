package com.bonny.springbootmall.constant;

public class MyTest {
    public static void main(String[] args) {

        // category 會存放 "FOOD" 的值，可以轉換成字串類型
        ProductCategory category = ProductCategory.FOOD;
        String s = category.name();
        System.out.println(s);

        //會查看是否已經存在固定值，如果有的話會把 s2 存放在 category2 當中
        String s2 = "CAR";
        ProductCategory category2 = ProductCategory.valueOf(s2);

    }
}
