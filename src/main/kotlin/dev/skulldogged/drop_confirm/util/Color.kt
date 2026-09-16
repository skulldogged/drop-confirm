package dev.skulldogged.drop_confirm.util

/**
 * Defines standard colors for UI components throughout the mod's interface.
 *
 * These colors provide consistent visual styling and accessibility across
 * different states of UI elements (normal, disabled, hover, error, etc.).
 *
 * @property rgb The value of the color as a number in 0x(AA)RRGGBB format
 * @constructor Creates a ComponentColor with the specified RGB value
 * @since 5.0.0
 */
@Suppress("unused")
enum class Color(val rgb: Number) {
  /**
   * Standard text color (light gray: `0xE0E0E0` or `0xFFE0E0E0` on 1.21.6+).
   *
   * Used for normal, interactive text elements throughout the interface.
   */
  TEXT(/*? if >=1.21.6 {*//*0xFFE0E0E0*//*?} else {*/0xE0E0E0/*?}*/),

  /**
   * Color for disabled components (gray: `0xA0A0A0`).
   *
   * Used to visually indicate when UI elements are non-interactive
   * or unavailable for the current context.
   */
  DISABLED(0xA0A0A0),

  /**
   * Highlight color for hovered components (yellow: `0xFFFFA0`).
   *
   * Note: This color is only used in Minecraft 1.14.4 builds.
   * In other versions, the default Minecraft hover handling is used.
   */
  HOVERED(0xFFFFA0),

  /**
   * Color for success states and confirmations (green: `0x55FF55`).
   *
   * Used for valid inputs, successful operations, and positive feedback.
   */
  SUCCESS(0x55FF55),

  /**
   * Color for error states and warnings (red: `0xFF5555`).
   *
   * Used for invalid inputs, error messages, and critical warnings.
   */
  ERROR(0xFF5555),

  /**
   * Fully transparent color (`0x00000000`).
   *
   * Used for invisible backgrounds or elements that should not
   * visually appear but still occupy space in the layout.
   * Format: `0xAARRGGBB` (Alpha=0, R=0, G=0, B=0)
   */
  TRANSPARENT(0x00000000),

  /**
   * Color used for dimming the background (dark gray: `0xC0101010`).
   *
   * Used to create a dimming effect over the background when
   * displaying popups or modal dialogs.
   */
  DIMMING(0xC0101010),

  /**
   * Border color for popups and UI containers (purple/pink: `0xDD9BA8FF`).
   *
   * Used to visually define the boundary of UI components.
   */
  BORDER(0xDD9BA8FF),

  /**
   * Separator color for dividing UI sections (light purple: `0xDDC0C9FF`).
   *
   * Used for horizontal or vertical dividers within UI components.
   */
  SEPARATOR(0xDDC0C9FF),

  /**
   * Starting color for title bar gradient (medium purple: `0xDD4B61D1`).
   *
   * Used as the first color in title bar gradient backgrounds.
   */
  TITLE_BAR_PRIMARY(0xDD4B61D1),

  /**
   * Ending color for title bar gradient (darker purple: `0xDD3B4DA7`).
   *
   * Used as the second color in title bar gradient backgrounds.
   */
  TITLE_BAR_SECONDARY(0xDD3B4DA7),

  /**
   * Starting color for content area gradient (dark blue-purple: `0xDD242852`).
   *
   * Used as the first color in content area gradient backgrounds.
   */
  CONTENT_PRIMARY(0xDD242852),

  /**
   * Ending color for content area gradient (very dark blue: `0xDD1A2040`).
   *
   * Used as the second color in content area gradient backgrounds.
   */
  CONTENT_SECONDARY(0xDD1A2040),

  /**
   * Decorative color for UI corners and accents (translucent light purple: `0xAAC0C9FF`).
   *
   * Used for visual enhancements and decorative elements.
   */
  CORNER_DECORATION(0xAAC0C9FF),

  /**
   * Confirmation button color (green: `0xFF2D7D4C`).
   *
   * Used for buttons that confirm an action or proceed with an operation.
   */
  BUTTON_CONFIRM(0xFF2D7D4C),

  /**
   * Hover color for confirmation buttons (lighter green: `0xFF3A8E5A`).
   *
   * Used when hovering over confirmation buttons.
   */
  BUTTON_CONFIRM_HOVER(0xFF3A8E5A),

  /**
   * Cancellation button color (red: `0xFF8D3F3F`).
   *
   * Used for buttons that cancel an action or dismiss a dialog.
   */
  BUTTON_CANCEL(0xFF8D3F3F),

  /**
   * Hover color for cancellation buttons (lighter red: `0xFF9E4F4F`).
   *
   * Used when hovering over cancellation buttons.
   */
  BUTTON_CANCEL_HOVER(0xFF9E4F4F);

  /**
   * Provides direct conversion of this color enum to its integer value.
   *
   * This operator allows using color enums directly in rendering methods
   * that expect integer colors without explicitly accessing the rgb property.
   *
   * Example usage:
   * ```kotlin
   * drawText("Example", x, y, Color.TEXT())
   * ```
   *
   * @return The RGB value as an integer
   */
  operator fun invoke(): Int = rgb.toInt()
}
