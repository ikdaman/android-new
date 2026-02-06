# ============================================================
# 읽다만 (ikdaman) App-level ProGuard Rules
# ============================================================
# Module-specific rules are in each module's consumer-rules.pro
# and are merged automatically.

# ---- Debug ----
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Kotlin ----
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-dontwarn kotlin.**

# ---- Gson / Serialization attributes ----
-keepattributes Signature
-keepattributes *Annotation*

# ---- Data model classes (data module is java-library, no consumer-rules) ----
-keep class project.side.data.model.** { *; }

# ---- Hilt / Dagger ----
-dontwarn dagger.hilt.**
