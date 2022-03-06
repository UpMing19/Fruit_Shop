package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.Message;
import com.javapandeng.service.MessageService;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Autowired
    private  MessageService messageService;

    @RequestMapping("/findBySql")
    public String findBySql(Model model , Message message)
    {
        String sql = "select * from message  where 1=1 ";
        if(!isEmpty(message.getName()))
        {
            sql+=" and name like '%" + message.getName() +  "%' ";
        }
        sql+=" order by id desc";
        Pager<Message> pagers = messageService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers",pagers);
        model.addAttribute("obj",message);
        return "message/message";
    }



    @RequestMapping("/delete")
    public String delete(Message message)
    {
        messageService.deleteByEntity(message);
        return "redirect:/message/findBySql.action";
    }
    @RequestMapping("/add")
    public String add()
    {
        return "message/add";
    }


}
