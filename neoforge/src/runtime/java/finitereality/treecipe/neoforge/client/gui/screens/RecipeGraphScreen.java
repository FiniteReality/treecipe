package finitereality.treecipe.neoforge.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
import finitereality.treecipe.client.aspect.AspectRenderer;
import finitereality.treecipe.client.aspect.AspectRenderers;
import finitereality.treecipe.registries.Registries;
import finitereality.treecipe.runtime.RecipeGraph;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.LayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.drawing.model.Points;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public final class RecipeGraphScreen extends Screen
{
    private static final double ZoomFactor = 1.1;
    private static final double MinZoom = 1;
    private static final double MaxZoom = 10;
    public static final int EdgeSourceColor = 0x4F_FF_00_00;
    public static final int EdgeDestinationColor = 0x4F_00_FF_00;
    public static final int EdgeSourceColorHighlighted = 0xFF_FF_00_00;
    public static final int EdgeDestinationColorHighlighted = 0xFF_00_FF_00;

    private double _xOffset = 0, _yOffset = 0;
    private double _zoom = 1;

    private final MapLayoutModel2D<ResourceKey<?>> _layout;

    @FunctionalInterface
    private interface LayoutSupplier
    {
        void layout(
            final ResourceKey<?> fixedPoint,
            final Supplier<LayoutAlgorithm2D<ResourceKey<?>, ?>> layoutSupplier,
            final MapLayoutModel2D<ResourceKey<?>> layout);
    }

    private RecipeGraphScreen(
        final ResourceKey<?> fixedPoint,
        final LayoutSupplier supplier)
    {
        super(Component.empty());

        _layout = new MapLayoutModel2D<>(Box2D.of(1000, 1000));
        if (fixedPoint != null)
        {
            _layout.put(fixedPoint, Point2D.of(500, 500));
            _layout.setFixed(fixedPoint, true);
        }

        supplier.layout(fixedPoint,
            () -> new FRLayoutAlgorithm2D<>(150, 1f/5f),
            _layout);
    }

    public static RecipeGraphScreen craftedFrom(final ResourceKey<?> resource)
    {
        return new RecipeGraphScreen(resource, RecipeGraph::layoutFrom);
    }

    public static RecipeGraphScreen craftedInto(final ResourceKey<?> resource)
    {
        return new RecipeGraphScreen(resource, RecipeGraph::layoutTo);
    }

    @Override
    public boolean mouseClicked(
        final double x, final double y,
        final int button)
    {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) return false;

        setDragging(true);
        return true;
    }

    @Override
    public boolean mouseDragged(
        final double x,
        final double y,
        final int button_,
        final double xDelta,
        final double yDelta)
    {
        if (!isDragging()) return false;
        if (button_ != InputConstants.MOUSE_BUTTON_LEFT) return false;

        _xOffset += xDelta / _zoom;
        _yOffset += yDelta / _zoom;

        return true;
    }

    @Override
    public boolean mouseReleased(
        final double x, final double y,
        final int button)
    {
        setDragging(false);
        return true;
    }

    @Override
    public boolean mouseScrolled(
        double x,
        double y,
        final double xScroll,
        final double yScroll)
    {
        x -= (float)width / 2;
        y -= (float)height / 2;
        if (yScroll < 0)
        {
            _xOffset -= x / _zoom;
            _yOffset -= y / _zoom;
            _zoom = Math.max(MinZoom, _zoom / ZoomFactor);
            _xOffset += x / _zoom;
            _yOffset += y / _zoom;
        }
        else if (yScroll > 0)
        {
            _xOffset -= x / _zoom;
            _yOffset -= y / _zoom;
            _zoom = Math.min(MaxZoom, _zoom * ZoomFactor);
            _xOffset += x / _zoom;
            _yOffset += y / _zoom;
        }
        return true;
    }

    private Point2D getRealCoordinates(final Point2D original)
    {
        final var worldCoords = Point2D.of(
            original.getX() - _layout.getDrawableArea().getWidth() / 2,
            _layout.getDrawableArea().getHeight()
                - original.getY()
                - _layout.getDrawableArea().getHeight() / 2);

        return Points.add(
            Points.scalarMultiply(
                Points.add(worldCoords, Point2D.of(_xOffset, _yOffset)),
                _zoom),
            Point2D.of((double)width / 2, (double)height / 2));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void render(
        final GuiGraphics graphics,
        final int mouseX, final int mouseY,
        final float partialTick)
    {
        //renderBackground(graphics, mouseX, mouseY, partialTick);
        renderEdges(graphics, mouseX, mouseY);

        final var keyComparator
            = Map.Entry.<ResourceKey<?>, Point2D>comparingByKey(
                Comparator.comparing(this::getRenderer, AspectRenderers.renderOrder()));
        final var valueComparator
            = Map.Entry.<ResourceKey<?>, Point2D>comparingByValue(
                Comparator.comparingDouble(Point2D::getY).reversed());

        StreamSupport.stream(_layout.spliterator(), false)
            .sorted(keyComparator.thenComparing(valueComparator))
            .forEachOrdered(entry -> {
                final var renderer = getRenderer(entry.getKey());
                final var position = getRealCoordinates(entry.getValue());

                if (position.getX() + 16 < 0
                    || position.getX() - 16 > width)
                    return;
                if (position.getY() + 16 < 0
                    || position.getY() - 16 > height)
                    return;

                graphics.pose().pushPose();

                graphics.pose().translate(
                    position.getX(),
                    position.getY(),
                    150 + 100 * position.getY() / _layout.getDrawableArea().getHeight());

                renderer.render(
                    (ResourceKey)entry.getKey(),
                    graphics.bufferSource(),
                    graphics.pose(),
                    partialTick);

                graphics.pose().popPose();
            });

        // We don't *actually* care for the result here, just that we only
        // render one tooltip at a time.
        final var hasTooltip
            = StreamSupport.stream(_layout.spliterator(), false)
                .anyMatch(pair -> {
                    final var resource = pair.getKey();
                    final var position = getRealCoordinates(pair.getValue());
                    if (mouseX < position.getX() - 8
                        || mouseX > position.getX() + 8)
                        return false;
                    if (mouseY < position.getY() - 8
                        || mouseY > position.getY() + 8)
                        return false;

                    renderTooltip(resource, graphics, mouseX, mouseY);
                    return true;
                });
    }

    // TODO: check if caching this mesh is necessary
    private void renderEdges(
        final GuiGraphics graphics,
        final float mouseX, final float mouseY)
    {
        final var bufferSource = graphics.bufferSource();
        final var buffer = bufferSource.getBuffer(RenderType.gui());
        _layout.forEach(pair -> {
            final var input = pair.getKey();
            final var inputPosition = getRealCoordinates(pair.getValue());

            final var highlightRecipes
                = (mouseX >= inputPosition.getX() - 8)
                && (mouseX <= inputPosition.getX() + 8)
                && (mouseY >= inputPosition.getY() - 8)
                && (mouseY <= inputPosition.getY() + 8);

            RecipeGraph.craftableWithResource(input)
                .forEach(output -> {
                    if (_layout.get(output) == null) return;

                    var bottomOffset = RecipeGraph.canCraftInto(output, input)
                        ? -1f
                        : 1f;
                    var topOffset = RecipeGraph.canCraftInto(output, input)
                        ? 3f
                        : 1f;

                    final var outputPosition = getRealCoordinates(_layout.get(output));
                    final var angle = (float)Mth.atan2(
                        outputPosition.getY() - inputPosition.getY(),
                        outputPosition.getX() - inputPosition.getX());
                    final var invAngle = Mth.PI + angle;
                    buffer.vertex(
                        inputPosition.getX() + Mth.cos(angle + Mth.PI * (3f/4f)) * bottomOffset,
                        inputPosition.getY() + Mth.sin(angle + Mth.PI * (3f/4f)) * bottomOffset,
                        0)
                        .color(highlightRecipes
                            ? EdgeSourceColorHighlighted
                            : EdgeSourceColor)
                        .endVertex();
                    buffer.vertex(
                        outputPosition.getX() + Mth.cos(invAngle - Mth.PI * (3f/4f)) * bottomOffset,
                        outputPosition.getY() + Mth.sin(invAngle - Mth.PI * (3f/4f)) * bottomOffset,
                        0)
                        .color(highlightRecipes
                            ? EdgeDestinationColorHighlighted
                            : EdgeDestinationColor)
                        .endVertex();
                    buffer.vertex(
                        outputPosition.getX() + Mth.cos(invAngle + Mth.PI * (3f/4f)) * topOffset,
                        outputPosition.getY() + Mth.sin(invAngle + Mth.PI * (3f/4f)) * topOffset,
                        0)
                        .color(highlightRecipes
                            ? EdgeDestinationColorHighlighted
                            : EdgeDestinationColor)
                        .endVertex();
                    buffer.vertex(
                        inputPosition.getX() + Mth.cos(angle - Mth.PI * (3f/4f)) * topOffset,
                        inputPosition.getY() + Mth.sin(angle - Mth.PI * (3f/4f)) * topOffset,
                        0)
                        .color(highlightRecipes
                            ? EdgeSourceColorHighlighted
                            : EdgeSourceColor)
                        .endVertex();
                });
        });
        bufferSource.endBatch(RenderType.gui());
    }

    private <T> void renderTooltip(
        final ResourceKey<?> resource,
        final GuiGraphics graphics,
        final int mouseX, final int mouseY)
    {
        final var tooltip = new ArrayList<Component>();
        tooltip.add(getComponent(resource));

        tooltip.add(Component.literal("Crafted with:")
            .withStyle(it -> it.withUnderlined(true)));
        RecipeGraph.craftsIntoResource(resource)
            .distinct()
            .map(subResource -> getComponent(subResource)
                .withStyle(it -> it.withColor(ChatFormatting.GRAY)))
            .forEach(tooltip::add);

        tooltip.add(Component.literal("Used in:")
            .withStyle(it -> it.withUnderlined(true)));
        RecipeGraph.craftableWithResource(resource)
            .distinct()
            .map(subResource -> getComponent(subResource)
                .withStyle(it -> it.withColor(ChatFormatting.GRAY)))
            .forEach(tooltip::add);

        graphics.renderTooltip(font,
            tooltip.stream()
                .map(Component::getVisualOrderText)
                .toList(),
            mouseX, mouseY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> MutableComponent getComponent(final ResourceKey<T> resource)
    {
        final var aspect = Objects.requireNonNull(getMinecraft().level)
            .registryAccess()
            .registryOrThrow(Registries.ASPECT)
            .stream()
            .filter(it -> resource.isFor(it.registryKey()))
            .findFirst()
            .orElseThrow();

        return aspect.getResourceDescription(
            Objects.requireNonNull(getMinecraft().level)
                .registryAccess(),
                (ResourceKey)resource)
            .copy()
            .append(CommonComponents.SPACE)
            .append(aspect.getDescription()
                .copy()
                .withStyle(it -> it
                    .withColor(ChatFormatting.DARK_GRAY)
                    .withItalic(true)));
    }

    private AspectRenderer<?> getRenderer(final ResourceKey<?> resource)
    {
        final var aspect = Objects.requireNonNull(getMinecraft().level)
            .registryAccess()
            .registryOrThrow(Registries.ASPECT)
            .stream()
            .filter(it -> resource.isFor(it.registryKey()))
            .findFirst()
            .orElseThrow();

        return AspectRenderers.getRenderer(aspect);
    }
}
