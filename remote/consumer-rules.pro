# ============================================================
# :remote module consumer ProGuard rules
# These rules are automatically included by the app module
# ============================================================

# ---- Retrofit 2 / OkHttp ----
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

# ---- Moshi ----
-keep,allowobfuscation,allowshrinking class com.squareup.moshi.JsonAdapter
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonQualifier @interface *
-keepnames @com.squareup.moshi.JsonClass class *

# ---- Gson (@SerializedName) ----
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ---- Remote model classes ----
-keep class project.side.remote.model.** { *; }

# ---- Kakao SDK ----
-keep class com.kakao.sdk.**.model.* { <fields>; }

# ---- Naver SDK ----
-keep class com.navercorp.nid.** { *; }
-dontwarn com.navercorp.nid.**

# ---- Google Credentials / Identity ----
-keep class com.google.android.libraries.identity.** { *; }
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**
