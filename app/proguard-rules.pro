# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#
#-injars       in.jar
#-outjars      out.jar
#-libraryjars  <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
#-printmapping out.map
#
#
#-keep class com.kyc.nashidmrz.Utility {
#  public protected *;
#}
#
#
#-keep public class * {
#    public protected *;
#}
#
#-keepparameternames
#-renamesourcefileattribute SourceFile
#-keepattributes Signature,Exceptions,*Annotation*,
#                InnerClasses,PermittedSubclasses,EnclosingMethod,
#                Deprecated,SourceFile,LineNumberTable
#
#-keepclasseswithmembernames,includedescriptorclasses class * {
#    native <methods>;
#}
#
#-keepclassmembers,allowoptimization enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
#
#-keepclassmembers class * {
#    *** get*();
#    void set*(***);
#}

#######################
#
#-optimizationpasses 5
#-dump class_files.txt
#-printseeds seeds.txt
#-printusage unused.txt
#-printmapping mapping.txt
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging*/
#-allowaccessmodification
#-repackageclasses ''
#
#-keep class androidx.databinding.DataBindingComponent {*;}
#
#
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#
#-libraryjars  libs/commons-io-2.2.jar
#-libraryjars  libs/gson-2.2.2.jar
#-keep public class org.apache.commons.io.**
#-keep public class com.google.gson.**
#-keep public class com.google.gson.** {public private protected *;}
#
###---------------Begin: proguard configuration for Gson ----------
#-keepattributes *Annotation*,Signature
#-keep class com.kyc.nashidmrz.Utility.** { *; }
#-keep public class com.kyc.nashidmrz$Utility   { public protected *; }
## To support Enum type of class members
#-keepclassmembers enum * { *; }
##---------------End: proguard configuration for Gson ----------
# Keep Retrofit classes
-keep class retrofit.** { *; }
-keepattributes Signature

# Keep OkHttp classes
-keep class okhttp3.** { *; }
-keepattributes Signature

# Keep OkHttp LoggingInterceptor
-dontwarn okhttp3.logging.**

# Keep OkHttp logging annotations
-keepattributes Exceptions, Signature, InnerClasses

# Keep Gson classes
-keep class com.google.gson.** { *; }
-keepattributes Signature

# Keep Retrofit interfaces and their methods
-keep,allowobfuscation interface com.kyc.nashidmrz.networking.APIInterface {
    @retrofit2.http.* <methods>;
}

# Keep Retrofit service classes that are used as return types
-keep,allowobfuscation class com.kyc.nashidmrz.networking.APIClient


#related to nfc
  -dontwarn org.junit.**
  -dontwarn junit.**
# Keep all classes and interfaces from the scuba-sc-android library
#-keep class net.sf.scuba.** { *; }
# Preserve annotations
-keepattributes *Annotation*

# Preserve classes used for reflection
-keepclassmembers class * {
    @java.lang.annotation.* *;
}

-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class org.ejbca.** {*;}
#-keep class com.kyc.nashidmrz.** {
#    *;
#}
#end here
# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson serialization
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Add rules for any other libraries used for JSON parsing or serialization
# (e.g., Moshi, GSON, Jackson) if you're using any.
