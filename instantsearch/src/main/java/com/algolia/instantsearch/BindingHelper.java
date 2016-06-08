package com.algolia.instantsearch;

import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.support.annotation.ColorRes;
import android.view.View;

import com.algolia.instantsearch.model.Errors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BindingHelper {
    private static final Map<Integer, String> attributes = new HashMap<>();

    private static boolean notAlreadyMapped(int id) {
        return attributes.get(id) == null;
    }

    private static void mapAttribute(String attributeName, int viewId) {
        attributes.put(viewId, attributeName);
    }

    public static Set<Map.Entry<Integer, String>> getEntrySet() {
        return attributes.entrySet();
    }

    @SuppressWarnings("unused") // called via Data Binding
    @BindingAdapter({"attribute"})
    public static void bindAttribute(View view, String attributeName) {
        final int id = view.getId();
        if (notAlreadyMapped(id)) { // only map when you see a view for the first time.
            mapAttribute(attributeName, id);
        }
    }

    @SuppressWarnings({"unused"}) // called via Data Binding
    @BindingAdapter({"attribute", "highlighted"})
    public static void bindHighlighted(View view, String attributeName, Boolean isHighlighted) {
        // Bind attribute, enable highlight with default color
        bindAndHighlight(view, attributeName, RenderingHelper.DEFAULT_COLOR);
    }

    @SuppressWarnings({"unused"}) // called via Data Binding
    @BindingAdapter({"attribute", "highlightingColor"})
    public static void bindHighlighted(View view, String attributeName, String colorStr) {
        // Bind attribute, enable highlight with color
        bindAndHighlight(view, attributeName, colorStr);
    }

    @SuppressWarnings({"unused", "UnusedParameters"}) // called via Data Binding
    @BindingAdapter({"attribute", "highlighted", "highlightingColor"})
    public static void bindHighlighted(View view, String attributeName, Boolean isHighlighted, String colorStr) {
        // Bind attribute, enable highlight with color
        bindAndHighlight(view, attributeName, colorStr);
    }

    @SuppressWarnings({"unused", "UnusedParameters"}) // called via Data Binding and throws
    @BindingAdapter({"highlighted"})
    public static void bindInvalid(View view, Boolean isHighlighted) {
        throwBindingError(view, Errors.BINDING_HIGHLIGHTED_NO_ATTR);
    }

    @SuppressWarnings({"unused", "UnusedParameters"}) // called via Data Binding and throws
    @BindingAdapter({"highlightingColor"})
    public static void bindInvalid(View view, @ColorRes int color) {
        throwBindingError(view, Errors.BINDING_HIGHLIGHTING_NO_ATTR);
    }

    @SuppressWarnings({"unused", "UnusedParameters"}) // called via Data Binding and throws
    @BindingAdapter({"highlighted", "highlighting"})
    public static void bindInvalid(View view, Boolean isHighlighted, String colorStr) {
        throwBindingError(view, Errors.BINDING_NO_ATTR);
    }

    private static void throwBindingError(View view, String message) {
        final Resources r = view.getContext().getResources();
        int id = view.getId();
        String viewName = r.getResourcePackageName(id) + ":" + r.getResourceTypeName(id) + "/" + r.getResourceEntryName(id);
        throw new IllegalStateException("Cannot bind " + viewName + ": " + message);
    }

    private static void bindAndHighlight(View view, String attributeName, String colorStr) {
        if (notAlreadyMapped(view.getId())) {
            final String[] split = colorStr.split("/");
            final String identifierType = split[0];
            final String colorName = split[1];

            final int colorId;
            if (identifierType.equals("@android:color")) {
                colorId = Resources.getSystem().getIdentifier(colorName, "color", "android");
            } else if (identifierType.equals("@color")) {
                colorId = view.getResources().getIdentifier(colorName, "color", view.getContext().getPackageName());
            } else {
                throw new IllegalStateException(Errors.BINDING_COLOR_INVALID);
            }

            bindAttribute(view, attributeName);
            RenderingHelper.addHighlight(attributeName);
            RenderingHelper.addColor(attributeName, colorId);
        }
    }
}