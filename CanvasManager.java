import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import java.util.Stack;

public class CanvasManager {

    public static Pane  selected  = null;
    public static Stack<Pane> undoStack = new Stack<>();
    public static Stack<Pane> redoStack = new Stack<>();

    private static final String CATEGORY_KEY = "featureCategory";
    private static final String LOCKED_KEY   = "featureLocked";

    // ── Single resize-handle style — clean, no orange ──
    private static final String HANDLE_STYLE =
        "-fx-background-color: #8a9bb0;" +
        "-fx-border-color: #222830; -fx-border-width: 2;" +
        "-fx-background-radius: 7; -fx-border-radius: 7;";

    // -------------------------------------------------------
    // Place or replace feature by category
    // -------------------------------------------------------
    public static void addImageFeature(Pane layer, Image image, String category) {
        Pane existing = findByCategory(layer, category);

        if (existing != null) {
            if (isLocked(existing)) return;   // locked — ignore click

            ImageView old = getImageView(existing);
            double w  = (old != null) ? old.getFitWidth() : 130;
            double x  = existing.getLayoutX();
            double y  = existing.getLayoutY();
            int    zi = layer.getChildren().indexOf(existing);

            Pane replacement = buildWrapper(layer, image, x, y, w, category);
            layer.getChildren().remove(existing);
            undoStack.remove(existing);

            layer.getChildren().add(Math.min(zi, layer.getChildren().size()), replacement);
            undoStack.push(replacement);
            redoStack.clear();

            deselectAll(layer);
            showHandle(replacement, true);
            selected = replacement;

        } else {
            Pane wrapper = buildWrapper(layer, image, 160, 140, 130, category);
            layer.getChildren().add(wrapper);
            undoStack.push(wrapper);
            redoStack.clear();

            deselectAll(layer);
            showHandle(wrapper, true);
            selected = wrapper;
            wrapper.toFront();
        }
    }

    // -------------------------------------------------------
    // Lock toggle — clicking the lock button calls this
    // -------------------------------------------------------
    public static void toggleLock(Pane layer) {
        if (selected == null) return;
        boolean current = isLocked(selected);
        selected.getProperties().put(LOCKED_KEY, !current);
        // No visual change on the handle — state communicated via button text only
    }

    public static boolean isSelectedLocked() {
        return selected != null && isLocked(selected);
    }

    // -------------------------------------------------------
    // Z-order — all respect lock
    // -------------------------------------------------------
    public static void bringToFront(Pane layer) {
        if (selected != null && !isLocked(selected)) selected.toFront();
    }
    public static void sendToBack(Pane layer) {
        if (selected == null || isLocked(selected)) return;
        layer.getChildren().remove(selected);
        layer.getChildren().add(0, selected);
    }
    public static void bringForward(Pane layer) {
        if (selected == null || isLocked(selected)) return;
        int i = layer.getChildren().indexOf(selected);
        if (i < layer.getChildren().size() - 1) {
            layer.getChildren().remove(selected);
            layer.getChildren().add(i + 1, selected);
        }
    }
    public static void sendBackward(Pane layer) {
        if (selected == null || isLocked(selected)) return;
        int i = layer.getChildren().indexOf(selected);
        if (i > 0) {
            layer.getChildren().remove(selected);
            layer.getChildren().add(i - 1, selected);
        }
    }

    // -------------------------------------------------------
    // Deselect all — hides handles
    // -------------------------------------------------------
    public static void deselectAll(Pane layer) {
        for (Node n : layer.getChildren())
            if (n instanceof Pane) showHandle((Pane) n, false);
        selected = null;
    }

    // ================================================================
    // PRIVATE
    // ================================================================
    private static Pane buildWrapper(Pane layer, Image image,
                                     double sx, double sy,
                                     double fw, String category) {
        ImageView feature = new ImageView(image);
        feature.setFitWidth(fw);
        feature.setPreserveRatio(true);
        feature.setPickOnBounds(true);

        Pane wrapper = new Pane(feature);
        wrapper.setLayoutX(sx);
        wrapper.setLayoutY(sy);
        wrapper.getProperties().put(CATEGORY_KEY, category);
        wrapper.getProperties().put(LOCKED_KEY, false);

        // Resize handle — single neutral dot, hidden by default
        Region handle = new Region();
        handle.setPrefSize(14, 14);
        handle.setStyle(HANDLE_STYLE);
        handle.setVisible(false);
        handle.setCursor(javafx.scene.Cursor.SE_RESIZE);

        feature.boundsInParentProperty().addListener((obs, o, b) -> {
            handle.setLayoutX(b.getWidth()  - 7);
            handle.setLayoutY(b.getHeight() - 7);
        });
        wrapper.getChildren().add(handle);

        final double[] off = new double[4]; // [0,1]=drag  [2]=resizeX  [3]=resizeW

        // Select + drag
        wrapper.setOnMousePressed(e -> {
            if (e.getTarget() == handle) return;
            deselectAll(layer);
            showHandle(wrapper, true);
            selected = wrapper;
            if (!isLocked(wrapper)) wrapper.toFront();
            off[0] = e.getX(); off[1] = e.getY();
            e.consume();
        });
        wrapper.setOnMouseDragged(e -> {
            if (e.getTarget() == handle) return;
            if (!isLocked(wrapper)) {
                wrapper.setLayoutX(wrapper.getLayoutX() + e.getX() - off[0]);
                wrapper.setLayoutY(wrapper.getLayoutY() + e.getY() - off[1]);
            }
            e.consume();
        });
        wrapper.setOnMouseReleased(e -> e.consume());

        // Resize
        handle.setOnMousePressed(e -> {
            if (isLocked(wrapper)) { e.consume(); return; }
            off[2] = e.getSceneX(); off[3] = feature.getFitWidth();
            e.consume();
        });
        handle.setOnMouseDragged(e -> {
            if (isLocked(wrapper)) { e.consume(); return; }
            double nw = off[3] + (e.getSceneX() - off[2]);
            if (nw > 24) feature.setFitWidth(nw);
            e.consume();
        });
        handle.setOnMouseReleased(e -> e.consume());

        // Double-click delete — only when unlocked
        wrapper.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && !isLocked(wrapper)) {
                layer.getChildren().remove(wrapper);
                undoStack.remove(wrapper);
                if (selected == wrapper) selected = null;
                e.consume();
            }
        });

        return wrapper;
    }

    static void showHandle(Pane wrapper, boolean visible) {
        for (Node c : wrapper.getChildren())
            if (c instanceof Region) c.setVisible(visible);
    }

    private static Pane findByCategory(Pane layer, String cat) {
        for (Node n : layer.getChildren())
            if (n instanceof Pane && cat.equals(((Pane)n).getProperties().get(CATEGORY_KEY)))
                return (Pane) n;
        return null;
    }

    private static ImageView getImageView(Pane wrapper) {
        for (Node c : wrapper.getChildren())
            if (c instanceof ImageView) return (ImageView) c;
        return null;
    }

    static boolean isLocked(Pane wrapper) {
        return Boolean.TRUE.equals(wrapper.getProperties().get(LOCKED_KEY));
    }
}