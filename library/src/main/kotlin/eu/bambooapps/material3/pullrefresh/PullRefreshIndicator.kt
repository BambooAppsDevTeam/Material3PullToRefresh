/*
 * Copyright 2022 The Android Open Source Project
 * Copyright 2023 Bamboo Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.bambooapps.material3.pullrefresh

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.AlphaTween
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.ArcRadius
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.ArrowHeight
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.ArrowWidth
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.IndicatorSize
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.MAX_ALPHA
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.MAX_PROGRESS_ARC
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.MIN_ALPHA
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicatorDefaults.SpinnerShape
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * The default indicator for Compose pull-to-refresh, based on Android's SwipeRefreshLayout.
 * Taken from compose-material library and adapted for Material 3
 *
 *
 * @param refreshing A boolean representing whether a refresh is occurring.
 * @param state The [PullRefreshState] which controls where and how the indicator will be drawn.
 * @param modifier Modifiers for the indicator.
 * @param colors The colors of the indicator's container, arc and arrow.
 * @param shadowElevation The colors of the indicator's container, arc and arrow.
 * @param scale A boolean controlling whether the indicator's size scales with pull progress or not.
 */
@Composable
@ExperimentalMaterial3Api
fun PullRefreshIndicator(
    refreshing: Boolean,
    state: PullRefreshState,
    modifier: Modifier = Modifier,
    colors: PullRefreshIndicatorColors = PullRefreshIndicatorDefaults.colors(),
    tonalElevation: Dp = PullRefreshIndicatorDefaults.Elevation,
    shadowElevation: Dp = 0.dp,
    scale: Boolean = false
) {
    val showElevation by remember(refreshing, state) {
        derivedStateOf {
            refreshing || state.position > PullRefreshIndicatorDefaults.POSITION_THRESHOLD
        }
    }

    Surface(
        color = colors.containerColor().value,
        contentColor = colors.contentColor().value,
        tonalElevation = if (showElevation) {
            tonalElevation
        } else {
            0.dp
        },
        shadowElevation = if (showElevation) {
            shadowElevation
        } else {
            0.dp
        },
        shape = SpinnerShape,
        modifier = modifier
            .size(IndicatorSize)
            .pullRefreshIndicatorTransform(state, scale)
    ) {
        Crossfade(
            targetState = refreshing,
            animationSpec = tween(
                durationMillis = PullRefreshIndicatorDefaults.CROSSFADE_DURATION_MS
            ),
            label = "PullRefreshIndicator"
        ) { refreshing ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val spinnerSize =
                    (ArcRadius + PullRefreshIndicatorDefaults.StrokeWidth).times(
                        2
                    )

                if (refreshing) {
                    CircularProgressIndicator(
                        color = colors.contentColor().value,
                        strokeWidth = PullRefreshIndicatorDefaults.StrokeWidth,
                        modifier = Modifier.size(spinnerSize)
                    )
                } else {
                    CircularArrowIndicator(
                        state,
                        colors.contentColor().value,
                        Modifier.size(spinnerSize)
                    )
                }
            }
        }
    }
}

/**
 * Modifier.size MUST be specified.
 */
@Composable
@ExperimentalMaterial3Api
private fun CircularArrowIndicator(
    state: PullRefreshState,
    color: Color,
    modifier: Modifier = Modifier
) {
    val path = remember { Path().apply { fillType = PathFillType.EvenOdd } }

    val targetAlpha by remember(state) {
        derivedStateOf {
            if (state.progress >= 1f) {
                MAX_ALPHA
            } else {
                MIN_ALPHA
            }
        }
    }

    val alphaState = animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = AlphaTween,
        label = "alphaState"
    )

    // Empty semantics for tests
    Canvas(modifier.semantics {}) {
        val values = ArrowValues(state.progress)
        val alpha = alphaState.value

        rotate(degrees = values.rotation) {
            val arcRadius = ArcRadius.toPx() + PullRefreshIndicatorDefaults.StrokeWidth.toPx() / 2f
            val arcBounds = Rect(
                size.center.x - arcRadius,
                size.center.y - arcRadius,
                size.center.x + arcRadius,
                size.center.y + arcRadius
            )
            drawArc(
                color = color,
                alpha = alpha,
                startAngle = values.startAngle,
                sweepAngle = values.endAngle - values.startAngle,
                useCenter = false,
                topLeft = arcBounds.topLeft,
                size = arcBounds.size,
                style = Stroke(
                    width = PullRefreshIndicatorDefaults.StrokeWidth.toPx(),
                    cap = StrokeCap.Square
                )
            )
            drawArrow(path, arcBounds, color, alpha, values)
        }
    }
}

