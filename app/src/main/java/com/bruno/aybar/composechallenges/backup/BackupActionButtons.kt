package com.bruno.aybar.composechallenges.backup

import androidx.compose.animation.core.*
import androidx.compose.animation.core.AnimationConstants.DefaultDurationMillis
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bruno.aybar.composechallenges.common.AnimationStateHolder
import com.bruno.aybar.composechallenges.common.transition
import com.bruno.aybar.composechallenges.ui.buttonHeight


private val backupButtonAlpha = FloatPropKey("Backup button alpha")
private val backupButtonSizeMultiplier = FloatPropKey("Backup button size multiplier")

private val cancelButtonAlpha = FloatPropKey("Cancel button alpha")
private val cancelButtonWidth = IntPropKey("Cancel button width")

enum class ButtonsState {
    CreateBackup,
    Cancel
}

class BackupActionButtonState: AnimationStateHolder<ButtonsState>(
    initialState = ButtonsState.CreateBackup
){

    fun update(ui: BackupUi) {
        animateTo(newState = when(ui) {
            is BackupUi.RequestBackup -> ButtonsState.CreateBackup
            else -> ButtonsState.Cancel
        })
    }

}


@Composable
fun BackupActionButtons(
    ui: BackupUi,
    onBackup: ()->Unit,
    onCancel: ()->Unit,
    modifier: Modifier
) {
    val animationState = remember { BackupActionButtonState() }

    animationState.update(ui)

    val transition = transition(
        definition = AnimateButtonsTransition,
        stateHolder = animationState
    )

    Box(modifier) {

        BackupButton(
            onClick = onBackup,
            transition = transition,
            modifier = Modifier.align(Alignment.Center)
        )

        CancelButton(
            onClick = onCancel,
            transition = transition,
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@Composable
private fun BackupButton(onClick: ()->Unit, modifier: Modifier, transition: TransitionState) {
    if(transition[backupButtonAlpha] == 0f) return

    Button(
        onClick = onClick,
        modifier = modifier
            .alpha(transition[backupButtonAlpha])
            .preferredWidth(240.dp * transition[backupButtonSizeMultiplier])
            .preferredHeight(buttonHeight * transition[backupButtonSizeMultiplier]),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Create Backup")
    }
}

@Composable
private fun CancelButton(onClick: ()->Unit, modifier: Modifier, transition: TransitionState) {
    if(transition[cancelButtonAlpha] == 0f) return

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .alpha(transition[cancelButtonAlpha])
            .preferredWidth(transition[cancelButtonWidth].dp)
            .preferredHeight(buttonHeight),
    ) {
        Text(text = "Cancel")
    }
}

private val AnimateButtonsTransition = transitionDefinition<ButtonsState> {
    val animationDuration = DefaultDurationMillis

    state(ButtonsState.CreateBackup) {
        this[backupButtonAlpha] = 1f
        this[backupButtonSizeMultiplier] = 1f

        this[cancelButtonAlpha] = 0f
        this[cancelButtonWidth] = 180
    }
    state(ButtonsState.Cancel) {
        this[backupButtonAlpha] = 0f
        this[backupButtonSizeMultiplier] = 1f
        this[cancelButtonAlpha] = 0.6f
        this[cancelButtonWidth] = 150
    }

    transition(fromState = ButtonsState.CreateBackup, toState = ButtonsState.Cancel) {
        backupButtonSizeMultiplier using keyframes { 0.95f at animationDuration / 2 }
        backupButtonAlpha using tween(delayMillis = animationDuration, easing = LinearEasing)
        cancelButtonAlpha using tween(delayMillis = animationDuration, easing = LinearEasing)
        cancelButtonWidth using tween(delayMillis = animationDuration + 150)
    }

    transition(fromState = ButtonsState.Cancel, toState = ButtonsState.CreateBackup) {
        backupButtonAlpha using tween(easing = LinearEasing)
        cancelButtonAlpha using tween(easing = LinearEasing)
        cancelButtonWidth using tween()
        backupButtonSizeMultiplier using keyframes { 1.05f at animationDuration / 2 }
    }

}