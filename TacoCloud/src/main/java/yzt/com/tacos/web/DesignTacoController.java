package yzt.com.tacos.web;

import lombok.extern.slf4j.Slf4j;
import yzt.com.tacos.Order;
import yzt.com.tacos.Taco;
import yzt.com.tacos.Ingredient;
import yzt.com.tacos.Ingredient.Type;
import yzt.com.tacos.User;
import yzt.com.tacos.data.IngredientRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import yzt.com.tacos.data.TacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;
import yzt.com.tacos.data.UserRepository;

import javax.validation.Valid;

@Slf4j
//Lombok提供的注解,在这个类中自动生成一个SLF4J Logger (SLF4J --Simple Logging Facade for Java Logger)
@Controller
// 将这个类识别为控制器
@RequestMapping("/design")
// 指定处理器所处理的请求类型
@SessionAttributes("order")
// 指定模型对象(如订单属性)要保存在session中, 这样才能跨请求使用
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private TacoRepository tacoRepo;
    private UserRepository userRepo;

    @Autowired //构造器 将得到的对象赋值给实例变量
    public DesignTacoController(IngredientRepository ingredientRepo,
                                TacoRepository tacoRepo,
                                UserRepository userRepo){
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
        this.userRepo = userRepo;
    }

    @ModelAttribute(name = "order") // 确保会在模型中创建一个Order对象
    public Order order(){
        return new Order();
    }

    @ModelAttribute(name = "design") // 确保会在模型中创建一个Taco对象
    public Taco design(){
        return new Taco();
    }

    @GetMapping
    // @GetMapping注解声明showDesignFrom()要处理针对"/design"的HTTP GET请求
    // 细化@RequestMapping,指明当接收到"/design"的HTTP GET请求时,调用showDesignForm()来处理请求
    // @GetMapping自Spring 4.3引入,4.3之前使用@RequestMapping(method = RequestMethod.GET)代替
    public String showDesignForm(Model model, Principal principal) {
        log.info("   --- Designing Taco");
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(i -> ingredients.add(i));
        //调用注入的IngredientRepository是findAll(), 从数据库获取所有的配料

        //将获取到的配料过滤成不同类型然后放到模型中
        Type[] types = Ingredient.Type.values();
        for(Type type : types){
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }

        String username = principal.getName();
        User user = userRepo.findByUsername(username);
        model.addAttribute("user", user);

        return "design";
    }

    @PostMapping
    //注解声明processDesign()要处理HTTP POST请求
    public String processDesign(@Valid Taco taco,
                                Errors errors,
                                @ModelAttribute Order order){
        /* @Valid 注解: Spring MVC要对提交的Taco对象进行检查,校验在绑定表单数据之后, 调用processDesign()之前
           如果存在校验错误,错误信息将会捕获到一个Errors对象,并作为参数传递给processDesign() */
        /* @ModelAttribute 注解: 表明Order对象的值应该是来自模型的, SpringMVC不会将请求参数绑定到它上面 */

        log.info("   --- Saving taco");

        if (errors.hasErrors()){ // 如果Errors对象包含错误信息, return "design" -- 即重新呈现design视图
           return "design";
        }/* 检查完校验错误之后,processDesign()使用注入的TacoRepository来保存Taco,
           Taco对象保存在Session里面的Order中,
           在用户提交表单之前, Order对象会一直保存在Session中, 并没有保存到数据库中,
           到时, orderController调用OrderController的实现来保存订单
         */

        Taco saved = tacoRepo.save(taco);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(
            List<Ingredient> ingredients, Ingredient.Type type){
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

}
