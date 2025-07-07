package com.example.reglab7firebase.QRCode

import android.graphics.Bitmap
import com.example.reglab7firebase.MainDispatcherRule
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.QrCodeGenerator
import com.example.reglab7firebase.view.generateQrCode.QRCodeViewModel
import com.google.firebase.Timestamp
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class QRCodeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Siapkan mock untuk semua dependensi
    private val mockUserRepo: UserRepo = mockk()
    private val mockPraktikumRepo: PraktikumRepo = mockk()
    private val mockTimeRepo: TimeRepo = mockk()

    private val mockQrCodeGenerator: QrCodeGenerator = mockk()
    private lateinit var viewModel: QRCodeViewModel

    @Before
    fun setUp() {
        // Atur perilaku default untuk fungsi di init block
        coEvery { mockUserRepo.cekCurent() } returns "fake_uid"
        coEvery { mockUserRepo.getUser(any()) } returns User()

        viewModel = QRCodeViewModel(
            userRepo = mockUserRepo,
            praktikumrepo = mockPraktikumRepo,
            timerepo = mockTimeRepo,
            qrCodeGenerator = mockQrCodeGenerator
        )
    }

    // --- Skenario 1: Sukses Generate QR Code ---
    @Test
    fun `onGetServerTimeClicked - saat semua valid, status harus sukses`() = runTest {
        // Arrange
        val fakeBitmap = mockk<Bitmap>()
        coEvery { mockTimeRepo.fetchServerTimeViaDummyDoc(any()) } returns Timestamp.now()

        // Atur agar mock generator mengembalikan bitmap palsu
        every { mockQrCodeGenerator.generate(any()) } returns fakeBitmap

        // HAPUS BARIS INI:
        // coEvery { viewModel.generateCurrentUserQrCode(any()) } returns Unit

        // Act
        viewModel.onGetServerTimeClicked("prak1", "pertemuan_01", Timestamp.now(), isAdmin = true)

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
        assertEquals(fakeBitmap, viewModel.qrCodeBitmap.value) // Pastikan bitmap di-set

        // Verifikasi bahwa fungsi tersebut dipanggil tepat satu kali
        verify (exactly = 1) { mockQrCodeGenerator.generate(any()) }
    }

    // --- Skenario 2: Gagal karena Waktu Praktikum Habis/Belum Mulai (Bukan Admin) ---
    @Test
    fun `onGetServerTimeClicked - saat bukan admin dan di luar jangkauan waktu, status harus error`() = runTest {
        // Arrange
        // Waktu server sekarang
        val serverTimestamp = Timestamp(Date(1720238400000L)) // Contoh: 6 Juli 2024, 11:00
        coEvery { mockTimeRepo.fetchServerTimeViaDummyDoc(any()) } returns serverTimestamp

        // Waktu praktikum yang sudah lewat
        val waktuPrakHabis = Timestamp(Date(1730152000000L)) // Contoh: 5 Juli 2024, 11:00

        // Act
        viewModel.onGetServerTimeClicked("prak1", "pertemuan_01", waktuPrakHabis, isAdmin = false)

        // Assert
        val expectedError = Cek.Error("Maaf sudah tidak bisa presensi atau belum waktunya")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 3: Gagal karena Waktu Praktikum Null ---
    @Test
    fun `onGetServerTimeClicked - saat waktu praktikum null, status harus error`() {
        // Arrange
        // Tidak perlu setup mock repo karena fungsi akan return lebih dulu

        // Act
        viewModel.onGetServerTimeClicked("prak1", "pertemuan_01", null, isAdmin = true)

        // Assert
        val expectedError = Cek.Error("Gagal")
        assertEquals(expectedError, viewModel.uistatus.value)
    }
}