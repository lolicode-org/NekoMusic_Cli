package org.lolicode.nekomusiccli.config;

import dev.stashy.soundcategories.CategoryLoader;
import net.minecraft.sound.SoundCategory;

public class CustomSoundCategory implements CategoryLoader {
    @Register
    public static SoundCategory NEKOMUSIC;
}
