package yzt.com.tacos;

import lombok.Data;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Taco_Order")
/*
* @Table注解:
* 表明Order实体应该持久化到数据库中名为Taco_Order的表中
* */
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // @Id注解: 将其指定为数据库中唯一标识该实体的属性
    @GeneratedValue (strategy = GenerationType.AUTO) // 依赖数据库自动生成的ID值
    private Long id;

    private Date placedAt;

    @ManyToOne // 多对一, 一个用户可以拥有多个订单
    private User user;

    @NotBlank(message = "Name is required")
    // 不使用@NotNull, 因为" "即不为空, @NotBlank表示不能为 空白字段
    private String deliveryName;

    @NotBlank( message = "Street is required")
    private String deliveryStreet;

    @NotBlank(message = "City is required")
    private String deliveryCity;

    @NotBlank(message = "State is required")
    private String deliveryState;

    @NotBlank(message = "Zip is required")
    private String deliveryZip;

    @CreditCardNumber(message = "Not a valid credit card number")
    private String ccNumber;

    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
             message = "Must be formatted MM/YY")
    // "Pattern - 样式", 使用正则表达式, 确保其符合预期的格式
    private String ccExpiration;

    @Digits(integer = 3, fraction = 0, message = "Invalid CVV")
    // "Digit - 数字/位", 确保包含3个整数, 并且没有分数
    private String ccCVV;

    @ManyToMany(targetEntity = Taco.class)
    // 声明Order与其关联的Taco之间的关系
    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco design) {
        this.tacos.add(design);
    }

    @PrePersist
    // 在持久化之前, 使用placedAt()设置当前日期和时间
    void placedAt() {
        this.placedAt = new Date();
    }
}
