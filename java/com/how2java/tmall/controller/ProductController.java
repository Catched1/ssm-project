package com.how2java.tmall.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.Page;

@Controller
@RequestMapping("")
public class ProductController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    //映射访问的admin_product_add的路径
    @RequestMapping("admin_product_add")
    //在ProductController中获取Product对象，并插入到数据库
    public String add(Model model, Product p){
        p.setCreateDate(new Date());
        //通过productService保存对象/保存到数据库
        productService.add(p);
        //客户端跳转到admin_product_list,并带上参数cid
        return "redirect:admin_product_list?cid="+p.getCid();
    }

    @RequestMapping("admin_product_delete")
    public String delete(int id) {
        Product p = productService.get(id);
        productService.delete(id);
        return "redirect:admin_product_list?cid="+p.getCid();
    }

    @RequestMapping("admin_product_edit")
    public String edit(Model model, int id) {
        Product p = productService.get(id);
        Category c = categoryService.get(p.getCid());
        p.setCategory(c);
        model.addAttribute("p", p);
        return "admin/editProduct";
    }

    @RequestMapping("admin_product_update")
    public String update(Product p){
        productService.update(p);
        return "redirect:admin_product_list?cid="+p.getCid();
    }

    @RequestMapping("admin_product_list")
    //获取分类cid，分页对象Page
    public String list(int cid, Model model,Page page){
        //根据cid获取Category对象
        Category c = categoryService.get(cid);
        //通过PageHelper设置分页参数
        PageHelper.offsetPage(page.getStart(), page.getCount());
        //基于cid，获取当前分类下的产品集合
        List<Product> ps = productService.list(cid);

        //通过PageInfo获取产品总数
        int total = (int) new PageInfo<>(ps).getTotal();
        //把总数设置给分页page对象
        page.setTotal(total);
        //拼接字符串"&cid="+c.getId()，设置给page对象的Param值。
        page.setParam("&cid="+c.getId());

        //把产品集合设置到 request的 "ps" 产品上
        model.addAttribute("ps", ps);
        //把分类对象设置到 request的 "c" 产品上。
        model.addAttribute("c", c);
        //把分页对象设置到 request的 "page" 对象上
        model.addAttribute("page", page);

        //服务端跳转到admin/listProduct.jsp页面
        return "admin/listProduct";
    }
}
