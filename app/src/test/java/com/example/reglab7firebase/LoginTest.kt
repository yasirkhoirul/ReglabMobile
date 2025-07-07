package com.example.reglab7firebase

import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.util.EmailValidator
import com.example.reglab7firebase.view.login.ViewModelLogin
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class LoginTest {
    private val mockUserRepo: UserRepo = mockk(relaxUnitFun = true)
    private lateinit var viewModel: ViewModelLogin
    private class FakeEmailValidator : EmailValidator {
        var shoudBeValid = true
        override fun isValid(email: String): Boolean {
            return shoudBeValid
        }
    }
    private val fakeEmailValidator = FakeEmailValidator()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp(){
        viewModel = ViewModelLogin(
            userRepo = mockUserRepo,
            emailValidator = fakeEmailValidator,
            dispatcher = mainDispatcherRule.testDispatcher
        )
    }

    @Test
    fun `onclickHandling login sukses sebagai user biasa, set isAdmin false dan status sukses`() =
        runTest {
            //Arrange
            val email = "user@test.com"
            val password = "password"
            fakeEmailValidator.shoudBeValid = true
            viewModel.UpdateName(email)
            viewModel.UpdatePassword(password)


            val fakeLoginResult = mockk<FirebaseUser> { coEvery { uid } returns "user_uid" }
            val fakeUserResult = User(role = "mahasiswa")
            coEvery { mockUserRepo.login(email, password) } returns fakeLoginResult
            coEvery { mockUserRepo.getUser("user_uid") } returns fakeUserResult

            // ACT
            viewModel.onclickHandling()
            advanceUntilIdle()

            // ASSERT
            assertEquals(Cek.Sukses, viewModel.uistatus.value)
            assertFalse(viewModel.uiIsAdmin.value)
        }

    @Test
    fun `onclickHandling - login sukses sebagai admin, set isAdmin true dan status sukses`() = runTest {
        // Arrange
        val email = "admin@test.com"
        val password = "password"
        fakeEmailValidator.shoudBeValid = true
        viewModel.UpdateName(email)
        viewModel.UpdatePassword(password)

        val fakeLoginResult = mockk<FirebaseUser> { coEvery { uid } returns "admin_uid" }
        val fakeUserResult = User(role = "admin")
        coEvery { mockUserRepo.login(email, password) } returns fakeLoginResult
        coEvery { mockUserRepo.getUser("admin_uid") } returns fakeUserResult

        // Act
        viewModel.onclickHandling()

        // Assert
        assertEquals(Cek.Sukses, viewModel.uistatus.value)
        assertTrue(viewModel.uiIsAdmin.value)
    }

    @Test
    fun `onclickHandling - login gagal karena user tidak terdaftar, set status error`() = runTest {
        // Arrange
        val email = "unknown@test.com"
        val password = "password"
        fakeEmailValidator.shoudBeValid = true
        viewModel.UpdateName(email)
        viewModel.UpdatePassword(password)

        // Atur agar repository melempar error saat login dipanggil
        coEvery { mockUserRepo.login(email, password) } throws Exception("USER_NOT_FOUND")

        // Act
        viewModel.onclickHandling()

        // Assert
        val expectedError = Cek.Error(message = "Gagal melakukan login cek kembali email dan password")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    @Test
    fun `onclickHandling - input email kosong, set status error`() = runTest {
        // Arrange
        viewModel.UpdateName("") // Email dikosongkan
        viewModel.UpdatePassword("password")
        fakeEmailValidator.shoudBeValid = true

        // Act
        viewModel.onclickHandling()

        // Assert
        val expectedError = Cek.Error(message = "password atau email tidak boleh kososng")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

    @Test
    fun `onclickHandling - format email salah, set status error`() = runTest {
        // Arrange
        fakeEmailValidator.shoudBeValid = false // Atur validator untuk gagal
        viewModel.UpdateName("bukanemail")
        viewModel.UpdatePassword("password")

        // Act
        viewModel.onclickHandling()

        // Assert
        val expectedError = Cek.Error(message = "email tidak valid")
        assertEquals(expectedError, viewModel.uistatus.value)
    }

}