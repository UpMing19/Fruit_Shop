package com.javapandeng.controller;

import com.alibaba.fastjson.JSONObject;
import com.javapandeng.base.BaseController;
import com.javapandeng.po.*;
import com.javapandeng.service.ItemCategoryService;
import com.javapandeng.service.ItemService;
import com.javapandeng.service.ManageService;
import com.javapandeng.service.UserService;
import com.javapandeng.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
    @Autowired
    private ManageService manageService;

    @Autowired
    private ItemCategoryService itemCategoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login() {
        return "/login/mLogin";
    }

    @RequestMapping("/toLogin")
    public String toLogin(Manage manage, HttpServletRequest httpServletRequest) {
        Manage byEntity = manageService.getByEntity(manage);
        if (byEntity == null) {
            return "redirect:/login/mtuichu";
        }
        httpServletRequest.getSession().setAttribute(Consts.MANAGE, byEntity);
        return "/login/mIndex";
    }

    @RequestMapping("mtuichu")
    public String mtuichu(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().setAttribute(Consts.MANAGE, null);
        return "/login/mLogin";
    }

    @RequestMapping("/uIndex")
    public String uIndex(Model model, ItemCategory itemCategory) {

        String sql1 = "select * from item_category where isDelete = 0 and pid is null order by name";
        List<ItemCategory> fatherList = itemCategoryService.listBySqlReturnEntity(sql1);
        ArrayList<CategoryDto> list = new ArrayList<>();

        if (!CollectionUtils.isEmpty(fatherList)) {

            for (ItemCategory ic : fatherList) {
                CategoryDto dto = new CategoryDto();
                dto.setFather(ic);

                String sql2 = "select * from item_category where isDelete =0 and pid = " + ic.getId();
                List<ItemCategory> childrens = itemCategoryService.listBySqlReturnEntity(sql2);
                dto.setChildrens(childrens);
                list.add(dto);
                model.addAttribute("lbs", list);
            }


        }

        //折扣
        List<Item> zks = itemService.listBySqlReturnEntity("select * from item where isDelete = 0 and zk is not null order by zk desc limit 0,10");
        model.addAttribute("zks", zks);
        //热销
        List<Item> rxs = itemService.listBySqlReturnEntity("select * from item where isDelete = 0 order by gmNum desc limit 0,10");
        model.addAttribute("rxs", rxs);
        return "login/uIndex";
    }

    @RequestMapping("/res")
    public String res() {
        return "login/res";
    }

    @RequestMapping("/toRes")
    public String toRes(User user) {
        userService.insert(user);
        return "login/uLogin";
    }

    @RequestMapping("/uLogin")
    public String uLogin() {
        return "login/uLogin";
    }

    @RequestMapping("utoLogin")
    public String utoLogin(HttpServletRequest request, User user) {
        User byEntity = userService.getByEntity(user);
        if (byEntity == null) {
            return "redirect:/login/res.action";
        } else {
            request.getSession().setAttribute("role",2);
            request.getSession().setAttribute(Consts.USERNAME,byEntity.getUserName());
            request.getSession().setAttribute(Consts.USERID,byEntity.getId());
            return "redirect:/login/uIndex.action";
        }
    }
    @RequestMapping("/uTui")
    public String uTui(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login/uIndex.action";
    }


    @RequestMapping("/pass")
    public String pass(HttpServletRequest request,Model model)
    {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) return "redirect:/login/uLogin";
        Integer userId = Integer.valueOf(attribute.toString());
        User user = userService.load(userId);
        model.addAttribute("obj",user);
        return "login/pass";
    }

    @RequestMapping("/upass")
    @ResponseBody
    public String upass(HttpServletRequest request,String password)
    {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        JSONObject js = new JSONObject();
        if (attribute == null) {
            js.put(Consts.RES,0);
            return js.toString();
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User user = userService.load(userId);
        user.setPassWord(password);
        userService.updateById(user);
        js.put(Consts.RES,1);
        return js.toString();
    }

}
