package com.prometheus.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wenlinzou
 * @date 2020/5/21 11:34:00
 */

@RestController
@RequestMapping("/file")
public class DownloadController {

    @GetMapping("/download")
    public void downloadTxt(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment;filename=test.txt");

        ServletOutputStream out = response.getOutputStream();
        out.write("hello world".getBytes());
    }
}
