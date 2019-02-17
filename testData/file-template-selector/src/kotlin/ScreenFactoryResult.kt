package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.Route
import com.github.rougsig.filetemplateloader.ScreenKey1
import com.github.rougsig.filetemplateloader.ScreenKey2
import com.github.rougsig.filetemplateloader.ScreenKey3

class ScreenFactory : Function1<Route, Key> {
  override fun invoke(route: Route): Key {
    return when (route) {
      is Route.Key1 -> ScreenKey1()
      is Route.Key2 -> ScreenKey2()
      is Route.Key3 -> ScreenKey3()
      is Route.Key4 -> ScreenKey4()
    }
  }
}
