package com.parsun.parsun_api.infrastructure.seed.valery;

import static com.parsun.parsun_api.infrastructure.seed.SeedIds.id;

import java.util.UUID;

public final class ValeryIds {

    public static final String PRICE_TYPE_MINIMO = "minimo";
    public static final String PRICE_TYPE_MAYOR = "mayor";
    public static final String PRICE_TYPE_MAXIMO = "maximo";
    public static final String PRICE_TYPE_OFERTA = "oferta";

    private ValeryIds() {
    }

    public static UUID priceType(String key) {
        return id("valery.price-type." + key);
    }

    public static UUID category(String code) {
        return id("valery.category." + code);
    }

    public static UUID warehouse(String code) {
        return id("valery.warehouse." + code);
    }

    public static UUID product(String code) {
        return id("valery.product." + code);
    }

    public static UUID productPrice(String code, String priceType) {
        return id("valery.product-price." + code + "." + priceType);
    }

    public static UUID stock(String productCode, String warehouseCode) {
        return id("valery.warehouse-stock." + productCode + "."
                + warehouseCode);
    }

    public static UUID stockMovement(String productCode, String warehouseCode) {
        return id("valery.stock-movement." + productCode + "."
                + warehouseCode);
    }

    public static UUID customer(String code) {
        return id("valery.customer." + code);
    }

    public static UUID customerEmail(String code, int index) {
        return id("valery.customer-email." + code + "." + index);
    }

    public static UUID customerPhone(String code, int index) {
        return id("valery.customer-phone." + code + "." + index);
    }

    public static UUID customerAddress(String code, int index) {
        return id("valery.customer-address." + code + "." + index);
    }

    public static UUID customerBalance(String code) {
        return id("valery.customer-balance." + code);
    }

    public static UUID supplier(String code) {
        return id("valery.supplier." + code);
    }

    public static UUID supplierEmail(String code) {
        return id("valery.supplier-email." + code);
    }

    public static UUID supplierPhone(String code) {
        return id("valery.supplier-phone." + code);
    }

    public static UUID supplierAddress(String code, int index) {
        return id("valery.supplier-address." + code + "." + index);
    }

    public static UUID invoice(String document) {
        return id("valery.invoice." + document);
    }

    public static UUID invoiceLine(String document, int line) {
        return id("valery.invoice-line." + document + "." + line);
    }

    public static UUID order(String document) {
        return id("valery.order." + document);
    }

    public static UUID orderLine(String document, int line) {
        return id("valery.order-line." + document + "." + line);
    }

    public static UUID quote(String document) {
        return id("valery.quote." + document);
    }

    public static UUID quoteLine(String document, int line) {
        return id("valery.quote-line." + document + "." + line);
    }

    public static UUID balanceTransaction(String document) {
        return id("valery.balance-transaction." + document);
    }
}
