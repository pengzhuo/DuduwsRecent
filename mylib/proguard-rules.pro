# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Pengz/Documents/Android/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontwarn
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes InnerClasses,LineNumberTable

#友盟
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.duduws.ads.main.AdsManager{
    <fields>;
    <methods>;
}

-keep class com.duduws.ads.model.CallbackListener{
    <fields>;
    <methods>;
}

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn com.cmcm.adsdk.**
-dontwarn com.cleanmaster.**
-keep class  com.cmcm.adsdk.** { *;}
-keep class  com.cleanmaster.** { *;}
#for facebook
-dontwarn com.facebook.ads.**
-keep class com.facebook.ads.**{*;}
#for mopub
-dontwarn com.mopub.nativeads.**
-keep class com.mopub.**{*;}
-keep class  com.mopub.nativeads.*{*;}
-keep class  com.mopub.common.**{*;}
-keep class  com.mopub.network.**{*;}
-dontwarn com.mopub.**
-keep class com.cmcm.adsdk.nativead.MopubNativeAdLoader{
        <fields>;
        <methods>;
}
-keep class com.mopub.mobileads.MoPubActivity{
        <fields>;
        <methods>;
}
-keep class com.mopub.mobileads.MraidActivity{
        <fields>;
        <methods>;
}
-keep class com.mopub.common.MoPubBrowser{
        <fields>;
        <methods>;
}
-keep class com.mopub.mobileads.MraidVideoPlayerActivity{
        <fields>;
        <methods>;
}

# for admob

-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *;}
-keep class com.cmcm.adsdk.nativead.AdmobNativeLoader{
      <fields>;
      <methods>;
}
-keep public class com.google.android.gms.ads.**{
    public *;
}
# For old ads classes
-keep public class com.google.ads.**{
    public *;
}

# For mediation
-keepattributes *Annotation*
# Other required classes for Google Play Services
# Read more at http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# for yahoo
-keep class com.flurry.** {*;}
-dontwarn com.flurry.**
-keepattributes *Annotation*,EnclosingMethod
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

 -keep class com.cmcm.adsdk.adapter.MopubNativeAdapter{ *; }
 -keep class com.cmcm.adsdk.adapter.FacebookNativeAdapter{ *; }
 -keep class com.cmcm.adsdk.adapter.AdmobNativeAdapter{ *; }
 -keep class com.cmcm.adsdk.adapter.YahooNativeAdapter{ *; }

 # mopub 广告混淆脚本
 -keep class  com.mopub.nativeads.*{*;}
 -keep class  com.mopub.common.**{*;}
 -keep class  com.mopub.network.**{*;}
 -dontwarn com.mopub.**
 -keep class com.cmcm.adsdk.nativead.MopubNativeAdLoader{
         <fields>;
         <methods>;
 }
 -keep class com.mopub.mobileads.MoPubActivity{
         <fields>;
         <methods>;
 }
 -keep class com.mopub.mobileads.MraidActivity{
         <fields>;
         <methods>;
 }
 -keep class com.mopub.common.MoPubBrowser{
         <fields>;
         <methods>;
 }
 -keep class com.mopub.mobileads.MraidVideoPlayerActivity{
         <fields>;
         <methods>;
 }


 #for yahoo
 -keep class com.cmcm.adsdk.nativead.FlurryNativeLoader{
         <fields>;
         <methods>;
      }
 -keep class com.flurry.** { *; }
 -dontwarn com.flurry.**
 -keepattributes *Annotation*,EnclosingMethod

 -keep class com.google.android.gms.ads.** {*;}
 -dontwarn com.google.android.gms.ads.**
 -keep class com.google.android.gms.** {*;}
 -dontwarn com.google.android.gms.**



#for admob
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep class com.google.ads.AdActivity{
        <fields>;
		<methods>;
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keep public class com.google.ads.**{
   public *;
}
