package github.shrekshellraiser.cctech.common.sounds;

import github.shrekshellraiser.cctech.CCTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CCTech.MODID);

    public static final RegistryObject<SoundEvent> CASSETTE_DECK_OPEN =
            registerSoundEvent("cassette_deck_open");

    public static final RegistryObject<SoundEvent> CASSETTE_DECK_CLOSE =
            registerSoundEvent("cassette_deck_close");

    public static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(CCTech.MODID, name)));
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
