package io.github.mikan.sample.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
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

@Composable
fun AnimatedContentSample(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // simple usage
        Row {
            // targetState
            var count by remember { mutableIntStateOf(0) }
            Button(onClick = { count++ }) {
                Text("Add")
            }
            AnimatedContent(
                targetState = count,
                label = "animated content"
            ) { targetCount ->
                // lambdaパラメータを使う必要がある
                Text(text = "Count: $targetCount")
            }
        }

        // customize animation
        Column {
            var count by remember { mutableIntStateOf(0) }

            Row {
                Button(onClick = { count++ }) {
                    Text("Add")
                }
                Button(onClick = { count-- }) {
                    Text("Subtract")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("clip = true")
                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        val contentTransform = if (targetState > initialState) {
                            val enter = slideInVertically(tween(500)) { it } + fadeIn(tween(500))
                            val exit = slideOutVertically(tween(500)) { -it } + fadeOut(tween(500))
                            enter togetherWith exit
                        } else {
                            // デクリメントの際は、上から下にアニメーション
                            val enter = slideInVertically(tween(500)) { -it } + fadeIn(tween(500))
                            val exit = slideOutVertically(tween(500)) { it } + fadeOut(tween(500))
                            enter togetherWith exit
                        }
                        // clip = false にすることで、コンテンツの領域をはみ出してアニメーションする
                        // そのままの状態だと、クリッピングされるのでコンテンツ領域外になると見えなくなる
                        contentTransform
                    },
                    label = "animated content",
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, Color.Black),
                ) { targetCount ->
                    Text(
                        text = "$targetCount",
                    )
                }

                Text("clip = false")
                AnimatedContent(
                    targetState = count,
                    transitionSpec = {
                        val contentTransform = if (targetState > initialState) {
                            val enter = slideInVertically(tween(500)) { it } + fadeIn(tween(500))
                            val exit = slideOutVertically(tween(500)) { -it } + fadeOut(tween(500))
                            enter togetherWith exit
                        } else {
                            val enter = slideInVertically(tween(500)) { -it } + fadeIn(tween(500))
                            val exit = slideOutVertically(tween(500)) { it } + fadeOut(tween(500))
                            enter togetherWith exit
                        }
                        // clip = false にすることで、コンテンツの領域をはみ出してアニメーションする
                        // そのままの状態だと、クリッピングされるのでコンテンツ領域外になると見えなくなる
                        val sizeTransform = SizeTransform(false)
                        contentTransform using sizeTransform
                    },
                    label = "animated content",
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, Color.Black),
                ) { targetCount ->
                    Text(
                        text = "$targetCount",
                    )
                }
            }
        }

        // SizeTransform sample
        var expanded by remember { mutableStateOf(false) }
        Surface(
            color = MaterialTheme.colorScheme.primary,
            onClick = { expanded = !expanded },
        ) {
            AnimatedContent(
                contentAlignment = Alignment.TopStart,
                targetState = expanded,
                transitionSpec = {
                    fadeIn() togetherWith
                            fadeOut() using
                            SizeTransform(false) { initialSize, targetSize ->
                                if (targetState) {
                                    // expand
                                    keyframes {
                                        // まず横に伸ばす
                                        IntSize(targetSize.width, initialSize.height) at 150
                                        durationMillis = 300
                                        // その後縦に伸ばす
                                    }
                                } else {
                                    // shrink
                                    keyframes {
                                        // まず縦に縮める
                                        IntSize(initialSize.width, targetSize.height) at 150
                                        durationMillis = 300
                                        // その後横に縮める
                                    }
                                }
                            }
                },
                label = "size transform",
                modifier = Modifier.padding(16.dp)
            ) { targetExpanded ->
                if (targetExpanded) {
                    Text(
                        """
                        EnterTransition defines how the target content should appear, and ExitTransition defines how the initial content should disappear. In addition to all of the EnterTransition and ExitTransition functions available for AnimatedVisibility, AnimatedContent offers slideIntoContainer and slideOutOfContainer. These are convenient alternatives to slideInHorizontally/Vertically and slideOutHorizontally/Vertically that calculate the slide distance based on the sizes of the initial content and the target content of the AnimatedContent content.

                        SizeTransform defines how the size should animate between the initial and the target contents. You have access to both the initial size and the target size when you are creating the animation. SizeTransform also controls whether the content should be clipped to the component size during animations.
                    """.trimIndent()
                    )
                } else {
                    Icon(Icons.Default.Edit, null)
                }
            }
        }
    }
}

// Preview
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

@Preview
@Composable
private fun AnimatedContentSamplePreview() {
    AnimationTheme {
        Surface {
            AnimatedContentSample()
        }
    }
}
