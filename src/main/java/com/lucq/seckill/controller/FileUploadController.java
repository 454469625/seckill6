package com.lucq.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


@Controller
public class FileUploadController {


    // 这里的是application.properties中配置的地址
//    @Value("${uploadpic.path}")
//    private String uploadPicPath;


    // 主界面
    @GetMapping("/admin")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("messages", "cpxxxxx");
        return "uploadPic";
    }


    // 文件上传按钮action
    @PostMapping("/uploadPic")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Model model) throws Exception {


        // 存储图片到本地
        storePic(file);
        redirectAttributes.addFlashAttribute("message", "成功上传" + file.getOriginalFilename() + "!");
        System.out.println("上传的文件名字：" + file.getOriginalFilename());
        model.addAttribute("picName", file.getOriginalFilename()); // 将文件传输成功之后的名字传回界面，用于展示图片
        return "uploadPic";
    }


    private String storePic(MultipartFile file) throws Exception {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {

            byte[] bytes = file.getBytes();
            URL url = this.getClass().getClassLoader().getResource("static/img");
            Path path = Paths.get(new File(url.toURI()).getAbsolutePath(), file.getOriginalFilename());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, bytes, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            throw new Exception("失败！" + filename, e);
        }
        return filename;
    }
}