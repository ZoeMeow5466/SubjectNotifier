package io.zoemeow.dutnotify.view.account

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.zoemeow.dutnotify.MainActivity
import io.zoemeow.dutnotify.model.enums.AccountServiceCode
import io.zoemeow.dutnotify.model.enums.LoginState
import io.zoemeow.dutnotify.service.AccountService
import io.zoemeow.dutnotify.viewmodel.MainViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AccountDialogLogin(
    enabled: MutableState<Boolean>,
    mainViewModel: MainViewModel,
) {
    val passTextFieldFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current as MainActivity

    val enabledControl: MutableState<Boolean> = remember { mutableStateOf(true) }
    val username: MutableState<String> = remember { mutableStateOf("") }
    val password: MutableState<String> = remember { mutableStateOf("") }
    val rememberLogin: MutableState<Boolean> = remember { mutableStateOf(false) }

    fun login() {
        focusManager.clearFocus()
        // mainViewModel.accountDataStore.login(
        //     username = username.value,
        //     password = password.value,
        //     remembered = rememberLogin.value
        // )

        Intent(context, AccountService::class.java).apply {
            putExtra(AccountServiceCode.ACTION, AccountServiceCode.ACTION_LOGIN)
            putExtra(AccountServiceCode.ARGUMENT_LOGIN_USERNAME, username.value)
            putExtra(AccountServiceCode.ARGUMENT_LOGIN_PASSWORD, password.value)
            putExtra(AccountServiceCode.ARGUMENT_LOGIN_REMEMBERED, rememberLogin.value)
            putExtra(AccountServiceCode.ARGUMENT_LOGIN_PRELOAD, true)
        }.also {
            context.startService(it)
        }
    }

    @Composable
    fun LoggingIn(loginState: LoginState) {
        when (loginState) {
            LoginState.LoggingIn -> {
                Text("We are logging you in. Please wait...")
            }
            LoginState.NotLoggedIn, LoginState.NotLoggedInButRemembered -> {
                Text(
                    "Something went wrong with your account! " +
                            "Make sure your username and password is correct." +
                            "\nIf everything is ok, just try again, or check your internet connection."
                )
            }
            else -> {

            }
        }
    }

    LaunchedEffect(enabled.value) {
        if (enabled.value) {
            enabledControl.value = true
            username.value = ""
            password.value = ""
            rememberLogin.value = false
        }
    }

    LaunchedEffect(mainViewModel.Account_LoginProcess.value) {
        when (mainViewModel.Account_LoginProcess.value) {
            LoginState.NotLoggedIn, LoginState.NotLoggedInButRemembered, LoginState.NotTriggered -> {
                // Enable controls again
                enabledControl.value = true
                // TODO: Notify login failed here!
            }
            // If is logging in
            LoginState.LoggingIn -> {
                // Disable controls here to avoid editing
                enabledControl.value = false
            }
            // Successfully logged in!
            LoginState.LoggedIn -> {
                enabled.value = false
                // Enable controls again
                enabledControl.value = true
            }
            // Account locked
            LoginState.AccountLocked -> {
                // Enable controls again
                enabledControl.value = true
            }
        }
    }

    // Alert dialog for login
    if (enabled.value) {
        AlertDialog(
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onDismissRequest = {
                if (enabledControl.value)
                    enabled.value = false

                username.value = ""
                password.value = ""
                rememberLogin.value = false

            },
            title = {
                Text("Login")
            },
            dismissButton = {
                TextButton(
                    // enabled = enabledControl.value,
                    onClick = {
                        enabled.value = false
                    },
                    content = {
                        Text("Cancel")
                    }
                )
            },
            confirmButton = {
                TextButton(
                    enabled = enabledControl.value,
                    onClick = {
                        login()
                    },
                    content = {
                        Text("Login")
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabledControl.value,
                        value = username.value,
                        onValueChange = { username.value = it },
                        label = { Text("Username") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { passTextFieldFocusRequester.requestFocus() }
                        ),
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passTextFieldFocusRequester),
                        enabled = enabledControl.value,
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                login()
                            }
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                rememberLogin.value = !rememberLogin.value
                            }
                    ) {
                        Checkbox(
                            checked = rememberLogin.value,
                            onCheckedChange = { rememberLogin.value = it },
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        Text("Remember your login")
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    LoggingIn(mainViewModel.Account_LoginProcess.value)
                }
            }
        )
    }
}