import finitereality.modlocator.GradleModLocator;
import net.neoforged.neoforgespi.locating.IModLocator;

module finitereality.modlocator {
    requires static fml_spi;
    requires static fml_loader;

    provides IModLocator with GradleModLocator;
}