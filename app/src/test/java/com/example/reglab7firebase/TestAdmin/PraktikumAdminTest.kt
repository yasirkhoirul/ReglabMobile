package com.example.reglab7firebase.TestAdmin

import java.util.Calendar
import com.example.reglab7firebase.MainDispatcherRule
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.DetailPertemuan
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.TimeProvider
import com.example.reglab7firebase.view.praktikumAdmin.PraktikumAdminViewModel
import com.example.reglab7firebase.view.praktikumAdmin.detailPraktikumAdmin.DetailPraktikumAdminViewModel
import com.example.reglab7firebase.view.praktikumAsprak.detailPraktikumAsprak.DetailAsprakViewModel
import com.example.reglab7firebase.view.tambahDataPraktikum.TambahPraktikumViewModel
import com.example.reglab7firebase.view.tambahOrangPraktikum.TambahMahasiswaViewModel
import com.google.firebase.Timestamp
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNotEquals

@ExperimentalCoroutinesApi
class PraktikumAdminViewModelTest {


    private val mockPraktikumRepo: PraktikumRepo = mockk(relaxUnitFun = true)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PraktikumAdminViewModel

    @Before
    fun setUp() {
        coEvery { mockPraktikumRepo.getAllPrakAdminCall() } returns flowOf(emptyList())

        viewModel = PraktikumAdminViewModel(mockPraktikumRepo)
    }

    // --- Skenario 1: Berhasil Mengambil Data ---
    @Test
    fun `getPrak - saat repository berhasil, uilistPraktikum harus diupdate dan status sukses`() = runTest {
        // Arrange
        val fakePraktikumList = listOf(Praktikum(id_prak = "001", nama_prak = "Mobile"))
        coEvery { mockPraktikumRepo.getAllPrakAdminCall() } returns flowOf(fakePraktikumList)

        // Act
        viewModel.getPrak()

        // Assert
        assertEquals(fakePraktikumList, viewModel.uilistPraktikum.value)
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
    }

    // --- Skenario 2: Gagal Mengambil Data ---
    @Test
    fun `getPrak - saat repository error, status harus error`() = runTest {
        // Arrange
        val errorMessage = "Koneksi database gagal"

        val errorFlow = flow<List<Praktikum>> { throw Exception(errorMessage) }
        coEvery { mockPraktikumRepo.getAllPrakAdminCall() } returns errorFlow

        // Act
        viewModel.getPrak()

        // Assert
        val expectedError = Cek.Error(message = errorMessage)
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 3: Berhasil Menghapus Praktikum ---
    @Test
    fun `getDeletePrak - saat repository sukses, status harus sukses`() = runTest {
        // Arrange
        val uidToDelete = "prak_to_delete"
        // Atur agar fungsi suspend deletePrak tidak melakukan apa-apa (berhasil)
        coEvery { mockPraktikumRepo.deletePrak(uidToDelete) } returns Unit

        // Act
        viewModel.getDeletePrak(uidToDelete)

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
        // Verifikasi bahwa fungsi di repo benar-benar dipanggil
        coVerify(exactly = 1) { mockPraktikumRepo.deletePrak(uidToDelete) }
    }

    // --- Skenario 4: Gagal Menghapus Praktikum ---
    @Test
    fun `getDeletePrak - saat repository error, status harus error`() = runTest {
        // Arrange
        val uidToDelete = "prak_to_delete"

        coEvery { mockPraktikumRepo.deletePrak(uidToDelete) } throws Exception("Gagal hapus dari database")

        // Act
        viewModel.getDeletePrak(uidToDelete)

        // Assert
        val expectedError = Cek.Error(message = "gagal melakukan penghapusan praktikum")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

}

@ExperimentalCoroutinesApi
class TambahPraktikumViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val mockPraktikumRepo: PraktikumRepo = mockk()
    private lateinit var viewModel: TambahPraktikumViewModel
    private class FakeTimeProvider : TimeProvider {
        override fun getCurrentTimestamp(): Timestamp = Timestamp.now()
        override fun getCalendarInstance(): Calendar = Calendar.getInstance()
    }
    private val fakeTimeProvider = FakeTimeProvider()

    @Before
    fun setUp() {
        viewModel = TambahPraktikumViewModel(mockPraktikumRepo, fakeTimeProvider)
    }

    // --- Skenario Sukses ---
    @Test
    fun `onSubmitHandling - semua input valid, status harus sukses`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.jadwalTimestamp.collect {}
        }

