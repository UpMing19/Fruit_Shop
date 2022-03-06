package com.javapandeng.controller;

import com.javapandeng.base.BaseController;
import com.javapandeng.po.News;
import com.javapandeng.service.NewsService;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@RequestMapping("/news")
@Controller
public class NewsController extends BaseController {

    @Autowired
    private NewsService newsService;

    @RequestMapping("/findBySql")
    public String findBySql(Model model, News news) {
        String sql = "select * from news where 1 = 1";
        if (!isEmpty(news.getName())) sql += " and name like '%" + news.getName() + "%'";
        sql += " order by id  desc";
        Pager<News> pagers = newsService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        model.addAttribute("obj", news);
        return "news/news";
    }

    @RequestMapping("/delete")
    public String delete(News news) {
        newsService.deleteByEntity(news);
        return "redirect:/news/findBySql.action";
    }

    @RequestMapping("/add")
    public String add() {
        return "news/add";
    }

    @RequestMapping("/exAdd")
    public String exAdd(News news) {
        news.setAddTime(new Date());
        newsService.insert(news);
        return "redirect:/news/findBySql.action";
    }

    @RequestMapping("/update")
    public String update(Model model, Integer id) {
        News news = newsService.load(id);
        model.addAttribute("obj", news);
        return "news/update";
    }

    @RequestMapping("/exUpdate")
    public String exUpdate(News news) {
        news.setAddTime(new Date());
        newsService.updateById(news);
        return "redirect:/news/findBySql.action";
    }

    @RequestMapping("/list")
    public String list(Model model) {
        Pager<News> pagers = newsService.findByEntity(new News());
        model.addAttribute("pagers",pagers);
        return "news/list";
    }

    @RequestMapping("/view")
    public String view(Model model, Integer id) {
        News news = newsService.load(id);
        model.addAttribute("obj", news);
        return "news/view";
    }

}
