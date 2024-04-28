package intellijmigrationplugin.settings.utils

import intellijmigrationplugin.settings.utils.ColorUtils.ColorName
import java.awt.Color

/**
 * Utility class for working with predefined colors and finding the closest color name based on RGB values.
 */
private fun initColorList(): ArrayList<ColorName> {
    val colorList = ArrayList<ColorName>()
    colorList.add(ColorName("Alice Blue", 0xF0, 0xF8, 0xFF))
    colorList.add(ColorName("Antique White", 0xFA, 0xEB, 0xD7))
    colorList.add(ColorName("Aqua", 0x00, 0xFF, 0xFF))
    colorList.add(ColorName("Aquamarine", 0x7F, 0xFF, 0xD4))
    colorList.add(ColorName("Azure", 0xF0, 0xFF, 0xFF))
    colorList.add(ColorName("Beige", 0xF5, 0xF5, 0xDC))
    colorList.add(ColorName("Bisque", 0xFF, 0xE4, 0xC4))
    colorList.add(ColorName("Black", 0x00, 0x00, 0x00))
    colorList.add(ColorName("Blanched Almond", 0xFF, 0xEB, 0xCD))
    colorList.add(ColorName("Blue", 0x00, 0x00, 0xFF))
    colorList.add(ColorName("Blue Violet", 0x8A, 0x2B, 0xE2))
    colorList.add(ColorName("Brown", 0xA5, 0x2A, 0x2A))
    colorList.add(ColorName("Burly Wood", 0xDE, 0xB8, 0x87))
    colorList.add(ColorName("Cadet Blue", 0x5F, 0x9E, 0xA0))
    colorList.add(ColorName("Chartreuse", 0x7F, 0xFF, 0x00))
    colorList.add(ColorName("Chocolate", 0xD2, 0x69, 0x1E))
    colorList.add(ColorName("Coral", 0xFF, 0x7F, 0x50))
    colorList.add(ColorName("Cornflower Blue", 0x64, 0x95, 0xED))
    colorList.add(ColorName("Cornsilk", 0xFF, 0xF8, 0xDC))
    colorList.add(ColorName("Crimson", 0xDC, 0x14, 0x3C))
    colorList.add(ColorName("Cyan", 0x00, 0xFF, 0xFF))
    colorList.add(ColorName("Dark Blue", 0x00, 0x00, 0x8B))
    colorList.add(ColorName("Dark Cyan", 0x00, 0x8B, 0x8B))
    colorList.add(ColorName("Dark GoldenRod", 0xB8, 0x86, 0x0B))
    colorList.add(ColorName("Dark Gray", 0xA9, 0xA9, 0xA9))
    colorList.add(ColorName("Dark Green", 0x00, 0x64, 0x00))
    colorList.add(ColorName("Dark Khaki", 0xBD, 0xB7, 0x6B))
    colorList.add(ColorName("Dark Magenta", 0x8B, 0x00, 0x8B))
    colorList.add(ColorName("Dark Olive Green", 0x55, 0x6B, 0x2F))
    colorList.add(ColorName("Dark Orange", 0xFF, 0x8C, 0x00))
    colorList.add(ColorName("Dark Orchid", 0x99, 0x32, 0xCC))
    colorList.add(ColorName("Dark Red", 0x8B, 0x00, 0x00))
    colorList.add(ColorName("Dark Salmon", 0xE9, 0x96, 0x7A))
    colorList.add(ColorName("Dark Sea Green", 0x8F, 0xBC, 0x8F))
    colorList.add(ColorName("Dark Slate Blue", 0x48, 0x3D, 0x8B))
    colorList.add(ColorName("Dark Slate Gray", 0x2F, 0x4F, 0x4F))
    colorList.add(ColorName("Dark Turquoise", 0x00, 0xCE, 0xD1))
    colorList.add(ColorName("Dark Violet", 0x94, 0x00, 0xD3))
    colorList.add(ColorName("Deep Pink", 0xFF, 0x14, 0x93))
    colorList.add(ColorName("Deep Sky Blue", 0x00, 0xBF, 0xFF))
    colorList.add(ColorName("Dim Gray", 0x69, 0x69, 0x69))
    colorList.add(ColorName("Dodger Blue", 0x1E, 0x90, 0xFF))
    colorList.add(ColorName("Fire Brick", 0xB2, 0x22, 0x22))
    colorList.add(ColorName("Floral White", 0xFF, 0xFA, 0xF0))
    colorList.add(ColorName("Forest Green", 0x22, 0x8B, 0x22))
    colorList.add(ColorName("Fuchsia", 0xFF, 0x00, 0xFF))
    colorList.add(ColorName("Gainsboro", 0xDC, 0xDC, 0xDC))
    colorList.add(ColorName("Ghost White", 0xF8, 0xF8, 0xFF))
    colorList.add(ColorName("Gold", 0xFF, 0xD7, 0x00))
    colorList.add(ColorName("Golden Rod", 0xDA, 0xA5, 0x20))
    colorList.add(ColorName("Gray", 0x80, 0x80, 0x80))
    colorList.add(ColorName("Green", 0x00, 0x80, 0x00))
    colorList.add(ColorName("Green Yellow", 0xAD, 0xFF, 0x2F))
    colorList.add(ColorName("Honey Dew", 0xF0, 0xFF, 0xF0))
    colorList.add(ColorName("Hot Pink", 0xFF, 0x69, 0xB4))
    colorList.add(ColorName("Indian Red", 0xCD, 0x5C, 0x5C))
    colorList.add(ColorName("Indigo", 0x4B, 0x00, 0x82))
    colorList.add(ColorName("Ivory", 0xFF, 0xFF, 0xF0))
    colorList.add(ColorName("Khaki", 0xF0, 0xE6, 0x8C))
    colorList.add(ColorName("Lavender", 0xE6, 0xE6, 0xFA))
    colorList.add(ColorName("Lavender Blush", 0xFF, 0xF0, 0xF5))
    colorList.add(ColorName("Lawn Green", 0x7C, 0xFC, 0x00))
    colorList.add(ColorName("Lemon Chiffon", 0xFF, 0xFA, 0xCD))
    colorList.add(ColorName("Light Blue", 0xAD, 0xD8, 0xE6))
    colorList.add(ColorName("Light Coral", 0xF0, 0x80, 0x80))
    colorList.add(ColorName("Light Cyan", 0xE0, 0xFF, 0xFF))
    colorList.add(ColorName("Light Golden Rod Yellow", 0xFA, 0xFA, 0xD2))
    colorList.add(ColorName("Light Gray", 0xD3, 0xD3, 0xD3))
    colorList.add(ColorName("Light Green", 0x90, 0xEE, 0x90))
    colorList.add(ColorName("Light Pink", 0xFF, 0xB6, 0xC1))
    colorList.add(ColorName("Light Salmon", 0xFF, 0xA0, 0x7A))
    colorList.add(ColorName("Light Sea Green", 0x20, 0xB2, 0xAA))
    colorList.add(ColorName("Light Sky Blue", 0x87, 0xCE, 0xFA))
    colorList.add(ColorName("Light Slate Gray", 0x77, 0x88, 0x99))
    colorList.add(ColorName("Light Steel Blue", 0xB0, 0xC4, 0xDE))
    colorList.add(ColorName("Light Yellow", 0xFF, 0xFF, 0xE0))
    colorList.add(ColorName("Lime", 0x00, 0xFF, 0x00))
    colorList.add(ColorName("Lime Green", 0x32, 0xCD, 0x32))
    colorList.add(ColorName("Linen", 0xFA, 0xF0, 0xE6))
    colorList.add(ColorName("Magenta", 0xFF, 0x00, 0xFF))
    colorList.add(ColorName("Maroon", 0x80, 0x00, 0x00))
    colorList.add(ColorName("Medium Aqua Marine", 0x66, 0xCD, 0xAA))
    colorList.add(ColorName("Medium Blue", 0x00, 0x00, 0xCD))
    colorList.add(ColorName("Medium Orchid", 0xBA, 0x55, 0xD3))
    colorList.add(ColorName("Medium Purple", 0x93, 0x70, 0xDB))
    colorList.add(ColorName("Medium Sea Green", 0x3C, 0xB3, 0x71))
    colorList.add(ColorName("Medium Slate Blue", 0x7B, 0x68, 0xEE))
    colorList.add(ColorName("Medium Spring Green", 0x00, 0xFA, 0x9A))
    colorList.add(ColorName("Medium Turquoise", 0x48, 0xD1, 0xCC))
    colorList.add(ColorName("Medium Violet Red", 0xC7, 0x15, 0x85))
    colorList.add(ColorName("Midnight Blue", 0x19, 0x19, 0x70))
    colorList.add(ColorName("Mint Cream", 0xF5, 0xFF, 0xFA))
    colorList.add(ColorName("Misty Rose", 0xFF, 0xE4, 0xE1))
    colorList.add(ColorName("Moccasin", 0xFF, 0xE4, 0xB5))
    colorList.add(ColorName("Navajo White", 0xFF, 0xDE, 0xAD))
    colorList.add(ColorName("Navy", 0x00, 0x00, 0x80))
    colorList.add(ColorName("Old Lace", 0xFD, 0xF5, 0xE6))
    colorList.add(ColorName("Olive", 0x80, 0x80, 0x00))
    colorList.add(ColorName("Olive Drab", 0x6B, 0x8E, 0x23))
    colorList.add(ColorName("Orange", 0xFF, 0xA5, 0x00))
    colorList.add(ColorName("Orange Red", 0xFF, 0x45, 0x00))
    colorList.add(ColorName("Orchid", 0xDA, 0x70, 0xD6))
    colorList.add(ColorName("Pale Golden Rod", 0xEE, 0xE8, 0xAA))
    colorList.add(ColorName("Pale Green", 0x98, 0xFB, 0x98))
    colorList.add(ColorName("Pale Turquoise", 0xAF, 0xEE, 0xEE))
    colorList.add(ColorName("Pale Violet Red", 0xDB, 0x70, 0x93))
    colorList.add(ColorName("Papaya Whip", 0xFF, 0xEF, 0xD5))
    colorList.add(ColorName("Peach Puff", 0xFF, 0xDA, 0xB9))
    colorList.add(ColorName("Peru", 0xCD, 0x85, 0x3F))
    colorList.add(ColorName("Pink", 0xFF, 0xC0, 0xCB))
    colorList.add(ColorName("Plum", 0xDD, 0xA0, 0xDD))
    colorList.add(ColorName("Powder Blue", 0xB0, 0xE0, 0xE6))
    colorList.add(ColorName("Purple", 0x80, 0x00, 0x80))
    colorList.add(ColorName("Red", 0xFF, 0x00, 0x00))
    colorList.add(ColorName("Rosy Brown", 0xBC, 0x8F, 0x8F))
    colorList.add(ColorName("Royal Blue", 0x41, 0x69, 0xE1))
    colorList.add(ColorName("Saddle Brown", 0x8B, 0x45, 0x13))
    colorList.add(ColorName("Salmon", 0xFA, 0x80, 0x72))
    colorList.add(ColorName("Sandy Brown", 0xF4, 0xA4, 0x60))
    colorList.add(ColorName("Sea Green", 0x2E, 0x8B, 0x57))
    colorList.add(ColorName("Sea Shell", 0xFF, 0xF5, 0xEE))
    colorList.add(ColorName("Sienna", 0xA0, 0x52, 0x2D))
    colorList.add(ColorName("Silver", 0xC0, 0xC0, 0xC0))
    colorList.add(ColorName("Sky Blue", 0x87, 0xCE, 0xEB))
    colorList.add(ColorName("Slate Blue", 0x6A, 0x5A, 0xCD))
    colorList.add(ColorName("Slate Gray", 0x70, 0x80, 0x90))
    colorList.add(ColorName("Snow", 0xFF, 0xFA, 0xFA))
    colorList.add(ColorName("Spring Green", 0x00, 0xFF, 0x7))
    colorList.add(ColorName("Steel Blue", 0x46, 0x82, 0xB4))
    colorList.add(ColorName("Tan", 0xD2, 0xB4, 0x8C))
    colorList.add(ColorName("Teal", 0x00, 0x80, 0x80))
    colorList.add(ColorName("Thistle", 0xD8, 0xBF, 0xD8))
    colorList.add(ColorName("Tomato", 0xFF, 0x63, 0x47))
    colorList.add(ColorName("Turquoise", 0x40, 0xE0, 0xD0))
    colorList.add(ColorName("Violet", 0xEE, 0x82, 0xEE))
    colorList.add(ColorName("Wheat", 0xF5, 0xDE, 0xB3))
    colorList.add(ColorName("White", 0xFF, 0xFF, 0xFF))
    colorList.add(ColorName("White Smoke", 0xF5, 0xF5, 0xF5))
    colorList.add(ColorName("Yellow", 0xFF, 0xFF, 0x00))
    colorList.add(ColorName("Yellow Green", 0x9A, 0xCD, 0x32))
    return colorList
}

