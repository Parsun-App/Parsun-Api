package com.cashly.cashly_api.infrastructure.seed;

import static com.cashly.cashly_api.infrastructure.seed.SeedIds.CATEGORY_DRINKS;
import static com.cashly.cashly_api.infrastructure.seed.SeedIds.CATEGORY_FOOD;

import java.util.List;
import java.util.UUID;

public final class SeedCatalogProducts {

    public record Spec(String key, String code, String name, UUID categoryId,
            String purchasePrice, String salePrice, String margin) {
        public UUID productId() {
            return SeedIds.id("product." + key);
        }
    }

    public static final List<Spec> PRODUCTS = List.of(
            new Spec("rice", "PRD-0004", "White rice", CATEGORY_FOOD, "1.20",
                    "1.80", "50.00"),
            new Spec("pasta", "PRD-0005", "Pasta", CATEGORY_FOOD, "1.10",
                    "1.65", "50.00"),
            new Spec("sugar", "PRD-0006", "Sugar", CATEGORY_FOOD, "0.90",
                    "1.35", "50.00"),
            new Spec("salt", "PRD-0007", "Salt", CATEGORY_FOOD, "0.40",
                    "0.60", "50.00"),
            new Spec("milk", "PRD-0008", "Milk", CATEGORY_DRINKS, "1.00",
                    "1.50", "50.00"),
            new Spec("bread", "PRD-0009", "Bread", CATEGORY_FOOD, "1.30",
                    "1.95", "50.00"),
            new Spec("cheese", "PRD-0010", "Cheese", CATEGORY_FOOD, "2.50",
                    "3.75", "50.00"),
            new Spec("ham", "PRD-0011", "Ham", CATEGORY_FOOD, "2.80",
                    "4.20", "50.00"),
            new Spec("eggs", "PRD-0012", "Eggs", CATEGORY_FOOD, "1.60",
                    "2.40", "50.00"),
            new Spec("oil", "PRD-0013", "Cooking oil", CATEGORY_FOOD, "2.20",
                    "3.30", "50.00"),
            new Spec("flour", "PRD-0014", "Wheat flour", CATEGORY_FOOD, "1.10",
                    "1.65", "50.00"),
            new Spec("beans", "PRD-0015", "Beans", CATEGORY_FOOD, "1.40",
                    "2.10", "50.00"),
            new Spec("corn-flour", "PRD-0016", "Corn flour", CATEGORY_FOOD,
                    "1.00", "1.50", "50.00"),
            new Spec("tuna", "PRD-0017", "Tuna", CATEGORY_FOOD, "1.90",
                    "2.85", "50.00"),
            new Spec("sardines", "PRD-0018", "Sardines", CATEGORY_FOOD, "1.20",
                    "1.80", "50.00"),
            new Spec("juice", "PRD-0019", "Juice", CATEGORY_DRINKS, "1.30",
                    "1.95", "50.00"),
            new Spec("soda", "PRD-0020", "Soda", CATEGORY_DRINKS, "0.80",
                    "1.20", "50.00"),
            new Spec("tea", "PRD-0021", "Tea", CATEGORY_DRINKS, "0.70",
                    "1.05", "50.00"));

    private SeedCatalogProducts() {
    }
}
