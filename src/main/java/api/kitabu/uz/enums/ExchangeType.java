package api.kitabu.uz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ExchangeType {
    FREE("Hadya"), //  Hadya, Даром, Free
    TEMPORARILY("Vaqtinchalik"), // Vaqtinchalik,Временно,Temporarily
    EXCHANGE("Almashish"), // Almashish,Обмен,Exchange
    SELL("Sotish"); // Sotish, Продать, Sell
    private String name;
}
