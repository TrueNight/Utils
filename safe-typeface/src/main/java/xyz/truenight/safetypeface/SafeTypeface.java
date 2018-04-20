/**
 * Copyright (C) 2018 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.safetypeface;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;

import java.io.File;

public class SafeTypeface {
    private SafeTypeface() {
    }

    private static SimpleArrayMap<String, Typeface> TYPEFACES = new SimpleArrayMap<>();


    /**
     * Create a new typeface from the specified font data.
     *
     * @param mgr  The application's asset manager
     * @param path The file name of the font data in the assets directory
     * @return The new typeface.
     */
    public static Typeface createFromAsset(AssetManager mgr, String path) {
        Typeface typeface = TYPEFACES.get(path);
        if (typeface != null) {
            return typeface;
        } else {
            typeface = Typeface.createFromAsset(mgr, path);
            TYPEFACES.put(path, typeface);
            return typeface;
        }
    }

    /**
     * Create a new typeface from the specified font file.
     *
     * @param path The path to the font data.
     * @return The new typeface.
     */
    public static Typeface createFromFile(File path) {
        // For the compatibility reasons, leaving possible NPE here.
        // See android.graphics.cts.TypefaceTest#testCreateFromFileByFileReferenceNull
        return createFromFile(path.getAbsolutePath());
    }

    /**
     * Create a new typeface from the specified font file.
     *
     * @param path The full path to the font data.
     * @return The new typeface.
     */
    public static Typeface createFromFile(@Nullable String path) {
        Typeface typeface = TYPEFACES.get(path);
        if (typeface != null) {
            return typeface;
        } else {
            typeface = Typeface.createFromFile(path);
            TYPEFACES.put(path, typeface);
            return typeface;
        }
    }
}
