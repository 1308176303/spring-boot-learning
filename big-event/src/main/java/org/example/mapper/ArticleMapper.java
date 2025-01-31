package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Article;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Insert("insert into article(title,content,cover_img,state,category_id,create_user,create_time,update_time)" +
            "values(#{title},#{content},#{coverImg},#{state},#{categoryId},#{createUser},#{createTime},#{updateTime})")
    public void add(Article article) ;


    List<Article> list(Integer userId, Integer categoryId, String state);

    @Update("update article set title=#{article.title},content=#{article.content},cover_img=#{article.coverImg},state=#{article.state},category_id=#{article.categoryId},update_time=#{article.updateTime}  where create_user=#{userId} and id=#{article.id}")
    void update(Article article, Integer userId);

    @Select("select * from article where id=#{id} and create_user=#{userId}")
    Article detail(Integer id, Integer userId);

    @Delete("delete from article where id=#{id} and create_user=#{userId}")
    void delete(Integer id, Integer userId);
}
