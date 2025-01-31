package org.example.service;

import org.example.pojo.Category;

import java.util.List;

public interface CategoryService {
    void add(Category category);

    List<Category> list();

    //根据id查询分类信息
    Category findById(Integer id);

    void update(Category category);

    void delete(Integer id);
}
