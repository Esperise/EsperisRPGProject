package com.altale.esperis.mixin;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.UUID;
import java.util.function.Supplier;


@Mixin(EntityAttributeModifier.class)
public class VanillaToolDamageMixin {
    @ModifyArgs(method="<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V",
    at=@At(value="INVOKE", target="Lnet/minecraft/entity/attribute/EntityAttributeModifier;"+"<init>(Ljava/util/UUID;Ljava/util/function/Supplier;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V"))
    private static void modifyVanillaToolDamage(Args args) {
        UUID uuid= args.get(0);
        UUID attackDamageID= ItemAttackDamageModifierIdAccessorMixin.getAttackDamageModifierId();
        if(attackDamageID.equals(uuid)){
            Supplier<String> supplier = args.get(1);
            String name = supplier.get();
            double value= args.get(2);
            if(name.equals("Weapon modifier") || name.equals("Tool modifier")) {
                value= value * 0.2 ;
                args.set(2,value);
            }
        }



    }
}
