package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.User;
import com.javapandeng.service.UserService;
import com.javapandeng.utils.Consts;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @RequestMapping("/findBySql")
    public String findBySql(Model model, User user) {
        String sql = "select * from user where 1 = 1 ";
        if (!isEmpty(user.getUserName())) {
            sql += " and userName like '%" + user.getUserName() + "%' ";
        }
        sql += " order by id ";
        Pager<User> pagers = userService.findBySqlRerturnEntity(sql);
        model.addAttribute("obj", user);
        model.addAttribute("pagers", pagers);
        return "user/user";
    }

    @RequestMapping("/view")
    public String view(Model model, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) {

            return "redirect:/login/uLogin";
        }
        Integer useId = Integer.valueOf(attribute.toString());
        User user = userService.load(useId);
        model.addAttribute("obj", user);
        return "user/view";
    }
    @RequestMapping("/exUpdate")
    public String exUpdate(User user , HttpServletRequest request )
    {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) {

            return "redirect:/login/uLogin";
        }
        Integer useId = Integer.valueOf(attribute.toString());
        user.setId(useId);
        userService.updateById(user);
        return "redirect:/user/view.action";
    }


}
