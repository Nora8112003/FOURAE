package com.vanlang.webbanhang.controller;

import com.vanlang.webbanhang.model.Category;
import com.vanlang.webbanhang.model.User;
import com.vanlang.webbanhang.service.CategoryService;
import com.vanlang.webbanhang.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // Đánh dấu lớp này là một Controller trong Spring MVC.
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private CategoryService categoryService;// Đảm bảo bạn đã inject CategoryService
    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("categories",categoryService.getAllCategories());//
        return "users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validate đối tượng User
                           @NotNull BindingResult bindingResult, // Kết quả của quá trình validate
                           Model model) {
        model.addAttribute("categories",categoryService.getAllCategories());//
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định cho người dùng
        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("categories",categoryService.getAllCategories());//
        model.addAttribute("user", users);
        return "management/users-list";
    }
//    // Hiển thị form chỉnh sửa người dùng dựa trên tên đăng nhập
//    @GetMapping("/users/edit/{id}")
//    public String editUserForm(@PathVariable Long id, Model model) {
//        model.addAttribute("categories", categoryService.getAllCategories());
//        User user = userService.getUserById(id);
//        model.addAttribute("user", user);
//        return "/management/user-edit"; // Trả về view template chỉnh sửa người dùng
//    }
//
//    // Xử lý yêu cầu chỉnh sửa người dùng dựa trên tên đăng nhập
//    @PostMapping("/users/update/{id}")
//    public String editUser(@Valid @ModelAttribute("user") User user,
//                           BindingResult bindingResult,
//                           Model model) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("user", user);
//            return "/management/user-edit"; // Trả về lại form chỉnh sửa nếu có lỗi
//        }
//        userService.updateUser(user);
//        return "redirect:/users"; // Chuyển hướng về danh sách người dùng sau khi chỉnh sửa
//    }
// GET request to show category edit form
@GetMapping("/users/edit/{id}")
public String showUpdateForm(@PathVariable("id") Long id, Model model) {
    User user = userService.getUserById(id);
    model.addAttribute("user", user);
    return "/management/user-edit";
}

    // POST request to update category
    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            user.setId(id);
            return "/management/user-edit";
        }
        userService.updateUser(user);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/users";
    }

    // GET request for deleting category
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        userService.deleteUserById(id);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/users";
    }
}

