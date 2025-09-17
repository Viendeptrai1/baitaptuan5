package com.vienphan.baitaptuan5.controller;

import com.vienphan.baitaptuan5.entity.Category;
import com.vienphan.baitaptuan5.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Hiển thị danh sách categories với phân trang và tìm kiếm
    @GetMapping
    public String listCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        Page<Category> categoryPage = categoryService.searchCategories(keyword, page, size, sortBy, sortDir);

        model.addAttribute("categories", categoryPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "categories/list";
    }

    // Hiển thị form thêm category mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Thêm Category Mới");
        return "categories/form";
    }

    // Xử lý thêm category mới
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute Category category,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm Category Mới");
            return "categories/form";
        }

        try {
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Category '" + category.getName() + "' đã được thêm thành công!");
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Thêm Category Mới");
            return "categories/form";
        }
    }

    // Hiển thị form chỉnh sửa category
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> category = categoryService.getCategoryById(id);
        
        if (category.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category không tồn tại!");
            return "redirect:/categories";
        }

        model.addAttribute("category", category.get());
        model.addAttribute("pageTitle", "Chỉnh Sửa Category");
        return "categories/form";
    }

    // Xử lý cập nhật category
    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                               @Valid @ModelAttribute Category category,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Chỉnh Sửa Category");
            return "categories/form";
        }

        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Category '" + category.getName() + "' đã được cập nhật thành công!");
            return "redirect:/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", "Chỉnh Sửa Category");
            return "categories/form";
        }
    }

    // Hiển thị chi tiết category
    @GetMapping("/detail/{id}")
    public String showCategoryDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> category = categoryService.getCategoryById(id);
        
        if (category.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Category không tồn tại!");
            return "redirect:/categories";
        }

        model.addAttribute("category", category.get());
        return "categories/detail";
    }

    // Vô hiệu hóa category (soft delete)
    @PostMapping("/deactivate/{id}")
    public String deactivateCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            if (category.isPresent()) {
                categoryService.deactivateCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Category '" + category.get().getName() + "' đã được vô hiệu hóa!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }

    // Kích hoạt lại category
    @PostMapping("/activate/{id}")
    public String activateCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            if (category.isPresent()) {
                categoryService.activateCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Category '" + category.get().getName() + "' đã được kích hoạt lại!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }

    // Xóa hoàn toàn category (hard delete)
    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Category> category = categoryService.getCategoryById(id);
            if (category.isPresent()) {
                categoryService.deleteCategory(id);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Category '" + category.get().getName() + "' đã được xóa hoàn toàn!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }
}

// Controller cho trang chủ
@Controller
class HomeController {
    
    private final CategoryService categoryService;
    
    public HomeController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalCategories", categoryService.getTotalCategories());
        model.addAttribute("activeCategories", categoryService.getActiveCategoriesCount());
        return "redirect:/categories";
    }
}
