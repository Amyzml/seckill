package com.zml.controller;

import com.zml.controller.result.AjaxResult;
import com.zml.entity.Account;
import com.zml.entity.Product;
import com.zml.exception.ServiceException;
import com.zml.service.AccountService;
import com.zml.service.ProductService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class SecKillController {
   @Autowired
    private ProductService productService;
   @Autowired
   private AccountService accountService;

    @GetMapping("/")
   public String login(){
       return "login";
   }

   @PostMapping("/")
    public String login(String name,String password,HttpSession session){
       Account account = accountService.findByName(name,password);
        session.setAttribute("name",name);
       return "redirect:/list";
    }


    @GetMapping("/list")
   public String home(Model model){
        List<Product> productList = productService.findAll();
        model.addAttribute("productList",productList);
       return "list";
   }

   @GetMapping("/product/add")
   public String addProduct(){
       return "add";
   }
   @PostMapping("/product/add")
   public String addProduct(Product product, MultipartFile image,String sTime,String eTime){

       DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

       DateTime stateDateTime = DateTime.parse(sTime,timeFormatter);
       DateTime endDateTime = DateTime.parse(eTime,timeFormatter);

       product.setStartTime(stateDateTime.toDate());
       product.setEndTime(endDateTime.toDate());
       if (image.isEmpty()){
           productService.save(product,null);
       }else {
           try {
               productService.save(product,image.getInputStream());
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       return "redirect:/list";
   }

   @GetMapping("/product/{id:\\d+}")
   public String show(@PathVariable Integer id, Model model){

       Product product = productService.findById(id);
       model.addAttribute("product",product);
       return "product";
   }

    /**
     * 秒杀
     * @param id
     * @return
     */
   @GetMapping("/product/seckill/{id:\\d+}")
   @ResponseBody
   public AjaxResult secKill(@PathVariable Integer id, HttpSession session){
       try{
           String name = (String) session.getAttribute("name");
           System.out.println("name -- >" + name);
           System.out.println("id -- > " + id);
           productService.secKill(id,name);

           return AjaxResult.success();
       }catch (ServiceException e){

            return AjaxResult.error(e.getMessage());
       }


   }
}
