module treecipe.common {
    exports finitereality.treecipe.runtime;

    requires static finitereality.annotations.common;
    requires static org.jetbrains.annotations;

    requires treecipe.api;

    requires minecraft;

    requires org.jgrapht.core;
    requires org.joml;
    requires org.slf4j;
}