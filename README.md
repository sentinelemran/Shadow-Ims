# Shadow Ims  (`com.shadow.ims`)

Root ছাড়া Android 12+ ফোনে VoLTE / VoWiFi / VoNR / UT ইত্যাদি IMS ফিচার carrier-config
ওভাররাইড করে চালু/বন্ধ করার টুল। পুরোটাই ফোনের ভেতরে — **INTERNET পারমিশন নেই**।

## কীভাবে কাজ করে
1. অ্যাপ mDNS দিয়ে Wireless debugging-এর pairing/connect পোর্ট খোঁজে (`adb/AdbDiscovery`)।
2. libadb-android দিয়ে নিজেই ADB pair + connect করে, `pm grant` দিয়ে
   `WRITE_SECURE_SETTINGS` ও `READ_PHONE_STATE` নেয় (`adb/AdbController`)।
3. ADB shell থেকে `app_process … com.shadow.ims.Broker` চালায় — shell uid হিসেবে hidden
   `ICarrierConfigLoader.overrideConfig` / `ITelephony.resetIms` কল হয় (`broker/Broker`)।
4. "Apply on boot" অন থাকলে রিবুটের পর `BootReceiver → ReapplyWorker` আবার প্রয়োগ করে।

## GitHub দিয়ে APK বানানো
1. এই ফোল্ডারের সব ফাইল আপনার রিপোতে push করুন (`main` ব্রাঞ্চ)।
2. **Actions** ট্যাব → *Build Shadow Ims APK* → রান শেষে **Artifacts → ShadowIms-apk** ডাউনলোড।
3. রিলিজ চাইলে: `git tag v1.0.0 && git push origin v1.0.0` — APK Releases-এ উঠবে।

### নিজের কী দিয়ে সাইন (ঐচ্ছিক)
Settings → Secrets → Actions-এ দিন: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`।
না দিলে debug কী দিয়ে সাইন হয় (ইনস্টল করা যায়, কিন্তু প্রতিবার একই কী নিশ্চিত করতে নিজের কী ভালো)।

## ফোনে ব্যবহার
Wi-Fi চালু → *Start pairing* → Developer options-এ Wireless debugging অন করে
*Pair device with pairing code* চাপুন → নোটিফিকেশনে বা অ্যাপে ৬-ডিজিট কোড দিন → Paired ✓।
তারপর SIM কার্ডে ফিচার বেছে **Apply**। ভুল হলে **Restore**।

## সতর্কতা
- ভুল carrier config কিছু ফোনে কল/ডেটা সমস্যা করতে পারে — Restore দিয়ে ফেরানো যায়।
- Android-এর hidden API ভার্সনভেদে বদলায় (বিশেষ করে Android 16); Broker reflection-এ একাধিক সিগনেচার ধরার চেষ্টা করে।
