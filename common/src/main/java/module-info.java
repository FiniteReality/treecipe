module treecipe.api {
    exports finitereality.treecipe;
    exports finitereality.treecipe.aspect;
    exports finitereality.treecipe.attribute;
    exports finitereality.treecipe.client.aspect;
    exports finitereality.treecipe.platform;
    exports finitereality.treecipe.recipes;
    exports finitereality.treecipe.registries;

    requires static finitereality.annotations.common;
    requires static org.jetbrains.annotations;

    requires minecraft;

    requires org.joml;

    uses finitereality.treecipe.platform.RegistryProvider;
    uses finitereality.treecipe.platform.client.AspectRendererProvider;
}
