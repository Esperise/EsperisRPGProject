package com.altale.esperis.shop;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class ShopItemManager {
    public static final String SHOP_INFO = "ShopInfo";
    public static final String PURCHASE_PRICE = "Purchase:Price";
    public static final String SALES_PRICE = "Sale:Price";
        public static final String TRADE = "Trade_Amount";
            public static final String TRADE_BEFORE_PURCHASE_PRICE = "Trade:Before_PURCHASE_PRICE";
            public static final String TRADE_PURCHASE_AMOUNT = "Trade:PURCHASE_AMOUNT";
            public static final String TRADE_BEFORE_SALES_PRICE = "Trade:Before_SALES_PRICE";
            public static final String TRADE_SALES_AMOUNT = "Trade:SALES_AMOUNT";

    public static ItemStack makeShopItem(PlayerEntity player, int purchasePrice, int salesPrice) {
        ItemStack itemStack = player.getMainHandStack();
            NbtCompound root = itemStack.getOrCreateNbt();
            NbtCompound shopInfo = getNbtCompound(root, SHOP_INFO);
                setPrice(shopInfo, PURCHASE_PRICE, purchasePrice);
                setPrice(shopInfo, SALES_PRICE, salesPrice);
                NbtCompound trade = getNbtCompound(shopInfo, TRADE);
                    if(trade.contains(TRADE_BEFORE_PURCHASE_PRICE)) {
                        trade.putInt(TRADE_BEFORE_PURCHASE_PRICE, purchasePrice);
                    }
                    if(trade.contains(TRADE_PURCHASE_AMOUNT)) {
                        trade.putInt(TRADE_PURCHASE_AMOUNT, 0);
                    }
                    if(trade.contains(TRADE_BEFORE_SALES_PRICE)) {
                        trade.putInt(TRADE_BEFORE_SALES_PRICE, salesPrice);
                    }
                    if(trade.contains(TRADE_SALES_AMOUNT)) {
                        trade.putInt(TRADE_SALES_AMOUNT, 0);
                    }
                    return itemStack;
    }

//    public static ItemStack priceFluctuation(ItemStack itemStack){
//        int purchasePrice = getPurchasePrice(itemStack);
//        int salesPrice = getSalesPrice(itemStack);
//        int beforePurchasePrice = getBeforePurchasePrice(itemStack);
//        int beforeSalesPrice = getBeforeSalesPrice(itemStack);
//        int purchaseAmount = getPurchaseAmount(itemStack);
//        int salesAmount = getSalesAmount(itemStack);
//
//
//        return itemStack;
//    }
    public static ItemStack salesPriceFluctuation(ItemStack itemStack){
        int salesPrice = getSalesPrice(itemStack);
        int beforeSalesPrice = getBeforeSalesPrice(itemStack);
        int salesAmount = getSalesAmount(itemStack);
        int itemTradeMinAmount = itemStack.getCount();
        int tradedItemSetAmount = salesAmount/(itemTradeMinAmount * itemStack.getMaxCount());
        double fluctuationCoefficient = 1;
        if(tradedItemSetAmount >=  beforeSalesPrice * 5 ) return itemStack;
        if(tradedItemSetAmount < 16){
            fluctuationCoefficient =  ((1/4.0) - (Math.log10(Math.abs(salesPrice - 25.0)/Math.log10(2) )/8.0 ));
            fluctuationCoefficient = Math.min(fluctuationCoefficient, 0.25);
        }else {
            fluctuationCoefficient =  (-1)* ((1/4.0) - (Math.log10(Math.abs(salesPrice - 25.0)/Math.log10(2) )/8.0 ));
            fluctuationCoefficient = Math.min(fluctuationCoefficient, 0.25);

        }
        setSalesPrice(itemStack, (int) (salesPrice * fluctuationCoefficient));


        return itemStack;
    }



    public static boolean hasShopInfo(ItemStack itemStack) {
        NbtCompound root = itemStack.getOrCreateNbt();
        return root.contains(SHOP_INFO);

    }
    public static NbtCompound getNbtCompound(NbtCompound nbt, String key){
        if(!nbt.contains(key, NbtElement.COMPOUND_TYPE)){
            NbtCompound temp = new NbtCompound();
            nbt.put(key, temp);
            return temp;
        }
        return nbt.getCompound(key);
    }
    private static void setPrice(NbtCompound nbt, String key, int price){
        nbt.putInt(key, price);
    }
    public static void setPurchasePrice(NbtCompound nbt, int price){
        setPrice(nbt, PURCHASE_PRICE, price);
    }
    public static void setSalesPrice(NbtCompound nbt, int price){
        setPrice(nbt, SALES_PRICE, price);
    }
    public static void setPurchasePrice(ItemStack stack , int price){
        if(hasShopInfo(stack)){
            NbtCompound shopInfo = stack.getOrCreateNbt().getCompound(SHOP_INFO);
            shopInfo.putInt(PURCHASE_PRICE, price);
        }

    }
    public static void setSalesPrice(ItemStack stack , int price){
        if(hasShopInfo(stack)){
            NbtCompound shopInfo = stack.getOrCreateNbt().getCompound(SHOP_INFO);
            System.out.println(price);
            shopInfo.putInt(SALES_PRICE, price);
        }

    }
    public static int getPurchasePrice(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        NbtCompound shopInfo = root.getCompound(SHOP_INFO);
        if(shopInfo.contains(PURCHASE_PRICE, NbtElement.INT_TYPE)){
            return shopInfo.getInt(PURCHASE_PRICE);
        }
        return -1;
    }
    public static int getSalesPrice(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        NbtCompound shopInfo = root.getCompound(SHOP_INFO);
        if(shopInfo.contains(SALES_PRICE, NbtElement.INT_TYPE)){
            return shopInfo.getInt(SALES_PRICE);
        }
        return -1;
    }
    public static int getPurchaseAmount(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        NbtCompound shopInfo = root.getCompound(SHOP_INFO);
        NbtCompound trade = shopInfo.getCompound(TRADE);
        if(trade.contains(TRADE_PURCHASE_AMOUNT, NbtElement.INT_TYPE)){
            return trade.getInt(TRADE_BEFORE_PURCHASE_PRICE);
        }
        return -1;

    }
    public static int getSalesAmount(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        NbtCompound shopInfo = root.getCompound(SHOP_INFO);
        NbtCompound trade = shopInfo.getCompound(TRADE);
        if(trade.contains(TRADE_SALES_AMOUNT, NbtElement.INT_TYPE)){
            return trade.getInt(TRADE_BEFORE_SALES_PRICE);
        }
        return -1;
    }
    public static int getBeforePurchasePrice(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        NbtCompound shopInfo = root.getCompound(SHOP_INFO);
        NbtCompound trade = shopInfo.getCompound(TRADE);
        if(trade.contains(TRADE_BEFORE_PURCHASE_PRICE, NbtElement.INT_TYPE)){
            return trade.getInt(TRADE_BEFORE_PURCHASE_PRICE);
        }
        return -1;

    }
    public static int getBeforeSalesPrice(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        NbtCompound shopInfo = root.getCompound(SHOP_INFO);
        NbtCompound trade = shopInfo.getCompound(TRADE);
        if(trade.contains(TRADE_BEFORE_SALES_PRICE, NbtElement.INT_TYPE)){
            return trade.getInt(TRADE_BEFORE_SALES_PRICE);
        }
        return -1;
    }
    public static void addPurchaseCount(ItemStack itemStack, int count){
        if(getPurchaseAmount(itemStack) >= 0){
            NbtCompound trade = getNbtCompound(itemStack.getOrCreateNbt(), TRADE_BEFORE_PURCHASE_PRICE);
            trade.putInt(TRADE_BEFORE_PURCHASE_PRICE, getPurchasePrice(itemStack)+ count);
        }
    }
    public static void addSalesCount(ItemStack itemStack, int count){
        if(getSalesAmount(itemStack) >= 0){
            NbtCompound trade = getNbtCompound(itemStack.getOrCreateNbt(), TRADE_BEFORE_SALES_PRICE);
            trade.putInt(TRADE_BEFORE_PURCHASE_PRICE, getSalesPrice(itemStack)+ count);
        }
    }
    public static void setBeforePurchasePrice(ItemStack itemStack, int price){
        NbtCompound root = itemStack.getOrCreateNbt();
        if(root.contains(SHOP_INFO)){
            NbtCompound shopInfo = root.getCompound(SHOP_INFO);
            NbtCompound trade = shopInfo.getCompound(TRADE);
            trade.putInt(TRADE_BEFORE_PURCHASE_PRICE, price);
        }

    }
    public static void setBeforeSalesPrice(ItemStack itemStack, int price){
        NbtCompound root = itemStack.getOrCreateNbt();
        if(root.contains(SHOP_INFO)){
            NbtCompound shopInfo = root.getCompound(SHOP_INFO);
            if(shopInfo.contains(TRADE)){
                NbtCompound trade = shopInfo.getCompound(TRADE);
                trade.putInt(TRADE_BEFORE_SALES_PRICE, price);
            }
        }
    }
    public static void resetTradeAmount(ItemStack itemStack){
        NbtCompound root = itemStack.getOrCreateNbt();
        if(root.contains(SHOP_INFO)){
            NbtCompound shopInfo = root.getCompound(SHOP_INFO);
            if(shopInfo.contains(TRADE)){
                NbtCompound trade = shopInfo.getCompound(TRADE);
                trade.putInt(TRADE_PURCHASE_AMOUNT, 0);
                trade.putInt(TRADE_SALES_AMOUNT, 0);
            }
        }
    }



}
