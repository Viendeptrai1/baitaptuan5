package com.vienphan.baitaptuan5.service;

import com.vienphan.baitaptuan5.entity.Category;
import com.vienphan.baitaptuan5.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Lấy tất cả categories với phân trang
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    // Lấy categories với phân trang và sắp xếp
    public Page<Category> getAllCategories(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.findAll(pageable);
    }

    // Tìm kiếm categories với từ khóa
    public Page<Category> searchCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, pageable);
    }

    // Tìm kiếm categories với từ khóa, sắp xếp và phân trang
    public Page<Category> searchCategories(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, pageable);
    }

    // Lấy categories đang hoạt động
    public Page<Category> getActiveCategories(Pageable pageable) {
        return categoryRepository.findByActiveTrue(pageable);
    }

    // Tìm category theo ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Tìm category theo tên
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    // Lưu category mới
    @Transactional
    public Category saveCategory(Category category) {
        // Kiểm tra tên category đã tồn tại chưa
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Tên category đã tồn tại");
        }
        return categoryRepository.save(category);
    }

    // Cập nhật category
    @Transactional
    public Category updateCategory(Long id, Category updatedCategory) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        
        if (existingCategory.isEmpty()) {
            throw new IllegalArgumentException("Category không tồn tại");
        }

        // Kiểm tra tên category đã tồn tại (trừ chính nó)
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(updatedCategory.getName(), id)) {
            throw new IllegalArgumentException("Tên category đã tồn tại");
        }

        Category category = existingCategory.get();
        category.setName(updatedCategory.getName());
        category.setDescription(updatedCategory.getDescription());
        category.setActive(updatedCategory.getActive());

        return categoryRepository.save(category);
    }

    // Xóa category (soft delete - chỉ đặt active = false)
    @Transactional
    public void deactivateCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Category cat = category.get();
            cat.setActive(false);
            categoryRepository.save(cat);
        } else {
            throw new IllegalArgumentException("Category không tồn tại");
        }
    }

    // Kích hoạt lại category
    @Transactional
    public void activateCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Category cat = category.get();
            cat.setActive(true);
            categoryRepository.save(cat);
        } else {
            throw new IllegalArgumentException("Category không tồn tại");
        }
    }

    // Xóa hoàn toàn category (hard delete)
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category không tồn tại");
        }
        categoryRepository.deleteById(id);
    }

    // Lấy danh sách categories cho dropdown (chỉ active)
    public List<Category> getActiveCategoriesForDropdown() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    // Kiểm tra tên category có tồn tại không
    public boolean categoryNameExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    // Kiểm tra tên category có tồn tại không (trừ ID hiện tại)
    public boolean categoryNameExists(String name, Long excludeId) {
        return categoryRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
    }

    // Đếm tổng số categories
    public long getTotalCategories() {
        return categoryRepository.count();
    }

    // Đếm số categories đang hoạt động
    public long getActiveCategoriesCount() {
        return categoryRepository.findByActiveTrueOrderByNameAsc().size();
    }
}
