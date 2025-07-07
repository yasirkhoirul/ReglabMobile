package com.example.reglab7firebase.QRCode

import com.example.reglab7firebase.MainDispatcherRule
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.LocationRepo
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.model.WifiRepo
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.view.presensi.PresensiViewModel
import com.google.firebase.Timestamp
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class PresensiViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Siapkan mock untuk semua dependensi
    private val mockTimeRepo: TimeRepo = mockk()
    private val mockUserRepo: UserRepo = mockk()
    private val mockPraktikumRepo: PraktikumRepo = mockk(relaxUnitFun = true)
    private val mockLocationRepo: LocationRepo = mockk()
    private val mockWifiRepo: WifiRepo = mockk()

    private lateinit var viewModel: PresensiViewModel

    @Before
    fun setUp() {
        // Atur perilaku default untuk fungsi di init block
        coEvery { mockLocationRepo.getLocation() } returns null

        // Buat instance ViewModel
        viewModel = PresensiViewModel(
            timerepo = mockTimeRepo,
            userRepo = mockUserRepo,
            praktikum = mockPraktikumRepo,
            lokasiRepo = mockLocationRepo,
            wifiRepo = mockWifiRepo
        )
    }

    // --- Skenario 1: Sukses Melakukan Presensi ---
    @Test
    fun `validasiPresensi - saat semua valid, status harus sukses`() = runTest {
        viewModel.getIle() // <-- RESET STATUS KE IDLE
        // Arrange
        val now = System.currentTimeMillis()
        val qrCodeData = "${now + 10000}/prak1/pertemuan_01" // QR Code valid 10 detik lagi

        coEvery { mockUserRepo.cekUser() } returns true
        coEvery { mockUserRepo.cekCurent() } returns "mahasiswa_uid"
        coEvery { mockPraktikumRepo.cekMahasiswa("mahasiswa_uid") } returns listOf(Praktikum())
        coEvery { mockTimeRepo.fetchServerTimeViaDummyDoc(any()) } returns Timestamp(Date(now))
        coEvery { mockPraktikumRepo.addPresensiUser(any(), any(), any()) } returns Unit
        advanceUntilIdle()
        // Act
        viewModel.validasiPresensi(qrCodeData)

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
    }

    // --- Skenario 2: Gagal karena Belum Login ---
    @Test
    fun `validasiPresensi - saat user belum login, status harus error`() {
        // Arrange
        coEvery { mockUserRepo.cekUser() } returns false

        // Act
        viewModel.validasiPresensi("apapun")

        // Assert
        assertEquals(Cek.Error("Anda belum login"), viewModel.uistatus.value)
    }

    // --- Skenario 3: Gagal karena Bukan Praktikan Terdaftar ---
    @Test
    fun `validasiPresensi - saat user bukan praktikan, status harus error`() = runTest {
        // Arrange
        val qrCodeData = "${System.currentTimeMillis() + 10000}/prak1/pertemuan_01"
        coEvery { mockUserRepo.cekUser() } returns true
        coEvery { mockUserRepo.cekCurent() } returns "mahasiswa_asing"
        // Atur repo untuk mengembalikan list kosong (tidak terdaftar)
        coEvery { mockPraktikumRepo.cekMahasiswa("mahasiswa_asing") } returns emptyList()

        // Act
        viewModel.validasiPresensi(qrCodeData)

        // Assert
        assertEquals(Cek.Error("Anda bukan Praktikan Ini"), viewModel.uistatus.value)
    }

    // --- Skenario 4: Gagal karena QR Code Kedaluwarsa ---
    @Test
    fun `validasiPresensi - saat QR code kedaluwarsa, status harus error`() = runTest {
        // Arrange
        val now = System.currentTimeMillis()
        val qrCodeKedaluwarsa = "${now - 10000}/prak1/pertemuan_01" // QR Code sudah lewat 10 detik

        coEvery { mockUserRepo.cekUser() } returns true
        coEvery { mockUserRepo.cekCurent() } returns "mahasiswa_uid"
        coEvery { mockPraktikumRepo.cekMahasiswa("mahasiswa_uid") } returns listOf(Praktikum())
        coEvery { mockTimeRepo.fetchServerTimeViaDummyDoc(any()) } returns Timestamp(Date(now))

        // Act
        viewModel.validasiPresensi(qrCodeKedaluwarsa)

        // Assert
        assertEquals(Cek.Error("Maaf Qr code sudah tidak valid"), viewModel.uistatus.value)
    }

}