package com.lucq.seckill.controller;

import com.lucq.seckill.domain.SeckillUser;
import com.lucq.seckill.redis.GoodsKey;
import com.lucq.seckill.redis.RedisService;
import com.lucq.seckill.result.Result;
import com.lucq.seckill.service.GoodsService;
import com.lucq.seckill.service.SeckillUserService;
import com.lucq.seckill.vo.GoodsDetailVo;
import com.lucq.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    SeckillUserService seckillUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    SeckillController seckillController;


//    /**
//     * 这个方法的几个参数以及被封装到UserArgumentResolver中,只需要传入user就行了
//     * @param model
//     * @param cookieToken
//     * @param paramToken  有时图方便并不会放入cookie而是直接放入param
//     * @return
//     */
//    @RequestMapping("/to_list")
//    public String list(HttpServletResponse response, Model model,
//                       @CookieValue(value = SeckillUserService.COOKIE_NAME_TOKEN, required = false) String cookieToken,
//                       @RequestParam(value = SeckillUserService.COOKIE_NAME_TOKEN, required = false) String paramToken) {
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
//            return "login";
//        }
//        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
//        SeckillUser seckillUser = seckillUserService.getByToken(response, token);
//        model.addAttribute("user", seckillUser);
//        return "goods_list";
//    }

    /**
     * 进行页面缓存,减少对MySQL的访问
     *
     * @param model
     * @param seckillUser
     * @return
     */

    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response,
                       Model model, SeckillUser seckillUser) {
        model.addAttribute("user", seckillUser);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
//        return "goods_list";

        //手动渲染
        IWebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }


    @RequestMapping(value = "/insert")
    public String insert(@RequestParam("goodsName") String goodsName, @RequestParam("goodsTitle") String goodsTitle, @RequestParam("goodsDetail") String goodsDetail,
                         @RequestParam("goodsPrice") Double goodsPrice, @RequestParam("stock") Integer stock, @RequestParam("seckillPrice") Double seckillPrice,
                         @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("uploadFile") MultipartFile uploadFile, RedirectAttributes redirectAttributes, Model model) throws Exception {

        // 存储图片到本地
        String fileName = this.storePic(uploadFile);
        redirectAttributes.addFlashAttribute("message", "成功上传" + uploadFile.getOriginalFilename() + "!");
        // 将文件传输成功之后的名字传回界面，用于展示图片
        model.addAttribute("picName", uploadFile.getOriginalFilename());
        String goodsImg = "/img/" + fileName;

        Long id = goodsService.insert(goodsName, goodsTitle, goodsImg, goodsDetail, goodsPrice, seckillPrice, stock, this.stringToDate(startDate), this.stringToDate(endDate));

        redisService.set(GoodsKey.getSeckillGoodsStock, "" + id, stock);
        seckillController.getLocalOverMap().put(id, false);

        return "uploadPic";

    }

    private Date stringToDate(String startDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTime = null;
        try {
            dateTime = simpleDateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    private String storePic(MultipartFile file) throws Exception {
        String filename = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
        try {

            byte[] bytes = file.getBytes();
            URL url = this.getClass().getClassLoader().getResource("static/img");
            Path path = Paths.get(new File(url.toURI()).getAbsolutePath(), file.getOriginalFilename());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, bytes, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new Exception("失败！" + filename, e);
        }
        return filename;
    }


    /**
     * @param model
     * @param seckillUser
     * @param goodsId     实际开发中数据库的goodsId很少自增,因为这样容易被人循环遍历
     *                    一般都是用雪花算法
     * @return
     */
/*
    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response,
                         Model model, SeckillUser seckillUser,
                         @PathVariable(value = "goodsId") long goodsId) {

        model.addAttribute("user", seckillUser);

        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        //手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀开启状态
        int seckillStatus = 0;
        //倒计时
        int remainSeconds = 0;
        if (now < startAt) {
            seckillStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";


        IWebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;

    }
*/
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, SeckillUser seckillUser,
                                        @PathVariable(value = "goodsId") long goodsId) {


        //手动渲染
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀开启状态
        int seckillStatus = 0;
        //倒计时
        int remainSeconds = 0;
        if (now < startAt) {
            seckillStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goodsVo);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        goodsDetailVo.setSeckillUser(seckillUser);
        return Result.success(goodsDetailVo);

    }

}
