# Eight Canteen (E-Kantin SMKN 8 Jakarta)

<p align="center">
  <img src="app/src/main/res/drawable/ic_app_logo.png" alt="Eight Canteen Logo" width="120" style="border-radius: 24px;" />
</p>

<p align="center">
  <strong>Kantin Sehat Digital & Bebas Antre — SMKN 8 Jakarta</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024%2B)-3DDC84?logo=android&logoColor=white" alt="Platform Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Networking-Retrofit%202%20%26%20OkHttp-FF6F00" alt="Retrofit" />
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" />
</p>

---

## Tentang Aplikasi

**Eight Canteen** adalah aplikasi mobile berbasis Android yang dirancang khusus untuk digitalisasi ekosistem kantin sekolah di **SMKN 8 Jakarta**. Aplikasi ini menghubungkan siswa dan penjual stan kantin dalam satu platform terintegrasi, yang terhubung langsung dengan backend service pada repository: [backend-kantin-mobile](https://github.com/alviangalen/backend-kantin-mobile.git).

Dengan Eight Canteen, siswa dapat memesan makanan/minuman tanpa perlu mengantre lama saat jam istirahat, melakukan pembayaran non-tunai (QRIS) maupun tunai di kasir, memindai QR code stan secara instan, serta mengumpulkan poin reward setiap transaksi.

---

## Fitur Utama

### 1. Siswa / Pembeli (Student)
- **Autentikasi & Keamanan**: Registrasi siswa, login aman, dan verifikasi OTP.
- **Katalog Stand & Menu**: Jelajahi seluruh stand kantin, kategori menu, harga, rating, dan status ketersediaan stok.
- **Pindai QR Stand (QR Scanner)**: Didukung oleh **CameraX** dan **Google ML Kit Barcode Scanning** untuk langsung membuka menu stand tertentu.
- **Keranjang & Checkout**: Tambah catatan khusus pesanan, pilih jumlah porsi, dan hitung total biaya otomatis.
- **Metode Pembayaran Ganda**:
  - **QRIS Digital**: Pembayaran instan via scan QRIS dinamis.
  - **Bayar di Kasir**: Kode transaksi untuk pembayaran tunai di kasir stand.
- **Struk Digital & Riwayat Transaksi**: Bukti pembayaran real-time yang dapat ditunjukkan ke penjual saat pengambilan pesanan.
- **Poin Reward**: Dapatkan poin dari setiap pembelian dan tukarkan dengan voucher diskon/hadiah menarik.
- **Notifikasi Pesanan**: Pemberitahuan status pesanan (*Menunggu Konfirmasi*, *Sedang Dimasak*, *Siap Diambil*, *Selesai*).

### 2. Penjual / Mitra Stand (Seller)
- **Dashboard Seller**: Pantau total pesanan masuk, pendapatan harian, dan statistik stan.
- **Manajemen Menu**: Tambah, edit harga, ubah foto/deskripsi, dan setel stok habis/tersedia.
- **Proses Pesanan Real-time**: Kelola alur pesanan dari konfirmasi, proses masak, hingga siap diambil.
- **Kontrol Toko**: Buka/tutup stand kantin secara fleksibel.

---

## Arsitektur & Teknologi

- **Bahasa**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + [Material 3](https://m3.material.io/)
- **Arsitektur**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + [OkHttp 3](https://square.github.io/okhttp/) Logging Interceptor
- **Backend API**: Terhubung ke RESTful API di repository [alviangalen/backend-kantin-mobile](https://github.com/alviangalen/backend-kantin-mobile.git)
- **JSON Serialization**: Google Gson
- **Hardware & Vision**: [CameraX](https://developer.android.com/training/camerax) & [Google ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
- **Asinkron & Reaktivitas**: Kotlin Coroutines & StateFlow
- **Manajemen Sesi**: SharedPreferences (JWT Token Storage & Automatic Session Refresh)

---

## Struktur Direktori Proyek

```
Eight-Canteen/
├── app/
│   ├── build.gradle.kts             # Konfigurasi dependensi & build Android
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Manifest izin (Internet, Kamera) & Activity
│           ├── java/com/januarzidanetinendeng/eightcanteen/
│           │   ├── EightCanteenApp.kt   # Application class & inisialisasi API
│           │   ├── MainActivity.kt      # Navigation controller & compose entry
│           │   ├── data/
│           │   │   ├── local/          # SessionManager & storage token
│           │   │   ├── model/          # Data class DTO request & response
│           │   │   ├── remote/         # Retrofit ApiService & ApiConfig
│           │   │   └── repository/     # CanteenRepository & handle error API
│           │   └── ui/
│           │       ├── admin/          # Admin dashboard & kelola stand
│           │       ├── checkout/       # Keranjang belanja & checkout
│           │       ├── components/     # Reusable UI (Button, Card, QR Scanner)
│           │       ├── dashboard/      # Dashboard siswa & riwayat pesanan
│           │       ├── login/          # Layar login siswa & admin
│           │       ├── otp/            # Verifikasi OTP
│           │       ├── payment/        # Layar QRIS & Struk Lunas
│           │       ├── points/         # Poin Reward & penukaran
│           │       ├── profile/        # Profil pengguna & logout
│           │       ├── register/       # Registrasi siswa & stand
│           │       ├── seller/         # Dashboard penjual & manajemen pesanan
│           │       ├── splash/         # Animasi splash screen
│           │       ├── stand/          # Daftar stand & katalog menu
│           │       └── theme/          # Color tokens, Typography, Shape M3
│           └── res/
│               ├── drawable/           # Ikon background & logo resolusi tinggi
│               ├── mipmap-*/           # Ikon aplikasi (Adaptive & Legacy)
│               └── values/             # strings.xml, colors.xml, themes.xml
├── .env.example                     # Contoh konfigurasi environment backend
├── build.gradle.kts                 # Konfigurasi Gradle root
└── settings.gradle.kts              # Pengaturan modul proyek
```

---

## Konfigurasi Environment (`.env`) & Backend

Aplikasi Android ini berkomunikasi langsung dengan backend service yang dikembangkan di repository:
 **[https://github.com/alviangalen/backend-kantin-mobile.git](https://github.com/alviangalen/backend-kantin-mobile.git)**

Konfigurasi koneksi dibaca secara otomatis saat proses build melalui file `.env` di direktori utama:

1. Salin `.env.example` menjadi `.env`:
   ```bash
   cp .env.example .env
   ```
2. Sesuaikan konfigurasi URL server dan API key yang sesuai dengan backend `backend-kantin-mobile`:
   ```env
   BASE_URL=https://api-eight-canteen.onrender.com/api/v1/
   API_KEY=your_x_api_key_here
   ```

*Catatan: Jika file `.env` tidak disediakan, Gradle akan otomatis menggunakan nilai fallback default yang telah terpasang di `app/build.gradle.kts`.*

---

## Cara Export Proyek ke File `.apk` (Siap Unduh & Pakai)

Tersedia dua metode mudah untuk mengekspor aplikasi ini menjadi file installer `.apk` yang bisa langsung dipasang di HP Android:

### Cara 1: Menggunakan Terminal / Command Line (Paling Cepat)

Jika Anda ingin langsung menghasilkan file `.apk` tanpa membuka antarmuka Android Studio:

1. Buka Terminal / PowerShell di folder proyek ini (`d:\Documents\Eight-Canteen`).
2. Pastikan `JAVA_HOME` mengarah ke Java/JDK (misalnya bawaan Android Studio):
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
   ```
3. Jalankan perintah build Gradle:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
4. Setelah muncul tulisan **`BUILD SUCCESSFUL`**, file APK siap pakai akan berada di:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

> **Info:** File `app-debug.apk` ini sudah langsung ditandatangani (*auto-signed*) dengan debug certificate dan siap dikirim ke smartphone untuk langsung diinstal!

---

### Cara 2: Menggunakan Android Studio (Antarmuka GUI)

#### Opsi A: Build Debug APK (Untuk Penggunaan Langsung & Testing)
1. Buka folder proyek ini di **Android Studio**.
2. Tunggu proses **Gradle Sync** hingga selesai.
3. Pada bilah menu atas, klik:
   **`Build`** ➔ **`Build Bundle(s) / APK(s)`** ➔ **`Build APK(s)`**.
4. Tunggu beberapa saat hingga proses build selesai.
5. Pop-up notifikasi akan muncul di pojok kanan bawah:
   *`APK(s) generated successfully for module 'Eight_Canteen.app'`*.
6. Klik tulisan biru **`locate`** pada notifikasi tersebut. File explorer akan langsung terbuka menampilkan file **`app-debug.apk`**.

#### Opsi B: Generate Signed Release APK (Untuk Distribusi Resmi / Produksi)
1. Pada menu Android Studio, klik:
   **`Build`** ➔ **`Generate Signed Bundle / APK...`**.
2. Pilih opsi **`APK`** lalu klik **Next**.
3. Jika belum memiliki Keystore:
   - Klik **`Create new...`**
   - Tentukan lokasi penyimpanan file key (contoh: `canteen-release-key.jks`), kata sandi, dan data sertifikat.
   - Klik **OK**.
4. Masukkan password key, lalu klik **Next**.
5. Pilih Build Variant **`release`**, centang **V1 (Jar Signature)** dan **V2 (Full APK Signature)** jika diminta.
6. Klik **Finish**.
7. File APK release yang telah dienkripsi dan dioptimalkan (ProGuard) akan tersimpan di:
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

---

## Cara Instalasi di Smartphone Android

1. **Kirim file `.apk`** ke smartphone Android Anda (bisa melalui kabel USB, WhatsApp, Telegram, atau Google Drive).
2. Di HP Android, buka aplikasi **File Manager** atau **Files**, lalu cari file `.apk` yang tadi dikirim.
3. Ketuk file `.apk` tersebut untuk memulai instalasi.
4. Jika muncul peringatan keamanan sistem:
   - Pilih **Setelan (Settings)**.
   - Aktifkan toggle **"Izinkan dari sumber ini" (Allow from this source)**.
   - Kembali dan tekan tombol **Instal (Install)**.
5. Selesai! Buka aplikasi **Eight Canteen** di layar utama HP Anda.

---

## Tim Pengembang

* **Abee Maalik Salahudin** - *Project Tester* 
* **Galen Alvian** - *Lead Backend Engineer & API Architect* ([GitHub: @alviangalen](https://github.com/alviangalen))
* **Haikal Rezqi Putra** - *UI/UX Designer* ([GitHub: @Kalllaja](https://github.com/Kalllaja))
* **Januar Zidane Tinendeng** - *Lead Android & Mobile Developer* ([GitHub: @Papi0404](https://github.com/Papi0404))
* **Muhammad Afdhal Al Fairuz** - *Logo Designer* ([GitHub: @MAFDHALALFAIRUZ](https://github.com/MAFDHALALFAIRUZ))
* **Muhammad Alfarezel Arsano** - *UI/UX & Mobile Developer* ([GitHub: @ezelaliluu](https://github.com/ezelaliluu))

* **SMKN 8 Jakarta** - *Mitra Implementasi & Pengguna Utama*

---

## Lisensi

Proyek ini dilisensikan di bawah [ISC License](LICENSE).  
Hak Cipta &copy; 2026 Tim E-Kantin SMKN 8 Jakarta.
