package com.example.auth.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.common_compose.theme.ButtonColors
import com.example.common_compose.theme.CommonText
import com.example.common_compose.theme.HeaderText
import com.example.common_compose.theme.HelperTextColor
import com.example.common_compose.theme.RobotoFontFamily
import com.example.common_compose.theme.SimbirSoftMobileTheme
import com.example.common_compose.theme.TextFieldColors
import com.example.common_view.R
import com.example.auth.R as authR
import com.example.common_view.R as commonR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    sideEffect: AuthSideEffect,
    onSuccessAuth: () -> Unit,
) {
    val context = LocalContext.current
    val forgetPassword = stringResource(id = authR.string.forget_password)
    val registration = stringResource(id = authR.string.registration)
    when (sideEffect) {
        AuthSideEffect.NavigateToContent -> {
            SideEffect {
                onSuccessAuth()
            }
        }

        is AuthSideEffect.NavigateToForgetPassword -> {
            SideEffect {
                Toast.makeText(
                    context,
                    forgetPassword,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        is AuthSideEffect.NavigateToRegistration -> {
            SideEffect {
                Toast.makeText(
                    context,
                    registration,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        AuthSideEffect.NoEffect -> {}
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
                        contentDescription = "",
                    )
                },
                title = {
                    HeaderText(
                        text = stringResource(id = authR.string.authorization),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = authR.dimen.auth_padding_horizontal),
                    vertical = dimensionResource(id = authR.dimen.auth_padding_vertical)
                ),
            ) {
                CommonText(
                    text = stringResource(id = authR.string.login_via_socials),
                    color = colorResource(id = R.color.black70),
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = authR.dimen.auth_elements_margin_top)),
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = commonR.drawable.ic_vk),
                        contentDescription = "VK",
                        modifier = Modifier
                            .size(dimensionResource(id = authR.dimen.auth_icon_size))
                            .weight(1f),
                    )
                    Image(
                        imageVector = ImageVector.vectorResource(id = commonR.drawable.ic_fb),
                        contentDescription = "Facebook",
                        modifier = Modifier
                            .size(dimensionResource(id = authR.dimen.auth_icon_size))
                            .weight(1f),
                    )
                    Image(
                        imageVector = ImageVector.vectorResource(id = commonR.drawable.ic_ok),
                        contentDescription = "OK",
                        modifier = Modifier
                            .size(dimensionResource(id = authR.dimen.auth_icon_size))
                            .weight(1f),
                    )
                }

                CommonText(
                    text = stringResource(id = authR.string.login_through_app),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.black70),
                    modifier = Modifier
                        .padding(top = dimensionResource(id = authR.dimen.auth_section_margin_top)),

                    )

                TextField(
                    value = state.email.ifBlank { stringResource(id = authR.string.enter_email) },
                    onValueChange = { onEvent(AuthEvent.Ui.UpdateEmail(it)) },
                    colors = TextFieldColors,
                    label = {
                        CommonText(
                            text = stringResource(id = authR.string.email),
                            color = HelperTextColor
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = authR.dimen.auth_elements_margin_top)),
                )

                TextField(
                    value = state.password,
                    onValueChange = { onEvent(AuthEvent.Ui.UpdatePassword(it)) },
                    colors = TextFieldColors,
                    label = {
                        CommonText(
                            text = if (state.password.isNotBlank())
                                stringResource(id = authR.string.password)
                            else stringResource(
                                id = authR.string.enter_password
                            ),
                            color = HelperTextColor
                        )
                    },
                    trailingIcon = {
                        Image(
                            imageVector = ImageVector.vectorResource(
                                id = if (state.showPassword)
                                    commonR.drawable.ic_hide_password
                                else
                                    commonR.drawable.ic_show_password
                            ),
                            contentDescription = stringResource(
                                id = if (state.showPassword)
                                    authR.string.hide_password
                                else
                                    authR.string.show_password
                            ),
                            modifier = Modifier.clickable { onEvent(AuthEvent.Ui.ChangePasswordVisibility) }
                        )
                    },
                    visualTransformation = if (state.showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = authR.dimen.auth_elements_margin_top)),
                )

                Button(
                    onClick = {
                        onEvent(AuthEvent.Ui.Authenticate)
                    },
                    enabled = state.isAuthClickable,
                    colors = ButtonColors(),
                    elevation = ButtonDefaults.elevatedButtonElevation(dimensionResource(id = authR.dimen.button_elevation)),
                    shape = RoundedCornerShape(dimensionResource(id = authR.dimen.button_corners)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = authR.dimen.auth_elements_margin_top)),
                ) {
                    CommonText(
                        text = stringResource(id = authR.string.login).uppercase(),
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = authR.dimen.auth_elements_margin_top)),
                ) {
                    ClickableText(
                        text = AnnotatedString(stringResource(id = authR.string.forget_password)),
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            textAlign = TextAlign.Start,
                            fontFamily = RobotoFontFamily,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier.weight(1f),
                    ) {
                        onEvent(AuthEvent.Ui.NavigateToForgetPassword)
                    }
                    ClickableText(
                        text = AnnotatedString(stringResource(id = authR.string.registration)),
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            fontFamily = RobotoFontFamily,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier.weight(1f),
                    ) {
                        onEvent(AuthEvent.Ui.NavigateToRegistration)
                    }
                }
            }
        }
    }
}

@Preview(locale = "ru")
@Composable
private fun AuthScreenPreview() {
    SimbirSoftMobileTheme {
        AuthScreen(
            modifier = Modifier,
            state = AuthState(),
            onEvent = {},
            sideEffect = AuthSideEffect.NoEffect,
            {}
        )
    }
}
