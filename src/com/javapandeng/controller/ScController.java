package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.Item;
import com.javapandeng.po.Sc;
import com.javapandeng.service.ItemService;
import com.javapandeng.service.ScService;
import com.javapandeng.utils.Consts;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/sc")
@Controller
public class ScController extends BaseController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ScService scService;

    @RequestMapping("/exAdd")
    public String exAdd(Sc sc, HttpServletRequest request) {

        Object attribute = request.getSession().getAttribute(Consts.USERID);

        if (attribute == null) {

            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        sc.setUserId(userId);
        scService.insert(sc);


        Item load = itemService.load(sc.getItemId());
        load.setScNum(load.getScNum() + 1);
        itemService.updateById(load);

        return "redirect:/sc/findBySql.action";
    }

    @RequestMapping("/findBySql")
    public String findBySql(Model model,HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if(attribute==null){
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        String sql = "select * from sc where user_id="+userId+" order by id desc";
        Pager<Sc> pagers = scService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers",pagers);
        return "sc/my";
    }


    @RequestMapping("/delete")
    public String delete(HttpServletRequest request, Integer id) {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) {
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        scService.deleteById(id);
        return "redirect:/sc/findBySql.action";
    }

}
