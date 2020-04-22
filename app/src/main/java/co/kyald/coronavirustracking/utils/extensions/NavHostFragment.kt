package co.kyald.coronavirustracking.utils.extensions

import androidx.navigation.fragment.NavHostFragment

fun NavHostFragment.setNavigationGraph(graph: Int) {
    navController.graph = navController.navInflater.inflate(graph)
}