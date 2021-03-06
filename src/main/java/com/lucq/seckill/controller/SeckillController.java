package com.lucq.seckill.controller;

import com.lucq.seckill.access.AccessLimit;
import com.lucq.seckill.domain.OrderInfo;
import com.lucq.seckill.domain.SeckillOrder;
import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.domain.User;
import com.lucq.seckill.rabbitmq.MQSender;
import com.lucq.seckill.rabbitmq.SeckillMessage;
import com.lucq.seckill.redis.AccessKey;
import com.lucq.seckill.redis.GoodsKey;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.redis.SeckillKey;
import com.lucq.seckill.result.CodeMsg;
import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.GoodsService;
import com.lucq.seckill.service.OrderService;
import com.lucq.seckill.service.SeckillService;
import com.lucq.seckill.service.SeckillUserService;
import com.lucq.seckill.util.MD5Util;
import com.lucq.seckill.util.UUIDUitl;
import com.lucq.seckill.vo.GoodsVo;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender mqSender;



    //??????redis????????????,??????????????????????????????,??????????????????map???????????????
    //key: ??????ID; value: ????????????????????????;
    public Map<Long, Boolean> localOverMap = new HashMap<>();
    public Map<Long, Boolean> getLocalOverMap() {
        return localOverMap;
    }


    /**
     * ???????????????,??????????????????????????????,?????????????????????
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //???????????????????????????????????????????????????
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }

    }

    /**
     * ?????????????????????????????????put
     *
     * @param model
     * @param seckillUser
     * @param goodsId
     * @return
     */

//    @RequestMapping("/do_seckill")
//    public String list(Model model, SeckillUser seckillUser,
//                       @RequestParam("goodsId") long goodsId) {
//        if (seckillUser == null) {
//            return "login";
//        }
//        model.addAttribute("user", seckillUser);
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getStockCount();
//        if (stock <= 0) {
//            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
//            return "seckill_fail";
//        }
//        //????????????????????????????????????,??????????????????
//        SeckillOrder order = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goods.getId());
//        if (order != null) {
//            model.addAttribute("errmsg", CodeMsg.REPEAT_SECKILL.getMsg());
//            return "seckill_fail";
//        }
//        //????????? ????????? ??????????????????
//        OrderInfo orderInfo = seckillService.seckill(seckillUser, goods);
//        model.addAttribute("orderInfo", orderInfo);
//        model.addAttribute("goods", goods);
//        return "order_detail";
//    }
    @RequestMapping(value = "/{path}/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckill(Model model, SeckillUser seckillUser,
                                   @RequestParam("goodsId") long goodsId,
                                   @PathVariable("path") String path) {
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user", seckillUser);
        //??????path
        boolean check = seckillService.checkPath(seckillUser, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //???????????????10,??????11,12,13...????????????????????????????????????redis???,???????????????
        //??????????????????map(????????????)?????????,??????????????????map(??????redis??????)?????????
        boolean isOver = localOverMap.get(goodsId);
        if (isOver) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //????????????
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);
        //????????????0,????????????map
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //????????????????????????????????????,??????????????????
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goodsId);
        if (order != null) {
            //todo ??????????????????redis????????????????
            return Result.error(CodeMsg.REPEAT_SECKILL);
        }

        //????????????????????????????????????,?????????????????????
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setSeckillUser(seckillUser);
        seckillMessage.setGoodsId(goodsId);
        //receiver?????????????????????????????????
        mqSender.sendSeckillMessage(seckillMessage);
        return Result.success(0);//?????????
        /*
        model.addAttribute("user", seckillUser);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if (stock <= 0) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //????????????????????????????????????,??????????????????
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(seckillUser.getId(), goodsVo.getId());
        if (order != null) {
            return Result.error(CodeMsg.REPEAT_SECKILL);
        }
        //????????? ????????? ??????????????????
        OrderInfo orderInfo = seckillService.seckill(seckillUser, goodsVo);
        return Result.success(orderInfo);
        */
    }

    /**
     * @param model
     * @param seckillUser
     * @param goodsId
     * @return orderId: ????????????; -1: ????????????(????????????); 0: ?????????
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, SeckillUser seckillUser,
                                      @RequestParam("goodsId") long goodsId) {
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = seckillService.getSeckillResult(seckillUser.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 30, maxCount = 5)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request, SeckillUser seckillUser,
                                         @RequestParam("goodsId") long goodsId,
//                                         @RequestParam("verifyCode") int verifyCode
                                         @RequestParam("verifyCode") int verifyCode) {
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
//        //?????????????????????,??????????????????
//        String uri = request.getRequestURI();
//        String key = uri + "_" + seckillUser.getId();
//        Integer count = redisService.get(AccessKey.access, key, Integer.class);
//        if (count == null) {
//            redisService.set(AccessKey.access, key, 1);
//        } else if (count < 5) {
//            redisService.incr(AccessKey.access, key);
//        } else {
//            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
//        }
        boolean check = seckillService.checkVerifyCode(seckillUser, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        String path = seckillService.createSeckillPath(seckillUser, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, SeckillUser seckillUser,
                                               @RequestParam("goodsId") long goodsId) {
        if (seckillUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = seckillService.createVerifyCode(seckillUser, goodsId);
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        } catch (Exception e) {
            System.out.println("getSeckillVerifyCode error");
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }

}