class ColorUtils {

    data class ColorName(val name: String, val r: Int, val g: Int, val b: Int) {

        fun computeMSE(pixR: Int, pixG: Int, pixB: Int): Int {
            return ((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b) * (pixB - b)) / 3
        }
    }

    @Suppress("UseJBColor")
    companion object {
        fun getColorNameFromRgb(r: Int, g: Int, b: Int): String {
            val colorList = initColorList()
            var closestMatch: ColorName? = null
            var minMSE = Int.MAX_VALUE
            var mse: Int
            for (c in colorList) {
                mse = c.computeMSE(r, g, b)
                if (mse < minMSE) {
                    minMSE = mse
                    closestMatch = c
                }
            }
            return closestMatch?.name ?: "No matched color name."
        }
        fun decodeColor(colorStr: String): Color {
            return if (colorStr.length == 9) { // #RRGGBBAA format
                val alpha = Integer.parseInt(colorStr.substring(1, 3), 16)
                val red = Integer.parseInt(colorStr.substring(3, 5), 16)
                val green = Integer.parseInt(colorStr.substring(5, 7), 16)
                val blue = Integer.parseInt(colorStr.substring(7, 9), 16)
                Color(red, green, blue, alpha)
            } else {
                Color.decode(colorStr)
            }
        }
    }
}
