package org.lolicode.nekomusiccli.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lolicode.nekomusiccli.mixin.WarningScreenAccessor;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class MyWarningScreen extends WarningScreen {
    private final Screen parent;
    private final Text message;
    public final ButtonWidget okButton = ButtonWidget.builder(Text.translatable("nekomusiccli.screen.warning.ok"), button -> this.close()).build();

    public MyWarningScreen(Text text, Screen parent) {
        super(Text.translatable("nekomusiccli.screen.warning.title"), Text.empty(), Text.empty());
        this.parent = parent;
        this.message = text;
    }

    @Override
    protected void init() {
        ((WarningScreenAccessor) this).setMessageText(MultilineText.create(textRenderer, message, width - 50));
        int yOffset = (((WarningScreenAccessor) this).getMessageText().count() + 1) * textRenderer.fontHeight * 2 - 20;
        initButtons(yOffset);
    }

    @Override
    protected void initButtons(int yOffset) {
        okButton.setPosition(width / 2 - 75, yOffset + 100);
        addDrawableChild(okButton);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(Objects.requireNonNullElseGet(parent, TitleScreen::new));
        }
    }
}
