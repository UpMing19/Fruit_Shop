package com.javapandeng.controller;

import com.alibaba.fastjson.JSONObject;
import com.javapandeng.base.BaseController;
import com.javapandeng.po.*;
import com.javapandeng.service.*;
import com.javapandeng.utils.Consts;
import com.javapandeng.utils.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/itemOrder")
public class ItemOrderController extends BaseController {
    @Autowired
    private ItemOrderService itemOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CarService carService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ItemService itemService;

    @RequestMapping("/findBySql")
    public String findBySql(ItemOrder itemOrder, Model model) {
        String sql = "select * from item_order where 1 = 1 ";
        if (!isEmpty(itemOrder.getCode())) {
            sql += "and code like '%" + itemOrder.getCode() + "%'";
        }
        sql += " order by id desc";
        Pager<ItemOrder> pagers = itemOrderService.findBySqlRerturnEntity(sql);
        model.addAttribute("pagers", pagers);
        model.addAttribute("obj", itemOrder);
        return "itemOrder/itemOrder";
    }

    @RequestMapping("/my")
    public String my(Model model, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        if (attribute == null) {
            return "redirect:/login/uLogin";
        }
        Integer userId = Integer.valueOf(attribute.toString());
        //全部订单
        String sql = "select * from item_order where user_id="+userId+" order by id desc";
        List<ItemOrder> all = itemOrderService.listBySqlReturnEntity(sql);
        //待发货
        String sql2 = "select * from item_order where user_id="+userId+" and status=0 order by id desc";
        List<ItemOrder> dfh = itemOrderService.listBySqlReturnEntity(sql2);

        //已取消
        String sql3 = "select * from item_order where user_id="+userId+" and status=1 order by id desc";
        List<ItemOrder> yqx = itemOrderService.listBySqlReturnEntity(sql3);
        //已发货
        String sql4 = "select * from item_order where user_id="+userId+" and status=2 order by id desc";
        List<ItemOrder> dsh = itemOrderService.listBySqlReturnEntity(sql4);
        //已收货
        String sql5 = "select * from item_order where user_id="+userId+" and status=3 order by id desc";
        List<ItemOrder> ysh = itemOrderService.listBySqlReturnEntity(sql5);

        model.addAttribute("all",all);
        model.addAttribute("dfh",dfh);
        model.addAttribute("yqx",yqx);
        model.addAttribute("dsh",dsh);
        model.addAttribute("ysh",ysh);

        return "itemOrder/my";
    }

    private static String date;
    private static long orderNum = 0L;

    public static synchronized String getOrderNo() {
        String str = new SimpleDateFormat("yyyMMddHHmm").format(new Date());
        if (date == null || !date.equals(str)) {
            date = str;
            orderNum = 0L;
        }
        orderNum++;
        long orderNO = Long.parseLong(date) * 10000;
        orderNO += orderNum;
        return orderNO + "";
    }


    @RequestMapping("/exAdd")
    @ResponseBody
    public String exAdd(HttpServletRequest request, @RequestBody List<CarDto> list) {
        Object attribute = request.getSession().getAttribute(Consts.USERID);
        JSONObject js = new JSONObject();
        if (attribute == null) {
            js.put(Consts.RES, 0);
            return js.toJSONString();
        }
        Integer userId = Integer.valueOf(attribute.toString());
        User byId = userService.getById(userId);

        if (StringUtils.isEmpty(byId.getAddress())) {
            js.put(Consts.RES, 2);
            return js.toJSONString();
        }

        List<Integer> ids = new ArrayList<>();
        BigDecimal to = new BigDecimal(0);
        for (CarDto c : list) {
            Car load = carService.load(c.getId());
            ids.add(c.getId());
            to = to.add(new BigDecimal(load.getPrice()).multiply(new BigDecimal(c.getNum())));
        }
        ItemOrder order = new ItemOrder();
        order.setStatus(0);
        order.setCode(getOrderNo());
        order.setAddTime(new Date());
        order.setIsDelete(0);
        order.setUserId(userId);
        order.setTotal(to.setScale(2,BigDecimal.ROUND_HALF_UP).toString());
        itemOrderService.insert(order);


        //订单放入 orderDetail  然后从购物车中删除
        if(!CollectionUtils.isEmpty(ids)){

            for(CarDto c:list)
            {
                Car load = carService.load(c.getId());
                OrderDetail de = new OrderDetail();
                de.setItemId(load.getItemId());
                de.setStatus(0);
                de.setNum(c.getNum());
                de.setOrderId(order.getId());
                de.setTotal(String.valueOf(c.getNum()*load.getPrice()));
                orderDetailService.insert(de);
                //修改成交次数
                Item load2 = itemService.load(load.getItemId());
                load2.setGmNum(load2.getGmNum()+c.getNum());
                itemService.updateById(load2);
                //删除购物车
                carService.deleteById(c.getId());
            }


        }

        js.put(Consts.RES, 1);
        return js.toJSONString();
    }

    @RequestMapping("/qx")
    public String qx(Model model,Integer id)
    {
        ItemOrder load = itemOrderService.load(id);
        load.setStatus(1);
        itemOrderService.updateById(load);
        model.addAttribute("obj",load);
        return "redirect:/itemOrder/my";
    }
    @RequestMapping("/fh")
    public String fh(Model model,Integer id)
    {
        ItemOrder load = itemOrderService.load(id);
        load.setStatus(2);
        itemOrderService.updateById(load);
        model.addAttribute("obj",load);
        return "redirect:/itemOrder/findBySql";
    }
    @RequestMapping("/sh")
    public String sh(Model model,Integer id)
    {
        ItemOrder load = itemOrderService.load(id);
        load.setStatus(3);
        itemOrderService.updateById(load);
        model.addAttribute("obj",load);
        return "redirect:/itemOrder/my";
    }

    @RequestMapping("/pj")
    public String pj(Model model,Integer id)
    {
        model.addAttribute("id",id);
        return "itemOrder/pj";
    }




}