        // Arrange
        viewModel.getNamaDocPrak("Praktikum Mobile")
        viewModel.getJumlahSlot(14)
        viewModel.getTanggal(System.currentTimeMillis())
        viewModel.getJam(10)
        viewModel.getMenit(30)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        // Atur agar repo mengembalikan Sukses
        coEvery { mockPraktikumRepo.tambahPraktikum(any()) } returns Cek.Sukses

        // Act
        viewModel.onSubmitHandling()

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)

        job.cancel()
    }

    // --- Skenario Gagal (Validasi Input) ---
    @Test
    fun `onSubmitHandling - nama praktikum kosong, status harus error`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.jadwalTimestamp.collect {}
        }
        // Arrange
        viewModel.getNamaDocPrak("")
        viewModel.getJumlahSlot(14)
        viewModel.getTanggal(System.currentTimeMillis())
        viewModel.getJam(10)
        viewModel.getMenit(30)
        advanceUntilIdle()

        // Act
        viewModel.onSubmitHandling()

        // Assert
        val expectedError = Cek.Error("Silahkan masukkan nama praktikum")
        assertEquals(expectedError, viewModel.uistatus.value)
        job.cancel()
    }

    @Test
    fun `onSubmitHandling - jumlah pertemuan nol, status harus error`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.jadwalTimestamp.collect {}
        }
        // Arrange
        viewModel.getNamaDocPrak("Praktikum Mobile")
        viewModel.getJumlahSlot(0)
        viewModel.getTanggal(System.currentTimeMillis())
        viewModel.getJam(10)
        viewModel.getMenit(30)
        advanceUntilIdle()

        // Act
        viewModel.onSubmitHandling()

        // Assert
        val expectedError = Cek.Error("Jumlah Pertemuan tidak boleh kosong")
        assertEquals(expectedError, viewModel.uistatus.value)
        job.cancel()
    }
    @Test
    fun `onSubmitHandling - tanggal atau jam belum dipilih, status harus error`() {
        // Arrange
        viewModel.getNamaDocPrak("Praktikum Mobile")
        viewModel.getJumlahSlot(14)
        // Tanggal dan jam tidak diatur (null)

        // Act
        viewModel.onSubmitHandling()

        // Assert
        val expectedError = Cek.Error("Silahkan pilih jam & tanggal terlebih dahulu")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario Gagal (Repository) ---
    @Test
    fun `onSubmitHandling - repository gagal, status harus error`() = runTest {
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.jadwalTimestamp.collect {}
        }
        // Arrange
        viewModel.getNamaDocPrak("Praktikum Mobile")
        viewModel.getJumlahSlot(14)
        viewModel.getTanggal(System.currentTimeMillis())
        viewModel.getJam(10)
        viewModel.getMenit(30)

        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Gagal terhubung ke database"
        // Atur agar repo mengembalikan Error
        coEvery { mockPraktikumRepo.tambahPraktikum(any()) } returns Cek.Error(errorMessage)

        // Act
        viewModel.onSubmitHandling()

        // Assert
        assertEquals(Cek.Error(errorMessage), viewModel.uistatus.value)
        job.cancel()
    }
}

