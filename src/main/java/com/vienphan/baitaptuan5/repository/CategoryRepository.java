package com.vienphan.baitaptuan5.repository;

import com.vienphan.baitaptuan5.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Tìm kiếm theo tên (không phân biệt chữ hoa thường) với phân trang
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Category> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            @Param("keyword") String keyword, Pageable pageable);
    
    // Tìm kiếm categories đang hoạt động
    Page<Category> findByActiveTrue(Pageable pageable);
    
    // Tìm kiếm categories theo trạng thái và từ khóa
    @Query("SELECT c FROM Category c WHERE c.active = :active AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Category> findByActiveAndKeyword(@Param("active") Boolean active, 
                                         @Param("keyword") String keyword, 
                                         Pageable pageable);
    
    // Tìm category theo tên chính xác
    Optional<Category> findByNameIgnoreCase(String name);
    
    // Kiểm tra tên category đã tồn tại (trừ ID hiện tại)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
    
    // Kiểm tra tên category đã tồn tại
    boolean existsByNameIgnoreCase(String name);
    
    // Lấy danh sách categories đang hoạt động (cho dropdown)
    List<Category> findByActiveTrueOrderByNameAsc();
}
