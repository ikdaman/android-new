# ============================================================
# :ui module consumer ProGuard rules
# ============================================================

# ---- ML Kit Barcode ----
-keep class com.google.mlkit.vision.barcode.** { *; }
-dontwarn com.google.mlkit.**

# ---- Coil ----
-dontwarn coil.**
