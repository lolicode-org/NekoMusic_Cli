package org.lolicode.nekomusiccli.mixin;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.WarningScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// https://github.com/Luligabi1/NoIndium/blob/1.20/src/main/java/me/luligabi/noindium/mixin/WarningScreenAccessor.java

@Mixin(WarningScreen.class)
public interface WarningScreenAccessor {

    @Accessor
    MultilineText getMessageText();

    @Accessor("messageText")
    void setMessageText(MultilineText messageText);
}
