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
-ignorewarnings
-dontwarn com.epgis.gdt.maplibrary.**
-keep public class com.epgis.gdt.maplibrary.**{*;}
-dontwarn com.epgis.gdt.maplibrary.utils.**
-keep public class com.epgis.gdt.maplibrary.utils.**{*;}

-dontwarn com.tencent.**
-keep public class com.tencent.**{*;}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

##
#-dontwarn com.**
#-keep  class com.**{*;}

-dontwarn com.android.**
-keep  class com.android.**{*;}

-dontwarn com.alibaba.**
-keep  class com.alibaba.**{*;}


-dontwarn com.qualcomm.**
-keep  class com.qualcomm.**{*;}

-dontwarn com.lbe.**
-keep  class com.lbe.**{*;}

-dontwarn com.amap.**
-keep  class com.amap.**{*;}

-dontwarn com.annimon.**
-keep  class com.annimon.**{*;}

-dontwarn com.autonavi.**
-keep  class com.autonavi.**{*;}

-dontwarn com.amazonaws.**
-keep  class com.amazonaws.**{*;}

-dontwarn com.facebook.**
-keep  class com.facebook.**{*;}


-dontwarn com.github.**
-keep  class com.github.**{*;}

-dontwarn com.j256.**
-keep  class com.j256.**{*;}

-dontwarn com.jakewharton.**
-keep  class com.jakewharton.**{*;}

-dontwarn com.scwang.**
-keep  class com.scwang.**{*;}

-dontwarn com.ryanharter.**
-keep  class com.ryanharter.**{*;}

-dontwarn com.google.vr.**
-keep  class com.google.vr.**{*;}

-dontwarn com.google.**
-keep  class com.google..**{*;}

-dontwarn com.squareup.**
-keep  class com.squareup..**{*;}

-dontwarn com.baidu.**
-keep  class com.baidu..**{*;}

-dontwarn com.hp.**
-keep  class com.hp..**{*;}

-dontwarn com.loc.**
-keep  class com.loc..**{*;}

-dontwarn com.amazonaws.**
-keep  class com.amazonaws..**{*;}




-dontwarn android.**
-keep  class android.**{*;}

-dontwarn autovalue.**
-keep  class autovalue.**{*;}

-dontwarn okio.**
-keep  class okio.**{*;}

-dontwarn org.**
-keep  class org.**{*;}


-dontwarn net.**
-keep  class net.**{*;}

-dontwarn rx.**
-keep  class rx.**{*;}


-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}

-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

-dontwarn timer.**
-keep class timber.**{*;}

-dontwarn okio.**


-dontwarn com.mapbox.**
-keep class com.mapbox.**{*;}

-keep interface com.mapbox.** { *; }



-keepattributes Signature, *Annotation*, EnclosingMethod
-keep class almeros.android.multitouch.gesturedetectors.** { *; }
-keep class com.mapbox.mapboxsdk.** { *; }
-keep interface com.mapbox.mapboxsdk.** { *; }
-keep class com.epgis.services.android.telemetry.** { *; }
-keep class com.epgis.services.commons.** { *;}
-keep class com.google.gson.** { *; }
-keep class com.google.gson.internal.** { *; }
-keep class android.telephony.**{*;}

# config for okhttp 3.8.0, https://github.com/square/okhttp/pull/3354
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault






-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#
#
-dontshrink
-dontpreverify
-dontoptimize
-optimizations optimization_filter
-dontusemixedcaseclassnames

-flattenpackagehierarchy
-allowaccessmodification
-printmapping map.txt

-optimizationpasses 7
-verbose
-keepattributes Exceptions,InnerClasses
-dontwarn InnerClasses
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-keepattributes Signature

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# adding this in to preserve line numbers so that the stack traces
# can be remapped
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keepattributes *Annotation*

-keep public class **.R$*{
   public static final int *;
}

-keep class com.google.gson.stream.** { *; }

## greendao start
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**
## greendao end

## butterknife start
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
## butterknife end









-keep class com.ousrslook.shimao.commen.ioc.** { *; } #不能混淆 否则注解无效
-keep class com.ousrslook.shimao.model.** { *; } #不能混淆
-keep class com.ousrslook.shimao.net.XaResult{ *; }#统一返回的实体类泛型不能混淆
#-keep class com.ousrslook.shimao.net.** { *; }


####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####


-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}


#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}


#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}


#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable


#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}


-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}


-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}
 -keep class **.R$* { *; }
#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

##------------------------------------------------------------------------
#保留注解，如果不添加改行会导致我们的@Keep注解失效
-dontwarn android.support.annotation.Keep
-keepattributes *Annotation*
-keep @android.support.annotation.Keep class **{
@android.support.annotation.Keep <fields>;
@android.support.annotation.Keep <methods>;
}

###auth
#-dontwarn com.epgis.auth.**
#-keep  class com.epgis.auth.**{*;}
#-dontwarn com.epgis.coordinate.**
#-keep  class com.epgis.coordinate.**{*;}
#-keep  class com.epgis.LibraryLoader{*;}
#
###common
#-dontwarn com.epgis.commons.**
#-keep  class com.epgis.commons.**{*;}
#
###data
#-dontwarn com.epgis.data.**
#-keep  class com.epgis.data.**{*;}
#
###location
#-dontwarn com.epgis.location.**
#-keep  class com.epgis.location.**{*;}
#
###location
#-dontwarn com.epgis.offline.**
#-keep  class com.epgis.offline.**{*;}
#
###service
#-dontwarn com.epgis.service.**
#-keep  class com.epgis.service.**{*;}
#
###maplayer
#-dontwarn com.epgis.mapsdk.plugin.**
#-keep  class com.epgis.mapsdk.plugin.**{*;}
#
###maplayer
#-dontwarn com.epgis.android.gestures.**
#-keep  class com.epgis.android.gestures.**{*;}
#
###maplayer
#-dontwarn com.epgis.mapsdk.maps**
#-keep  class com.epgis.mapsdk.maps**{*;}


###maplayer
#-dontwarn com.epgis.mapsdk.**
#-keep  class com.epgis.mapsdk.**{*;}
-dontwarn com.epgis.**
-keep  class com.epgis.**{*;}

-keep class com.flyco.tablayout.**{*;}

-dontwarn **
-keep  class **{*;}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}