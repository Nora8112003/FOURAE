package com.vanlang.webbanhang.controller;

import com.vanlang.webbanhang.model.Category;
import com.vanlang.webbanhang.model.Product;
import com.vanlang.webbanhang.service.CategoryService;
import com.vanlang.webbanhang.service.ProductService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.vanlang.webbanhang.repository.CategoryRepository;
import java.util.List;

@Controller

@RequestMapping("/products")
public class ProductController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;// Đảm bảo bạn đã inject CategoryService
    // Display a list of all products
    @GetMapping
    public String showProducts(Model model) {
        model.addAttribute("products",productService.getAllProducts());
        model.addAttribute("categories",categoryService.getAllCategories());//
        return "/products/products-list";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories",categoryService.getAllCategories());// Load categories
        return "/products/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult bindingResult, Model model) {
        model.addAttribute("categories",categoryService.getAllCategories());
        if(bindingResult.hasErrors()){
            return "products/add-product";
        }
        productService.addProduct(product);
        return "redirect:/home";
    }
    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories",categoryService.getAllCategories());//Load categories
        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @Valid Product product, BindingResult bindingResult, Model model) {
        model.addAttribute("categories",categoryService.getAllCategories());//
        if(bindingResult.hasErrors()){
            product.setId(id);//// set id to keep it in the form in case of errors
            return "/products/update-product";
        }
        productService.updateProduct(product);
        return "redirect:/home";
    }

    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return "redirect:/home";
    }
    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "layout";
    }

    // Mapping to handle product retrieval by category
    @GetMapping("/byCategory/{categoryId}")
    public String getProductsByCategory(@PathVariable Long categoryId, Model model) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("categories",categoryService.getAllCategories());//
        return "home"; // Trả về tên view template hiển thị danh sách sản phẩm
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam(name = "keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword); // Gọi service để tìm kiếm sản phẩm
        model.addAttribute("products", products);
        return "home"; // Trả về view hiển thị danh sách sản phẩm
    }
    @GetMapping("/details/{id}")
    public String showProductDetails(@PathVariable("id") Long productId, Model model) {
        // Đoạn mã để lấy thông tin sản phẩm từ Service hoặc Repository
        Product product = productService.findById(productId);

        // Kiểm tra xem sản phẩm có tồn tại không
        if (product == null) {
            // Xử lý khi sản phẩm không tồn tại
            return "redirect:/home"; // Hoặc chuyển hướng đến một trang lỗi khác
        }

        // Truyền thông tin sản phẩm vào Model để hiển thị trên View
        model.addAttribute("product", product);

        // Trả về tên của View để hiển thị chi tiết sản phẩm
        return "/products/product-details"; // Tên view có thể là "product-details.html" (Thymeleaf sẽ giải thích)
    }
    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "0") int page, Model model) {
        // Số sản phẩm trên mỗi trang
        int pageSize = 10;

        // Lấy danh sách sản phẩm dựa trên trang và kích thước trang
        Page<Product> productPage = productService.findPaginated(PageRequest.of(page, pageSize));

        // Dữ liệu sản phẩm trên trang hiện tại
        model.addAttribute("products", productPage.getContent());

        // Tổng số trang
        model.addAttribute("totalPages", productPage.getTotalPages());

        // Trang hiện tại
        model.addAttribute("currentPage", page);

        return "/products/products-list";
    }
}

