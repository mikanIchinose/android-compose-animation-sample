package io.github.mikan.sample.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mikan.sample.animation.ui.theme.AnimationTheme

/**
 * references
 * - https://developer.android.com/develop/ui/compose/animation/quick-guide
 */

@Composable
fun SimpleAnimatedVisibility(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    Column(modifier) {
        Button({ visible = !visible }) {
            Text(if (visible) "Hide" else "Show")
        }
        AnimatedVisibility(
            visible = visible,
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
        }
    }
}

/**
 * Enter animation
 *   親が先に表示されて、遅れて子がfadeInする
 * Exit animation
 *   子が先にfadeOutして、親も非表示になる
 */
@Composable
fun AnimatedVisibilityWithAnimateEnterExit(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    Column(modifier) {
        Button({ visible = !visible }) {
            Text(if (visible) "Hide" else "Show")
        }
        AnimatedVisibility(
            visible = visible,
            exit = fadeOut(
                animationSpec = tween(delayMillis = 300)
            ) + shrinkVertically(
                animationSpec = tween(delayMillis = 300)
            ),
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0x88000000))
                    .padding(16.dp)
            ) {
                Box(
                    Modifier
                        .animateEnterExit(
                            enter = fadeIn(
                                animationSpec = tween(delayMillis = 300)
                            )
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .requiredHeight(100.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Text("Hello World")
                }
            }
        }
    }
}

@Composable
fun AnimatedVisibilityWithMutableTransitionState(modifier: Modifier = Modifier) {
    val state = remember {
        MutableTransitionState(false).apply {
            // アニメーションを直ちに開始する
            targetState = true
        }
    }

    Column(modifier) {
        Button({ state.targetState = !state.targetState }) {
            Text(if (state.currentState) "Hide" else "Show")
        }
        Text(
            text = when {
                state.isIdle && state.currentState -> "Visible"
                !state.isIdle && state.currentState -> "Disappearing"
                state.isIdle && !state.currentState -> "Invisible"
                else -> "Appearing"
            }
        )
        AnimatedVisibility(
            visibleState = state,
            enter = fadeIn(
                animationSpec = tween(delayMillis = 300)
            ) + expandHorizontally(
                animationSpec = tween(delayMillis = 300)
            ),
            exit = shrinkHorizontally(
                animationSpec = tween(delayMillis = 300)
            ) + fadeOut(
                animationSpec = tween(delayMillis = 300)
            )
        ) {
            Text(text = "Hello, world!")
        }
    }
}

@Preview
@Composable
private fun AnimateVisibilityPreview() {
    AnimationTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                SimpleAnimatedVisibility()
                AnimatedVisibilityWithAnimateEnterExit()
                AnimatedVisibilityWithMutableTransitionState()
            }
        }
    }
}
