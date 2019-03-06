package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Category;
import  java.util.List;
/*
service层：存放业务逻辑处理，也是一些关于数据库处理的操作，但不是直接与数据库打交道，
他有接口和接口的实现方法，在接口的实现方法中需要导入mapper层，mapper层是直接与数据库打交道的
他也是个接口，只有方法名字，具体的实现在mapper.xml文件里，service层是工给我使用的方法。
 */
public interface CategoryService {
    //提供一个支持分页的查询方法list(Page page)和获取总数的方法total
    //这个service层接口里的list方法没有public，而Mapper里的list方法采用了public
    List<Category> list();
    void add(Category category);
    void delete(int id);
    Category get(int id);
    void update(Category category);
}
