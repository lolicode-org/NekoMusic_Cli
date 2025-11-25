package org.lolicode.nekomusiccli.config;

import dev.stashy.soundcategories.CategoryLoader;
import net.minecraft.sounds.SoundSource;

public class CustomSoundCategory implements CategoryLoader {
    @Register
    public static SoundSource NEKOMUSIC;
}
