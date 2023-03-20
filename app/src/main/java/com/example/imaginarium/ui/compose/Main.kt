package com.example.imaginarium.compose

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.imaginarium.R
import com.example.imaginarium.models.SearchBarState
import com.example.imaginarium.navgraphs.DetailsScreen
import com.example.imaginarium.navgraphs.InitScreen
import com.example.imaginarium.navgraphs.MainScreen
import com.example.imaginarium.viewmodels.PhotoViewModel
import com.example.imaginarium.viewmodels.UserInfoViewModel


@Composable
fun Main(
    userInfoViewModel: UserInfoViewModel,
    photoViewModel: PhotoViewModel
) {
    val navController: NavHostController = rememberNavController()
    var topBarTitle by rememberSaveable { mutableStateOf(" ") }
    val searchBarState by photoViewModel.searchBarState
    val searchTextState by photoViewModel.searchTextState

    Scaffold(
        topBar = {
            MainBar(
                navController = navController,
                searchBarState = searchBarState,
                searchTextState = searchTextState,
                onTextChange = { photoViewModel.updateSearchTextState(newValue = it) },
                onCloseClicked = {
                    photoViewModel.updateSearchBarState(newValue = SearchBarState.Closed)
                    photoViewModel.updateSearchTextState(newValue = "")
                },
                onSearchClicked = {
                    navController.navigate(DetailsScreen.SearchView.route + "/$it")
                    photoViewModel.updateSearchBarState(newValue = SearchBarState.Closed)
                },
                onSearchTriggered = { photoViewModel.updateSearchBarState(newValue = SearchBarState.Opened) },
                topBarTitle = topBarTitle,
                userInfoViewModel = userInfoViewModel,
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController)
        }) { innerPadding ->
        NavHost(
            navController,
            startDestination = MainScreen.Photos.route,
            Modifier.padding(innerPadding),
            route = InitScreen.Main.route
        ) {

            composable(MainScreen.Profile.route) {
                ProfileView(photoViewModel, userInfoViewModel, navController)
                topBarTitle = stringResource(R.string.profile)
            }
            composable(MainScreen.Photos.route) {
                HomeView(photoViewModel, navController)
                topBarTitle = stringResource(R.string.todays_top)
            }
            composable(
                DetailsScreen.SearchView.route + "/{query}",
                arguments = listOf(
                    navArgument("query") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    }
                )
            ) { entry ->
                entry.arguments?.getString("query")
                    ?.let { SearchView(photoViewModel, navController, it) }
                topBarTitle = stringResource(R.string.search_results)
            }
            composable(
                route = DetailsScreen.SinglePhoto.route + "/{profileImage}",
                arguments = listOf(
                    navArgument("profileImage") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    },
                ),
            )

            { entry ->
                SinglePhotoView(
                    photoViewModel = photoViewModel,
                    profileImage = entry.arguments?.getString("profileImage"),
                    id = null
                )
                topBarTitle = stringResource(R.string.photo)
            }


            composable(route = DetailsScreen.SinglePhotoDeepLink.route + "/{photoId}",
                arguments = listOf(
                    navArgument("photoId") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    },
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://unsplash.com/photos/{photoId}"
                        action = Intent.ACTION_VIEW
                    }
                )
            )
            { entry ->
                SinglePhotoViewDeepLink(
                    photoViewModel = photoViewModel,
                    id = entry.arguments?.getString("photoId")
                )
                topBarTitle = stringResource(R.string.photo)
            }

            composable(
                DetailsScreen.SingleCollection.route + "/{username}/{profileImage}",
                arguments = listOf(
                    navArgument("username") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    },
                    navArgument("profileImage") {
                        type = NavType.StringType
                        defaultValue = ""
                        nullable = true
                    },
                )
            ) { entry ->
                SingleCollectionView(
                    username = entry.arguments?.getString("username"),
                    navController = navController,
                    photoViewModel = photoViewModel
                )

                topBarTitle = stringResource(R.string.collection)

            }


            composable(DetailsScreen.Collections.route) {
                CollectionsView(photoViewModel, navController)
                topBarTitle = stringResource(R.string.collections)
            }

        }
    }
}

@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.secondary
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = { onTextChange(it) },

            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(modifier = Modifier.alpha(ContentAlpha.medium),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    onCloseClicked()
                }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Black,

                        )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchClicked(text) }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.6f),
                cursorColor = Color.Black,
                textColor = Color.Black
            )
        )
    }
}

@Composable
fun MainBar(
    userInfoViewModel: UserInfoViewModel,
    searchBarState: SearchBarState,
    searchTextState: String,
    navController: NavHostController,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    topBarTitle: String
) {


    when (searchBarState) {
        SearchBarState.Closed -> {
            TopBar(
                topBarTitle = topBarTitle,
                onSearchClicked = onSearchTriggered,
                userInfoViewModel = userInfoViewModel,
                navController = navController
            )
        }

        SearchBarState.Opened -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }

}

@Composable
fun TopBar(
    topBarTitle: String,
    userInfoViewModel: UserInfoViewModel,
    navController: NavHostController,
    onSearchClicked: () -> Unit
) {
    val openDialog = remember {
        mutableStateOf(false)
    }
    TopAppBar(


        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = topBarTitle,
                    color = Color.Black
                )
                if (topBarTitle == stringResource(R.string.profile)) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Image(painter = painterResource(R.drawable.logout_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .padding(end = 8.dp)
                                .clickable {
                                    openDialog.value = true

                                })
                    }
                }
            }
        },
        actions = {
            if (topBarTitle != stringResource(id = R.string.profile)) {
                IconButton(onClick = { onSearchClicked() }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        },
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = MaterialTheme.colors.onSecondary,
    )

    if (openDialog.value) {

        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.logout))
            },
            text = {
                Text(stringResource(R.string.really_log_out))
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                        userInfoViewModel.logout()
                        openDialog.value = false
                    }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}


@Composable
fun BottomNavBar(navController: NavHostController) {

    val items = listOf(
        MainScreen.Photos,
        MainScreen.Collections,
        MainScreen.Profile
    )

    BottomNavigation(backgroundColor = MaterialTheme.colors.primary) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = screen.resourceId), contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(24.dp)
                    .height(24.dp)
                    .background(color = MaterialTheme.colors.primary),
                selectedContentColor = MaterialTheme.colors.onBackground,
                unselectedContentColor = MaterialTheme.colors.onBackground.copy(0.4f),
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


