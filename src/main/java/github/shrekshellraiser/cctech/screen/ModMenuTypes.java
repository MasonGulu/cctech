package github.shrekshellraiser.cctech.screen;

import github.shrekshellraiser.cctech.CCTech;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CCTech.MODID);

    public static final RegistryObject<MenuType<CassetteDeckMenu>> CASSETTE_DECK = registerMenuType(CassetteDeckMenu::new,
            "cassette_deck_menu");

    public static final RegistryObject<MenuType<ReelToReelMenu>> REEL_TO_REEL = registerMenuType(ReelToReelMenu::new,
            "reel_to_reel_menu");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                  String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
