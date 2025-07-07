package com.example.reglab7firebase

import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.EmailValidator
import com.example.reglab7firebase.view.signup.ViewModelSignUp
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class SignUpTest {

    // Siapkan dependensi palsu (mock & fake)
    private val mockUserRepo: UserRepo = mockk(relaxUnitFun = true)
    private val fakeEmailValidator = FakeEmailValidator()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ViewModelSignUp

    private class FakeEmailValidator : EmailValidator {
        var shouldBeValid = true
        override fun isValid(email: String): Boolean {
            return shouldBeValid
        }
    }

    @Before
    fun setUp() {
        viewModel = ViewModelSignUp(
            auth = mockUserRepo,
            emailValidator = fakeEmailValidator
        )
    }

    @Test
    fun `onClickHandling - semua input valid, registrasi seharusnya sukses`() = runTest {
        // Arrange
        fakeEmailValidator.shouldBeValid = true
        viewModel.Update("test@example.com")
        viewModel.UpdatePas("password123")
        viewModel.UpdaterePas("password123")
        viewModel.getNim("12345678")

        coEvery { mockUserRepo.cekNimUser("12345678") } returns emptyList()
        coEvery { mockUserRepo.Register(any(), any(), any()) } returns mockk<FirebaseUser>()

        // Act
        viewModel.onClickHandling()

        // Assert
        assertEquals(Cek.Sukses, viewModel.stateceksignup.value)
    }

    @Test
    fun `onClickHandling - input kosong, harus mengembalikan error`() = runTest {
        // Arrange
        viewModel.Update("") // Email kosong

        // Act
        viewModel.onClickHandling()

        // Assert
        val expectedError = Cek.Error(message = "kolom email atau password tidak boleh kosong")
        assertEquals(expectedError, viewModel.stateceksignup.value)
    }

    @Test
    fun `onClickHandling - format email salah, harus mengembalikan error`() = runTest {
        // Arrange
        fakeEmailValidator.shouldBeValid = false
        viewModel.Update("email.salah")
        viewModel.UpdatePas("password123")
        viewModel.UpdaterePas("password123")

        // Act
        viewModel.onClickHandling()

        // Assert
        val expectedError = Cek.Error(message = "Format email tidak benar")
        assertEquals(expectedError, viewModel.stateceksignup.value)
    }

    @Test
    fun `onClickHandling - password terlalu pendek, harus mengembalikan error`() = runTest {
        // Arrange
        fakeEmailValidator.shouldBeValid = true
        viewModel.Update("test@example.com")
        viewModel.getNim("12345678")
        viewModel.UpdatePas("123") // Password pendek
        viewModel.UpdaterePas("123")

        // Act
        viewModel.onClickHandling()

        // Assert
        val expectedError = Cek.Error(message = "Password tidak boleh kurang dari 8")
        assertEquals(expectedError, viewModel.stateceksignup.value)
    }

    @Test
    fun `onClickHandling - password tidak cocok, harus mengembalikan error`() = runTest {
        // Arrange
        fakeEmailValidator.shouldBeValid = true
        viewModel.Update("test@example.com")
        viewModel.UpdatePas("password123")
        viewModel.getNim("12345678")
        viewModel.UpdaterePas("password456") // Password tidak cocok

        // Act
        viewModel.onClickHandling()

        // Assert
        val expectedError = Cek.Error(message = "password harus sama")
        assertEquals(expectedError, viewModel.stateceksignup.value)
    }

    // --- SKENARIO GAGAL (LOGIKA BISNIS) ---

    @Test
    fun `onClickHandling - NIM sudah terdaftar, harus mengembalikan error`() = runTest {
        // Arrange
        fakeEmailValidator.shouldBeValid = true
        viewModel.Update("test@example.com")
        viewModel.UpdatePas("password123")
        viewModel.UpdaterePas("password123")
        viewModel.getNim("12345678")

        // Atur agar repo mengembalikan data bahwa NIM sudah ada
        coEvery { mockUserRepo.cekNimUser("12345678") } returns listOf(User())

        // Act
        viewModel.onClickHandling()

        // Assert
        val expectedError = Cek.Error(message = "NIM sudah ada")
        assertEquals(expectedError, viewModel.stateceksignup.value)
    }

    @Test
    fun `onClickHandling - repository gagal mendaftar, harus mengembalikan error`() = runTest {
        // Arrange
        fakeEmailValidator.shouldBeValid = true
        viewModel.Update("test@example.com")
        viewModel.UpdatePas("password123")
        viewModel.UpdaterePas("password123")
        viewModel.getNim("12345678")

        val errorMessage = "Email sudah terdaftar"
        coEvery { mockUserRepo.cekNimUser("12345678") } returns emptyList()
        // Atur agar repo melempar exception saat register
        coEvery { mockUserRepo.Register(any(), any(), any()) } throws Exception(errorMessage)

        // Act
        viewModel.onClickHandling()

        // Assert
        val expectedError = Cek.Error(message = errorMessage)
        assertEquals(expectedError, viewModel.stateceksignup.value)
    }
}