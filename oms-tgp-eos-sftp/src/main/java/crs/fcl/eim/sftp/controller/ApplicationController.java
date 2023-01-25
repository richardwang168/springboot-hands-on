package crs.fcl.eim.sftp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApplicationController {
    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/download")
    public String download() {
        return "download";
    }

    @GetMapping("/validate")
    public String validate() {
        return "validate";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/user")
    public String userIndex() {
        return "user/index";
    }
    
    @GetMapping("/reset-password-success")
    public String resetPasswordSuccess() {
        return "reset-password-success";
    }
    
    
    @RequestMapping(value="/hello",method = RequestMethod.GET)
    public ModelAndView hello(){
        return new ModelAndView("hello-world");//demo.html
    }  
}