@ExperimentalCoroutinesApi
class TambahMahasiswaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Siapkan mock untuk semua dependensi
    private val mockPraktikumRepo: PraktikumRepo = mockk(relaxUnitFun = true)
    private val mockUserRepo: UserRepo = mockk(relaxUnitFun = true)

    private lateinit var viewModel: TambahMahasiswaViewModel

    @Before
    fun setUp() {

        coEvery { mockUserRepo.searchMahasiswa(any()) } returns flowOf(emptyList())

        viewModel = TambahMahasiswaViewModel(mockPraktikumRepo, mockUserRepo)
    }

    // --- Skenario 1: Sukses Menambah Mahasiswa ---
    @Test
    fun `tambahMahasiswa - saat semua valid, status harus sukses`() = runTest {
        // Arrange
        val idPrak = "prak_mobile"
        val listUidToAdd = listOf("uid_mahasiswa_1")


        val fakePraktikum = Praktikum(uid_asprak = listOf("uid_asprak_1"))
        coEvery { mockPraktikumRepo.getOnePrak(idPrak) } returns fakePraktikum


        coEvery { mockPraktikumRepo.tambahMahasiswaPraktikum(uidMahasiswa = listUidToAdd, idPrak = idPrak) } returns true

        // Act
        viewModel.tambahMahasiswa(idPrak, listUidToAdd)

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
    }

    // --- Skenario 2: Gagal karena UID sudah terdaftar sebagai Asprak ---
    @Test
    fun `tambahMahasiswa - saat UID sudah ada sebagai asprak, status harus error`() = runTest {
        // Arrange
        val idPrak = "prak_mobile"
        val listUidToAdd = listOf("uid_asprak_yang_sama")


        val fakePraktikum = Praktikum(uid_asprak = listOf("uid_asprak_yang_sama"))
        coEvery { mockPraktikumRepo.getOnePrak(idPrak) } returns fakePraktikum

        // Act
        viewModel.tambahMahasiswa(idPrak, listUidToAdd)

        // Assert
        val expectedError = Cek.Error(message = "Uid sudah terdaftar sebgai asprak")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    @Test
    fun `tambahAsprak - saat UID sudah ada sebagai mahasiswa, status harus error`() = runTest {
        // Arrange
        val idPrak = "prak_mobile"
        val listUidToAdd = listOf("uid_mahasiswa_yang_sama")

        // Data praktikum palsu berisi UID yang sama dengan yang akan ditambahkan
        val fakePraktikum = Praktikum(uid_mahasiswa = listOf("uid_mahasiswa_yang_sama"))
        coEvery { mockPraktikumRepo.getOnePrak(idPrak) } returns fakePraktikum

        // Act
        viewModel.tambahAsprak(idPrak, listUidToAdd)

        // Assert
        val expectedError = Cek.Error(message = "Uid sudah terdaftar sebgai mahasiswa")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 3: Gagal karena list mahasiswa yang dipilih kosong ---
    @Test
    fun `tambahMahasiswa - saat list mahasiswa kosong, status harus error`() = runTest {
        // Arrange
        val idPrak = "prak_mobile"
        val emptyList = emptyList<String>()

        // Act
        viewModel.tambahMahasiswa(idPrak, emptyList)

        // Assert
        val expectedError = Cek.Error(message = "Belum ada mahasiswa yang dipilih")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 4: Gagal karena terjadi exception di repository ---
    @Test
    fun `tambahMahasiswa - saat repo gagal menambah, status harus error`() = runTest {
        // Arrange
        val idPrak = "prak_mobile"
        val listUidToAdd = listOf("uid_mahasiswa_1")
        val errorMessage = "Gagal terhubung ke database"

        coEvery { mockPraktikumRepo.getOnePrak(idPrak) } returns Praktikum()
        coEvery { mockPraktikumRepo.tambahMahasiswaPraktikum(uidMahasiswa = listUidToAdd, idPrak = idPrak) } returns false

        // Act
        viewModel.tambahMahasiswa(idPrak, listUidToAdd)

        // Assert
        val expectedError = Cek.Error(message = "Gagal tambah ke praktikum")
        assertEquals(expectedError, viewModel.uistatus.value)
    }
}

@ExperimentalCoroutinesApi
class DetailPraktikumAdminViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    private val mockUserRepo: UserRepo = mockk()
    private val mockPrakRepo: PraktikumRepo = mockk(relaxUnitFun = true)

    private lateinit var viewModel: DetailPraktikumAdminViewModel

    @Before
    fun setUp() {

        coEvery { mockUserRepo.getPeoplePraktikum(any()) } returns flowOf(emptyList())

        viewModel = DetailPraktikumAdminViewModel(mockUserRepo, mockPrakRepo)
    }

    // --- Skenario 1: Sukses Menghapus Mahasiswa ---
    @Test
    fun `getDeleteMahasiswa - saat repo sukses, status tidak error`() = runTest {
        // Arrange
        val uid = "mahasiswa_uid"
        val idPrak = "prak_id"

        coEvery { mockPrakRepo.hapusOrang(idPrak, uid) } returns true

        // Act
        viewModel.getDeleteMahasiswa(uid, idPrak)

        // Assert

        assertNotEquals(Cek.Error("Gagal menghapus"), viewModel.uistatus.value)
    }

    // --- Skenario 2: Gagal Menghapus Mahasiswa dari Repo ---
    @Test
    fun `getDeleteMahasiswa - saat repo gagal, status harus error`() = runTest {
        // Arrange
        val uid = "mahasiswa_uid"
        val idPrak = "prak_id"

        coEvery { mockPrakRepo.hapusOrang(idPrak, uid) } returns false

        // Act
        viewModel.getDeleteMahasiswa(uid, idPrak)

        // Assert
        val expectedError = Cek.Error("Gagal menghapus")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 3: Gagal karena Input UID Kosong ---
    @Test
    fun `getDeleteMahasiswa - saat input uid kosong, status harus error`() {
        // Arrange
        val emptyUid = ""
        val idPrak = "prak_id"

        // Act
        viewModel.getDeleteMahasiswa(emptyUid, idPrak)

        // Assert
        val expectedError = Cek.Error("Gagal Memilih")
        assertEquals(expectedError, viewModel.uistatus.value)


        coVerify(exactly = 0) { mockPrakRepo.hapusOrang(any(), any()) }
    }
}

@ExperimentalCoroutinesApi
class DetailAsprakViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Siapkan mock untuk semua dependensi
    private val mockTimeRepo: TimeRepo = mockk()
    private val mockPraktikumRepo: PraktikumRepo = mockk(relaxUnitFun = true)
    private val mockUserRepo: UserRepo = mockk()

    private lateinit var viewModel: DetailAsprakViewModel

    @Before
    fun setUp() {
        // Atur perilaku default
        coEvery { mockUserRepo.cekUser() } returns true
        coEvery { mockUserRepo.cekCurent() } returns "fake_asprak_uid"
        coEvery { mockUserRepo.getUser(any()) } returns User(role = "asprak")

        viewModel = DetailAsprakViewModel(mockTimeRepo, mockPraktikumRepo, mockUserRepo)
    }

    // --- Skenario 1: Sukses Mengubah Nilai ---
    @Test
    fun `subbitHandling - saat semua data valid, status harus sukses`() = runTest {
        // Arrange
        val idPrak = "prak1"
        val idPertemuan = "pertemuan_01"
        val idMahasiswa = "mahasiswa123"
        val detailPertemuan = DetailPertemuan(uid_mahasiswa = "mahasiswa123")

        viewModel.onUpdateData(idPrak, idPertemuan, idMahasiswa, detailPertemuan)
        viewModel.updateNilai("90")
        viewModel.changeStatusKehadiran(true)

        coEvery { mockPraktikumRepo.updatePraktikan(any(), any(), any(), any()) } returns Unit

        // Act
        viewModel.subbitHandling()

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
    }

    // --- Skenario 2: Gagal karena Data Tidak Lengkap ---
    @Test
    fun `subbitHandling - saat data update tidak lengkap, status harus error`() {
        // Arrange
        viewModel.updateNilai("90")

        // Act
        viewModel.subbitHandling()

        // Assert
        val expectedError = Cek.Error("Gagal mahasiswa tidak ditemukan")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 3: Gagal karena Nilai Kosong/Nol ---
    @Test
    fun `subbitHandling - saat nilai kosong, status harus error`() {
        // Arrange
        val idPrak = "prak1"
        val idPertemuan = "pertemuan_01"
        val idMahasiswa = "mahasiswa123"

        viewModel.onUpdateData(idPrak, idPertemuan, idMahasiswa, DetailPertemuan())
        viewModel.updateNilai("") // <-- Nilai sengaja dikosongkan

        // Act
        viewModel.subbitHandling()

        // Assert
        val expectedError = Cek.Error("Field Tidak Boleh Kosong")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    // --- Skenario 4: Gagal karena Repository Error ---
    @Test
    fun `subbitHandling - saat repo error, status harus error`() = runTest {
        // Arrange
        val idPrak = "prak1"
        val idPertemuan = "pertemuan_01"
        val idMahasiswa = "mahasiswa123"
        val errorMessage = "Database connection failed"

        viewModel.onUpdateData(idPrak, idPertemuan, idMahasiswa, DetailPertemuan())
        viewModel.updateNilai("85")

        coEvery { mockPraktikumRepo.updatePraktikan(any(), any(), any(), any()) } throws Exception(errorMessage)

        // Act
        viewModel.subbitHandling()

        // Assert
        assertEquals(Cek.Error(errorMessage), viewModel.uistatus.value)
    }
}