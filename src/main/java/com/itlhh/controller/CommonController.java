package com.itlhh.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.itlhh.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author lhh
 * @Date 2022/1/18 20:55
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) {
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        log.info("originalFilename====={}", originalFilename);
        //通过String的subString截取后面的.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        log.info("截取的结果:{}", suffix);

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //log.info("生成新的图片文件:{}",fileName);

        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //1.定义输入流,通过输入流读取文件内容
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            //2.通过reponse对象,获取输出流
            ServletOutputStream outputStream = response.getOutputStream();

            //3.通过response对象设置响应数据格式(img/jpeg)
            response.setContentType("image/jpg");

            //4.通过输入流读取文件数据,然后通过上述的输出流写回浏览器
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //5.关闭资源
            outputStream.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
