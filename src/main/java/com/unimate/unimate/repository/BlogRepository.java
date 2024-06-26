package com.unimate.unimate.repository;

import com.unimate.unimate.entity.Blog;
import com.unimate.unimate.enums.BlogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;


public interface BlogRepository extends JpaRepository<Blog, Long> {
    Blog findBlogById(Long id);
    ArrayList<Blog> findBlogsByBlogType(BlogType blogType);
    ArrayList<Blog> findBlogsByTitleContainingIgnoreCase(String keyWord);
    ArrayList<Blog> findBlogsByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(e) FROM Blog e")
    Long countAllBlog();
}
