package com.how2java.tmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page;

@Controller
@RequestMapping("")
public class PropertyController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    PropertyService propertyService;

    //在listProperty.jsp页面提交数据的时候，除了提交属性名称，还会提交cid
    //add方法映射路径admin_property_add的访问
    @RequestMapping("admin_property_add")
    //在PropertyController通过参数Property接受注入
    public String add(Model model, Property p){
        //通过propertyService保存对象/保存到数据库
        propertyService.add(p);
        //客户端跳转到admin_property_list,并带上参数cid
        return "redirect:admin_property_list?cid="+p.getCid();
    }

    //在PropertyController的delete方法中获取id
    @RequestMapping("admin_property_delete")
    public String delete(int id){
        //根据id获取Property对象
        Property p = propertyService.get(id);
        //借助propertyService删除这个对象对应的数据
        propertyService.delete(id);
        //客户端跳转到admin_property_list，并带上参数cid
        return "redirect:admin_property_list？cid="+p.getCid();
    }

    //映射admin_category_edit路径的访问
    @RequestMapping("admin_property_edit")
    //参数id用来接收注入
    public String edit(Model model, int id){
        //在PropertyController的edit方法中，根据id获取Property对象
        Property p = propertyService.get(id);
        //根据properoty对象的cid属性获取Category对象
        Category c = categoryService.get(p.getCid());//cid应用来电捕捉技术
        //并把其设置在Property对象的category属性上
        p.setCategory(c);
        //把Property对象放在request的 "p" 属性中
        model.addAttribute("p", p);//addAttribute往前台传数据，可以传对象，可以传List，通过el表达式 ${}可以获取到
        //服务端跳转到admin/editProperty.jsp
        //在editProperty.jsp中显示属性名称
        return "admin/editProperty";
    }

    //在PropertyController的update方法中获取Property对象
    @RequestMapping("admin_property_update")
    public String update(Property p){
        //借助propertyService更新这个对象到数据库
        propertyService.update(p);
        //客户端跳转到admin_property_list，并带上参数cid
        return "redirect:admin_property_list?cid="+p.getCid();
    }

    //查询地址admin_property_list映射的是PropertyController的list()方法
    @RequestMapping("admin_property_list")
    public String list(int cid, Model model, Page page){
        //获取分类cid，和分页对象page
        Category c = categoryService.get(cid);
        //通过PageHelper获取分页参数
        PageHelper.offsetPage(page.getStart(),page.getCount());
        //基于cid，获取当前分类下的属性集合
        List<Property> ps = propertyService.list(cid);
        //通过PageInfo获取属性总数
        int total = (int) new PageInfo<>(ps).getTotal();
        //把总数设置给分页page对象
        page.setTotal(total);
        //拼接字符串"&cid="+c.getId()，设置给page对象的Param值。
        //因为属性分页都是基于当前分类下的分页，所以分页的时候需要传递这个cid
        page.setParam("&cid="+c.getId());
        //把属性集合设置到 request的 "ps" 属性上
        model.addAttribute("ps", ps);
        //把分类对象设置到 request的 "c" 属性上。
        model.addAttribute("c", c);
        //把分页对象设置到 request的 "page" 对象上
        model.addAttribute("page", page);
        //服务端跳转到admin/listProperty.jsp页面
        //在listProperty.jsp页面上使用c:forEach 遍历ps集合，并显示
        return "admin/listProperty";
    }
}
