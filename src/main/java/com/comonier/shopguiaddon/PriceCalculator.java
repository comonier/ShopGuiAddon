package com.comonier.shopguiaddon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {

    /**
     * Calcula o novo preço usando BigDecimal para evitar erros de precisão do Java.
     * Garante arredondamento de 2 casas decimais e valor mínimo de 0.0.
     */
    public static double calculate(double current, double adjustment, boolean isSubtract) {
        // BigDecimal é essencial para lidar com dinheiro em Java
        BigDecimal currentBD = BigDecimal.valueOf(current);
        BigDecimal adjustmentBD = BigDecimal.valueOf(adjustment);
        
        BigDecimal result;
        if (isSubtract) {
            result = currentBD.subtract(adjustmentBD);
        } else {
            result = currentBD.add(adjustmentBD);
        }

        // HALF_UP arredonda para cima a partir de .005 (padrão bancário/Minecraft)
        double finalValue = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
        
        // Lógica Inversa: O preço de um item nunca pode ser negativo na loja
        if (finalValue < 0.0) {
            return 0.0;
        }
        return finalValue;
    }

    /**
     * Calcula a nova quantidade de itens garantindo integridade no inventário.
     * Limites: Mínimo 1 | Máximo 1000 (Segurança visual).
     */
    public static int calculateQuantity(int current, int adjustment, boolean isSubtract) {
        int result;
        if (isSubtract) {
            result = current - adjustment;
        } else {
            result = current + adjustment;
        }

        // Lógica Inversa: Quantidade mínima permitida é 1
        if (result < 1) {
            return 1;
        }
        
        // Impede que quantidades absurdas quebrem a exibição do item
        if (result > 1000) {
            return 1000;
        }
        
        return result;
    }
}
