package org.github.jamie.buckley.math

fun sigmoid(x: Double): Double {
    return 1 / (1 + Math.pow(Math.E, -1 * x))
}