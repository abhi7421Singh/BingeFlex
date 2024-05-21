package com.example.bingeflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bingeflex.model.MoviesSchema
import com.example.bingeflex.ui.theme.BingeFlexTheme
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {
    private val retrofit: Retrofit by lazy {
        // Create an OkHttpClient to be able to make a log of the network traffic
        val httpClient = OkHttpClient.Builder().run {
            addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            })
            build()
        }
        // Create an instance of Retrofit
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
    }

    private val moviesApiService: MoviesApiService by lazy {
        retrofit.create(MoviesApiService::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BingeFlexTheme {
                val parentNavController = rememberNavController()
                val currentNavController = remember {
                    mutableStateOf(parentNavController)
                }
                Scaffold(
                    topBar = {
                        MyTopAppBar(
                            currentNavController = currentNavController.value,
                            parentNavController = parentNavController,
                        )
                    },
                    bottomBar = {
                        BottomAppBar(modifier = Modifier){
                            MyBottomTabsBar(parentController = parentNavController)
                        }
                    },
                    content = { padding ->
                        MyContent(
                            padding = padding,
                            parentNavController = parentNavController,
                            moviesApiService = moviesApiService,
                            currentNavController = currentNavController,
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BingeFlexTheme {
        Greeting("Android")
    }
}

sealed class Route(val routeName: String, val bottomTab: BottomTab) {
    data object MainTab: Route("mainTab", BottomTab.Main)
    data object MoviesListScreen: Route("moviesList", BottomTab.Main)
    data object MovieDetailsScreen: Route("movieDetails/{movieId}/", BottomTab.Main)
    data object SearchTab: Route("searchTab", BottomTab.Search)
    data object SearchMoviesListScreen: Route("favorites", BottomTab.Search)

    data object RecommendationTab: Route("recommendationTab", BottomTab.Recommendation)

    data object ProfileTab: Route("profileTab", BottomTab.Profile)
}

sealed class BottomTab(val icon: ImageVector?, var title: String) {
    data object Main : BottomTab(Icons.Rounded.Home, "Home")
    data object Search : BottomTab(Icons.Rounded.Search, "Search")
    data object Recommendation : BottomTab(Icons.Rounded.Favorite, "Wishlist")
    data object Profile : BottomTab(Icons.Rounded.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    currentNavController: NavHostController,
    parentNavController: NavHostController,
) {
    val scope = rememberCoroutineScope()
    val backstackEntryState = currentNavController.currentBackStackEntryAsState()
    val isRootRoute = remember(backstackEntryState.value) {
        backstackEntryState.value?.destination?.route == Route.MainTab.routeName
    }

    CenterAlignedTopAppBar(
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            if (!isRootRoute) {
                IconButton(
                    onClick = {
                        if (!currentNavController.popBackStack()) {
                            parentNavController.popBackStack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "menu items"
                    )
                }
            }
        },
        actions = {},
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.primary),
    )
}


@Composable
private fun MyBottomTabsBar(parentController: NavHostController) {
    val bottomTabsToRootRoutes = remember {
        mapOf(
            BottomTab.Main to Route.MainTab,
            BottomTab.Search to Route.SearchTab,
            BottomTab.Recommendation to Route.RecommendationTab,
            BottomTab.Profile to Route.ProfileTab,
        )
    }
    val navBackStackEntry = parentController.currentBackStackEntryAsState()
    val currentRoute = remember(navBackStackEntry) {
        when (val currRouteName = navBackStackEntry.value?.destination?.route) {
            Route.MoviesListScreen.routeName -> Route.MoviesListScreen
            Route.MovieDetailsScreen.routeName -> Route.MovieDetailsScreen
            Route.SearchMoviesListScreen.routeName -> Route.SearchMoviesListScreen
            Route.MainTab.routeName -> Route.MainTab
            Route.SearchTab.routeName -> Route.SearchTab
            Route.RecommendationTab.routeName -> Route.RecommendationTab
            Route.ProfileTab.routeName -> Route.ProfileTab
            null -> null
            else -> throw IllegalArgumentException("Unknown route: $currRouteName")
        }
    }

    NavigationBar {
        bottomTabsToRootRoutes.keys.forEachIndexed{ _, bottomTab ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(bottomTab.icon!!, contentDescription = bottomTab.title) },
                label = { Text(bottomTab.title) },
                selected = currentRoute?.bottomTab == bottomTab,
                onClick = {
                    parentController.navigate(bottomTabsToRootRoutes[bottomTab]!!.routeName){
                        parentController.graph.startDestinationRoute?.let { startRoute ->
                            popUpTo(startRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun MyContent(
    padding: PaddingValues,
    parentNavController: NavHostController,
    moviesApiService: MoviesApiService,
    currentNavController: MutableState<NavHostController>,
){
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = parentNavController,
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            startDestination = Route.MainTab.routeName,
        ) {
            composable(route = Route.MainTab.routeName){
                val nestedNavController = rememberNavController()
                currentNavController.value = nestedNavController
                NavHost(
                    navController = nestedNavController,
                    startDestination = Route.MoviesListScreen.routeName,
                ) {
                    composable(route = Route.MoviesListScreen.routeName){
                        MoviesListScreen(
                            moviesApiService = moviesApiService,
                            onMovieClick = { movieId, movieTitle ->
                                nestedNavController.navigate(Route.MovieDetailsScreen.routeName
                                    .replace("{movieId}", movieId)
                                    .replace("{movieTitle}", movieTitle)
                                )
                            },
                        )
                    }
                    composable(route = Route.MovieDetailsScreen.routeName){ backStackEntry ->
                        MovieDetailsScreen(
                            movieId = backStackEntry.arguments?.getString("movieId")!!,
                            moviesApiService = moviesApiService,
                            navController = nestedNavController
                        )
                    }
                }
            }

            composable(route = Route.SearchTab.routeName) {
                val nestedNavController = rememberNavController()
                currentNavController.value = nestedNavController
                NavHost(navController = nestedNavController, startDestination = Route.SearchMoviesListScreen.routeName) {
                    composable(route = Route.SearchMoviesListScreen.routeName) {
                        SearchMoviesListScreen(
                            navController = nestedNavController
                        )
                    }
                    composable(route = Route.MovieDetailsScreen.routeName) { backStackEntry ->
                        MovieDetailsScreen(
                            movieId = backStackEntry.arguments?.getString("movieId")!!,
                            moviesApiService = moviesApiService,
                            navController = nestedNavController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoviesListScreen(
    moviesApiService: MoviesApiService,
    onMovieClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var movies by remember { mutableStateOf<List<MoviesSchema>>(listOf()) }

    LaunchedEffect(Unit) {
        movies = moviesApiService.getTrendingMovies(20)!!.results
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 5.dp)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
    ) {
        items(movies.size) { index ->
            val movie = movies[index]
            MovieItem(
                movieTitle = movie.title,
                onMovieClicked = { onMovieClick(movie.imdbId ?: "todo", movie.title) },
            )
            if (index < movies.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    thickness = 2.dp
                )
            }
        }
    }
}


@Composable
fun MovieItem(
    movieTitle: String,
    onMovieClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {
                onMovieClicked()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Text(
                modifier = Modifier
                    .weight(1.8f),
                text = movieTitle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun MovieDetailsScreen(
    movieId: String,
    moviesApiService: MoviesApiService,
    navController: NavHostController,
) {
    var questionDetails by remember { mutableStateOf<MoviesSchema?>(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        try {
            questionDetails = moviesApiService.getTrendingMovies(2).results[0]
        } catch (e: Exception) {
            isError = true
        }
    }

    val scrollState = rememberScrollState()

    if (questionDetails != null) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            questionDetails?.let {

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = it.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = it.overview,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (isError) {
        AlertDialog(
            text = {
                Text("Ooops, something went wrong")
            },
            onDismissRequest = {
                navController.popBackStack()
            },
            confirmButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
fun SearchMoviesListScreen(
    navController: NavHostController,
) {
    val favorites = emptyList<MoviesSchema>()

    if (favorites.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
        ) {
            items(favorites.size) { index ->
                val favoriteMovie = favorites[index]
                MovieItem(
                    movieTitle = favoriteMovie.title,
                    onMovieClicked = {
                        navController.navigate(
                            Route.MovieDetailsScreen.routeName
                                .replace("{movieTitle}", favoriteMovie.title)
                        )
                    },
                )
                if (index < favorites.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 20.dp),
                        thickness = 2.dp
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "No favorites",
            )
        }

    }

}