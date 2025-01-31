package org.example.controller;

import org.example.pojo.Article;
import org.example.pojo.Category;
import org.example.pojo.PageBean;
import org.example.pojo.Result;
import org.example.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articalService;

    @PostMapping
    public Result add(@RequestBody @Validated(Category.Add.class) Article article){
        articalService.add(article);
        return Result.success();
    }

    @GetMapping
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state
    ){
        PageBean<Article> pb = articalService.list(pageNum,pageSize,categoryId,state);
        return Result.success(pb);
    }

    @PutMapping
    public Result update(@RequestBody @Validated(Category.Update.class) Article article){
        articalService.update(article);
        return Result.success();
    }

    @GetMapping("/detail")
    public Result<Article> detail(Integer id){
        Article article = articalService.detail(id);
        return Result.success(article);
    }

    @DeleteMapping
    public Result delete(Integer id){
        articalService.delete(id);
        return Result.success();
    }
}
