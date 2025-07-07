const functions = require("firebase-functions");

/**
 * Sebuah fungsi yang bisa dipanggil dari aplikasi untuk mendapatkan
 * timestamp dari server dalam milidetik.
 */
exports.getServerTimestamp = functions.https.onCall((data, context) => {
  // Log ini akan muncul di console Firebase Functions Anda
  console.log("Fungsi getServerTimestamp dipanggil oleh klien.");

  // Mengembalikan waktu server saat ini (dalam milidetik sejak epoch)
  return {
    serverTimeMillis: Date.now(),
  };
});