@Immutable
private class ArrowValues(
    val rotation: Float,
    val startAngle: Float,
    val endAngle: Float,
    val scale: Float
)

private const val DISCARD_PROGRESS_PERCENTAGE = 0.4f
private const val TENSION_PERCENT_REDUCER = 4
private const val ADJUSTED_PERCENT_MULTIPLIER = 5
private const val ADJUSTED_PERCENT_ROTATION_MULTIPLIER = 0.4f
private const val ROTATION_REDUCER = -0.25f
private const val ADJUSTED_PERCENT_REDUCER = 3
private const val FULL_ROTATION_ANGLE = 360
private const val ROTATION_MULTIPLIER = 0.5f

private fun ArrowValues(progress: Float): ArrowValues {
    // Discard first 40% of progress. Scale remaining progress to full range between 0 and 100%.
    val adjustedPercent = max(
        min(1f, progress) - DISCARD_PROGRESS_PERCENTAGE,
        0f
    ) * ADJUSTED_PERCENT_MULTIPLIER / ADJUSTED_PERCENT_REDUCER
    // How far beyond the threshold pull has gone, as a percentage of the threshold.
    val overshootPercent = abs(progress) - 1.0f
    // Limit the overshoot to 200%. Linear between 0 and 200.
    val linearTension = overshootPercent.coerceIn(0f, 2f)
    // Non-linear tension. Increases with linearTension, but at a decreasing rate.
    val tensionPercent = linearTension - linearTension.pow(2) / TENSION_PERCENT_REDUCER

    // Calculations based on SwipeRefreshLayout specification.
    val endTrim = adjustedPercent * MAX_PROGRESS_ARC
    val rotation = (
        ROTATION_REDUCER +
            ADJUSTED_PERCENT_ROTATION_MULTIPLIER * adjustedPercent +
            tensionPercent
        ) * ROTATION_MULTIPLIER
    val startAngle = rotation * FULL_ROTATION_ANGLE
    val endAngle = (rotation + endTrim) * FULL_ROTATION_ANGLE
    val scale = min(1f, adjustedPercent)

    return ArrowValues(rotation, startAngle, endAngle, scale)
}

private fun DrawScope.drawArrow(
    arrow: Path,
    bounds: Rect,
    color: Color,
    alpha: Float,
    values: ArrowValues
) {
    arrow.reset()
    arrow.moveTo(0f, 0f) // Move to left corner
    arrow.lineTo(x = ArrowWidth.toPx() * values.scale, y = 0f) // Line to right corner

    // Line to tip of arrow
    arrow.lineTo(
        x = ArrowWidth.toPx() * values.scale / 2,
        y = ArrowHeight.toPx() * values.scale
    )

    val radius = min(bounds.width, bounds.height) / 2f
    val inset = ArrowWidth.toPx() * values.scale / 2f
    arrow.translate(
        Offset(
            x = radius + bounds.center.x - inset,
            y = bounds.center.y + PullRefreshIndicatorDefaults.StrokeWidth.toPx() / 2f
        )
    )
    arrow.close()
    rotate(degrees = values.endAngle) {
        drawPath(path = arrow, color = color, alpha = alpha)
    }
}

object PullRefreshIndicatorDefaults {
    const val CROSSFADE_DURATION_MS = 100
    const val MAX_PROGRESS_ARC = 0.8f
    private const val ALPHA_TWEEN_DURATION_MILLIS = 300
    const val POSITION_THRESHOLD = 0.5f

    val IndicatorSize = 40.dp
    val SpinnerShape = CircleShape
    val ArcRadius = 7.5.dp
    val StrokeWidth = 2.5.dp
    val ArrowWidth = 10.dp
    val ArrowHeight = 5.dp
    val Elevation = 6.dp

    // Values taken from SwipeRefreshLayout
    const val MIN_ALPHA = 0.3f
    const val MAX_ALPHA = 1f
    val AlphaTween = tween<Float>(ALPHA_TWEEN_DURATION_MILLIS, easing = LinearEasing)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = MaterialTheme.colorScheme.onSurface
    ): PullRefreshIndicatorColors = PullRefreshIndicatorColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}

@Immutable
class PullRefreshIndicatorColors internal constructor(
    private val containerColor: Color,
    private val contentColor: Color
) {
    @Composable
    internal fun containerColor(): State<Color> {
        return rememberUpdatedState(containerColor)
    }

    @Composable
    internal fun contentColor(): State<Color> {
        return rememberUpdatedState(contentColor)
    }
}
