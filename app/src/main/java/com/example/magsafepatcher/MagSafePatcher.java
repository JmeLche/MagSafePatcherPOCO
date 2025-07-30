
package com.example.magsafepatcher;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class MagSafePatcher implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            // Hook към PowerManager.isWirelessChargingSupported
            XposedHelpers.findAndHookMethod(
                "android.os.PowerManager",
                lpparam.classLoader,
                "isWirelessChargingSupported",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return true;
                    }
                }
            );

            // Hook към android.os.Build.hasWirelessCharging
            XposedHelpers.findAndHookMethod(
                "android.os.Build",
                "hasWirelessCharging",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return true;
                    }
                }
            );

            // Hook към SystemProperties.get() за множество ключове
            XposedHelpers.findAndHookMethod(
                "android.os.SystemProperties",
                lpparam.classLoader,
                "get",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String key = (String) param.args[0];
                        if (
                            "ro.hardware.wireless_charging".equals(key) ||
                            "persist.vendor.wireless.charging".equals(key) ||
                            "ro.miui.magsafe.enabled".equals(key) ||
                            "persist.sys.magsafe.present".equals(key) ||
                            "ro.product.hw_charging".equals(key)
                        ) {
                            param.setResult("true");
                        }
                    }
                }
            );

        } catch (Throwable t) {
            XposedHelpers.log("MagSafePatcher error: " + t.getMessage());
        }
    }
}
