/**
 * @author Nikolaus Knop
 */

package org.fos.gui

import javafx.scene.control.*


/**
 * Return a [MenuBar] configured with the given [builder].
 */
inline fun menuBar(builder: MenuBarBuilder.() -> Unit) = MenuBarBuilder().apply(builder).build()

/**
 * Builder class for [MenuBar]s
 */
class MenuBarBuilder {
    private val menus = mutableListOf<Menu>()

    /**
     * Add the given [menu]
     */
    fun menu(menu: Menu) {
        menus.add(menu)
    }

    /**
     * Add a menu with the given [name] and configure it with the given [block].
     */
    inline fun menu(name: String, block: MenuBuilder.() -> Unit) {
        menu(MenuBuilder(name).apply(block).build())
    }

    @PublishedApi internal fun build() = MenuBar(*menus.toTypedArray())
}

/**
 * Builder class for [Menu]s
 */
class MenuBuilder(private val name: String) {
    private var items = mutableListOf<MenuItem>()

    /**
     * Add an item with the specified [name], which executes the given [action] when clicked.
     */
    fun item(name: String, action: () -> Unit) {
        val item = MenuItem(name)
        item.setOnAction { action() }
        items.add(item)
    }

    @PublishedApi internal fun build() = Menu(name, null, *items.toTypedArray())
}