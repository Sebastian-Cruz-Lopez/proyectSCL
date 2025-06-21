package com.ejemplo.SCruzProgramacionNCapasMaven.DemoController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/demo")
public class RequestParamController {  
    
    @GetMapping("request") 
    public String request(Model model, @RequestParam String nombre) {
        
        model.addAttribute("request", nombre);
        
        return "Request";
    }
    //http://localhost:8080/demo/request?nombre=
}
