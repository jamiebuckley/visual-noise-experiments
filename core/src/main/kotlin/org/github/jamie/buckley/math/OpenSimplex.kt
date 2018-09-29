package org.github.jamie.buckley.math

import kotlin.math.floor

/**
 * Kotlin translation from Kurt Spencer's OpenSimplexNoise.java
 * https://gist.github.com/KdotJPG/b1270127455a94ac5d19
 */
class OpenSimplex(val seed: Long) {

    /**
     * Inner helper class to hold 2D vectors
     */
    private data class Vector2(val x: Double, val y: Double)

    /**
     * Directions from center of octagon to its vertices
     */
    private val gradients = arrayOf(
            Vector2(5.0, 2.0),
            Vector2(2.0, 5.0),
            Vector2(-5.0, 2.0),
            Vector2(-2.0, 5.0),
            Vector2(5.0, -2.0),
            Vector2(2.0, -5.0),
            Vector2(-5.0, -2.0),
            Vector2(-2.0, -5.0)
    )

    private val STRETCH_CONSTANT_2D = -0.211324865405187
    private val SQUISH_CONSTANT_2D = 0.366025403784439
    private val NORM_CONSTANT_2D = 47

    /**
     * Pseudo random permutation
     */
    fun permute(value: Long): Long {
        return value * 6364136223846793005 + 1442695040888963407
    }

    val perm: MutableList<Int>
    init {
        val source = IntRange(0, 255).toMutableList()
        perm = IntArray(256).toMutableList()
        var newSeed = seed
        for (i in 0..3) newSeed = permute(newSeed)
        for(i in 255 downTo 0) {
            newSeed = permute(newSeed)
            val r = (newSeed + 31).rem(i + 1)
                    .let { if(it < 0) it + i + 1 else it }.toInt()
            perm[i] = source[r]
            source[r] = source[i]
        }
    }

    fun eval(x: Double, y: Double): Double {
        val stretchOffset = (x + y) * STRETCH_CONSTANT_2D
        val xs = x + stretchOffset
        val ys = y + stretchOffset

        var xsb =  floor(xs).toInt()
        var ysb = floor(ys).toInt()

        val squishOffset = (xsb + ysb) * SQUISH_CONSTANT_2D
        val xb = xsb + squishOffset
        val yb = ysb + squishOffset

        val xins = xs - xsb
        val yins = ys - ysb

        val inSum = xins + yins

        //Positions relative to origin point.
        var dx0 = x - xb
        var dy0 = y - yb

        //We'll be defining these inside the next block and using them afterwards.
        val dx_ext: Double
        val dy_ext: Double
        val xsv_ext: Int
        val ysv_ext: Int

        var value = 0.0

        //Contribution (1,0)
        val dx1 = dx0 - 1 - SQUISH_CONSTANT_2D
        val dy1 = dy0 - 0 - SQUISH_CONSTANT_2D
        var attn1 = 2.0 - dx1 * dx1 - dy1 * dy1
        if (attn1 > 0) {
            attn1 *= attn1
            value += attn1 * attn1 * extrapolate(xsb + 1, ysb + 0, dx1, dy1)
        }

        //Contribution (0,1)
        val dx2 = dx0 - 0 - SQUISH_CONSTANT_2D
        val dy2 = dy0 - 1 - SQUISH_CONSTANT_2D
        var attn2 = 2.0 - dx2 * dx2 - dy2 * dy2
        if (attn2 > 0) {
            attn2 *= attn2
            value += attn2 * attn2 * extrapolate(xsb + 0, ysb + 1, dx2, dy2)
        }

        if (inSum <= 1) { //We're inside the triangle (2-Simplex) at (0,0)
            val zins = 1 - inSum
            if (zins > xins || zins > yins) { //(0,0) is one of the closest two triangular vertices
                if (xins > yins) {
                    xsv_ext = xsb + 1
                    ysv_ext = ysb - 1
                    dx_ext = dx0 - 1
                    dy_ext = dy0 + 1
                } else {
                    xsv_ext = xsb - 1
                    ysv_ext = ysb + 1
                    dx_ext = dx0 + 1
                    dy_ext = dy0 - 1
                }
            } else { //(1,0) and (0,1) are the closest two vertices.
                xsv_ext = xsb + 1
                ysv_ext = ysb + 1
                dx_ext = dx0 - 1 - 2 * SQUISH_CONSTANT_2D
                dy_ext = dy0 - 1 - 2 * SQUISH_CONSTANT_2D
            }
        } else { //We're inside the triangle (2-Simplex) at (1,1)
            val zins = 2 - inSum
            if (zins < xins || zins < yins) { //(0,0) is one of the closest two triangular vertices
                if (xins > yins) {
                    xsv_ext = xsb + 2
                    ysv_ext = ysb + 0
                    dx_ext = dx0 - 2 - 2 * SQUISH_CONSTANT_2D
                    dy_ext = dy0 + 0 - 2 * SQUISH_CONSTANT_2D
                } else {
                    xsv_ext = xsb + 0
                    ysv_ext = ysb + 2
                    dx_ext = dx0 + 0 - 2 * SQUISH_CONSTANT_2D
                    dy_ext = dy0 - 2 - 2 * SQUISH_CONSTANT_2D
                }
            } else { //(1,0) and (0,1) are the closest two vertices.
                dx_ext = dx0
                dy_ext = dy0
                xsv_ext = xsb
                ysv_ext = ysb
            }
            xsb += 1
            ysb += 1
            dx0 = dx0 - 1 - 2 * SQUISH_CONSTANT_2D
            dy0 = dy0 - 1 - 2 * SQUISH_CONSTANT_2D
        }

        //Contribution (0,0) or (1,1)
        var attn0 = 2.0 - dx0 * dx0 - dy0 * dy0
        if (attn0 > 0) {
            attn0 *= attn0
            value += attn0 * attn0 * extrapolate(xsb, ysb, dx0, dy0)
        }

        //Extra Vertex
        var attn_ext = 2.0 - dx_ext * dx_ext - dy_ext * dy_ext
        if (attn_ext > 0) {
            attn_ext *= attn_ext
            value += attn_ext * attn_ext * extrapolate(xsv_ext, ysv_ext, dx_ext, dy_ext)
        }

        return value / NORM_CONSTANT_2D

    }

    private fun extrapolate(xsb: Int, ysb: Int, dx: Double, dy: Double): Double {
        val index = perm[perm[xsb and 0xFF] + ysb and 0xFF] and 0x0E
        return gradients[index / 2].x * dx + gradients[index / 2].y * dy
    }
}