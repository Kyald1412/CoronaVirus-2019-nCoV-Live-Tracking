package co.kyald.coronavirustracking.utils.extensions

import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView


fun BottomNavigationView.getMenuView(): BottomNavigationMenuView =
    this.getChildAt(0) as BottomNavigationMenuView

fun BottomNavigationView.getTabView(position: Int): BottomNavigationItemView =
    this.getMenuView().getChildAt(position) as BottomNavigationItemView