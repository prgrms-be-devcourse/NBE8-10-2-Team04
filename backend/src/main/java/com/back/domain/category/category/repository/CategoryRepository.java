package com.back.domain.category.category.repository;

import com.back.domain.category.category.dto.CategoryResponse;
import com.back.domain.category.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query(""" 
            select new com.back.domain.category.category.dto.CategoryResponse(
                c.id, c.name, count(i.id)
              )
              from Category c
              left join Item i on i.category = c and i.user.id = :userId
              group by c.id, c.name
              order by c.id
            """)
    List<CategoryResponse> findAllWithItemCount(Long userId);
}
