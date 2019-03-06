package com.how2java.tmall.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page;
import com.how2java.tmall.util.UploadedImageFile;

@Controller
@RequestMapping("")
public class ProductImageController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    //映射到admin_productImage_add的路径
    @RequestMapping("admin_productImage_add")
    //通过pi对象接收type和pid的注入
    public String add(ProductImage pi, HttpSession session, UploadedImageFile uploadedImageFile){
        //借助productImageService，向数据库中插入数据
        productImageService.add(pi);
        //文件命名以保存到数据库的产品图片对象的id+".jpg"的格式命名
        String fileName = pi.getId() + ".jpg";
        String imageFolder;
        String imageFolder_small=null;
        String imageFolder_middle=null;
        //根据session().getServletContext().getRealPath("img/productSingle"),定位到存放单个产品图片的目录
        //除了productSingle，还有productSingle_middle和productSingle_small。
        //因为每上传一张图片，都会有对应的正常，中等和小的三种大小图片，并且放在3个不同的目录下
        if(ProductImageService.type_single.equals(pi.getType())){
            imageFolder=session.getServletContext().getRealPath("img/productSingle");
            imageFolder_small= session.getServletContext().getRealPath("img/productSingle_small");
            imageFolder_middle= session.getServletContext().getRealPath("img/productSingle_middle");
        }else{
            imageFolder=session.getServletContext().getRealPath("img/productDetail");
        }

        File f= new File(imageFolder, fileName);//新建文件
        f.getParentFile().mkdirs();//可以创建多层目录，不需要父目录存在
        try{
            // 通过uploadedImageFile保存文件
            uploadedImageFile.getImage().transferTo(f);
            //借助ImageUtil.change2jpg()方法把格式真正转化为jpg，而不仅仅是后缀名为.jpg
            BufferedImage img = ImageUtil.change2jpg(f);
            ImageIO.write(img, "jpg",f);

            if(ProductImageService.type_single.equals(pi.getType())){
                File f_small = new File(imageFolder_small,fileName);
                File f_middle = new File(imageFolder_middle, fileName);

                //再借助ImageUtil.resizeImage把正常大小的图片，改变大小之后，
                // 分别复制到productSingle_middle和productSingle_small目录下。
                ImageUtil.resizeImage(f, 56, 56, f_small);
                ImageUtil.resizeImage(f, 217,190,f_middle);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //处理完毕之后，客户端条跳转到admin_productImage_list?pid=，并带上pid。
        return "redirect:admin_productImage_list?pid="+pi.getPid();
    }

    //点击删除超链，进入ProductImageController的delete方法
    @RequestMapping("admin_productImage_delete")
    public String delete(int id,HttpSession session){
        //获取id
        ProductImage pi = productImageService.get(id);

        String fileName = pi.getId()+".jpg";//file文件，folder目录
        String imageFolder;
        String imageFolder_small=null;
        String imageFolder_middle=null;

        //如果是单个图片，那么删除3张正常，中等，小号图片
        if(ProductImageService.type_single.equals(pi.getType())){
            //getServletContext().getRealPath()获得系统的绝对路径
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_small=session.getServletContext().getRealPath("immg/productSingle_small");
            imageFolder_middle=session.getServletContext().getRealPath("img/productSingle_middle");
            File imageFile = new File(imageFolder,fileName);
            File f_small = new File(imageFolder_small,fileName);
            File f_middle = new File(imageFolder_middle,fileName);
            imageFile.delete();
            f_small.delete();
            f_middle.delete();
        }else{
            //如果是详情图片，那么删除一张图片
            imageFolder=session.getServletContext().getRealPath("img/productDetail");
            File imageFile=new File(imageFolder,fileName);
            imageFile.delete();
        }
        //借助productImageService，删除数据
        productImageService.delete(id);
        // 客户端跳转到admin_productImage_list地址
        return "redirect:admin_productImage_list?pid="+pi.getPid();
    }

    @RequestMapping("admin_productImage_list")
    public String list(int pid, Model model) {
        Product p =productService.get(pid);
        List<ProductImage> pisSingle = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> pisDetail = productImageService.list(pid, ProductImageService.type_detail);

        model.addAttribute("p", p);
        model.addAttribute("pisSingle", pisSingle);
        model.addAttribute("pisDetail", pisDetail);

        return "admin/listProductImage";
    }
}
