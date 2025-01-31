package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.mapper.ArticleMapper;
import org.example.pojo.Article;
import org.example.pojo.PageBean;
import org.example.service.ArticleService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articalMapper;
    @Override
    public void add(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);
        articalMapper.add(article);
    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        PageHelper.startPage(pageNum, pageSize); // ✅ 启用分页

        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        List<Article> articles = articalMapper.list(userId, categoryId, state); // ✅ 这里 PageHelper 会自动包装成 Page<T>

        PageInfo<Article> pageInfo = new PageInfo<>(articles); // ✅ 使用 PageInfo 包装，自动获取分页信息

        // ✅ 手动填充 PageBean
        PageBean<Article> pb = new PageBean<>();
        pb.setTotal(pageInfo.getTotal());
        pb.setItems(pageInfo.getList());

        return pb;
    }

    @Override
    public void update(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateUser(userId);
        articalMapper.update(article,userId);
    }

    @Override
    public Article detail(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Article article = articalMapper.detail(id,userId);
        return article;
    }

    @Override
    public void delete(Integer id) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        articalMapper.delete(id,userId);
    }
}
