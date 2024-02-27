package com.unimate.unimate.service;

import com.unimate.unimate.entity.Blog;
import com.unimate.unimate.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl implements BlogService{
    @Autowired
    private BlogRepository blogRepository;

    @Override
    public void saveBlog(Blog blog) {
        blogRepository.save(blog);
    }

    @Override
    public List<Blog> getAllBlog() {
        return blogRepository.findAll();
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findBlogById(id);
    }
}
